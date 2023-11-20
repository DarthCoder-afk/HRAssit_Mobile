package com.example.bottomnavbar;

import  android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {

    EditText loginusername, loginpassword;
    Button loginBtn;

    FirebaseFirestore db;

    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginusername = findViewById(R.id.emailInput);
        loginpassword = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.LoginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateusername() && validatepassword()) {
                    String email = loginusername.getText().toString().trim();
                    String password = loginpassword.getText().toString().trim();
                    signInWithFirebaseAuth(email, password);
                }
            }
        });
    }

    private void signInWithFirebaseAuth(String email, String password) {
        fauth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            String uid = fauth.getCurrentUser().getUid();
                            queryUserDetails(uid);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void queryUserDetails(String uid) {
        // Query Firestore to get user details based on UID
        db.collection("User Account")
                .whereEqualTo("userID", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot userDocument = querySnapshot.getDocuments().get(0);
                                String userLevel = userDocument.getString("UserLevel");

                                // Check UserLevel to determine the role
                                if ("Employee".equals(userLevel)) {
                                    Toast.makeText(Login.this, "Login successful (Employee).", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    Log.d("UID", "Employee UID: " + uid);
                                } else if ("Admin".equals(userLevel)) {
                                    Toast.makeText(Login.this, "Login successful (Admin).", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, HRAdminMainActivity.class));
                                    Log.d("UID", "LOGIN UID: " + uid);
                                } else {
                                    Toast.makeText(Login.this, "Unknown UserLevel.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Login.this, "Invalid user details.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Login.this, "Error querying Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public Boolean validateusername() {
        String val = loginusername.getText().toString().trim();
        if (val.isEmpty()) {
            loginusername.setError("Username cannot be empty");
            return false;
        } else {
            loginusername.setError(null);
            return true;
        }
    }

    public Boolean validatepassword() {
        String val = loginpassword.getText().toString().trim();
        if (val.isEmpty()) {
            loginpassword.setError("Password cannot be empty");
            return false;
        } else {
            loginpassword.setError(null);
            return true;
        }
    }
}
