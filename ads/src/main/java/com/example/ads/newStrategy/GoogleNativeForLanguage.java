package com.example.ads.newStrategy;//package com.vyroai.autocutcut.ads.max;
//

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import timber.log.Timber;

public class GoogleNativeForLanguage {

    final private int totalLevels = 4;
    private ArrayList<ArrayList<Object>> adUnits;
    private final String adUnitId = "ca-app-pub-9507635869843997/3843368787";


    public GoogleNativeForLanguage(Context context) {
        instantiateList();
        loadnativead(context);
    }

    private void instantiateList() {
        adUnits = new ArrayList<>();

        adUnits.add(0, new ArrayList<Object>(Arrays.asList(adUnitId, new Stack<AppOpenAd>())));
        adUnits.add(1, new ArrayList<Object>(Arrays.asList(adUnitId, new Stack<AppOpenAd>())));
        adUnits.add(2, new ArrayList<Object>(Arrays.asList(adUnitId, new Stack<AppOpenAd>())));
        adUnits.add(3, new ArrayList<Object>(Arrays.asList(adUnitId, new Stack<AppOpenAd>())));
        adUnits.add(4, new ArrayList<Object>(Arrays.asList(adUnitId, new Stack<AppOpenAd>())));
    }

    public void loadnativead(Context context) {
        NativeAdLoad(context, totalLevels);
    }


    public NativeAd getDefaultAd(Context activity) {
        if (false) { // Check Premium here
            return null;
        }

        int levels = totalLevels;

        for (int i = levels; i >= 0; i--) {

            ArrayList<Object> list = adUnits.get(i);
            String adunitid = (String) list.get(0);
            Stack<NativeAd> stack = (Stack<NativeAd>) list.get(1);

            NativeAdLoadSpecific(activity, adunitid, stack);

            if (stack == null) {
            } else if (stack.isEmpty()) {
            } else {
                return stack.pop();
            }
        }

        return null;
    }

    public void NativeAdLoad(Context activity, int level) {
        if (level < 0) {
            return;
        }

        if (adUnits.size() < level) {
            Timber.tag("ERROR").e("Size is less than ad Units size");
        }

        ArrayList<Object> list = adUnits.get(level);
        String adunitid = (String) list.get(0);
        Stack<NativeAd> stack = (Stack<NativeAd>) list.get(1);

        final AdLoader adLoader = new AdLoader.Builder(activity, adunitid)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd ad) {
                        stack.push(ad);
                    }
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        Timber.tag("ADS_INFO").e("--- NO --- Ad Failed to Load of level " + level + " " + "With ad Id " + adunitid);
                        NativeAdLoad(activity, level - 1);
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public void NativeAdLoadSpecific(Context activity, String adUnitId, Stack<NativeAd> stack) {
        final AdLoader adLoader = new AdLoader.Builder(activity, adUnitId)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd ad) {
                        stack.push(ad);
                    }
                }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

}

