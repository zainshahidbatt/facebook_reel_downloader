package com.example.igreeldownloader.util;

import com.example.igreeldownloader.models.FVideo;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String ACTIVITY = "NotificationActivity";
    public static final String ACTIVITY_CREATED_BY_NOTI = "ACTIVITY_CREATED_BY_NOTI";
    public static final int FACEBOOK_URL = 1;
    public static final int INSTA_URL = 2;
    public static final int SNAPCHAT_URL = 3;
    public static final int LIKEE_url = 4;
    public static final int MOZ_URL = 5;
    public static Map<Long, FVideo> downloadVideos = new HashMap<>();

    public static final String TOP_BANNER_URL_KEY = "banner_action";
    public static final String TOP_BANNER_ACTION_KEY = "banner_url";

}
