package com.app_republic.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class listAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Article> list;


    listAdapter(Context context, ArrayList<Article> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        ((viewHolder) holder).title.setText(list.get(position).getTitle());
        ((viewHolder) holder).description.setText(list.get(position).getDescription());
        ((viewHolder) holder).author.setText(list.get(position).getAuthor());
        ((viewHolder) holder).section.setText(list.get(position).getSection());
        ((viewHolder) holder).date.setText(list.get(position).getDate());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        TextView title, description, author, section, date;
        RelativeLayout layout;

        viewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            author = itemView.findViewById(R.id.author);
            section = itemView.findViewById(R.id.section);
            date = itemView.findViewById(R.id.date);
            layout = itemView.findViewById(R.id.layout);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(getAdapterPosition()).getLink()));
                    context.startActivity(browserIntent);
                }
            });
        }
    }
}