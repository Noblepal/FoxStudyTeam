package com.trichain.foxstudyteam;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {
    String title,category,link,image,description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        if (intent.hasExtra("category")){
            title=intent.getStringExtra("tittle");
            category=intent.getStringExtra("category");
            link=intent.getStringExtra("url");
            image=intent.getStringExtra("image");
            description=intent.getStringExtra("description");
            ((TextView)findViewById(R.id.category)).setText(category);
            ((TextView)findViewById(R.id.title)).setText(title);
            ((TextView)findViewById(R.id.link)).setText(link);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ((TextView)findViewById(R.id.description)).setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                ((TextView)findViewById(R.id.description)).setText(Html.fromHtml(description));
            }
            Glide.with(this)
                    .load(image)
                    .fallback(R.drawable.ic_broken_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(((ImageView)findViewById(R.id.headLineImage)));

        }else {
            finish();
        }



        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }
    public void back(View v){
        super.onBackPressed();
    }
    public void share(View v){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing News");
        i.putExtra(Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(i, "Share URL"));
    }
    public void visit(View v){
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
}
