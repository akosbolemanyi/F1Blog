package com.example.f1blog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {

    HomeActivity homeActivity;
    List<Model> modelList;

    public CustomAdapter(HomeActivity homeActivity, List<Model> modelList) {
        this.homeActivity = homeActivity;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_layout, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String title = modelList.get(position).getTitle();
                String description = modelList.get(position).getDescription();
                String category = modelList.get(position).getCategory();
                String author = modelList.get(position).getAuthor();
                String date = modelList.get(position).getDate();
                Intent intent = new Intent(homeActivity, ViewActivity.class);
                intent.putExtra("v_blog_title", title);
                intent.putExtra("v_blog_description", description);
                intent.putExtra("v_blog_category", category);
                intent.putExtra("v_blog_author", author);
                intent.putExtra("v_blog_date", date);
                homeActivity.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(homeActivity);
                String[] options = {"Módosítás", "Törlés"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            String id = modelList.get(position).getId();
                            String title = modelList.get(position).getTitle();
                            String description = modelList.get(position).getDescription();
                            String category = modelList.get(position).getCategory();
                            String date = modelList.get(position).getDate();

                            Intent intent = new Intent(homeActivity, NewBlogActivity.class);
                            intent.putExtra("pId", id);
                            intent.putExtra("pTitle", title);
                            intent.putExtra("pDescription", description);
                            intent.putExtra("pCategory", category);
                            intent.putExtra("pDate", date);
                            homeActivity.startActivity(intent);
                        }
                        if (which == 1) {
                            homeActivity.deleteData(position);
                        }
                    }
                }).create().show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mTitleTv.setText(modelList.get(i).getTitle());
        viewHolder.mDescriptionTv.setText(modelList.get(i).getDescription());
        viewHolder.mCategoryTv.setText("Kategória: " + modelList.get(i).getCategory());
        viewHolder.mAuthorTv.setText("Szerző: " + modelList.get(i).getAuthor());
        viewHolder.mDateTv.setText(modelList.get(i).getDate());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
