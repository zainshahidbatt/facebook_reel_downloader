package com.example.ads.newStrategy;//package com.vyroai.autocutcut.ads.max;
//

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;


public class GoogleRewarded {

    private final int totalLevels = 4;
    private ArrayList<ArrayList<Object>> adUnits;
    private final String adUnitId = "ca-app-pub-9507635869843997/1955572049";

    public GoogleRewarded(Context context) {
        instantiateList();
        loadInitialRewards(context);
    }

    private void instantiateList() {
        adUnits = new ArrayList<>();

        adUnits.add(0, new ArrayList<Object>(Arrays.asList(adUnitId, new Stack<InterstitialAd>())));
        adUnits.add(1, new ArrayList<Object>(Arrays.asList(adUnitId, new Stack<InterstitialAd>())));
        adUnits.add(2, new ArrayList<Object>(Arrays.asList(adUnitId, new Stack<InterstitialAd>())));
        adUnits.add(3, new ArrayList<Object>(Arrays.asList(adUnitId, new Stack<InterstitialAd>())));
        adUnits.add(4, new ArrayList<Object>(Arrays.asList(adUnitId, new Stack<InterstitialAd>())));
    }

    public void loadInitialRewards(Context context) {
        RewardedAdLoad(context, totalLevels);
    }


    public RewardedAd getMediumAd(Context activity) {
        return getRewardedAd(activity, 1);
    }

    public RewardedAd getHighFloorAd(Context activity) {
        return getRewardedAd(activity, 2);
    }

    public RewardedAd getDefaultAd(Context activity) {
        return getRewardedAd(activity, 0);
    }


    public RewardedAd getRewardedAd(Context activity, int maxLevel) {
        for (int i = totalLevels; i >= 0; i--) {

            if (maxLevel > i) {
                break;
            }

            ArrayList<Object> list = adUnits.get(i);
            String adunitid = (String) list.get(0);
            Stack<RewardedAd> stack = (Stack<RewardedAd>) list.get(1);

            RewardedAdLoadSpecific(activity, adunitid, stack);

            if (stack != null && !stack.isEmpty()) {
                return stack.pop();
            }
        }

        return null;
    }

    public void RewardedAdLoad(Context activity, int level) {

        if (level < 0) {
            return;
        }

        if (adUnits.size() < level) {
            return;
        }

        ArrayList<Object> list = adUnits.get(level);
        String adunitid = (String) list.get(0);
        Stack<RewardedAd> stack = (Stack<RewardedAd>) list.get(1);

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(activity, adunitid, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                RewardedAdLoad(activity, level - 1);
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                stack.push(rewardedAd);
            }
        });
    }

    public void RewardedAdLoadSpecific(Context activity, String adUnitId, Stack<RewardedAd> stack) {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(activity, adUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                stack.push(rewardedAd);
            }
        });
    }
}

