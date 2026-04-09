package com.hakimi.model;

public class HealthArchiveEntry {
    private String date;
    private int mentalState;
    private int bodyPain;
    private int sleepQuality;
    private int dietRegularity;
    private int stressIndex;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMentalState() {
        return mentalState;
    }

    public void setMentalState(int mentalState) {
        this.mentalState = mentalState;
    }

    public int getBodyPain() {
        return bodyPain;
    }

    public void setBodyPain(int bodyPain) {
        this.bodyPain = bodyPain;
    }

    public int getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(int sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public int getDietRegularity() {
        return dietRegularity;
    }

    public void setDietRegularity(int dietRegularity) {
        this.dietRegularity = dietRegularity;
    }

    public int getStressIndex() {
        return stressIndex;
    }

    public void setStressIndex(int stressIndex) {
        this.stressIndex = stressIndex;
    }

    public float getAverageScore() {
        return (mentalState + bodyPain + sleepQuality + dietRegularity + stressIndex) / 5f;
    }
}
