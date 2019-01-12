package com.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.R;
import com.models.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;

    public ImageAdapter(Context context, List<Upload> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        Picasso.get()
                .load(uploadCurrent.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.ivBookImg);

        if(uploadCurrent.isCheck()){
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

        //take care of the last in list
        if(uploadCurrent.getName().equals("plusBtn")){
            holder.backgroundImg.setVisibility(View.INVISIBLE);
        } else {
            holder.backgroundImg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        public ImageView ivBookImg;
        public ImageView checkBox;
        public ImageView backgroundImg;

        public ImageViewHolder(View itemView) {
            super(itemView);

            ivBookImg = (ImageView) itemView.findViewById(R.id.book_img);
            checkBox = (ImageView) itemView.findViewById(R.id.check_box);
            backgroundImg = (ImageView) itemView.findViewById(R.id.background_img);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                    mListener.onItemClick(position);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem check = menu.add(Menu.NONE, 1, 1, "√");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "X");

            check.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch (item.getItemId()) {
                        case 1:
                            mListener.onCheckClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onCheckClick(int position);
        void onDeleteClick(int position);
    }

    public void onItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
}
