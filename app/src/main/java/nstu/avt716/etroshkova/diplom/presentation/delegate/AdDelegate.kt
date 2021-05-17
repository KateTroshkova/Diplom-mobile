package nstu.avt716.etroshkova.diplom.presentation.delegate

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdDelegate(val context: Activity) {

    private var interstitialAd: InterstitialAd? = null

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, AD_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                this@AdDelegate.interstitialAd = interstitialAd
                this@AdDelegate.interstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        }

                        override fun onAdShowedFullScreenContent() {
                            this@AdDelegate.interstitialAd = null
                        }
                    }
                this@AdDelegate.interstitialAd?.show(context)
            }
        })
    }

    private companion object {
        private const val AD_ID = "ca-app-pub-8942695023325187/2806978540"
    }
}
