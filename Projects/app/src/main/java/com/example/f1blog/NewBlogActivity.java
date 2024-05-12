package com.example.f1blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NewBlogActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterActivity.class.getName();

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    EditText blog_title, blog_description, blog_category;
    Button btn_publish;
    ProgressDialog pd;
    FirebaseFirestore db;

    String pId, pTitle, pDescription, pCategory;

    String pDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_blog);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        blog_title = findViewById(R.id.blog_title);
        blog_description = findViewById(R.id.blog_description);
        blog_category = findViewById(R.id.blog_category);
        btn_publish = findViewById(R.id.btn_publish);

        pd = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            actionBar.setTitle("Blog módosítása");
            btn_publish.setText("Módosítás");

            pId = bundle.getString("pId");
            pTitle = bundle.getString("pTitle");
            pDescription = bundle.getString("pDescription");
            pCategory = bundle.getString("pCategory");

            pDate = bundle.getString("pDate");

            blog_title.setText(pTitle);
            blog_description.setText(pDescription);
            blog_category.setText(pCategory);

        } else {
            actionBar.setTitle("Új blog");
            btn_publish.setText("Közzététel");
        }

        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    String id = pId;
                    String title = blog_title.getText().toString().trim();
                    String description = blog_description.getText().toString().trim();
                    String category = blog_category.getText().toString().trim();
                    String original_date = pDate;

                    updateData(id, title, description, category, original_date);
                } else {
                    String title = blog_title.getText().toString().trim();
                    String description = blog_description.getText().toString().trim();
                    String category = blog_category.getText().toString().trim();
                    String author = currentUser.getDisplayName();

                    uploadData(title, description, category, author);
                }

            }
        });
    }

    private void updateData(String id, String title, String description, String category, String original_date) {
        pd.setTitle("Frissítés folyamatban...");
        pd.show();

        // Ellenőrizzük, hogy a cím végén szerepel-e már a "(FRISSÍTVE)" szöveg
        if (!title.endsWith(" (FRISSÍTVE)")) {
            // Ha nem szerepel, akkor hozzáadjuk a cím végére
            title += " (FRISSÍTVE)";
        }

        db.collection("Blogs").document(id)
                .update("title", title, "search", title.toLowerCase(), "description", description, "category", category, "date", getCurrentDateTime())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        Toast.makeText(NewBlogActivity.this, "Frissítve!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(NewBlogActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(NewBlogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private String getCurrentDateTime() {
        // Aktuális dátum és idő inicializálása
        Calendar calendar = Calendar.getInstance();

        // Dátum és idő formátumának beállítása
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());

        // Dátum és idő formázása és visszaadása
        return dateFormat.format(calendar.getTime());
    }

    private void uploadData(String title, String description, String category, String author) {
        pd.setTitle("Bejegyzés közzététele...");
        pd.show();

        String id = UUID.randomUUID().toString();
        String date = getCurrentDateTime();

        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("title", title);
        doc.put("search", title.toLowerCase());
        doc.put("description", description);
        doc.put("category", category);
        doc.put("author", author);
        doc.put("date", date);


        db.collection("Blogs").document(id).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        Toast.makeText(NewBlogActivity.this, "Sikeres feltöltés!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewBlogActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewBlogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.homepage) {
            startActivity(new Intent(this, HomeActivity.class));
        }
        if (id == R.id.profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
        if (id == R.id.f1_official) {
            Uri uri = Uri.parse("https://www.formula1.com/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        return true;
    }
}