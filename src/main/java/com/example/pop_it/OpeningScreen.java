package com.example.pop_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class OpeningScreen extends AppCompatActivity implements View.OnClickListener {

    Button btnLogIn, btnSignIn, btnReport;
    EditText etEmail, etPass;
    FirebaseAuth firebaseAuth;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen);

        btnLogIn=findViewById(R.id.btnLogIn);
        btnSignIn=findViewById(R.id.btnSignIn);
        btnReport=findViewById(R.id.btnReport);
        btnLogIn.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnReport.setOnClickListener(this);

        etEmail=findViewById(R.id.etEmail);
        etPass=findViewById(R.id.etPass);
        firebaseAuth = FirebaseAuth.getInstance();

        layout=findViewById(R.id.OpeningScreenLay);
        AnimationDrawable animationDrawable= (AnimationDrawable)layout.getBackground();
        animationDrawable.setEnterFadeDuration(1500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

    }


    @Override
    public void onClick(View view) {
        if(btnLogIn==view){
                if (!etEmail.getText().toString().endsWith("@gmail.com") || etPass.getText().toString().length() < 6) {
                    Toast.makeText(OpeningScreen.this, "Email has to end with: @gmail.com, and password length has to be more than 5 numbers", Toast.LENGTH_LONG).show();
                }
                else if (etEmail.getText().toString().endsWith("@gmail.com")) {
                    firebaseAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPass.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(OpeningScreen.this, "auth_success", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(OpeningScreen.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                         else {
                                            Log.d("Exception1", task.getException().getMessage());
                                            Toast.makeText(OpeningScreen.this, "auth_failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            });
                }
        }
        else if(btnReport==view){
            Intent intent=new Intent(this,Bugs_Activity.class);
            startActivity(intent);
        }
        else if(btnSignIn==view){
            if (!etEmail.getText().toString().endsWith("@gmail.com") || etPass.getText().toString().length() < 6) {
                Toast.makeText(OpeningScreen.this, "Email has to end with: @gmail.com, and password length has to be more than 5 numbers", Toast.LENGTH_LONG).show();
            }
            else if (etEmail.getText().toString().endsWith("@gmail.com")) {
                firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPass.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(OpeningScreen.this, "auth_success", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(OpeningScreen.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.d("Exception1", task.getException().getMessage());
                                    Toast.makeText(OpeningScreen.this, "auth_failed", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        }
    }
}

