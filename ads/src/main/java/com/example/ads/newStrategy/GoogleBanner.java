package com.example.ads.newStrategy;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

import timber.log.Timber;


public class GoogleBanner {

    private static final String TAG = "GoogleBanner";

    public static final String BANNER_TEST = "ca-app-pub-3940256099942544/6300978111";
    public static final String BANNER_ALL = "ca-app-pub-9781925194514571/4688435804";
    public static final String BANNER_MEDIUM = "ca-app-pub-9781925194514571/4688435804";
    public static final String BANNER_HIGH = "ca-app-pub-9781925194514571/4688435804";
    public static final String BANNER_FOURTH = "ca-app-pub-9781925194514571/4688435804";
    public static final String BANNER_FIFTH = "ca-app-pub-9781925194514571/4688435804";

    private final String adUnitId = "ca-app-pub-9507635869843997/2164305669";

    private ArrayList<ArrayList<Object>> adUnits;
    final private int totalLevels = 4;


    public GoogleBanner(Context context) {
        instantiateList();
        loadBannerAd(context);
    }

    private void instantiateList() {
        adUnits = new ArrayList<>();

        adUnits.add(0, new ArrayList<Object>(Arrays.asList(adUnitId, new ArrayDeque<AppOpenAd>())));
        adUnits.add(1, new ArrayList<Object>(Arrays.asList(adUnitId, new ArrayDeque<AppOpenAd>())));
        adUnits.add(2, new ArrayList<Object>(Arrays.asList(adUnitId, new ArrayDeque<AppOpenAd>())));
        adUnits.add(3, new ArrayList<Object>(Arrays.asList(adUnitId, new ArrayDeque<AppOpenAd>())));
        adUnits.add(4, new ArrayList<Object>(Arrays.asList(adUnitId, new ArrayDeque<AppOpenAd>())));
    }

    public void loadBannerAd(Context context) {
        loadBannerAd(context, totalLevels);
    }


    public AdView getDefaultAd(Context context) {
        Log.d(TAG, "getDefaultAd()");
        int levels = totalLevels;

        for (int i = levels; i >= 0; i--) {

            ArrayList<Object> list = adUnits.get(i);
            String adunitid = (String) list.get(0);
            ArrayDeque<AdView> queue = (ArrayDeque<AdView>) list.get(1);

            loadSpecificBannerAd(context, adunitid, queue);

            if (queue != null && !queue.isEmpty()) {
                Log.d(TAG, "getDefaultAd: " + queue);
                AdView ad = queue.poll();
                Log.d(TAG, "getDefaultAd: " + queue);
                return ad;
            }
        }

        return null;
    }

    public void loadBannerAd(Context context, int level) {
        if (level < 0) {
            return;
        }

        if (adUnits.size() < level) {
            Timber.tag("ERROR").e("Size is less than ad Units size");
        }

        ArrayList<Object> list = adUnits.get(level);
        String adUnitId = (String) list.get(0);
        ArrayDeque<AdView> queue = (ArrayDeque<AdView>) list.get(1);

        AdView ad = loadBannerAd(context, adUnitId);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(TAG, "onAdLoaded: " + ad);
                queue.add(ad);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Timber.tag(TAG).e("onAdFailedToLoad(error: " + loadAdError.getMessage() + ")");
                loadBannerAd(context, level - 1);
            }
        });
    }

    public void loadSpecificBannerAd(Context context, String adUnitId, ArrayDeque<AdView> queue) {
        AdView ad = loadBannerAd(context, adUnitId);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(TAG, "loadSpecificBannerAd: " + ad);
                queue.add(ad);
            }
        });
    }

    private AdView loadBannerAd(Context context, String adUnitId) {
        AdView adView = new AdView(context);
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(AdSize.BANNER);
        adView.loadAd(new AdRequest.Builder().build());
        Log.d(TAG, "loadBannerAd: " + adView.getParent());
        return adView;
    }

}

