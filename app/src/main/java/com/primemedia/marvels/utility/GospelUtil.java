package com.primemedia.marvels.utility;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.primemedia.marvels.Constants;
import com.primemedia.marvels.R;
import com.stringcare.library.SC;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class GospelUtil {
    private Activity activity;

    public GospelUtil(Activity activity) {
        this.activity = activity;
    }

    public boolean isVpnConnectionAvailable() {
        String iface = "";
        try {
            for (NetworkInterface networkInst : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInst.isUp())
                    iface = networkInst.getName();
                if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }


        } catch (SocketException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getCertificateType(int numericValue) {
        switch (numericValue) {
            case 0:
                return "G";
            case 1:
                return "PG";
            case 2:
                return "PG-13";
            case 3:
                return "R";
            default:
                return "UR";
        }
    }

    public static boolean cr(Activity activity, boolean allowRoot) {
        if (!allowRoot) {
            for (String pathDir : System.getenv("PATH").split(":")) {
                if (new File(pathDir, "su").exists()) {
                    return true;
                } else {
                    ApplicationInfo restrictedApp = getRootApp(activity);
                    if (restrictedApp != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static ApplicationInfo getRootApp(Activity activity) {
        ApplicationInfo restrictPackageInfo = null;
        final PackageManager pm = activity.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.thirdparty.superuser") ||
                    packageInfo.packageName.equals("eu.chainfire.supersu") ||
                    packageInfo.packageName.equals("com.noshufou.android.su") ||
                    packageInfo.packageName.equals("com.koushikdutta.superuser") ||
                    packageInfo.packageName.equals("com.zachspong.temprootremovejb") ||
                    packageInfo.packageName.equals("com.ramdroid.appquarantine") ||
                    packageInfo.packageName.equals("com.topjohnwu.magisk") ||
                    packageInfo.packageName.equals("me.weishu.kernelsu")
            ) {
                //restrictPackageName = packageInfo.packageName;
                //restrictPackageName = packageInfo.loadLabel(activity.getPackageManager()).toString();
                restrictPackageInfo = packageInfo;
            }
        }

        return restrictPackageInfo;
    }

    public static Boolean isFirstOpen(Context context) {
        TinyDB tinyDB = new TinyDB(context);
        Boolean isFirstRun = tinyDB.getBoolean("isFirstRun");
        if (!isFirstRun) {
            tinyDB.putBoolean("isFirstRun", true);
            return true;
        } else {
            return false;
        }
    }

    public static void setWatchLog(Context context, String user_id, int content_id, int content_type, String apiKey) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, Constants.url + "addwatchlog", response -> {
            try {
                Integer.parseInt(response);
                Log.d("test", "Watch Log Added!");
            } catch (NumberFormatException ex) {
                Log.d("test", "Watch Log Not Added!");
            }

        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("content_id", String.valueOf(content_id));
                params.put("content_type", String.valueOf(content_type));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    public static void showWarningDialog(Activity context, String title, String message) {
        MaterialDialog mDialog = new MaterialDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Exit", R.drawable.baseline_exit_to_app_24, new MaterialDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        context.finish();
                        context.finishAndRemoveTask();
                    }
                })
                .build();

        // Show dialog
        mDialog.show();
    }

    public ApplicationInfo getRestrictApp() {
        ApplicationInfo restrictPackageInfo = null;
        final PackageManager pm = activity.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.guoshi.httpcanary") ||
                    packageInfo.packageName.equals("app.greyshirts.sslcapture") ||
                    packageInfo.packageName.equals("com.guoshi.httpcanary.premium") ||
                    packageInfo.packageName.equals("com.minhui.networkcapture.pro") ||
                    packageInfo.packageName.equals("com.minhui.networkcapture") ||
                    packageInfo.packageName.equals("com.egorovandreyrm.pcapremote") ||
                    packageInfo.packageName.equals("com.packagesniffer.frtparlak") ||
                    packageInfo.packageName.equals("jp.co.taosoftware.android.packetcapture") ||
                    packageInfo.packageName.equals("com.emanuelef.remote_capture") ||
                    packageInfo.packageName.equals("com.minhui.wifianalyzer") ||
                    packageInfo.packageName.equals("com.evbadroid.proxymon") ||
                    packageInfo.packageName.equals("com.evbadroid.wicapdemo") ||
                    packageInfo.packageName.equals("com.evbadroid.wicap") ||
                    packageInfo.packageName.equals("com.luckypatchers.luckypatcherinstaller") ||
                    packageInfo.packageName.equals("ru.UbLBBRLf.jSziIaUjL")
            ) {
                //restrictPackageName = packageInfo.packageName;
                //restrictPackageName = packageInfo.loadLabel(activity.getPackageManager()).toString();
                restrictPackageInfo = packageInfo;
            }
        }

        return restrictPackageInfo;
    }

    public static String getYearFromDate(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = null;
        try {
            parsedDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy");
        assert parsedDate != null;
        return df.format(parsedDate);
    }

    public static void setViewLog(Context context, String user_id, int content_id, int content_type, String apiKey) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, SC.reveal(Constants.url) + "addviewlog", response -> {
            try {
                Integer.parseInt(response);
                Log.d("test", "View Log Added!");
            } catch (NumberFormatException ex) {
                Log.d("test", "View Log Not Added!");
            }

        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("content_id", String.valueOf(content_id));
                params.put("content_type", String.valueOf(content_type));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    public static boolean checkStoragePermission(Context context) {
        if (checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("test", "Permission is granted");
            return true;
        } else {
            Log.d("test", "Permission is revoked");
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }
}
