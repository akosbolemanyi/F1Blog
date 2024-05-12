    package com.example.f1blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

    public class MainActivity extends AppCompatActivity {
        private static final String LOG_TAG = MainActivity.class.getName();
        private static final String PREF_KEY = MainActivity.class.getPackage().toString();
        private static final int SECRET_KEY = 99;

        EditText userEmailET;
        EditText passwordET;

        private SharedPreferences preferences;

        private FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmailET = findViewById(R.id.editTextUserEmail);
        passwordET = findViewById(R.id.editTextPassword);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }

        public void login(View view) {


            String email = userEmailET.getText().toString();
            String password = passwordET.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "User logged in succesfully");
                        startBlog();
                    } else {
                        Log.d(LOG_TAG, "User login fail");
                        Toast.makeText(MainActivity.this, "User login fail: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        private void startBlog() {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

        public void register(View view) {
            Intent intent  = new Intent(this, RegisterActivity.class);
            intent.putExtra("SECRET_KEY", 99);
            startActivity(intent);
        }

        /***
         * Ez a lifecycle hook egy értelmes megvalósítása. A bejelentkezésnél beírt adatok,
         * ha még nem létezik a beírt adatoknak megfeleő felhasználó, akkor a regisztációra
         * átlépve az E-mail cím és a jelszó ki lesz töltve. A jelszó ismétlése ettől
         * függetlenül meg kell, hogy történjen a felhasználó érdekében.
         */
        @Override
        protected void onPause() {
            super.onPause();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userEmail", userEmailET.getText().toString());
            editor.putString("password", passwordET.getText().toString());
            editor.apply();

            Log.i(LOG_TAG, "onPause");
        }
    }