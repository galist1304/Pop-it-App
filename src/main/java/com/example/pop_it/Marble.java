package com.example.pop_it;

import android.view.View;
import android.widget.Button;

public class Marble extends Location{
    private Boolean isInvis=false, isClicked;
    private String btnId;
    private Button gula;

    public Marble(int piles, int marbleNum,Button btn){
        super(piles, marbleNum);
        this.gula=btn;
        this.isClicked=false;
    }
    public Boolean getClicked() {
        return isClicked;
    }

    public void setClicked(Boolean clicked) {
        isClicked = clicked;
    }

    public Button getGula() {
        return gula;
    }

    public void setGula(Button gula) {
        this.gula = gula;
    }

    public String getBtnId() {
        return btnId;
    }

    public void setBtnId(String btnId) {
        this.btnId = btnId;
    }

    public Boolean getIsInvis() {
        return isInvis;
    }

    public void setIsInvis(Boolean isInv) {
        this.isInvis = isInv;
    }


    public static void Visibale(Marble marble)
    {
        if (marble.getIsInvis()==true){
            marble.gula.setVisibility(View.INVISIBLE);
        }
        else
            {
            marble.gula.setVisibility(View.VISIBLE);
        }
    }
}
