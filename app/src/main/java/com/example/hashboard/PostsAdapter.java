package com.example.hashboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {
    private String[] mDataset;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        MyViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.text_list_view);
        }
    }

    public PostsAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public PostsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTextView.setText(mDataset[position]);

    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}