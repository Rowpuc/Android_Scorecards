package com.mad.tennisscoringprototype.model;

/**
 * Created by rowdo on 13/09/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The MatchPlayer class contains all the information about the player that participates in
 *          a particular match.
 */
public class MatchPlayer {

    private User mPlayer;
    private String mUserId;
    private Boolean mIsServe;
    private Boolean mIsSecondServe;
    private Scores mScore;
    private String mGames;
    private Integer mSets;
    private Integer mTiebreak;
    private Boolean mIsWinner;

    /**
     * Empty constructor.
     */
    public MatchPlayer() {
    }

    /**
     * Constructor that takes the userId and serving status.
     *
     * @param userId  UserId of player.
     * @param isServe Whether or not its the players serve.
     */
    public MatchPlayer(String userId, Boolean isServe) {
        this.mUserId = userId;
        this.mIsServe = isServe;
        this.mScore = Scores.Zero;
        this.mIsWinner = false;
        this.mTiebreak = 0;
        this.mGames = "0";
        this.mSets = 0;
        this.mIsSecondServe = false;
    }

    /**
     * Increments the score based on the current score.
     */
    public void scorePoint() {
        switch (mScore) {
            case Thirty:
                mScore = Scores.Forty;
                break;
            case Fifteen:
                mScore = Scores.Thirty;
                break;
            case Zero:
                mScore = Scores.Fifteen;
                break;
        }
    }

    /**
     * Makes the score deuce.
     */
    public void makeDeuce() {
        mScore = Scores.Forty;
    }

    /**
     * Makes the score Adv.
     */
    public void makeAdvantage() {
        mScore = Scores.Advantage;
    }

    /**
     * Player wins the game.
     */
    public void winsGame() {
        mScore = Scores.Game;
    }

    /**
     * Increments the games won for the particular player.
     *
     * @param completedSets The number of compeleted sets so far.
     */
    public void scoreGame(int completedSets) {
        char[] gamesArray = mGames.toCharArray();
        int games = (int) mGames.charAt(completedSets) + 1;
        gamesArray[completedSets] = (char) games;
        mGames = String.valueOf(gamesArray);
    }

    public void scoreTiebreak() {
        mTiebreak++;
    }

    public User getPlayer() {
        return mPlayer;
    }

    public void setPlayer(User mPlayer) {
        this.mPlayer = mPlayer;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public Boolean getIsServe() {
        return mIsServe;
    }

    public void setIsServe(Boolean isServe) {
        this.mIsServe = isServe;
    }

    public Boolean getIsSecondServe() {
        return mIsSecondServe;
    }

    public void setIsSecondServe(Boolean isSecondServe) {
        this.mIsSecondServe = isSecondServe;
    }

    public Scores getScore() {
        return mScore;
    }

    public void setScore(Scores score) {
        this.mScore = score;
    }

    public String getGames() {
        return mGames;
    }

    public void setGames(String games) {
        this.mGames = games;
    }

    public Integer getSets() {
        return mSets;
    }

    public void setSets(Integer sets) {
        this.mSets = sets;
    }

    public Integer getTiebreak() {
        return mTiebreak;
    }

    public void setTiebreak(Integer tiebreak) {
        this.mTiebreak = tiebreak;
    }

    public Boolean getIsWinner() {
        return mIsWinner;
    }

    public void setIsWinner(Boolean isWinner) {
        this.mIsWinner = isWinner;
    }
}
