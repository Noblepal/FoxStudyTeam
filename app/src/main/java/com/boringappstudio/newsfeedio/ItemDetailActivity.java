package com.boringappstudio.newsfeedio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.material.snackbar.Snackbar;
import com.boringappstudio.newsfeedio.utils.UpdateCounter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity implements UpdateCounter {
    String title, category, link, image, description;
    private InterstitialAd mInterstitialAd;
    private ScheduledExecutorService scheduler, scheduler2;
    private boolean isVisible;
    RewardedVideoAd mAd;
    Boolean gtimeout=true;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        if (intent.hasExtra("category")) {
            title = intent.getStringExtra("tittle");
            category = intent.getStringExtra("category");
            link = intent.getStringExtra("url");
            image = intent.getStringExtra("image");
            description = intent.getStringExtra("description");
            ((TextView) findViewById(R.id.category)).setText(category);
            ((TextView) findViewById(R.id.title)).setText(title);
            ((TextView) findViewById(R.id.link)).setText(link);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ((TextView) findViewById(R.id.description)).setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                ((TextView) findViewById(R.id.description)).setText(Html.fromHtml(description));
            }
            Glide.with(this)
                    .load(image)
                    .fallback(R.drawable.ic_broken_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(((ImageView) findViewById(R.id.headLineImage)));

        } else {
            finish();
        }


        MobileAds.initialize(this, getResources().getString(R.string.ad_id_banner));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_id_interstitial));
        AdRequest adRequest = new AdRequest.Builder().build();

        // Load ads into Interstitial Ads

       /* if (!isVisible) {
            mInterstitialAd.loadAd(adRequest);
        }*/
        startTimer();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                isVisible = true;
                gtimeout=true;
//                ((View) findViewById(R.id.textViewb)).setVisibility(View.VISIBLE);

                final Toast toast=Toast.makeText(ItemDetailActivity.this,"This ad helps keep our app free of charge",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP,0,0);
                toast.show();
                View view = snackbar.getView();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;

                view.setLayoutParams(params);
                view.setElevation(20f);
                ViewCompat.setElevation(view,20f);
                ViewCompat.setTranslationZ(view,5);
//                snackbar.show();
                mInterstitialAd.show();
                view.bringToFront();
            }


            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                gtimeout=false;
                isVisible = false;
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
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public void startTimer(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        final int[] ctime = {preferences.getInt("myTime", 0)};
        isVisible = true;
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    Log.e("hello", "world"+ctime[0]);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ctime[0]++;
                            editor.putInt("myTime",ctime[0]);
                            editor.commit();
                            if (ctime[0] >=60){
                                ctime[0]=0;
                                AdRequest adRequest = new AdRequest.Builder().build();
                                // Load ads into Interstitial Ads
                                if (!isVisible){
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

        }
    }
    public void back(View v) {
        super.onBackPressed();
    }

    public void share(View v) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing News");
        i.putExtra(Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(i, "Share URL"));
    }

    public void visit(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdateCounter(int time) {
        //show add
        // Load ads into Interstitial Ads
        AdRequest adRequest = new AdRequest.Builder().build();
        if (!isVisible){
            mInterstitialAd.loadAd(adRequest);
            if (mInterstitialAd.isLoaded() && isVisible) {
                Log.e("TAG", " onUpdateCounter loaded");
            } else {
                Log.e("TAG", " onUpdateCounter not loaded");
            }
        }
    }
}
