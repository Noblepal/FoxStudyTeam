package com.trichain.foxstudyteam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kobakei.ratethisapp.RateThisApp;
import com.trichain.foxstudyteam.adapter.NewsAdapter;
import com.trichain.foxstudyteam.dummy.DummyContent;
import com.trichain.foxstudyteam.models.News;
import com.trichain.foxstudyteam.models.RSSItem;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.net.sip.SipErrorCode.TIME_OUT;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {


    private ArrayList<RSSItem> newsArrayList = new ArrayList<>();
    private NewsAdapter adapter;

    String category = null;
    private static final String TAG = "ItemListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        MobileAds.initialize(this, getResources().getString(R.string.ad_id_banner));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        RateThisApp.onStart(this);
        if (getIntent().getBooleanExtra("rate",true)){
            // Monitor launch times and interval from installation
            // If the condition is satisfied, "Rate this app" dialog will be shown
            RateThisApp.showRateDialogIfNeeded(this);
        }

        category = getIntent().getExtras().getString("category", "trending");
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("EEE MMMM dd");
        String now = df.format(new Date());
        Log.e(TAG, "onCreate: "+now );
        ((TextView)findViewById(R.id.textView)).setText(now);

        RecyclerView recyclerView = findViewById(R.id.item_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this, newsArrayList, category);
        recyclerView.setAdapter(adapter);


        getDataFromNEt(category);
    }
    public void rotateme(final View v){
        final Intent intent=new Intent(ItemListActivity.this,ItemListActivity.class);
        intent.putExtra("category",category);
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
                url= URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "getDataFromNEt URL:" + url);
            //Volley
            retrieveNewsItem(url);

        }
    }
    private String[] currentView(String category1){
        Log.e(TAG, "currentView: "+category1 );
        String[] v=null;
        switch (category1){
            case "trending":
                v=getResources().getStringArray(R.array.trending);
                break;
            case "breaking":
                v=getResources().getStringArray(R.array.breaking);
                break;
            case "environment":
                v=getResources().getStringArray(R.array.environment);
                break;
            case "politics":
                v=getResources().getStringArray(R.array.politics);
                break;
            case "sports":
                v=getResources().getStringArray(R.array.sports);
                break;
            case "stock":
                v=getResources().getStringArray(R.array.stock);
                break;
            case "lifestyle":
                v=getResources().getStringArray(R.array.lifestyle);
                break;
            case "health":
                v=getResources().getStringArray(R.array.health);
                break;
            case "tech":
                v=getResources().getStringArray(R.array.tech);
                break;
            case "business":
                v=getResources().getStringArray(R.array.business);
                break;
            case "entertainment":
                v=getResources().getStringArray(R.array.entertainment);
                break;
            case "weather":
                v=getResources().getStringArray(R.array.weather);
                break;
            case "art":
                v=getResources().getStringArray(R.array.art);
                break;
            case "travel":
                v=getResources().getStringArray(R.array.travel);
                break;
            case "science":
                v=getResources().getStringArray(R.array.science);
                break;
            case "food":
                v=getResources().getStringArray(R.array.food);
                break;
            case "other":
                v=getResources().getStringArray(R.array.other);
                break;
            default:
                v=getResources().getStringArray(R.array.trending);
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

                List<RSSItem> rssItemList = rssParser.getRSSFeedItems(url,adapter,newsArrayList,category);

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
}
