package com.sacnitp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class Model_Adapter extends RecyclerView.Adapter<Model_Adapter.MyViewholder> {
    Context context;
    ArrayList<Model> post;

    public Model_Adapter(Context c, ArrayList<Model> p) {
        context = c;
        post = p;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewholder(LayoutInflater.from(context).inflate(R.layout.element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int i) {
        holder.name.setText(post.get(i).getUsername());
        holder.desc.setText(post.get(i).getDesc());
        Glide.with(context).load(post.get(i).getImageurl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return post.size();
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public int getItemViewType(int pos)
    {
        return pos;
    }
    class MyViewholder extends RecyclerView.ViewHolder {
        TextView name, desc;
        ImageView imageView;
        public MyViewholder(View itemview) {
            super(itemview);
            name = (TextView) itemview.findViewById(R.id.list_title);
            desc = (TextView) itemview.findViewById(R.id.list_desc);
            imageView=(ImageView)itemview.findViewById(R.id.list_image);

        }
    }
}


