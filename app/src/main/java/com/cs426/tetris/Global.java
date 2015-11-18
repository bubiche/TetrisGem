package com.cs426.tetris;

public class Global {
    private static Global instance;
    private int highScoreNormal;
    private int highScoreTime;

    private Global() {};

    public int getHighScoreNormal() {
        return highScoreNormal;
    }

    public void setHighScoreNormal(int highScoreNormal) {
        this.highScoreNormal = highScoreNormal;
    }

    public int getHighScoreTime() {
        return highScoreTime;
    }

    public void setHighScoreTime(int highScoreTime) {
        this.highScoreTime = highScoreTime;
    }

    public static synchronized Global getInstance(){
        if(instance == null){
            instance = new Global();
        }
        return instance;
    }
}
