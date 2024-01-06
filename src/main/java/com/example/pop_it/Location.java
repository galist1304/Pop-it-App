package com.example.pop_it;

public class Location {
    private int piles;
    private int marblesNum;

    public Location (int piles, int marblesNum){
        this.piles=piles;
        this.marblesNum=marblesNum;
    }

    public int getPiles() {
        return piles;
    }

    public void setPiles(int piles) {
        this.piles = piles;
    }

    public int getMarblesNum() {
        return marblesNum;
    }

    public void setMarblesNum(int marblesNum) {
        this.marblesNum = marblesNum;
    }
}
