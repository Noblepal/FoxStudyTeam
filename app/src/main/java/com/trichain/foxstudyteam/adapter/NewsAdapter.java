package com.trichain.foxstudyteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trichain.foxstudyteam.R;
import com.trichain.foxstudyteam.models.News;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    Context context;
    ArrayList<News> newsArrayList;

    public NewsAdapter(Context context, ArrayList<News> newsArrayList) {
        this.context = context;
        this.newsArrayList = newsArrayList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_list_content, parent, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsArrayList.get(position);

        holder.category.setText(news.getCategory());
        holder.title.setText(news.getTitle());
        holder.link.setText(news.getLink());
        holder.category.setText(news.getCategory());


        Glide.with(context)
                .load(news.getImagr_url())
                .fallback(R.drawable.ic_broken_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        TextView category, title, link;
        ImageView image;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.id_textCategory);
            title = itemView.findViewById(R.id.contentTitle);
            link = itemView.findViewById(R.id.id_textSite);
            image = itemView.findViewById(R.id.imageNews);
        }
    }

}
