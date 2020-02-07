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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trichain.foxstudyteam.adapter.NewsAdapter;
import com.trichain.foxstudyteam.dummy.DummyContent;
import com.trichain.foxstudyteam.models.News;
import com.trichain.foxstudyteam.models.RSSItem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        category = getIntent().getExtras().getString("category", "trending");

        RecyclerView recyclerView = findViewById(R.id.item_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this, newsArrayList);
        recyclerView.setAdapter(adapter);


        getDataFromNEt(category);
    }

    private void getDataFromNEt(String category) {
        int resId = getResources().getIdentifier(category, "array", getPackageName());
        Log.e(TAG, "getDataFromNEt:" + getViewWordId(findViewById(resId)));
        String[] urls = getResources().getStringArray(R.array.trending);

        for (String url : urls) {

            //Volley
            retrieveNewsItem(url);

        }
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
                RSSParser rssParser = new RSSParser();
                List<RSSItem> rssItemList = rssParser.getRSSFeedItems(url);

                newsArrayList.add((RSSItem) Arrays.asList(rssItemList));
                //TODO: To be continued...

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();


    }

    private void populateRecyclerView() {


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
