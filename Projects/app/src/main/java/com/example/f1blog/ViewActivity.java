package com
        .example.f1blog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Ellenőrizze, hogy van-e adat az Intent-ben
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("v_blog_title")) {
                String title = intent.getStringExtra("v_blog_title");
                TextView titleTextView = findViewById(R.id.v_blog_title);
                titleTextView.setText(title);
            }
            if (intent.hasExtra("v_blog_description")) {
                String description = intent.getStringExtra("v_blog_description");
                TextView descriptionTextView = findViewById(R.id.v_blog_description);
                descriptionTextView.setText(description);
            }
            if (intent.hasExtra("v_blog_category")) {
                String category = intent.getStringExtra("v_blog_category");
                TextView categoryTextView = findViewById(R.id.v_blog_category);
                categoryTextView.setText("Kategória: " + category);
            }
            if (intent.hasExtra("v_blog_author")) {
                String author = intent.getStringExtra("v_blog_author");
                TextView authorTextView = findViewById(R.id.v_blog_author);
                authorTextView.setText("Szerző: " + author);
            }
            if (intent.hasExtra("v_blog_date")) {
                String date = intent.getStringExtra("v_blog_date");
                TextView dateTextView = findViewById(R.id.v_blog_date);
                if (intent.hasExtra("v_blog_title")) {
                    String title = intent.getStringExtra("v_blog_title");
                    if (title.endsWith(" (FRISSÍTVE)")) {
                        dateTextView.setText("Módosítva: " + date);
                    } else {
                        dateTextView.setText("Kelt: " + date);
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}