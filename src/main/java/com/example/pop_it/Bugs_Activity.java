package com.example.pop_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Bugs_Activity extends AppCompatActivity implements View.OnClickListener{
    LinearLayout layout;
    Button btnSendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugs_);

        btnSendEmail=findViewById(R.id.btnEmail);
        btnSendEmail.setOnClickListener(this);

        layout= findViewById(R.id.bugsLay);
    }

    @Override
    public void onClick(View view) {
        String [] emails = {"galist1304@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bugs Reporting");
        intent.putExtra(Intent.EXTRA_TEXT, "Type Here");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }
}