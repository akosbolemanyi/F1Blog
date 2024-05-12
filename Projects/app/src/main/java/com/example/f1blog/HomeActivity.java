package com.example.f1blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    List<Model> modelList = new ArrayList<>();
    RecyclerView mRecycleView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseFirestore db;

    CustomAdapter adapter;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();

        mRecycleView = findViewById(R.id.recyclerView);

        mRecycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);

        pd = new ProgressDialog(this);

        showData();
    }

    /***
     * Ez egy összetett lekérdezés. A bejegyzések megjelenési idejük szerint vannak sorrendbe
     * helyezve, a legfrissebb található értelemszerűen legfelül. Amennyiben firssül egy
     * bejegyzés, úgy annak időpontja is frissül, így az feljebb kerül a hierarchiában, azonban
     * meg is lesz jelölve cím végén, hogy módosult az adott poszt.
     */
    private void showData() {
        pd.setTitle("Blogok betöltése...");
        pd.show();
        db.collection("Blogs")
                .orderBy("date", Query.Direction.DESCENDING) // Megjelenés szerinti sorrendbe rendezés.
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelList.clear();
                        pd.dismiss();
                        for (DocumentSnapshot doc: task.getResult()) {
                            Model model = new Model(doc.getString("id"),
                                    doc.getString("title"),
                                    doc.getString("description"),
                                    doc.getString("category"),
                                    doc.getString("author"),
                                    doc.getString("date"));
                            modelList.add(model);
                        }
                        adapter = new CustomAdapter(HomeActivity.this, modelList);
                        mRecycleView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    public void deleteData(int index) {
        new AlertDialog.Builder(this)
                .setTitle("Bejegyzés törlése")
                .setMessage("Biztosan törölni szeretnéd ezt a bejegyzést?")
                .setCancelable(false)
                .setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.setTitle("Bejegyzés törlése...");
                        pd.show();

                        db.collection("Blogs").document(modelList.get(index).getId())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismiss();
                                        Toast.makeText(HomeActivity.this, "Sikeres törlés!", Toast.LENGTH_SHORT).show();
                                        showData();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    /***
     * Amennyiben pontos a keresés, kihozza a találatot, ha van.
     */
    private void searchData(String query) {
        pd.setTitle("Keresés...");
        pd.show();

        db.collection("Blogs").whereEqualTo("search", query.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelList.clear();
                        pd.dismiss();
                        for (DocumentSnapshot doc: task.getResult()) {
                            Model model = new Model(doc.getString("id"),
                                    doc.getString("title"),
                                    doc.getString("description"),
                                    doc.getString("category"),
                                    doc.getString("author"),
                                    doc.getString("date"));
                            modelList.add(model);
                        }
                        adapter = new CustomAdapter(HomeActivity.this, modelList);
                        mRecycleView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.newBlog) {
            startActivity(new Intent(this, NewBlogActivity.class));
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