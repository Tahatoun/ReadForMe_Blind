package ensias.readforme_blind.dao;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Util {
    static String configFileName = "config.properties";
    public static void requestPermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
        }
    }
    public static String getProperty(String key,Context context) throws IOException {
        Properties properties = new Properties();;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(configFileName);
        properties.load(inputStream);
        return properties.getProperty(key);
    }
    public static String getApiHost(Context context) throws IOException {
        Properties properties = new Properties();;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(configFileName);
        properties.load(inputStream);
        return properties.getProperty("Host")+properties.getProperty("Api");
    }
    public static String getImagesHost(Context context) throws IOException {
        Properties properties = new Properties();;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(configFileName);
        properties.load(inputStream);
        return properties.getProperty("Host")+properties.getProperty("Images");
    }
    public static UserInterface getVolunteerService (Context context){
        UserInterface blindInterface = null;  // crash
        try {
            System.out.println("HOST : "+getApiHost(context));
            blindInterface = RetrofitBuilder.createServiceWithAuth(UserInterface.class,Auth.getInstance(context.getSharedPreferences("token_prefs", MODE_PRIVATE)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  blindInterface;
    }

    public static UserInterface getBlindService (Context context){
        UserInterface blindInterface = null;  // crash
        try {
            System.out.println("HOST : "+getApiHost(context));
            blindInterface = RetrofitBuilder.createServiceWithAuth(UserInterface.class,Auth.getInstance(context.getSharedPreferences("token_prefs", MODE_PRIVATE)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  blindInterface;
    }
    public static FileInterface getFileService(Context context){
        FileInterface playlistIterface = null;
        try {
            System.out.println("HOST : "+getApiHost(context));

            playlistIterface = RetrofitBuilder.createServiceWithAuth(FileInterface.class,Auth.getInstance(context.getSharedPreferences("token_prefs", MODE_PRIVATE)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playlistIterface;
    }
    public static TrackInterface getTrackService(Context context){
        TrackInterface trackInterface = null;
        try {
            System.out.println("HOST : "+getApiHost(context));
            trackInterface = RetrofitBuilder.createServiceWithAuth(TrackInterface.class,Auth.getInstance(context.getSharedPreferences("token_prefs", MODE_PRIVATE)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trackInterface;
    }

    private static char[] c = new char[]{'k', 'm', 'b', 't'};
    public static String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration+1));
    }
    public static void printResponse(Response response){
        System.out.println("Response code : "+response.code());
        System.out.println("Response message : "+response.message());
        try {
            System.out.println("Response body : "+((okhttp3.ResponseBody)response.body()).string());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void printThrowable(Throwable throwable){
        try {
            System.out.println("Localized Message : "+throwable.getLocalizedMessage());
            System.out.println("Message : "+throwable.getMessage());
            System.out.println("Stack Trace : "+throwable.getStackTrace().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public static  int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }



    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{ split[1] };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}