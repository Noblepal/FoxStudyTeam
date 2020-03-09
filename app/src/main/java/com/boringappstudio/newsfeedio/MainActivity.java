package com.boringappstudio.newsfeedio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.material.snackbar.Snackbar;
import com.kobakei.ratethisapp.RateThisApp;
import com.boringappstudio.newsfeedio.utils.UpdateCounter;

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements UpdateCounter {

    String TAG="MainActivity";
    private InterstitialAd mInterstitialAd;
    private ScheduledExecutorService scheduler, scheduler2;
    private boolean isVisible;
    private boolean isActive=false;
    RewardedVideoAd mAd;
    Boolean gtimeout=true;
    Snackbar snackbar;

    private boolean bound = false;
    private boolean dying=true;
    private UpdateCounter updateCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isActive=true;
dying=true;
        RateThisApp.onStart(this);
        // Custom condition: 3 days and 5 launches
        RateThisApp.Config config = new RateThisApp.Config(0, 4);
        RateThisApp.init(config);
        RateThisApp.showRateDialogIfNeeded(this);

        MobileAds.initialize(this, getResources().getString(R.string.ad_id_banner));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_id_interstitial));
        AdRequest adRequest = new AdRequest.Builder().build();

        // Load ads into Interstitial Ads

       /* if (!isVisible) {
            mInterstitialAd.loadAd(adRequest);
        }*/
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                isVisible = true;
                gtimeout=true;
//                ((View) findViewById(R.id.textViewb)).setVisibility(View.VISIBLE);

                final Toast toast=Toast.makeText(MainActivity.this,"This ad helps keep our app free of charge",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP,0,0);
                toast.show();
                mInterstitialAd.show();
            }


            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                gtimeout=false;
                isVisible = false;
                setTime();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                gtimeout=false;
                isVisible=false;
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                gtimeout=false;
                isVisible=false;
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                gtimeout=false;
                isVisible=false;
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdClosed() {
                AdRequest adRequest = new AdRequest.Builder().build();
                ((View) findViewById(R.id.textViewb)).setVisibility(View.GONE);

                gtimeout=false;
                isVisible=false;
                // Load ads into Interstitial Ads
//                mInterstitialAd.loadAd(adRequest);
            }
        });
        //load reward videos
        mAd = MobileAds.getRewardedVideoAdInstance(this);
//        startService(new Intent(getApplicationContext(), AdService.class));
        setTime();
    }
    public void setTime(){
        dying=false;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("myTime",0);
        editor.commit();
        startTimer();

    }
    public void startTimer(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        final int[] ctime = {preferences.getInt("myTime", 0)};
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    Log.e("hello", "world"+ctime[0]+isVisible);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ctime[0]++;
                            editor.putInt("myTime",ctime[0]);
                            editor.commit();
                            if (ctime[0] >=60){
                                ctime[0]=0;
                                AdRequest adRequest = new AdRequest.Builder().build();
                                // Load ads into Interstitial Ads
                                if (isActive&&!isVisible){
                                    mInterstitialAd.loadAd(adRequest);
                                    if (mInterstitialAd.isLoaded() && isVisible) {
                                    } else {
                                        Log.d("TAG", " Interstitial not loaded");
                                    }
                                }
                            }
                        }
                    });
                }
            }, 1, 1, TimeUnit.SECONDS);

        }else {
            scheduler.shutdown();
            startTimer();
        }
    }

    @Override
    protected void onResume() {
        dying=true;
        super.onResume();
    }

    public void goToNext(View view){
        isActive=false;
        Intent intent=new Intent(MainActivity.this, ItemListActivity.class);
        intent.putExtra("category",getViewWordId(view));
        startActivity(intent);
        finish();
    }
    public void setCallbacks(UpdateCounter updateCounter1) {
        updateCounter = updateCounter1;
    }
    public String getViewWordId(View v){
        String name5 = null;
        Field[] campos = R.id.class.getFields();
        for(Field f:campos){
            try{
                if(v.getId()==f.getInt(null)){
                    name5 = f.getName();
                    break;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        Log.e(TAG, "getViewWordId: "+name5 );
        return name5;
    }
    public void back(View view){
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (dying){
            Intent intent = new Intent(MainActivity.this, MainService.class);

            startService(intent);
        }
        super.onDestroy();
    }


    @Override
    public void onUpdateCounter(int time) {
        //show add
        // Load ads into Interstitial Ads
        Log.e(TAG, " onUpdateCounter starting");
        AdRequest adRequest = new AdRequest.Builder().build();
        if (!isVisible){
            mInterstitialAd.loadAd(adRequest);
            if (mInterstitialAd.isLoaded() && isVisible) {
                Log.e(TAG, " onUpdateCounter loaded");
            } else {
                Log.e(TAG, " onUpdateCounter not loaded");
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        //show add
        // Load ads into Interstitial Ads
        Log.e(TAG, " onUpdateCounter starting");
        AdRequest adRequest = new AdRequest.Builder().build();
        if (!isVisible){
            mInterstitialAd.loadAd(adRequest);
            if (mInterstitialAd.isLoaded() && isVisible) {
                Log.e(TAG, " onUpdateCounter loaded");
            } else {
                Log.e(TAG, " onUpdateCounter not loaded");
            }
        }
    }
}
