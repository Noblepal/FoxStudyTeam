package com.trichain.foxstudyteam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.kobakei.ratethisapp.RateThisApp;
import com.trichain.foxstudyteam.adapter.NewsAdapter;
import com.trichain.foxstudyteam.models.RSSItem;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements RewardedVideoAdListener {


    private ArrayList<RSSItem> newsArrayList = new ArrayList<>();
    private NewsAdapter adapter;

    String category = null;
    private static final String TAG = "ItemListActivity";
    private InterstitialAd mInterstitialAd;
    private ScheduledExecutorService scheduler, scheduler2;
    private boolean isVisible;
    RewardedVideoAd mAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        MobileAds.initialize(this, getResources().getString(R.string.ad_id_banner));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_id_interstitial));
        AdRequest adRequest = new AdRequest.Builder().build();

        // Load ads into Interstitial Ads
//        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                isVisible = true;
                mInterstitialAd.show();
                ((View) findViewById(R.id.textViewb)).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdClosed() {
                AdRequest adRequest = new AdRequest.Builder().build();
                ((View) findViewById(R.id.textViewb)).setVisibility(View.GONE);

                // Load ads into Interstitial Ads
//                mInterstitialAd.loadAd(adRequest);
            }
        });

        //load reward videos
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
//        loadRewardedVideo(mAd);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        RateThisApp.onStart(this);
        if (getIntent().getBooleanExtra("rate", true)) {
            // Monitor launch times and interval from installation
            // If the condition is satisfied, "Rate this app" dialog will be shown
            RateThisApp.showRateDialogIfNeeded(this);
        }
        category = getIntent().getExtras().getString("category", "trending");
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("EEE MMMM dd");
        String now = df.format(new Date());
        Log.e(TAG, "onCreate: " + now);
        ((TextView) findViewById(R.id.textView)).setText(now);

        RecyclerView recyclerView = findViewById(R.id.item_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this, newsArrayList, category);
        recyclerView.setAdapter(adapter);

        TextView categoryTitle = findViewById(R.id.textViewCategory);
        categoryTitle.setText(category);


        getDataFromNEt(category);
    }

    public void getmenu(View view) {
        super.onBackPressed();
    }

    private void loadRewardedVideo(final RewardedVideoAd mAd) {
        isVisible = true;
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    Log.i("hello", "world");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (mAd.isLoaded() && isVisible) {
                                mAd.loadAd("ca-app-pub-4824494878097656/8403117409",//use this id for testing
                                        new AdRequest.Builder().build());
                            } else {
                                Log.d("TAG", " Interstitial not loaded");
                            }

                            displayInterstitial();
                        }
                    });
                }
            }, 10, 10, TimeUnit.SECONDS);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    Log.i("hello", "world");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (mInterstitialAd.isLoaded() && isVisible) {
                                mInterstitialAd.show();
                            } else {
                                Log.d("TAG", " Interstitial not loaded");
                            }
                            displayInterstitial();
                        }
                    });
                }
            }, 10, 10, TimeUnit.SECONDS);

        }


    }

    private void displayInterstitial() {

    }

    public void rotateme(final View v) {
        final Intent intent = new Intent(ItemListActivity.this, ItemListActivity.class);
        intent.putExtra("category", category);
        v.animate().rotation(180).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.animate().rotation(360).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().rotation(540).start();
                        startActivity(intent);
                        finish();
                    }
                }, 1000);
            }
        }, 1000);
    }

    private void getDataFromNEt(String category) {
        String[] urls = currentView(category);

        for (String url : urls) {
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "getDataFromNEt URL:" + url);
            //Volley
            retrieveNewsItem(url);

        }
    }

    private String[] currentView(String category1) {
        Log.e(TAG, "currentView: " + category1);
        String[] v = null;
        switch (category1) {
            case "trending":
                v = getResources().getStringArray(R.array.trending);
                break;
            case "breaking":
                v = getResources().getStringArray(R.array.breaking);
                break;
            case "environment":
                v = getResources().getStringArray(R.array.environment);
                break;
            case "politics":
                v = getResources().getStringArray(R.array.politics);
                break;
            case "sports":
                v = getResources().getStringArray(R.array.sports);
                break;
            case "stock":
                v = getResources().getStringArray(R.array.stock);
                break;
            case "lifestyle":
                v = getResources().getStringArray(R.array.lifestyle);
                break;
            case "health":
                v = getResources().getStringArray(R.array.health);
                break;
            case "tech":
                v = getResources().getStringArray(R.array.tech);
                break;
            case "business":
                v = getResources().getStringArray(R.array.business);
                break;
            case "entertainment":
                v = getResources().getStringArray(R.array.entertainment);
                break;
            case "weather":
                v = getResources().getStringArray(R.array.weather);
                break;
            case "art":
                v = getResources().getStringArray(R.array.art);
                break;
            case "travel":
                v = getResources().getStringArray(R.array.travel);
                break;
            case "science":
                v = getResources().getStringArray(R.array.science);
                break;
            case "food":
                v = getResources().getStringArray(R.array.food);
                break;
            case "other":
                v = getResources().getStringArray(R.array.other);
                break;
            default:
                v = getResources().getStringArray(R.array.trending);
                break;

        }
        return v;
    }

    @SuppressLint("StaticFieldLeak")
    private void retrieveNewsItem(final String url) {

        new AsyncTask<Void, String, Void>() {
            String res = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... voids) {
                RSSParser rssParser = new RSSParser(ItemListActivity.this);

                List<RSSItem> rssItemList = rssParser.getRSSFeedItems(url, adapter, newsArrayList, category);

                populateRecyclerView(rssItemList);

//                newsArrayList.add((RSSItem) Arrays.asList(rssItemList));
                //TODO: To be continued...

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();


    }

    private void populateRecyclerView(List<RSSItem> rssItemList) {

//        newsArrayList.addAll(rssItemList);
//        adapter.notifyDataSetChanged();


        //TODO: Get data from net into arraylist then into adapter


    }

    public String getViewWordId(View v) {
        String name5 = null;
        Field[] campos = R.id.class.getFields();
        for (Field f : campos) {
            try {
                if (v.getId() == f.getInt(null)) {
                    name5 = f.getName();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "getViewWordId: " + name5);
        return name5;
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        mAd.show();
        if (mAd.isLoaded()) {
            mAd.show();
            ((View) findViewById(R.id.textViewb)).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {

//        ((View)findViewById(R.id.textViewb)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onRewardedVideoStarted() {

//        ((View)findViewById(R.id.textViewb)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onRewardedVideoAdClosed() {

//        ((View)findViewById(R.id.textViewb)).setVisibility(View.GONE);
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

//        ((View)findViewById(R.id.textViewb)).setVisibility(View.GONE);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
//        ((View)findViewById(R.id.textViewb)).setVisibility(View.GONE);

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
//        ((View)findViewById(R.id.textViewb)).setVisibility(View.GONE);

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
