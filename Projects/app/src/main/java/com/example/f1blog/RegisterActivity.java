package com.example.f1blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();

    private SharedPreferences preferences;

    private static final int SECRET_KEY = 99;

    private DatePickerDialog picker;
    EditText userNameEditText;
    EditText userEmailEditText;
    EditText birthDateEditText;
    EditText passwordEditText;
    EditText passswordConfirmEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 99) {
            finish();
        }
        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        birthDateEditText = findViewById(R.id.birthDateEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passswordConfirmEditText = findViewById(R.id.passwordAgainEditText);

        birthDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Ellenőrizzük, hogy a hónap és a nap kétszámjegyű-e, és ha nem, akkor előtte adjunk hozzá egy nullát
                        String monthString = (month + 1 < 10) ? "0" + (month + 1) : String.valueOf(month + 1);
                        String dayString = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);

                        birthDateEditText.setText(year + "." + monthString + "." + dayString);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userEmail = preferences.getString("userEmail", "");
        String password = preferences.getString("password", "");

        userEmailEditText.setText(userEmail);
        passwordEditText.setText(password);
        passswordConfirmEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {
        String userName = userNameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passswordConfirmEditText.getText().toString();
        String birthDate = birthDateEditText.getText().toString();

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(RegisterActivity.this, "A két jelszó nem egyezik!", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "A két jelszó nem egyezik!");
        } else if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "A jelszónak legalább 6 karakter hosszúnak kell lennie!", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "A jelszónak legalább 6 karakter hosszúnak kell lennie!");
        } else {
            Log.i(LOG_TAG, "Regisztrált: " + userName + ", e-mail: " + email + ", születési dátum: " + birthDate);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "User created succesfully");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Display name beállítása
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startProfile();
                                            } else {
                                                Log.e(LOG_TAG, "Error updating user profile: " + task.getException().getMessage());
                                            }
                                        }
                                    });
                        } else {
                            Log.d(LOG_TAG, "User wasn't created successfully");
                            Toast.makeText(RegisterActivity.this, "User wasn't created successfully: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });
        }
    }


    public void cancel(View view) {
        finish();
    }

    private void startProfile() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
}