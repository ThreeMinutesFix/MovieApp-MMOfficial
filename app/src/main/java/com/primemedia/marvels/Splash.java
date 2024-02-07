package com.primemedia.marvels;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onesignal.OneSignal;
import com.primemedia.marvels.adapter.OnboardingPagerAdapter;
import com.primemedia.marvels.utility.GospelUtil;
import com.primemedia.marvels.utility.TinyDB;
import com.primemedia.marvels.utility.Utils;
import com.rd.PageIndicatorView;

import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire;
import org.imaginativeworld.oopsnointernet.snackbars.fire.SnackbarPropertiesFire;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class Splash extends AppCompatActivity {

    ImageView logo;

    private boolean vpnStatus = false;
    boolean pinLockStatus;
    int loginMandatory, maintenance;
    public static MediaPlayer mediaPlayer;
    public static String notificationData = "";
    private GospelUtil helperUtils;
    String userData;
    String pinLockPin;
    String apiKey;

    Context context = this;
    TinyDB tinyDB;
    String blocked_regions;
    AppUpdateManager appUpdateManager = null;
    String latestAPKVersionName;
    String latestAPKVersionCode;
    String apkFileUrl;
    String whatsNewOnLatestApk;
    int updateSkipable;
    int updateType;

    int googleplayAppUpdateType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        check_network();
        tinyDB = new TinyDB(context);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(2600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Constants.rawUrl = mFirebaseRemoteConfig.getString("SERVER_URL");
                        Constants.url = mFirebaseRemoteConfig.getString("SERVER_URL") + "android/";
                        Constants.apiKey = mFirebaseRemoteConfig.getString("API_KEY");
                        Log.d("rawurl", Constants.rawUrl);
                        Log.d("rawurl", Constants.apiKey);
                        switch (tinyDB.getInt("splashScreenType")) {
                            default:
                                Window window = Splash.this.getWindow();
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                setContentView(R.layout.activity_splash);
                                logo = findViewById(R.id.logo);
                                Glide.with(this)
                                        .asGif()
                                        .load(R.drawable.movie_app_logo)
                                        .skipMemoryCache(true)
                                        .addListener(new RequestListener<GifDrawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                                startAudio();
                                                return false;
                                            }
                                        })
                                        .into(logo);
                        }
                        helperUtils = new GospelUtil(Splash.this);
                        vpnStatus = new GospelUtil(Splash.this).isVpnConnectionAvailable();
                        ApplicationInfo restrictedApp = helperUtils.getRestrictApp();
                        if (restrictedApp != null) {
                            Log.e("test", restrictedApp.loadLabel(Splash.this.getPackageManager()).toString());
                            GospelUtil.showWarningDialog(Splash.this, "Restricted App!", "Please Uninstall " + restrictedApp.loadLabel(Splash.this.getPackageManager()).toString() + " to use this App On this Device!");
                        } else if (GospelUtil.cr(Splash.this, Constants.allowRoot)) {
                            GospelUtil.showWarningDialog(Splash.this, "ROOT!", "You are Not Allowed To Use this App on Rooted Device!");
                        } else if (Constants.allowVPN) {
                            loadData();
                        } else if (vpnStatus) {
                            GospelUtil.showWarningDialog(Splash.this, "VPN!", "You are Not Allowed To Use VPN Here!");
                        } else {
                            loadData();
                        }


                    } else {
                        serverError();
                    }
                });
    }

    private void startAudio() {
        if (GospelUtil.isFirstOpen(context)) {
            // donothing
        } else {

        }

    }

    private void loadData() {
        verifyInstaller();
    }

    private boolean checkStoragePermission() {

        return true;
    }

    private void verifyInstaller() {
        loadUserData();
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        userData = sharedPreferences.getString("UserData", null);
        loadConfig();
    }


    private void loadConfig() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "get_config", response ->
        {
            Log.d("responses_config", response);
            JsonObject jsonObjectJWT = new Gson().fromJson(response, JsonObject.class);
            String token = jsonObjectJWT.get("token").getAsString();
            Log.d("responses_config", token);
            try {
                com.auth0.jwt.algorithms.Algorithm algorithm = Algorithm.HMAC256(Constants.apiKey);
                JWTVerifier verifier = JWT.require(algorithm)
                        .build();
                //DecodedJWT verify = verifier.verify(token);
                DecodedJWT jwt = JWT.decode(token);

                String config = jwt.getClaim("config").toString();

                JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
                apiKey = jsonObject.get("api_key").getAsString();
                loginMandatory = jsonObject.get("login_mandatory").getAsInt();
                maintenance = jsonObject.get("maintenance").getAsInt();
                blocked_regions = jsonObject.get("blocked_regions").isJsonNull() ? "" : jsonObject.get("blocked_regions").getAsString();
                saveConfig(config);
                saveNotification();
                OneSignal.initWithContext(Splash.this);
                OneSignal.setAppId(jsonObject.get("onesignal_appid").getAsString());
                OneSignal.setNotificationOpenedHandler(
                        result -> Splash.notificationData = result.getNotification().getAdditionalData().toString());

                latestAPKVersionName = jsonObject.get("Latest_APK_Version_Name").getAsString();
                latestAPKVersionCode = jsonObject.get("Latest_APK_Version_Code").getAsString();
                apkFileUrl = jsonObject.get("APK_File_URL").getAsString();
                whatsNewOnLatestApk = jsonObject.get("Whats_new_on_latest_APK").getAsString();
                updateSkipable = jsonObject.get("Update_Skipable").getAsInt();
                updateType = jsonObject.get("Update_Type").getAsInt();
                googleplayAppUpdateType = jsonObject.get("googleplayAppUpdateType").getAsInt();

                String whatsNew = whatsNewOnLatestApk.replace(",", "\n").trim();

                int version = BuildConfig.VERSION_CODE;
                int latestVersionCode;
                try {
                    latestVersionCode = Integer.parseInt(latestAPKVersionCode);
                } catch (NumberFormatException e) {
                    latestVersionCode = 1;
                }

                int onboarding_status = 0;
                if (!jsonObject.get("onboarding_status").isJsonNull()) {
                    onboarding_status = jsonObject.get("onboarding_status").getAsInt();
                }

                if (GospelUtil.isFirstOpen(context)) {
                    if (onboarding_status == 1) {

                        View onboard_pager = findViewById(R.id.onboard_pager);
                        onboard_pager.setVisibility(View.VISIBLE);
                        ViewPager viewPager = onboard_pager.findViewById(R.id.OnBoarding_pager);
                        OnboardingPagerAdapter pagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());
                        viewPager.setAdapter(pagerAdapter);
                        PageIndicatorView pageIndicatorView = onboard_pager.findViewById(R.id.pageIndicatorView);
                        pageIndicatorView.setCount(4);
                        pageIndicatorView.setSelection(0);
                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*empty*/}

                            @Override
                            public void onPageSelected(int position) {
                                pageIndicatorView.setSelection(position);
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {/*empty*/}
                        });

                        MaterialButton get_started = onboard_pager.findViewById(R.id.get_started);
                        int finalLatestVersionCode = latestVersionCode;
                        get_started.setOnClickListener(v ->
                        {
                            if (finalLatestVersionCode > version) {
                                if (updateType == 2) {
                                    appUpdateManager = AppUpdateManagerFactory.create(context);
                                    Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                                    appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                                        if (googleplayAppUpdateType == 0) {
                                            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                                try {
                                                    appUpdateManager.startUpdateFlowForResult(
                                                            appUpdateInfo,
                                                            AppUpdateType.FLEXIBLE,
                                                            Splash.this,
                                                            15);
                                                } catch (
                                                        IntentSender.SendIntentException e) {
                                                    e.printStackTrace();
                                                }

                                                InstallStateUpdatedListener listener = state -> {
                                                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                                                        Snackbar snackbar =
                                                                Snackbar.make(
                                                                        findViewById(R.id.splash),
                                                                        "An update has just been downloaded.",
                                                                        Snackbar.LENGTH_INDEFINITE);
                                                        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
                                                        snackbar.setActionTextColor(
                                                                ContextCompat.getColor(context, R.color.white));
                                                        snackbar.show();
                                                    }
                                                };
                                            }
                                        } else if (googleplayAppUpdateType == 1) {
                                            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                                try {
                                                    appUpdateManager.startUpdateFlowForResult(
                                                            appUpdateInfo,
                                                            AppUpdateType.IMMEDIATE,
                                                            Splash.this,
                                                            15);
                                                } catch (
                                                        IntentSender.SendIntentException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    MaterialDialog mDialog = new MaterialDialog.Builder(Splash.this)
                                            .setTitle("Update " + latestAPKVersionName)
                                            .setMessage(whatsNew)
                                            .setCancelable(false)
                                            .setNegativeButton(updateSkipable == 0 ? "Exit" : "Cancel", R.drawable.baseline_exit_to_app_24, (dialogInterface, which) -> {
                                                if (updateSkipable == 0) { //NO
                                                    finish();
                                                } else if (updateSkipable == 1) { //YES
                                                    dialogInterface.dismiss();
                                                    openApp();
                                                }
                                            })
                                            .setPositiveButton("Update!", R.drawable.baseline_exit_to_app_24, (dialogInterface, which) -> {
                                                if (updateType == 0) {
                                                    Intent intent = new Intent(Splash.this, InAppUpdate.class);
                                                    intent.putExtra("Update_Title", "Update " + latestAPKVersionName);
                                                    intent.putExtra("Whats_new_on_latest_APK", whatsNewOnLatestApk);
                                                    intent.putExtra("APK_File_URL", apkFileUrl);
                                                    startActivity(intent);
                                                } else if (updateType == 1) {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkFileUrl));
                                                    startActivity(intent);
                                                }
                                            })
                                            .build();
                                    mDialog.show();
                                }
                            } else {
                                openApp();
                            }
                        });
                    } else {
                        if (latestVersionCode > version) {
                            if (updateType == 2) {
                                appUpdateManager = AppUpdateManagerFactory.create(context);
                                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                                    if (googleplayAppUpdateType == 0) {
                                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                            try {
                                                appUpdateManager.startUpdateFlowForResult(
                                                        appUpdateInfo,
                                                        AppUpdateType.FLEXIBLE,
                                                        this,
                                                        15);
                                            } catch (IntentSender.SendIntentException e) {
                                                e.printStackTrace();
                                            }

                                            InstallStateUpdatedListener listener = state -> {
                                                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                                                    Snackbar snackbar =
                                                            Snackbar.make(
                                                                    findViewById(R.id.splash),
                                                                    "An update has just been downloaded.",
                                                                    Snackbar.LENGTH_INDEFINITE);
                                                    snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
                                                    snackbar.setActionTextColor(
                                                            ContextCompat.getColor(context, R.color.white));
                                                    snackbar.show();
                                                }
                                            };
                                        }
                                    } else if (googleplayAppUpdateType == 1) {
                                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                            try {
                                                appUpdateManager.startUpdateFlowForResult(
                                                        appUpdateInfo,
                                                        AppUpdateType.IMMEDIATE,
                                                        this,
                                                        15);
                                            } catch (IntentSender.SendIntentException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            } else {
                                MaterialDialog mDialog = new MaterialDialog.Builder(Splash.this)
                                        .setTitle("Update " + latestAPKVersionName)
                                        .setMessage(whatsNew)
                                        .setCancelable(false)

                                        .setNegativeButton(updateSkipable == 0 ? "Exit" : "Cancel", R.drawable.baseline_exit_to_app_24, (dialogInterface, which) -> {
                                            if (updateSkipable == 0) { //NO
                                                finish();
                                            } else if (updateSkipable == 1) { //YES
                                                dialogInterface.dismiss();
                                                openApp();
                                            }
                                        })
                                        .setPositiveButton("Update!", R.drawable.baseline_exit_to_app_24, (dialogInterface, which) -> {
                                            if (updateType == 0) {
                                                Intent intent = new Intent(Splash.this, InAppUpdate.class);
                                                intent.putExtra("Update_Title", "Update " + latestAPKVersionName);
                                                intent.putExtra("Whats_new_on_latest_APK", whatsNewOnLatestApk);
                                                intent.putExtra("APK_File_URL", apkFileUrl);
                                                startActivity(intent);
                                            } else if (updateType == 1) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkFileUrl));
                                                startActivity(intent);
                                            }
                                        })
                                        .build();
                                mDialog.show();
                            }
                        } else {
                            openApp();
                        }
                    }
                } else {
                    if (latestVersionCode > version) {
                        if (updateType == 2) {
                            appUpdateManager = AppUpdateManagerFactory.create(context);
                            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                                if (googleplayAppUpdateType == 0) {
                                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                        try {
                                            appUpdateManager.startUpdateFlowForResult(
                                                    appUpdateInfo,
                                                    AppUpdateType.FLEXIBLE,
                                                    this,
                                                    15);
                                        } catch (IntentSender.SendIntentException e) {
                                            e.printStackTrace();
                                        }

                                        InstallStateUpdatedListener listener = state -> {
                                            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                                                Snackbar snackbar =
                                                        Snackbar.make(
                                                                findViewById(R.id.splash),
                                                                "An update has just been downloaded.",
                                                                Snackbar.LENGTH_INDEFINITE);
                                                snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
                                                snackbar.setActionTextColor(
                                                        ContextCompat.getColor(context, R.color.white));
                                                snackbar.show();
                                            }
                                        };
                                    }
                                } else if (googleplayAppUpdateType == 1) {
                                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                        try {
                                            appUpdateManager.startUpdateFlowForResult(
                                                    appUpdateInfo,
                                                    AppUpdateType.IMMEDIATE,
                                                    this,
                                                    15);
                                        } catch (IntentSender.SendIntentException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        } else {
                            MaterialDialog mDialog = new MaterialDialog.Builder(Splash.this)
                                    .setTitle("Update " + latestAPKVersionName)
                                    .setMessage(whatsNew)
                                    .setCancelable(false)

                                    .setNegativeButton(updateSkipable == 0 ? "Exit" : "Cancel", R.drawable.baseline_exit_to_app_24, (dialogInterface, which) -> {
                                        if (updateSkipable == 0) { //NO
                                            finish();
                                        } else if (updateSkipable == 1) { //YES
                                            dialogInterface.dismiss();
                                            openApp();
                                        }
                                    })
                                    .setPositiveButton("Update!", R.drawable.baseline_exit_to_app_24, (dialogInterface, which) -> {
                                        if (updateType == 0) {
//                                            Intent intent = new Intent(SplashScreen.this, InAppUpdate.class);
//                                            intent.putExtra("Update_Title", "Update "+latestAPKVersionName);
//                                            intent.putExtra("Whats_new_on_latest_APK", whatsNewOnLatestApk);
//                                            intent.putExtra("APK_File_URL", apkFileUrl);
//                                            startActivity(intent);
                                        } else if (updateType == 1) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkFileUrl));
                                            startActivity(intent);
                                        }
                                    })
                                    .build();
                            mDialog.show();
                        }
                    } else {
                        openApp();
                    }
                }
            } catch (JWTVerificationException exception) {
                Log.d("test", String.valueOf(exception));
            }
        }, error -> {
            serverError();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    private void saveConfig(String config) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MyApp", config);
        editor.apply();
        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
        Constants.bGljZW5zZV9jb2Rl = jsonObject.get("license_code").isJsonNull() ? "" : jsonObject.get("license_code").getAsString();
        if (jsonObject.get("safeMode").getAsInt() == 1) {
            String safeModeVersions = jsonObject.get("safeModeVersions").isJsonNull() ? "" : jsonObject.get("safeModeVersions").getAsString();
            if (!safeModeVersions.equals("")) {
                String[] safeModeVersionsArrey = safeModeVersions.split(",");
                for (String safeModeVersion : safeModeVersionsArrey) {
                    if (BuildConfig.VERSION_NAME.equals(safeModeVersion.trim())) {
                        Constants.safeMode = true;
                    }
                }
            } else {
                Constants.safeMode = true;
            }

        }
        if (!jsonObject.get("pinLockStatus").isJsonNull()) {
            if (jsonObject.get("pinLockStatus").getAsInt() == 1) {
                pinLockStatus = true;
            } else {
                pinLockStatus = false;
            }
        }
        pinLockPin = jsonObject.get("pinLockPin").isJsonNull() ? "" : jsonObject.get("pinLockPin").getAsString();
        if (tinyDB != null) {
            tinyDB.putInt("splashScreenType", jsonObject.get("splash_screen_type").isJsonNull() ? 0 : jsonObject.get("splash_screen_type").getAsInt());
            tinyDB.putString("splashImageUrl", jsonObject.get("splash_image_url").isJsonNull() ? "" : jsonObject.get("splash_image_url").getAsString());
            tinyDB.putString("splashLottieUrl", jsonObject.get("splash_lottie_url").isJsonNull() ? "" : jsonObject.get("splash_lottie_url").getAsString());
            tinyDB.putString("splashBgColor", jsonObject.get("splash_bg_color").isJsonNull() ? "" : jsonObject.get("splash_bg_color").getAsString());
        }
        Constants.messageAnimationUrl = jsonObject.get("message_animation_url").isJsonNull() ? "" : jsonObject.get("message_animation_url").getAsString();


        Constants.packageName = jsonObject.get("package_name").isJsonNull() ? "" : jsonObject.get("package_name").getAsString();
        loadRemoteConfig();
    }

    private void loadRemoteConfig() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.GET, Utils.fromBase64("aHR0cHM6Ly9jbG91ZC50ZWFtLWRvb28uY29tL0Rvb28vYXBpL2dldENvbmZpZy5waHA/Y29kZT0=") + Constants.bGljZW5zZV9jb2Rl, response -> {
            SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("RemoteConfig", String.valueOf(response));
            editor.apply();
        }, error -> {
            // Do nothing because
        });

        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    private void deleteData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("UserData");

        editor.apply();
    }

    private void saveNotification() {
        SharedPreferences sharedPreferences = getSharedPreferences("Notificatin_Data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Config", notificationData);
        editor.apply();
    }

    void openApp() {
        if (Objects.equals(Constants.packageName, "")) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.GET, "http://ip-api.com/json", response -> {
                if (!response.equals("")) {
                    JsonObject rootObject = new Gson().fromJson(response, JsonObject.class);
                    String countryCode = rootObject.get("countryCode").getAsString();

                    String[] blocked_regions_array = blocked_regions.split(",");
                    if (ArrayUtils.contains(blocked_regions_array, countryCode)) {
                        SpinKitView spin_kit = findViewById(R.id.spin_kit);
                        spin_kit.setVisibility(View.INVISIBLE);

                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.blocked_country_dialog);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);

                        Button dialogClose = dialog.findViewById(R.id.Dialog_Close);

                        dialogClose.setOnClickListener(v1 -> finish());

                        dialog.show();
                    } else {
                        initApp();
                    }
                } else {
                    initApp();
                }
            }, error -> {
                initApp();
            });
            queue.add(sr);
        } else if (BuildConfig.APPLICATION_ID.equals(Constants.packageName)) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.GET, "http://ip-api.com/json", response -> {
                if (!response.equals("")) {
                    JsonObject rootObject = new Gson().fromJson(response, JsonObject.class);
                    String countryCode = rootObject.get("countryCode").getAsString();

                    String[] blocked_regions_array = blocked_regions.split(",");
                    if (ArrayUtils.contains(blocked_regions_array, countryCode)) {
                        SpinKitView spin_kit = findViewById(R.id.spin_kit);
                        spin_kit.setVisibility(View.INVISIBLE);

                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.blocked_country_dialog);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);

                        Button dialogClose = dialog.findViewById(R.id.Dialog_Close);

                        dialogClose.setOnClickListener(v1 -> finish());

                        dialog.show();
                    } else {
                        initApp();
                    }
                } else {
                    initApp();
                }
            }, error -> {
                initApp();
            });
            queue.add(sr);
        } else {
            finish();
        }

    }

    private void initApp() {
        if (checkStoragePermission()) {
            if (maintenance == 0) {
                if (apiKey.equals(Constants.apiKey)) {
                    if (userData == null) {
                        mainAppOpen();
                    } else {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(Splash.this::verifyUser, 500);
                    }
                } else {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            } else {
                Intent intent = new Intent(Splash.this, Maintenance.class);
                startActivity(intent);
                finish();
            }
        } else {
            openApp();
        }
    }


    private void mainAppOpen() {
        if (loginMandatory == 0) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                saveNotification();
                Intent intent = new Intent(Splash.this, Dashboard.class);
                intent.putExtra("Notification_Data", notificationData);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                notificationData = "";
                finish();
            }, 2500);
        } else if (loginMandatory == 1) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                saveNotification();
                Intent intent = new Intent(Splash.this, Register.class);
                startActivity(intent);
            }, 2800);
        }
    }

    private void verifyUser() {
        JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
        String email = jsonObject.get("Email").getAsString();
        String password = jsonObject.get("Password").getAsString();
        String originalInput = "login:" + email + ":" + password;
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String authToken = AccountManager.get(context).getPassword(new Account(originalInput, "accountType"));
        String encoded = Utils.toBase64(originalInput);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, Constants.url + "authentication", response -> {
            if (!response.equals("")) {
                JsonObject jsonObject1 = new Gson().fromJson(response, JsonObject.class);
                String status = jsonObject1.get("Status").toString();
                status = status.substring(1, status.length() - 1);
                if (status.equals("Successful")) {
                    saveData(response);
                    JsonObject subObj = new Gson().fromJson(response, JsonObject.class);
                    int subscriptionType = subObj.get("subscription_type").getAsInt();
                    saveUserSubscriptionDetails(subscriptionType);
                    setOneSignalExternalID(String.valueOf(subObj.get("ID").getAsInt()));
                    saveNotification();
                    Intent intent = new Intent(Splash.this, Dashboard.class);
                    intent.putExtra("Notification_Data", notificationData);
                    startActivity(intent);
                    notificationData = "";
                    finish();
                } else if (status.equals("Invalid Credential")) {
                    deleteData();
                    if (loginMandatory == 0) {
                        saveNotification();
                        Intent intent = new Intent(Splash.this, Dashboard.class);
                        intent.putExtra("Notification_Data", notificationData);
                        startActivity(intent);
                        notificationData = "";
                        finish();
                    } else {
                        Intent intent = new Intent(Splash.this, Register.class);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
                deleteData();
                if (loginMandatory == 0) {
                    saveNotification();
                    Intent intent = new Intent(Splash.this, Dashboard.class);
                    intent.putExtra("Notification_Data", notificationData);
                    startActivity(intent);
                    notificationData = "";
                    finish();
                } else {
                    Intent intent = new Intent(Splash.this, Register.class);
                    startActivity(intent);
                    finish();
                }
            }

        }, error -> {
            // Do nothing because
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }

            @SuppressLint("HardwareIds")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("encoded", encoded);
                params.put("device", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    private void setOneSignalExternalID(String externalID) {
        OneSignal.setExternalUserId(externalID, new OneSignal.OSExternalUserIdUpdateCompletionHandler() {
            @Override
            public void onSuccess(JSONObject results) {
                //Log.d("test", results.toString());
            }

            @Override
            public void onFailure(OneSignal.ExternalIdError error) {
                //Log.d("test", error.toString());
            }
        });
    }

    private void saveUserSubscriptionDetails(int subscriptionType) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "dmVyaWZ5", response -> {
            Log.d("responsenew", response);
            if (!response.equals(Utils.fromBase64("ZmFsc2U=")) && !response.equals(Utils.fromBase64("SW5hY3RpdmUgcHVyY2hhc2UgY29kZQ==")) && !response.equals(Utils.fromBase64("SW52YWxpZCBwdXJjaGFzZSBjb2Rl"))) {
                SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("subscription_type", String.valueOf(subscriptionType));
                editor.apply();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("subscription_type", "0");
                editor.apply();
            }
        }, error -> {
            SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("subscription_type", "0");
            editor.apply();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void saveData(String response) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserData", response);

        editor.apply();
    }


    private void check_network() {
        NoInternetSnackbarFire.Builder builder = new NoInternetSnackbarFire.Builder(
                findViewById(android.R.id.content),
                getLifecycle()

        );
        SnackbarPropertiesFire properties = builder.getSnackbarProperties();

        properties.setConnectionCallback(hasActiveConnection -> {
        });
        properties.setDuration(Snackbar.LENGTH_INDEFINITE);

        properties.setNoInternetConnectionMessage("No active Internet connection!");
        properties.setOnAirplaneModeMessage("You have turned on the airplane mode!");
        properties.setSnackbarActionText("Settings");
        properties.setShowActionToDismiss(false);
        properties.setSnackbarDismissActionText("OK");
        builder.build();
    }

    private void serverError() {

    }
}