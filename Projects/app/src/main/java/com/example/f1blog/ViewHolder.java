package com.example.f1blog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView mTitleTv, mDescriptionTv,  mAuthorTv, mCategoryTv, mDateTv;
    View mView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return false;
            }
        });

        mTitleTv = itemView.findViewById(R.id.rTitle);
        mDescriptionTv = itemView.findViewById(R.id.rDescription);
        mCategoryTv = itemView.findViewById(R.id.rCategory);
        mAuthorTv = itemView.findViewById(R.id.rAuthor);
        mDateTv = itemView.findViewById(R.id.rDate);
    }

    private ViewHolder.ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}
