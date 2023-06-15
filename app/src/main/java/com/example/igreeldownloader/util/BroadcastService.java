package com.example.igreeldownloader.util;

import static com.example.igreeldownloader.util.Constants.downloadVideos;
import static com.example.igreeldownloader.util.Utils.RootDirectoryFacebook;
import static com.example.igreeldownloader.util.Utils.RootDirectoryInsta;
import static com.example.igreeldownloader.util.Utils.RootDirectoryLikee;
import static com.example.igreeldownloader.util.Utils.RootDirectoryMoz;
import static com.example.igreeldownloader.util.Utils.RootDirectorySnapchat;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.igreeldownloader.db.Database;
import com.example.igreeldownloader.models.FVideo;

public class BroadcastService extends Service {
    private static final String TAG = "BroadcastService";
    Database db;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: broadcastService");
        db = Database.init(getApplicationContext());
        registerBroadcastRecerver();
    }

    private void registerBroadcastRecerver() {
        Log.d(TAG, "registerBroadcastRecerver: broadcast service");

        BroadcastReceiver downloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                if (downloadVideos.containsKey(id)) {
                    Log.d("receiver", "onReceive: download complete");

                    FVideo fVideo = db.getVideo(id);
                    String videoPath = null;

                    if (fVideo.getVideoSource() == FVideo.FACEBOOK) {

                        videoPath = Environment.getExternalStorageDirectory() +
                                "/Download" + RootDirectoryFacebook + fVideo.getFileName();

                    } else if (fVideo.getVideoSource() == FVideo.INSTAGRAM) {
                        videoPath = Environment.getExternalStorageDirectory() +
                                "/Download" + RootDirectoryInsta + fVideo.getFileName();
                    } else if (((FVideo) fVideo).getVideoSource() == FVideo.SNAPCHAT) {
                        videoPath = Environment.getExternalStorageDirectory() +
                                "/Download" + RootDirectorySnapchat + fVideo.getFileName();
                    } else if (fVideo.getVideoSource() == FVideo.LIKEE) {
                        videoPath = Environment.getExternalStorageDirectory() +
                                "/Download" + RootDirectoryLikee + fVideo.getFileName();
                    } else if (fVideo.getVideoSource() == FVideo.MOZ) {
                        videoPath = Environment.getExternalStorageDirectory() +
                                "/Download" + RootDirectoryMoz + fVideo.getFileName();
                    }

                    Toast.makeText(getApplicationContext(), "Download complete", Toast.LENGTH_SHORT).show();
                    db.updateState(id, FVideo.COMPLETE);
                    if (videoPath != null)
                        db.setUri(id, videoPath);
                    downloadVideos.remove(id);
                }
            }
        };

        registerReceiver(downloadComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
