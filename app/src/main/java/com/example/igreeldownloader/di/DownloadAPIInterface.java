package com.example.igreeldownloader.di;


import com.example.igreeldownloader.models.FacebookReel;
import com.example.igreeldownloader.models.FacebookVideo;
import com.example.igreeldownloader.models.InstaVideo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DownloadAPIInterface {
    @GET("/link.php")
    Call<FacebookVideo> getFacebookVideos(@Query("video") String videoUrl);

    @GET("/instagram.php")
    Call<InstaVideo> getInstaVideos(@Query("video") String videoUrl);

    @GET("/instagram.php")
    Call<FacebookReel> getFacebookReels(@Query("video") String videoUrl);
}
