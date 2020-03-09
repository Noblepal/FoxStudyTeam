package com.boringappstudio.newsfeedio.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.boringappstudio.newsfeedio.ItemDetailActivity;
import com.boringappstudio.newsfeedio.R;
import com.boringappstudio.newsfeedio.models.RSSItem;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    Context context;
    ArrayList<RSSItem> newsArrayList;
    String category;

    public NewsAdapter(Context context, ArrayList<RSSItem> newsArrayList, String category) {
        this.context = context;
        this.newsArrayList = newsArrayList;
        this.category = category;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == 1) {

            View vv = LayoutInflater.from(context).inflate(R.layout.banner, parent, false);
            AdView adView = vv.findViewById(R.id.banner);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            v = vv;
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.item_list_content, parent, false);


        }
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        final RSSItem news = newsArrayList.get(position);
        if (position % 4 == 0) {

        } else {
            holder.category.setText(category);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.title.setText(Html.fromHtml(news.getTitle(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.title.setText(Html.fromHtml(news.getTitle()));
            }
            holder.link.setText(news.getLink());
            holder.thisitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra("category", category);
                    intent.putExtra("tittle", news.getTitle());
                    intent.putExtra("url", news.getLink());
                    intent.putExtra("image", news.getImage());
                    intent.putExtra("description", news.getDescription());
                    context.startActivity(intent);


                }
            });

//        holder.category.setText(news.getCategory());
            Log.e("NewsAdapter", "onBindViewHolder: " + news.getImage());
            Glide.with(context)
                    .load(news.getImage())
                    .fallback(R.drawable.ic_broken_image)
                    .placeholder(R.drawable.ic_broken_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.image);
//        holder.category.getParent().f
        }

    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 4 == 0)
            return 1;
        return 11;
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        TextView category, title, link;
        ImageView image;
        View thisitem;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.id_textCategory);
            title = itemView.findViewById(R.id.contentTitle);
            link = itemView.findViewById(R.id.id_textSite);
            image = itemView.findViewById(R.id.imageNews);
            thisitem = itemView.findViewById(R.id.thisitem);

        }
    }

}
