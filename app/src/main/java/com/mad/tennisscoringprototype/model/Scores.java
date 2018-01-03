package com.mad.tennisscoringprototype.model;

/**
 * Created by rowdo on 30/09/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          Enum class which assigns each string score to a enum variable.
 */
public enum Scores {
    Zero("0"),
    Fifteen("15"),
    Thirty("30"),
    Forty("40"),
    Advantage("Adv"),
    Game("Game");

    /**
     * Constructor.
     *
     * @param score The score
     */
    Scores(String score) {
        this.mScore = score;
    }

    private String mScore;

    public String getStringScore() {
        return mScore;
    }
}
