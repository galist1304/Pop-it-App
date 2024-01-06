package com.example.pop_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    Button btnStart;
    SeekBar sbPiles, sbNumGulot;
    TextView tvPiles, tvGulot;
    int Score;
    int count=0;
    FirebaseAuth firebaseAuth;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPiles=findViewById(R.id.tvPiles);
        tvGulot=findViewById(R.id.tvGulot);
        btnStart=findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
        sbPiles=findViewById(R.id.sbPiles);
        sbNumGulot=findViewById(R.id.sbNumGulot);
        sbPiles.setOnSeekBarChangeListener(this);
        sbNumGulot.setOnSeekBarChangeListener(this);
        sbPiles.setMax(3);
        sbNumGulot.setMax(6);
        sbPiles.setProgress(0);
        sbNumGulot.setProgress(0);

        layout=findViewById(R.id.mainLay);
        AnimationDrawable animationDrawable= (AnimationDrawable)layout.getBackground();
        animationDrawable.setEnterFadeDuration(1500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        try {
            Intent intent=getIntent();
            count=Integer.valueOf(intent.getStringExtra("count"));
        }
        catch (Exception e){

        }
        firebaseAuth=FirebaseAuth.getInstance();
        if(count==0) { //אם זה משחק ראשון של המשתמש
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Details");
            databaseReference.child("Player").child(firebaseAuth.getCurrentUser().getUid()).child("Score").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    count++;
                    if (task.getResult().exists()) {
                        Score=0;
                        Toast.makeText(MainActivity.this, "" + Score, Toast.LENGTH_SHORT).show();

                    } else {

                    }

                }
            });
        }
        else { //אם זה לא משחק ראשון של המשתמש
            try {
                Intent intent = getIntent();
                Score = Integer.valueOf(intent.getStringExtra("score")); //מקבל את הניקוד המעודכן שלו
                Toast.makeText(this, "" + Score, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if(id==R.id.menuLogOut){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Log Out");
            builder.setMessage("Are you sure you want to log out?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new HandleAlertDialogListener());
            builder.setNegativeButton("No", new HandleAlertDialogListener());
            AlertDialog dialog=builder.create();
            dialog.show();
        }
        else if(id==R.id.menuBack){
            Intent intent=new Intent(MainActivity.this, OpeningScreen.class);
            startActivity(intent);
        }
        return true;
    }

    public  class  HandleAlertDialogListener implements DialogInterface.OnClickListener
    {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==-1){ //אם לחץ על כן
                if(firebaseAuth.getCurrentUser()!=null) {
                    firebaseAuth.signOut();
                }
                Intent intent =new Intent(MainActivity.this, OpeningScreen.class);
                startActivity(intent);
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (btnStart==view) {
            Intent intent = new Intent(this, Game_Activity.class);
            intent.putExtra("pilesNum", tvPiles.getText().toString());
            intent.putExtra("gulotNum", tvGulot.getText().toString());
            intent.putExtra("score",String.valueOf(Score));
            startActivity(intent);
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(seekBar==sbPiles) {
            tvPiles.setText(String.valueOf(i));
            if (i<4){
                tvPiles.setText(String.valueOf(i+2));
            }
        }
        else if (seekBar==sbNumGulot){
            tvGulot.setText(String.valueOf(i));
            if (i<7){
                tvGulot.setText(String.valueOf(i+3));
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}