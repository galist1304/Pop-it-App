package com.example.pop_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Random;


public class Game_Activity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth firebaseAuth;
    int PilesNum, GulotNum, score,score2;
    Marble lastMarble;
    Marble[][] marbles = new Marble[10][6];
    Button[][] buttons = new Button[10][6];
    boolean turn=true;
    Button btnNextTurn, btnPlayAgain, btnScore, btnLogOut, btnRefresh;
    Dialog popUp, statsScreen;
    TextView tvTurn, tvWinner, tvScore;
    int count=0,count2=0,count3,count4=0,count5=0;
    FirebaseDatabase storage;
    DatabaseReference databaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_);

        storage = FirebaseDatabase.getInstance();
        databaseRef = storage.getReference("Details");
        firebaseAuth = FirebaseAuth.getInstance();

        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(this);
        btnNextTurn = findViewById(R.id.btnNextTurn);
        btnNextTurn.setOnClickListener(this);
        tvTurn = findViewById(R.id.tvTurn);

        //מחבר כל כפתור במערך לid המתאים לו לפי מיקומו על הלוח
        for (int i = 1; i < buttons.length; i++) {
            for (int j = 1; j < buttons[i].length; j++) {
                String btnName = "btn" + (i) + (j);
                int resID = getResources().getIdentifier(btnName, "id", getPackageName());
                buttons[i][j] = (findViewById(resID));
                buttons[i][j].setOnClickListener(this);
            }
        }
        //נותן לכל גולה במערך את מיקומה בלוח וכפתור מתאים ביחס למיקומה בלוח
        for (int row = 1; row < marbles.length; row++) {
            for (int col = 1; col < marbles[row].length; col++) {
                marbles[row][col] = new Marble(col, row, buttons[row][col]);
            }
        }

        Intent intent = getIntent();
        String pilesNum = intent.getExtras().getString("pilesNum");
        String gulotNum = intent.getExtras().getString("gulotNum");
        score = Integer.valueOf(intent.getStringExtra("score"));
        Toast.makeText(this, " " + score, Toast.LENGTH_SHORT).show();
        PilesNum = Integer.parseInt(pilesNum);
        GulotNum = Integer.parseInt(gulotNum);


        //הופך את כל הגולות שלא צריכות להיות על הלוח לבלתי נראות
        for (int row = 1; row < marbles.length; row++) {
            for (int col = 1; col < marbles[row].length; col++) {
                if (marbles[row][col].getPiles() > PilesNum) {
                    marbles[row][col].setIsInvis(true);
                    Marble.Visibale(marbles[row][col]);
                }
            }
        }

        //מגריל לכל ערימה את מספר הגולות שיהיו בה והופך את כל הגולות שלא צריכות להיות על הלוח לבלתי נראות
        int colNum = 1;
        for (int col = 1; col < marbles[colNum].length && colNum < 6; col++) {
            int rndGulot = random(GulotNum);
            while (rndGulot < 2) {
                rndGulot = random(GulotNum);
            }
            colNum++;
            for (int row = 1; row < marbles.length; row++) {
                if (marbles[row][col].getMarblesNum() <= 9 - rndGulot) {
                    marbles[row][col].setIsInvis(true);
                    Marble.Visibale(marbles[row][col]);
                }
            }
        }
        count4=numOfUnclickedMarbles(marbles); //מקבל בתחילת המשחק את מספר הגולות הלא לחוצות בלוח
    }


    //מעדכן את הניקוד בפיירבייס כל סוף משחק
    public void UploadScore(int score){
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference=databaseRef.child("Player/"+uId+"/").child("Score");
        Task<Void> voidTask = databaseReference.setValue(score).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Game_Activity.this,"Your score is saved",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Game_Activity.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //שולף מהפיירבייס את הניקוד המעודכן של המשתמש
    public void readData(String Id){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Details");
        databaseReference.child("Player").child(Id).child("Score").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Toast.makeText(Game_Activity.this, "Read data Successfully", Toast.LENGTH_SHORT).show();
                        DataSnapshot dataSnapshot = task.getResult();
                        score2 = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                        Toast.makeText(Game_Activity.this, "" + score2, Toast.LENGTH_SHORT).show();
                    }

                    else {
                        Toast.makeText(Game_Activity.this,"User does not exist",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(Game_Activity.this,"Data reading failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //מחזיר מספר רנדומלי בין 0 ל - מספר הגולות המקסימלי שבחר המשתמש
    public int random(int GulotNum) {
        Random rnd = new Random();
        int num = rnd.nextInt(GulotNum + 1);
        return num;
    }

    public void winDialog(){
        String id= firebaseAuth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Details").child("Player").child(id).child("Score").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    DataSnapshot dataSnapshot=task.getResult();
                    if(dataSnapshot.getValue()==null){ //אם אין לשחקן ניקוד
                        User user=new User(0,id); //יוצר עצם משתמש עם ניקוד 0 וה - id
                        score=user.getScore();
                    }
                    else {
                        User user = new User(Integer.parseInt(String.valueOf(dataSnapshot.getValue())), id); //אם יש לו ניקוד, יוצר עצם משתמש עם הניקוד וה - id
                        if (user != null) {
                            score = user.getScore();
                        } else {
                            Log.d("exception1", "user is null" + task.getResult().toString());

                        }
                    }
                }
            }
        });

        //יוצר דיאלוג כאשר יש מנצח
        popUp=new Dialog(this);
        popUp.setContentView(R.layout.pop_up);
        popUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUp.setCancelable(false);
        popUp.setCanceledOnTouchOutside(false);
        btnPlayAgain=popUp.findViewById(R.id.btnPlayAgain);
        btnLogOut=popUp.findViewById(R.id.btnLogOut);
        btnScore=popUp.findViewById(R.id.btnScore);
        tvWinner=popUp.findViewById(R.id.tvWinner);
        if(turn==false) { //אם המשתמש ניצח
            tvWinner.setText("You are the winner :)");
            score++;
        }
        else { //אם המשתמש הפסיד
            tvWinner.setText("Player 2 is the winner :(");
            score--;
        }

        popUp.show();
        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Game_Activity.this, MainActivity.class);
                intent.putExtra("score",String.valueOf(score2)); //מעביר את הניקוד המעודכן של המשתמש
                intent.putExtra("count",String.valueOf(2)); //מעביר מספר גדול מ - 0 (לא משנה איזה), כדי לדעת שזו לא פעם ראשונה שהוא משחק
                startActivity(intent);
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser()!=null) {
                    firebaseAuth.signOut();
                }
                Intent intent=new Intent(Game_Activity.this,OpeningScreen.class);
                startActivity(intent);
            }
        });
        btnScore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                statsDialog();
            }
        });

        UploadScore(score);
        readData(id);
    }

    //יוצר את הדיאלוג שמראה את הניקוד המעודכן של השחקן
    private void statsDialog() {
        statsScreen=new Dialog(this);
        statsScreen.setContentView(R.layout.stats_screen);
        statsScreen.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tvScore = statsScreen.findViewById(R.id.tvScore);
        tvScore.setText("" + score2);
        statsScreen.show();
    }


    //מחזיר את מספר הגולות הלא לחוצות בלוח
    public int numOfUnclickedMarbles(Marble [][] marbles){
        count2=0;
        for (int col = 1; col < marbles[col].length; col++) {
            for (int row = 1; row < marbles.length; row++) {
                if(marbles[row][col].getClicked() == false && marbles[row][col].getIsInvis()==false)
                    count2++;
            }
        }
        return count2;
    }


    @Override
    public void onClick(View view) {


        for (int col = 1; col < marbles[col].length ; col++) {
            for (int row = 1; row < marbles.length; row++) {
                if(marbles[row][col].getGula() == view && marbles[row][col].getIsInvis()==false) {
                    count3=0; //מאפס אותו בכל לחיצה
                    if (marbles[row][col].getClicked() == true) {
                        marbles[row][col].getGula().setBackground(ContextCompat.getDrawable(this, R.drawable.button_game));
                        marbles[row][col].setClicked(false);
                    }
                    else if (marbles[row][col].getClicked() == false) {
                        marbles[row][col].getGula().setBackground(ContextCompat.getDrawable(this, R.drawable.clicked_button));
                        marbles[row][col].setClicked(true);
                        for(int c = 1; c < marbles[c].length ; c++){
                            for (int r = 1; r < marbles.length; r++){
                                if(c!=col)
                                    marbles[r][c].getGula().setEnabled(false); //לא נותן ללחוץ על גולות שלא נמצאות באותה ערימה של הגולה הראשונה שלחץ עליה בתחילת התור
                            }
                        }
                        lastMarble=marbles[row][col];
                    }
                    count3=numOfUnclickedMarbles(marbles); //מקבל את מספר הגולות הלא לחוצות בלוח
                    if(count3==0){ //אם כל הגולות בלוח לחוצות
                        lastMarble.getGula().setBackground(ContextCompat.getDrawable(this, R.drawable.button_game));
                        lastMarble.setClicked(false);
                        Toast.makeText(this,"You have to leave at least one marble unclicked!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }


        if(btnNextTurn==view){
            count5=numOfUnclickedMarbles(marbles); //מקבל את מספר הגולות הלא לחוצות בלוח
            count=0;
            if(count4==count5) { //אם לא שינה את הלוח, כלומר מספר הגולות הלא לחוצות בסוף תורו לא השתנה
                Toast.makeText(this, "You have to click on at least one marble!", Toast.LENGTH_SHORT).show();
            }
            else { //אם כן שינה את הלוח בסוף תורו
                if (turn) {
                    tvTurn.setText("Player 2");
                    turn = false;
                } else {
                    tvTurn.setText("Your Turn");
                    turn = true;
                }
            }
            for (int col = 1; col < marbles[col].length ; col++) {
                for (int row = 1; row < marbles.length; row++) {
                    if (marbles[row][col].getClicked() == true) { //אם הגולה נלחצה בתור הזה
                        marbles[row][col].getGula().setEnabled(false); //לא יהיה ניתן ללחוץ עליה שוב
                    } else { //אם הגולה לא נלחצה בתור הזה
                        marbles[row][col].getGula().setEnabled(true); //ניתן יהיה ללחוץ עליה
                    }
                    if (marbles[row][col].getClicked() == false && marbles[row][col].getIsInvis() == false) { //אם הגולה לא לחוצה
                        count++;
                    }
                }
            }
            if (count == 1) { //אם נשארה רק גולה אחת לא לחוצה בלוח
                winDialog();
            }

                count4=count5; //מקבל את מספר הגולות הלא לחוצות בתחילת התור
        }





        if(btnRefresh==view){
            for (int row = 1; row < marbles.length; row++) {
                for (int col = 1; col < marbles[row].length; col++) {
                    marbles[row][col].getGula().setBackground(ContextCompat.getDrawable(this, R.drawable.button_game));
                    marbles[row][col].setClicked(false);
                    marbles[row][col].getGula().setEnabled(true);
                }
            }
            turn=true;
            tvTurn.setText("Your Turn");
            count4=numOfUnclickedMarbles(marbles); //מקבל את מספר הגולות הלא לחוצות בתחילת המשחק
        }
    }
}

