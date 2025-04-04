
package com.primemedia.studioflix.dialogs;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.Player;
import androidx.media3.common.TrackGroup;
import androidx.media3.common.TrackSelectionOverride;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.Tracks;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.TrackNameProvider;
import androidx.media3.ui.TrackSelectionView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.collect.ImmutableList;
import com.primemedia.studioflix.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class TrackSelectionDialog extends BottomSheetDialogFragment {
    private BottomSheetBehavior mBottomBehavior;

    public interface TrackSelectionListener {


        void onTracksSelected(TrackSelectionParameters trackSelectionParameters);
    }

    public static final ImmutableList<Integer> SUPPORTED_TRACK_TYPES =
            ImmutableList.of(C.TRACK_TYPE_VIDEO);

    private final SparseArray<TrackSelectionViewFragment> tabFragments;
    private final ArrayList<Integer> tabTrackTypes;

    private int titleId;
    private DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onDismissListener;

    public static boolean willHaveContent(Player player) {
        return willHaveContent(player.getCurrentTracks());
    }

    public static boolean willHaveContent(Tracks tracks) {
        for (Tracks.Group trackGroup : tracks.getGroups()) {
            if (SUPPORTED_TRACK_TYPES.contains(trackGroup.getType())) {
                return true;
            }
        }
        return false;
    }


    public static TrackSelectionDialog createForPlayer(
            Player player, DialogInterface.OnDismissListener onDismissListener) {
        return createForTracksAndParameters(
                R.string.track_selection_title,
                player.getCurrentTracks(),
                player.getTrackSelectionParameters(),
                /* allowAdaptiveSelections= */ true,
                /* allowMultipleOverrides= */ false,
                player::setTrackSelectionParameters,
                onDismissListener);
    }

    public static TrackSelectionDialog createForTracksAndParameters(
            int titleId,
            Tracks tracks,
            TrackSelectionParameters trackSelectionParameters,
            boolean allowAdaptiveSelections,
            boolean allowMultipleOverrides,
            TrackSelectionListener trackSelectionListener,
            DialogInterface.OnDismissListener onDismissListener) {
        TrackSelectionDialog trackSelectionDialog = new TrackSelectionDialog();
        trackSelectionDialog.init(
                tracks,
                trackSelectionParameters,
                titleId,
                allowAdaptiveSelections,
                allowMultipleOverrides,
                /* onClickListener= */ (dialog, which) -> {
                    TrackSelectionParameters.Builder builder = trackSelectionParameters.buildUpon();
                    for (int i = 0; i < SUPPORTED_TRACK_TYPES.size(); i++) {
                        int trackType = SUPPORTED_TRACK_TYPES.get(i);
                        builder.setTrackTypeDisabled(trackType, trackSelectionDialog.getIsDisabled(trackType));
                        builder.clearOverridesOfType(trackType);
                        Map<TrackGroup, TrackSelectionOverride> overrides =
                                trackSelectionDialog.getOverrides(trackType);
                        for (TrackSelectionOverride override : overrides.values()) {
                            builder.addOverride(override);
                        }
                    }
                    trackSelectionListener.onTracksSelected(builder.build());
                },
                onDismissListener);
        return trackSelectionDialog;
    }

    public TrackSelectionDialog() {
        tabFragments = new SparseArray<>();
        tabTrackTypes = new ArrayList<>();
        // Retain instance across activity re-creation to prevent losing access to init data.
        setRetainInstance(true);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void init(
            Tracks tracks,
            TrackSelectionParameters trackSelectionParameters,
            int titleId,
            boolean allowAdaptiveSelections,
            boolean allowMultipleOverrides,
            DialogInterface.OnClickListener onClickListener,
            DialogInterface.OnDismissListener onDismissListener) {
        this.titleId = titleId;
        this.onClickListener = onClickListener;
        this.onDismissListener = onDismissListener;

        for (int i = 0; i < SUPPORTED_TRACK_TYPES.size(); i++) {
            @C.TrackType int trackType = SUPPORTED_TRACK_TYPES.get(i);
            ArrayList<Tracks.Group> trackGroups = new ArrayList<>();
            for (Tracks.Group trackGroup : tracks.getGroups()) {
                if (trackGroup.getType() == trackType) {
                    trackGroups.add(trackGroup);
                }
            }
            if (!trackGroups.isEmpty()) {
                TrackSelectionViewFragment tabFragment = new TrackSelectionViewFragment();
                tabFragment.init(
                        trackGroups,
                        trackSelectionParameters.disabledTrackTypes.contains(trackType),
                        trackSelectionParameters.overrides,
                        allowAdaptiveSelections,
                        allowMultipleOverrides);
                tabFragments.put(trackType, tabFragment);
                tabTrackTypes.add(trackType);
            }
        }
    }


    @OptIn(markerClass = UnstableApi.class)
    public boolean getIsDisabled(int trackType) {
        TrackSelectionViewFragment trackView = tabFragments.get(trackType);
        return trackView != null && trackView.isDisabled;
    }


    @OptIn(markerClass = UnstableApi.class)
    public Map<TrackGroup, TrackSelectionOverride> getOverrides(int trackType) {
        TrackSelectionViewFragment trackView = tabFragments.get(trackType);
        return trackView == null ? Collections.emptyMap() : trackView.overrides;
    }

    @Override
    public BottomSheetDialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog =
                new BottomSheetDialog(getActivity(), R.style.CustomBottomSheetDialog);
        return bottomSheetDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDismissListener.onDismiss(dialog);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.audio_selection_dialog, container, false);
        ViewPager viewPager = dialogView.findViewById(R.id.track_selection_dialog_view_pager);
        Button cancelButton = dialogView.findViewById(R.id.track_selection_dialog_cancel_button);
        Button okButton = dialogView.findViewById(R.id.track_selection_dialog_ok_button);
        viewPager.setAdapter(new FragmentAdapter(getChildFragmentManager()));
        cancelButton.setOnClickListener(view -> dismiss());
        okButton.setOnClickListener(
                view -> {
                    onClickListener.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
                    dismiss();
                });
        return dialogView;
    }

    private static String getTrackTypeString(Resources resources, @C.TrackType int trackType) {
        switch (trackType) {
            case C.TRACK_TYPE_VIDEO:
                return resources.getString(androidx.media3.ui.R.string.exo_track_selection_title_video);

            default:
                throw new IllegalArgumentException();
        }
    }

    private final class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fragmentManager) {
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            return tabFragments.get(tabTrackTypes.get(position));
        }

        @Override
        public int getCount() {
            return tabTrackTypes.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getTrackTypeString(getResources(), tabTrackTypes.get(position));
        }
    }

    @UnstableApi

    public static final class TrackSelectionViewFragment extends Fragment
            implements TrackSelectionView.TrackSelectionListener {

        private List<Tracks.Group> trackGroups;
        private boolean allowAdaptiveSelections;
        private boolean allowMultipleOverrides;

        /* package */ boolean isDisabled;
        /* package */ Map<TrackGroup, TrackSelectionOverride> overrides;

        public TrackSelectionViewFragment() {
            // Retain instance across activity re-creation to prevent losing access to init data.
            setRetainInstance(true);
        }

        @OptIn(markerClass = UnstableApi.class)
        public void init(
                List<Tracks.Group> trackGroups,
                boolean isDisabled,
                Map<TrackGroup, TrackSelectionOverride> overrides,
                boolean allowAdaptiveSelections,
                boolean allowMultipleOverrides) {
            this.trackGroups = trackGroups;
            this.isDisabled = isDisabled;
            this.allowAdaptiveSelections = allowAdaptiveSelections;
            this.allowMultipleOverrides = allowMultipleOverrides;
            // TrackSelectionView does this filtering internally, but we need to do it here as well to
            // handle the case where the TrackSelectionView is never created.
            this.overrides =
                    new HashMap<>(
                            TrackSelectionView.filterOverrides(overrides, trackGroups, allowMultipleOverrides));
        }

        @OptIn(markerClass = UnstableApi.class)
        @Override
        public View onCreateView(
                LayoutInflater inflater,
                @Nullable ViewGroup container,
                @Nullable Bundle savedInstanceState) {
            @SuppressLint("PrivateResource") View rootView =
                    inflater.inflate(
                            androidx.media3.ui.R.layout.exo_track_selection_dialog, container, /* attachToRoot= */ false);
            TrackSelectionView trackSelectionView = rootView.findViewById(androidx.media3.ui.R.id.exo_track_selection_view);

            trackSelectionView.setShowDisableOption(false);
            trackSelectionView.setAllowMultipleOverrides(allowMultipleOverrides);
            trackSelectionView.setAllowAdaptiveSelections(allowAdaptiveSelections);
            TrackNameProvider trackNameProvider = new CustomTrackNameProvider(getResources());
            trackSelectionView.setTrackNameProvider(f -> f.height != Format.NO_VALUE ? (Math.round(f.frameRate) + f.height + " P") : trackNameProvider.getTrackName(f));

            trackSelectionView.setTrackNameProvider(trackNameProvider);
            trackSelectionView.init(
                    trackGroups,
                    isDisabled,
                    overrides,
                    /* trackFormatComparator= */ null,
                    /* listener= */ this);
            return rootView;
        }

        @Override
        public void onTrackSelectionChanged(
                boolean isDisabled, Map<TrackGroup, TrackSelectionOverride> overrides) {
            this.isDisabled = isDisabled;
            this.overrides = overrides;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}