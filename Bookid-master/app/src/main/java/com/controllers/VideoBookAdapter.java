package com.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.samples.vision.ocrreader.R;
import com.models.VideoBook;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoBookAdapter extends RecyclerView.Adapter<VideoBookAdapter.VideoBookViewHolder> {
    private Context mContext;
    private List<VideoBook> mBooks;
    private VideoBookAdapter.OnItemClickListener mListener;

    public VideoBookAdapter(Context context, List<VideoBook> books){
        mContext = context;
        mBooks = books;
    }

    @NonNull
    @Override
    public VideoBookAdapter.VideoBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new VideoBookAdapter.VideoBookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoBookAdapter.VideoBookViewHolder holder, int position) {
        VideoBook current = mBooks.get(position);
        Picasso.get()
                .load(current.getPic())
                .placeholder(R.drawable.emptybook)
                .fit()
                .centerInside()
                .into(holder.ivBookImg);
        holder.checkBox.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public class VideoBookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivBookImg;
        public ImageView checkBox;

        public VideoBookViewHolder(View itemView) {
            super(itemView);

            ivBookImg = (ImageView) itemView.findViewById(R.id.book_img);
            checkBox = (ImageView) itemView.findViewById(R.id.check_box);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                    mListener.onItemClick(position);
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void onItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
}
