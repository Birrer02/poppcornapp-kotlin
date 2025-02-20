package com.example.miningapp

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import android.content.Intent
import android.net.Uri

class MainActivity : AppCompatActivity() {

    private var coins = 0
    private var mining = false
    private lateinit var startMiningButton: Button
    private lateinit var coinsDisplay: TextView
    private lateinit var withdrawButton: Button
    private var interstitialAd: InterstitialAd? = null
    private lateinit var adTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // AdMob initialisieren
        MobileAds.initialize(this) {}

        startMiningButton = findViewById(R.id.startMiningButton)
        coinsDisplay = findViewById(R.id.coinsDisplay)
        withdrawButton = findViewById(R.id.withdrawButton)

        // Werbung laden
        loadAd()

        startMiningButton.setOnClickListener {
            if (!mining) {
                startMining()
            }
        }

        withdrawButton.setOnClickListener {
            withdrawCoins()
        }
    }

    private fun startMining() {
        mining = true
        startMiningButton.text = "Mining läuft..."

        adTimer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                coins += 1
                updateCoinsDisplay()
            }

            override fun onFinish() {
                showAd()
                mining = false
                startMiningButton.text = "Mining starten"
            }
        }
        adTimer.start()
    }

    private fun updateCoinsDisplay() {
        coinsDisplay.text = "Coins: $coins"
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-9448948126269808/INTERSTITIAL_AD_ID", adRequest, object :
            InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
            }
        })
    }

    private fun showAd() {
        interstitialAd?.show(this)
        loadAd()
    }

    private fun withdrawCoins() {
        if (coins < 1000000) {
            Toast.makeText(this, "Mindestbetrag für Auszahlung: 1.000.000 Coins", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Bitte PayPal-Adresse angeben für Auszahlung!", Toast.LENGTH_LONG).show()
        }
    }

    fun openPayPalPayment(amount: Double) {
        val url = "https://www.paypal.com/donate/?business=konqurenz@hotmail.com&currency_code=USD&amount=$amount"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
