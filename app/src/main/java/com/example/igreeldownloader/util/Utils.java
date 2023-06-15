package com.example.igreeldownloader.util;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.example.igreeldownloader.R;
import com.example.igreeldownloader.db.Database;
import com.example.igreeldownloader.models.FVideo;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Utils {
    private static final String TAG = "Utils";
    private static final String INSTA_REGEX = "(https?:\\/\\/(?:www\\.)?instagram\\.com\\/([^/?#&]+)).*";
    private static final String SNAPCHAT_REGEX = "^https:\\/\\/t\\.snapchat\\.com\\/[a-zA-Z0-9]+$";
    private static final String LIKEE_REGEX = "^https?:\\/\\/l\\.likee\\.video\\/v\\/[a-zA-Z0-9]{6,}$";
    private static final String MOJAPP_REGEX = "^https?:\\/\\/mojapp\\.in\\/@[\\w.-]+\\/video\\/\\d+\\?referrer=[\\w-]+$";
    public static File downloadWhatsAppDir = new File(Environment.getExternalStorageDirectory() + "/Download/Smart_Downloader/Whatsapp");
    public static File downloadWABusiDir = new File(Environment.getExternalStorageDirectory() + "/Download/Smart_Downloader/WABusiness");
    public static BottomSheetDialog customDialog;
    public static String RootDirectoryFacebook = "/Smart_Downloader/facebook/";
    public static String RootDirectoryInsta = "/Smart_Downloader/instagram/";
    public static String RootDirectorySnapchat = "/Smart_Downloader/snapchat/";
    public static String RootDirectoryLikee = "/Smart_Downloader/likee/";
    public static String RootDirectoryMoz = "/Smart_Downloader/moj/";
    public static File RootDirectoryFacebookShow = new File(Environment.getExternalStorageDirectory() + "/Download" + RootDirectoryFacebook);
    public static File RootDirectoryInstaShow = new File(Environment.getExternalStorageDirectory() + "/Download" + RootDirectoryInsta);
    public static File RootDirectorySnapchatShow = new File(Environment.getExternalStorageDirectory() + "/Download" + RootDirectorySnapchat);
    public static File RootDirectoryLikeeShow = new File(Environment.getExternalStorageDirectory() + "/Download" + RootDirectoryLikee);
    public static File RootDirectoryMozShow = new File(Environment.getExternalStorageDirectory() + "/Download" + RootDirectoryMoz);
    private final Context context;

    public Utils(Context mContext) {
        context = mContext;
    }

    public static void setToast(Context mContext, String str) {
        Toast toast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Create directory facebook video downloader in download directory
     */
    public static void createFacebookFolder() {
        if (!RootDirectoryFacebookShow.exists()) {
            RootDirectoryFacebookShow.mkdirs();
        }
    }

    public static void createInstaFolder() {
        if (!RootDirectoryInstaShow.exists()) {
            RootDirectoryInstaShow.mkdirs();
        }
    }

    public static void createSnapchatFolder() {
        if (!RootDirectorySnapchatShow.exists()) {
            RootDirectorySnapchatShow.mkdirs();
        }
    }

    public static void createLikeeFolder() {
        if (!RootDirectoryLikeeShow.exists()) {
            RootDirectoryLikeeShow.mkdirs();
        }
    }

    public static void createMozFolder() {
        if (!RootDirectoryMozShow.exists()) {
            RootDirectoryMozShow.mkdirs();
        }
    }

    public static void showProgressDialog(Activity activity) {
        System.out.println("Show");
        if (customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }
        customDialog = new BottomSheetDialog(activity, R.style.SheetDialog);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View mView = inflater.inflate(R.layout.layout_progress_dialog, null);
        customDialog.setCancelable(false);
        customDialog.setContentView(mView);
        if (!customDialog.isShowing() && !activity.isFinishing()) {
            customDialog.show();
        }
    }

    public static void hideProgressDialog(Activity activity) {
        System.out.println("Hide");
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
    }

    /**
     * start download using download manager
     * Downloads video in downloads/Facebook_video_downloader folder
     *
     * @param videoUrl video Url
     * @param context
     * @return FVideo object
     */
    public static FVideo startDownload(Context context, String videoUrl, int urlType) {

        setToast(context, context.getResources().getString(R.string.download_started));
        String downloadLocation;
        String fileName;

        if (urlType == Constants.FACEBOOK_URL) {
            fileName = "facebook_" + System.currentTimeMillis() + ".mp4";
            downloadLocation = RootDirectoryFacebook + fileName;
        } else if (urlType == Constants.INSTA_URL) {
            fileName = "instagram_" + System.currentTimeMillis() + ".mp4";
            downloadLocation = RootDirectoryInsta + fileName;
        } else if (urlType == Constants.SNAPCHAT_URL) {
            fileName = "snapchat_" + System.currentTimeMillis() + ".mp4";
            downloadLocation = RootDirectorySnapchat + fileName;
        } else if (urlType == Constants.LIKEE_url) {
            fileName = "likee_" + System.currentTimeMillis() + ".mp4";
            downloadLocation = RootDirectoryLikee + fileName;
        } else {
            fileName = "moj_" + System.currentTimeMillis() + ".mp4";
            downloadLocation = RootDirectoryMoz + fileName;
        }
        Log.d("jeje_url", videoUrl);
        Uri uri = Uri.parse(videoUrl); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);  // This will show notification on top when downloading the file.
        request.setTitle(fileName + ""); // Title for notification.
        request.setVisibleInDownloadsUi(true);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadLocation);// Storage directory path
        long downloadId = ((DownloadManager) context.getSystemService(DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading

        Log.d("jeje_req", request.toString());
        Log.d("jeje_downloadID", String.valueOf(downloadId));

        //Creating a video object to track download is completed
        FVideo video = new FVideo(Environment.getExternalStorageDirectory() +
                downloadLocation, fileName, downloadId, false, System.currentTimeMillis());
        video.setState(FVideo.DOWNLOADING);

        if (urlType == Constants.FACEBOOK_URL)
            video.setVideoSource(FVideo.FACEBOOK);
        else if (urlType == Constants.INSTA_URL)
            video.setVideoSource(FVideo.INSTAGRAM);
        else if (urlType == Constants.SNAPCHAT_URL)
            video.setVideoSource(FVideo.SNAPCHAT);
        else if (urlType == Constants.LIKEE_url)
            video.setVideoSource(FVideo.LIKEE);
        else
            video.setVideoSource(FVideo.MOZ);


        Database db = Database.init(context);
        db.addVideo(video);

        Log.d("jeje_startDownload: ",   Environment.getDataDirectory().getPath() + RootDirectoryFacebook + fileName);

        try {
            MediaScannerConnection.scanFile(context, new String[]{new File(DIRECTORY_DOWNLOADS + "/" + fileName).getAbsolutePath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("jeje_videoProcess", "onScanCompleted: " + path);
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("jeje_e",e.toString());
        }

        FVideo fVideo = new FVideo(Environment.getExternalStorageDirectory() +
                downloadLocation + fileName,
                fileName, downloadId, false, System.currentTimeMillis());
        fVideo.setVideoSource(FVideo.FACEBOOK);
        Log.d("jeje_set_Sorce","fVideo.setVie");
        return fVideo;
    }


    public static FVideo downloadVideoInsta(Context context, String videoUrl, String fileName) {
        setToast(context, context.getResources().getString(R.string.download_started));
        Uri uri = Uri.parse(videoUrl); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);  // This will show notification on top when downloading the file.
        request.setTitle(fileName + ""); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, RootDirectoryInsta + fileName);// Storage directory path
        long downloadId = ((DownloadManager) context.getSystemService(DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading

        //Creating a video object to track download is completed
        FVideo video = new FVideo(Environment.getExternalStorageDirectory() +
                "/Download" + RootDirectoryInsta, fileName, downloadId, false, System.currentTimeMillis());
        video.setState(FVideo.DOWNLOADING);
        video.setVideoSource(FVideo.INSTAGRAM);


        Database db = Database.init(context);
        db.addVideo(video);

        Log.d(TAG, "startDownload: " + Environment.getDataDirectory().getPath() + RootDirectoryFacebook + fileName);

        try {
            MediaScannerConnection.scanFile(context, new String[]{new File(DIRECTORY_DOWNLOADS + "/" + fileName).getAbsolutePath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("videoProcess", "onScanCompleted: " + path);
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
        FVideo fVideo = new FVideo(Environment.getExternalStorageDirectory() +
                "/Download" + RootDirectoryInsta + fileName,
                fileName, downloadId, false, System.currentTimeMillis());
        fVideo.setVideoSource(FVideo.INSTAGRAM);

        return fVideo;

    }

    public static boolean isFacebookReelsUrl(String url){
        return url.startsWith("https://www.facebook.com/reel");
    }

    public static boolean isInstaUrl(String url) {
        return url.matches(INSTA_REGEX);
    }

    public static boolean isSnapChatUrl(String url) {
        return url.matches(SNAPCHAT_REGEX);
    }

    public static boolean isLikeeUrl(String url) {
        return url.matches(LIKEE_REGEX);
    }

    public static boolean isMojUrl(String url) {
        return url.matches(MOJAPP_REGEX);
    }

    public static boolean isFacebookUrl(String url) {
        return url.contains("facebook") || url.contains("fb");
    }

    /**
     * Find video thumbnail form a file path
     *
     * @param filePath video path
     * @return frame at time 1 as bitmap image
     */
    public static Bitmap getThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            //mediaMetadataRetriever.setDataSource(filePath, new HashMap<String, String>());
            mediaMetadataRetriever.setDataSource(filePath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getThumbnail: file not found");
            Log.d(TAG, "getThumbnail: url " + filePath);
        } finally {
            if (mediaMetadataRetriever != null) {
                try {
                    mediaMetadataRetriever.release();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bitmap;
    }

    /**
     * This function check the app installed or not
     *
     * @param context     application context
     * @param packageName which app want to check ie. com.whatsapp
     * @return ture or false
     */
    public static boolean appInstalledOrNot(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * delete video file from the disk
     *
     * @param context
     * @param video   which video want to delete
     */
    public static void deleteVideoFromFile(Context context, FVideo video) {
        if (video.getState() == FVideo.COMPLETE) {

            File file = new File(video.getFileUri());
            Database db = Database.init(context);

            if (file.exists()) {
                new AlertDialog.Builder(context)
                        .setTitle("Want to delete this file?")
                        .setMessage("This will delete file from your Disk")
                        .setPositiveButton("Yes", (dialog, which) -> {

                            boolean isDeleted = file.delete();
                            if (isDeleted)
                                Toast.makeText(context, "Video deleted", Toast.LENGTH_SHORT).show();


                            db.deleteAVideo(video.getDownloadId());
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                        .show();
            } else {
                db.deleteAVideo((video.getDownloadId()));
                Toast.makeText(context, "video not found", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "File downloading...", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getBack(String paramString1, String paramString2) {
        Matcher localMatcher = Pattern.compile(paramString2).matcher(paramString1);
        if (localMatcher.find()) {
            return localMatcher.group(1);
        }
        return "";
    }

    public static boolean copyFileInSavedDir(Context context, String sourceFile, boolean isWApp) {

        String finalPath = getDir(isWApp).getAbsolutePath();
        String pathWithName;
        String fileName;

        if (isVideoFile(context, sourceFile)) {
            fileName = "wp_" + System.currentTimeMillis() + ".mp4";
            pathWithName = finalPath + File.separator + fileName;
        } else {
            fileName = "wp_" + System.currentTimeMillis() + ".jpg";
            pathWithName = finalPath + File.separator + fileName;
        }

        Uri destUri = Uri.fromFile(new File(pathWithName));

        InputStream is;
        OutputStream os;
        try {
            Uri uri = Uri.parse(sourceFile);
            is = context.getContentResolver().openInputStream(uri);
            os = context.getContentResolver().openOutputStream(destUri, "w");

            byte[] buffer = new byte[1024];

            int length;
            while ((length = is.read(buffer)) > 0)
                os.write(buffer, 0, length);

            is.close();
            os.flush();
            os.close();

            FVideo video = new FVideo(System.currentTimeMillis());
            video.setIsWatermarked(false);
            video.setFileName(fileName);
            video.setDownloadId(System.currentTimeMillis());
            video.setFileUri(pathWithName);

            video.setState(FVideo.COMPLETE);

            Database db = Database.init(context);
            db.addVideo(video);

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(destUri);
            context.sendBroadcast(intent);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static byte[] bitmapToByteArray(Bitmap thumbnail) {
        if (thumbnail == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 0, stream);

        return stream.toByteArray();
    }

    public static Bitmap byteArrayToBitmap(byte[] thumbnail) {
        if (thumbnail != null)
            return BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
        return null;
    }

    static File getDir(boolean isWApp) {

        File rootFile = downloadWhatsAppDir;
        if (!isWApp) {
            rootFile = downloadWABusiDir;
        }
        rootFile.mkdirs();

        return rootFile;

    }

    public static boolean download(Context context, String sourceFile, boolean isWApp) {
        return copyFileInSavedDir(context, sourceFile, isWApp);
    }

    public static boolean isVideoFile(Context context, String path) {
        if (path.startsWith("content")) {
            DocumentFile fromTreeUri = DocumentFile.fromSingleUri(context, Uri.parse(path));
            String mimeType = fromTreeUri.getType();
            return mimeType != null && mimeType.startsWith("video");
        } else {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("video");
        }
    }

    public static void shareFile(Context context, boolean isVideo, String path) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        if (isVideo)
            share.setType("Video/*");
        else
            share.setType("image/*");

        Uri uri;
        if (path.startsWith("content")) {
            uri = Uri.parse(path);
        } else {
            uri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".provider", new File(path));
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(share);
    }

    /**
     * parse json and find video link
     *
     * @param response is api response from server
     * @return sd and hd Video link
     */
    public static Map<String, String> getFacebookLink(String response) {
        Log.d(TAG, "getFacebookLink:  " + response);
        Map<String, String> map = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("data")) {
                JSONArray data = jsonObject.getJSONArray("data");

                for (int index = 0; index < data.length(); index++) {
                    JSONObject object = data.getJSONObject(index);
                    if (Objects.equals(object.getString("format_id"), "hd") ||
                            Objects.equals(object.getString("format_id"), "sd")) {
                        map.put(object.getString("format_id"), object.getString("url"));
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    /**
     * parse json and find video link
     *
     * @param response is api response from server
     * @return sd and hd Video link
     */
    public static Map<String, String> getInstaLink(String response) {

        Map<String, String> map = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("data")) {
                JSONArray data = jsonObject.getJSONArray("data");

                int sdIndex = -1;
                int hdIndex = -1;
                int hdres = 0;

                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    String format = object.getString("format");

                    if (!format.startsWith("dash")) {
                        String[] formatSArray = format.split(" ");
                        String[] array = formatSArray[formatSArray.length - 1].split("x");
                        int res = Integer.parseInt(array[0]);
                        if (res > 400 && res < 500) {
                            sdIndex = i;
                        }
                        if (res > hdres) {
                            hdres = res;
                            hdIndex = i;
                        }

                    }


                }

                if (hdIndex != -1) {
                    JSONObject object = data.getJSONObject(hdIndex);
                    map.put("hd", object.getString("url"));
                    Log.d(TAG, "getInstaLink: Hd " + object.getString("format"));
                }
                if (sdIndex != -1) {
                    JSONObject object = data.getJSONObject(sdIndex);
                    map.put("sd", object.getString("url"));
                    Log.d(TAG, "getInstaLink: sd " + object.getString("format"));
                }

            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static Map<String, String> getSnapchatLink(String response) {

        Map<String, String> map = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("data")) {
                JSONObject data = jsonObject.getJSONObject("data");

                String url = data.getString("url");
                map.put("hd", url);

            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static Map<String, String> getLikeeLink(String response) {

        Map<String, String> map = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("data")) {
                JSONArray data = jsonObject.getJSONArray("data");
                String url = null;
                if (data.length() == 1) {
                    url = data.getJSONObject(0).getString("url");
                } else if (data.length() > 1) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        if (object.getString("format_id").equals("mp4-without-watermark"))
                            url = object.getString("url");
                    }
                }

                map.put("hd", url);

            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static Map<String, String> getMozLink(String response) {

        Map<String, String> map = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("data")) {
                JSONArray data = jsonObject.getJSONArray("data");
                String url = data.getJSONObject(0).getString("url");

                map.put("hd", url);

            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static Map<String, String> getTopBanner(String response) {
        Map<String, String> banner = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(response);

            boolean error = jsonObject.getBoolean("error");
            if (!error) {
                JSONObject data = jsonObject.getJSONObject("data");

                String bannerUrl = data.getString("image_url");
                String bannerAction = data.getString("action_url");

                banner.put(Constants.TOP_BANNER_URL_KEY, bannerUrl);
                banner.put(Constants.TOP_BANNER_ACTION_KEY, bannerAction);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return banner;
    }
}