package com.example.igreeldownloader.remote

import android.util.Log
import com.example.igreeldownloader.remote.Constants.SettingsGetPremium
import com.flurry.sdk.it
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.squareup.moshi.Json
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "RemoteConfig"
private const val SHOW_APP_OPEN = "show_app_open_ad"
private const val SHOW_INTERSTITIAL = "show_interstitial"
private const val NATIVE_AD = "native_ad_on_all_features"
private const val SHOW_REWARD_AD = "show_reward_ad"

@Singleton
class RemoteConfig @Inject constructor() {

    private val defaults: Map<String, Any> = mapOf(
        SHOW_APP_OPEN to true,
        SHOW_INTERSTITIAL to true,
        NATIVE_AD to true,
        SHOW_REWARD_AD to true,
    )

    private val config = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettingsAsync(remoteConfigSettings {
            if (BuildConfig.DEBUG) minimumFetchIntervalInSeconds = 3600
        })
        setDefaultsAsync(defaults)
        fetchAndActivate().let {
            it.addOnCompleteListener {
                Log.d("jeje_remoteConfig", "fetchAndActivate: ${all.mapValues { (k, v) -> v.asString() }}")
            }
        }
    }

    val showRewardAd:Boolean
        get() = config[SHOW_REWARD_AD].asBoolean()
    val showAppOpenAd: Boolean
        get() = config[SHOW_APP_OPEN].asBoolean()

    val showInterstitial: Boolean
        get() = config[SHOW_INTERSTITIAL].asBoolean()

    val nativeAd: Boolean
        get() = config[NATIVE_AD].asBoolean()

}