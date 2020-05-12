package com.example.dsawyer.maddscore.Objects;

public class CourseStats {
    private String userID;
    private long bestRoundDate;
    private int courseAVG, numRounds, bestRoundScore, holesThrown,
            holeInOnes, eagles, pars, birdies, bogies, doublePlus, eagleAces;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getBestRoundDate() {
        return bestRoundDate;
    }

    public void setBestRoundDate(long bestRoundDate) {
        this.bestRoundDate = bestRoundDate;
    }

    public int getCourseAVG() {
        return courseAVG;
    }

    public void setCourseAVG(int courseAVG) {
        this.courseAVG = courseAVG;
    }

    public int getNumRounds() {
        return numRounds;
    }

    public void setNumRounds(int numRounds) {
        this.numRounds = numRounds;
    }

    public int getBestRoundScore() {
        return bestRoundScore;
    }

    public void setBestRoundScore(int bestRoundScore) {
        this.bestRoundScore = bestRoundScore;
    }

    public int getHolesThrown() {
        return holesThrown;
    }

    public void setHolesThrown(int holesThrown) {
        this.holesThrown = holesThrown;
    }

    public int getHoleInOnes() {
        return holeInOnes;
    }

    public void setHoleInOnes(int holeInOnes) {
        this.holeInOnes = holeInOnes;
    }

    public int getEagles() {
        return eagles;
    }

    public void setEagles(int eagles) {
        this.eagles = eagles;
    }

    public int getPars() {
        return pars;
    }

    public void setPars(int pars) {
        this.pars = pars;
    }

    public int getBirdies() {
        return birdies;
    }

    public void setBirdies(int birdies) {
        this.birdies = birdies;
    }

    public int getBogies() {
        return bogies;
    }

    public void setBogies(int bogies) {
        this.bogies = bogies;
    }

    public int getDoublePlus() {
        return doublePlus;
    }

    public void setDoublePlus(int doublePlus) {
        this.doublePlus = doublePlus;
    }

    public int getEagleAces() {
        return eagleAces;
    }

    public void setEagleAces(int eagleAces) {
        this.eagleAces = eagleAces;
    }
}
