package com.primemedia.studioflix.utility;

import static android.os.Environment.getExternalStorageDirectory;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;


import com.primemedia.studioflix.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;

public class Utils
{
    /**
     * @param message the message to be encoded
     *
     * @return the enooded from of the message
     */
    public static String toBase64(String message) {
        byte[] data;
        try {
            data = message.getBytes("UTF-8");
            String base64Sms = Base64.encodeToString(data, Base64.NO_WRAP);
            return base64Sms;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param message the encoded message
     *
     * @return the decoded message
     */
    public static String fromBase64(String message) {
        byte[] data = Base64.decode(message, Base64.NO_WRAP);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }



    public static boolean VerifyVPN() {
        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iface = networkInterface.getName();
                Log.d("DEBUG", "IFACE NAME: " + iface);
                if ( iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        return false;
    }

    public static String getDownloadDir(Context context) {
        //downloads/dooo
        // return  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator;
        //return context.getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator;

        File dir = new File(getExternalStorageDirectory() + "/Download/" + context.getString(R.string.app_name));
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir.getPath();
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public static String getMimeType(Uri uri, Context context) {
        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    public static String getMd5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashText = no.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean isReleaseDateInPastOrToday(String releaseDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
            LocalDate releaseLocalDate = LocalDate.parse(releaseDate, formatter);

            // Get the current date
            LocalDate currentDate = LocalDate.now();

            // Check if the release date is on or before the current date
            return !releaseLocalDate.isAfter(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Handle parsing exception, return false for simplicity
        }
    }

}
