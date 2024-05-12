package com.example.f1blog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterActivity.class.getName();
    FirebaseUser currentUser;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView numberOfBlogsTextView;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        String username = currentUser.getDisplayName();
        String email = currentUser.getEmail();

        usernameTextView = findViewById(R.id.u_name);
        emailTextView = findViewById(R.id.u_email);
        numberOfBlogsTextView = findViewById(R.id.numberOfBlogs);

        if (username != null) {
            usernameTextView.setText(username);
        }
        emailTextView.setText(email);

        countUserBlogs();

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    /***
     * Ez egy összetett lekérdezés, amely megszámolja, hogy a bejelentkezett felhasználónak
     * összesen mennyi bejegyzése szerepel az adatabázisban. Amennyiben ilyen nem található,
     * az érték értelemszerűen 0.
     */
    private void countUserBlogs() {
        db.collection("Blogs")
                .whereEqualTo("author", currentUser.getDisplayName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            numberOfBlogsTextView.setText(String.valueOf(count));
                        } else {
                            Toast.makeText(ProfileActivity.this, "Hiba a blogok számolásakor: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
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
        if (id == R.id.newBlog) {
            startActivity(new Intent(this, NewBlogActivity.class));
        }
        if (id == R.id.f1_official) {
            Uri uri = Uri.parse("https://www.formula1.com/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        return true;
    }

    private void logoutUser() {
        new AlertDialog.Builder(this)
                .setTitle("Kijelentkezés")
                .setMessage("Biztosan ki szeretnél jelentkezni?")
                .setCancelable(false)
                .setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        finish();
                    }

                }).show();
    }
}
