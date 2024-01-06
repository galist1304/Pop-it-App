package com.example.pop_it;

public class User {
    private int score;
    private String id;

    public User(int score,String id){
        this.score=score;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
