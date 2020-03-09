package com.boringappstudio.newsfeedio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kobakei.ratethisapp.RateThisApp;
import com.boringappstudio.newsfeedio.adapter.NewsAdapter;
import com.boringappstudio.newsfeedio.models.RSSItem;
import com.boringappstudio.newsfeedio.utils.UpdateCounter;

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
public class ItemListActivity extends AppCompatActivity implements RewardedVideoAdListener, UpdateCounter {


    private ArrayList<RSSItem> newsArrayList = new ArrayList<>();
    private NewsAdapter adapter;
    RecyclerView recyclerView;
    String category = null;
    ImageView headerImage;
    private static final String TAG = "ItemListActivity";
    private InterstitialAd mInterstitialAd;
    private ScheduledExecutorService scheduler, scheduler2;
    private boolean isVisible=false;
    RewardedVideoAd mAd;
    Boolean gtimeout=true;
    Boolean isActive=false;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        isActive=true;
        recyclerView = findViewById(R.id.item_list);
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

                final Toast toast=Toast.makeText(ItemListActivity.this,"This ad helps keep our app free of charge",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP,0,0);
                toast.show();
                 snackbar=Snackbar.make(recyclerView,"This ad helps keep our app free of charge", BaseTransientBottomBar.LENGTH_INDEFINITE);
                snackbar.setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
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
        mAd.setRewardedVideoAdListener(this);
//        loadRewardedVideo(mAd);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        RateThisApp.onStart(this);
        // Custom condition: 3 days and 5 launches
        RateThisApp.Config config = new RateThisApp.Config(0, 4);
        RateThisApp.init(config);
        RateThisApp.showRateDialogIfNeeded(this);

        category = getIntent().getExtras().getString("category", "trending");
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("EEE, MMMM dd");
        String now = df.format(new Date());
        Log.e(TAG, "onCreate: " + now);
        ((TextView) findViewById(R.id.textView)).setText(now);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this, newsArrayList, category);
        recyclerView.setAdapter(adapter);
        headerImage = findViewById(R.id.imageMain);

        TextView categoryTitle = findViewById(R.id.textViewCategory);
        categoryTitle.setText(category);
        startTimer();

        getDataFromNEt(category);
    }

    public void startTimer(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        final int[] ctime = {preferences.getInt("myTime", 0)};
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    Log.e("hello", "world"+ctime[0]);
                    Log.e(TAG, "run: "+isVisible );
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

        }
    }
    public void getmenu(View view) {
        isActive=false;
        Intent intent=new Intent(ItemListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        isActive=false;
        Log.e(TAG, "onBackPressed: " );

        Intent intent=new Intent(ItemListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
      /*  if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    Log.e("hello", "world");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            AdRequest adRequest = new AdRequest.Builder().build();

                            // Load ads into Interstitial Ads
                            if (!isVisible){
//                                mInterstitialAd.loadAd(adRequest);
                                if (mInterstitialAd.isLoaded() && isVisible) {
                                } else {
                                    Log.d("TAG", " Interstitial not loaded");
                                }
                            }
                            displayInterstitial();
                        }
                    });
                }
            }, 60, 60, TimeUnit.SECONDS);*/

//        }


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
    private void displayInterstitial() {

    }

    public void spinMeRound(final View v) {
        final Intent intent = new Intent(ItemListActivity.this, ItemListActivity.class);
        intent.putExtra("category", category);

        final RotateAnimation rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(250);
        rotate.setRepeatCount(Animation.INFINITE);
        v.startAnimation(rotate);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshData(category);
                v.clearAnimation();

                /*startActivity(intent);
                finish();*/
            }
        }, 3000);

       /* v.animate().rotation(180).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                v.animate().rotation(360).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().rotation(540).start();

                    }
                }, 1);
            }
        }, 2);*/
    }

    private void refreshData(String category) {
        newsArrayList.clear();
        adapter.notifyDataSetChanged();
        getDataFromNEt(category);
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
                headerImage.setImageResource(R.drawable.trending);
                break;
            case "breaking":
                v = getResources().getStringArray(R.array.breaking);
                headerImage.setImageResource(R.drawable.breaking_news);
                break;
            case "environment":
                v = getResources().getStringArray(R.array.environment);
                headerImage.setImageResource(R.drawable.environment);
                break;
            case "politics":
                v = getResources().getStringArray(R.array.politics);
                headerImage.setImageResource(R.drawable.politics);
                break;
            case "sports":
                v = getResources().getStringArray(R.array.sports);
                headerImage.setImageResource(R.drawable.sports);
                break;
            case "stock":
                v = getResources().getStringArray(R.array.stock);
                headerImage.setImageResource(R.drawable.stock);
                break;
            case "lifestyle":
                v = getResources().getStringArray(R.array.lifestyle);
                headerImage.setImageResource(R.drawable.lifestyle);
                break;
            case "health":
                v = getResources().getStringArray(R.array.health);
                headerImage.setImageResource(R.drawable.health);
                break;
            case "tech":
                v = getResources().getStringArray(R.array.tech);
                headerImage.setImageResource(R.drawable.technology);
                break;
            case "business":
                v = getResources().getStringArray(R.array.business);
                headerImage.setImageResource(R.drawable.business);
                break;
            case "entertainment":
                v = getResources().getStringArray(R.array.entertainment);
                headerImage.setImageResource(R.drawable.entertainment);
                break;
            case "weather":
                v = getResources().getStringArray(R.array.weather);
                headerImage.setImageResource(R.drawable.weather);
                break;
            case "art":
                v = getResources().getStringArray(R.array.art);
                headerImage.setImageResource(R.drawable.art_music);
                break;
            case "travel":
                v = getResources().getStringArray(R.array.travel);
                headerImage.setImageResource(R.drawable.travel);
                break;
            case "science":
                v = getResources().getStringArray(R.array.science);
                headerImage.setImageResource(R.drawable.science);
                break;
            case "food":
                v = getResources().getStringArray(R.array.food);
                headerImage.setImageResource(R.drawable.food);
                break;
            case "other":
                v = getResources().getStringArray(R.array.other);
                headerImage.setImageResource(R.drawable.other);
                break;
            default:
                v = getResources().getStringArray(R.array.trending);
                headerImage.setImageResource(R.drawable.trending);
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
