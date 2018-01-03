package com.mad.tennisscoringprototype.model;

import com.google.firebase.database.Exclude;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rowdo on 13/09/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The Match class contains the score, status, tiebreak, sets, completed and both the player objects
 *          involved with the match.
 */
public class Match {

    private MatchPlayer mPlayer1;
    private MatchPlayer mPlayer2;
    private String mStatus;
    private Integer mSets;
    private Integer mCompletedSets;
    private Boolean mIsTieBreak;
    private Boolean mIsCompleted;

    /**
     * Empty Constructor.
     */
    public Match() {

    }

    /**
     * Constructor which sets the two MatchPlayers, the number of sets, the status, the completed
     * sets, the tiebreak, and the completetion state.
     *
     * @param matchPlayer1 The current user.
     * @param matchPlayer2 THe selected player.
     * @param sets         The number of sets selected from the spinner.
     */
    public Match(MatchPlayer matchPlayer1, MatchPlayer matchPlayer2, int sets) {
        this.mPlayer1 = matchPlayer1;
        this.mPlayer2 = matchPlayer2;
        this.mSets = sets;

        mStatus = "Incomplete";
        mCompletedSets = 0;
        mIsTieBreak = false;
        mIsCompleted = false;
    }

    /**
     * Method that scores either the current score or the tiebreak score.
     *
     * @param winner Player who won the point.
     * @param loser  Player who lost the point.
     */
    public void score(MatchPlayer winner, MatchPlayer loser) {
        if (mIsCompleted) {
            // Match is completed
            return;
        }
        if (mIsTieBreak) {
            // Match is in a tiebreak
            scoreTiebreak(winner);
        } else {
            // Match is incomplete
            scorePoint(winner, loser);
            scoreGame(winner, loser);
        }
        // Score the games in each set
        scoreSets(winner, loser);

        // Set both players back to first serve
        mPlayer1.setIsSecondServe(false);
        mPlayer2.setIsSecondServe(false);
    }

    /**
     * Scores the game based on the current scores for both players.
     *
     * @param winner Player that won the point.
     * @param loser  Player that lost the point.
     */
    private void scorePoint(MatchPlayer winner, MatchPlayer loser) {
        if (loser.getScore() == Scores.Advantage) {
            // Score is deuce
            winner.makeDeuce();
            loser.makeDeuce();
        } else if (mPlayer1.getScore() == Scores.Forty && mPlayer2.getScore() == Scores.Forty) {
            // Score is Adv
            winner.makeAdvantage();
        } else if (winner.getScore() == Scores.Advantage || winner.getScore() == Scores.Forty) {
            // Game is won
            winner.winsGame();
        } else {
            // Score is less than 40
            winner.scorePoint();
        }
    }

    /**
     * Scores the number of games in each set.
     *
     * @param winner The player who won the point.
     * @param loser  The player who lost the point.
     */
    private void scoreGame(MatchPlayer winner, MatchPlayer loser) {
        if (winner.getScore() == Scores.Game) {
            winner.scoreGame(mCompletedSets);

            // Sets score for both players to zero
            mPlayer1.setScore(Scores.Zero);
            mPlayer2.setScore(Scores.Zero);

            // Switch player to serve
            mPlayer1.setIsServe(!mPlayer1.getIsServe());
            mPlayer2.setIsServe(!mPlayer2.getIsServe());
        }
    }

    /**
     * Scores the number of sets one.
     *
     * @param winner The player who won the point.
     * @param loser  The player who lost the point.
     */
    private void scoreSets(MatchPlayer winner, MatchPlayer loser) {
        if (mIsTieBreak) {
            if (winner.getTiebreak() >= 7 && loser.getTiebreak() + 2 <= winner.getTiebreak()) {
                // Update the completed sets
                winner.scoreGame(mCompletedSets);
                winner.setSets(winner.getSets() + 1);
                mCompletedSets++;

                // Reset tiebreak to 0
                mPlayer1.setTiebreak(0);
                mPlayer2.setTiebreak(0);

                // Check if match is complete
                scoreMatch(winner);
            }
        } else if ((Character.getNumericValue(winner.getGames().charAt(mCompletedSets)) >= 6 &&
                Character.getNumericValue(loser.getGames().charAt(mCompletedSets)) + 2 <= Character.getNumericValue(winner.getGames().charAt(mCompletedSets)))) {
            // Update the completed sets
            winner.setSets(winner.getSets() + 1);
            mCompletedSets++;

            // Check if match is complete
            scoreMatch(winner);
        } else if (Character.getNumericValue(winner.getGames().charAt(mCompletedSets)) == 6 && Character.getNumericValue(loser.getGames().charAt(mCompletedSets)) == 6) {
            // Match is 6 games all
            mIsTieBreak = true;
        }
    }

    /**
     * Scores the tiebreak.
     *
     * @param winner Player who won the point.
     */
    private void scoreTiebreak(MatchPlayer winner) {
        // Increment winner tiebreak score
        winner.setTiebreak(winner.getTiebreak() + 1);

        // Set player to serve based on whether tiebreak score is odd number
        int overallTiebreak = mPlayer1.getTiebreak() + mPlayer2.getTiebreak();
        if (!((overallTiebreak % 2) == 0)) {
            // Switch player to serve
            mPlayer1.setIsServe(!mPlayer1.getIsServe());
            mPlayer2.setIsServe(!mPlayer2.getIsServe());
        }
    }

    /**
     * Check if the match is completed or add another set if not.
     *
     * @param winner The winner of the point.
     */
    private void scoreMatch(MatchPlayer winner) {
        if (mSets == mCompletedSets || winner.getSets() > mSets / 2) {
            // Winner has won the game
            mIsCompleted = true;
            winner.setIsWinner(true);
            mPlayer1.setIsServe(false);
            mPlayer2.setIsServe(false);
        } else {
            // Add zero to games for both players for new sets
            mPlayer1.setGames(mPlayer1.getGames() + "0");
            mPlayer2.setGames(mPlayer2.getGames() + "0");

            // Set isTiebreak to false
            mIsTieBreak = false;
        }
    }

    /**
     * Maps the Match object to be uploaded to Firebase.
     *
     * @return
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mPlayer1", mPlayer1);
        result.put("mPlayer2", mPlayer2);
        result.put("mSets", mSets);
        result.put("mStatus", mStatus);
        result.put("mCompletedSets", mCompletedSets);
        result.put("mIsTiebreak", mIsTieBreak);
        result.put("mIsCompleted", mIsCompleted);

        return result;
    }

    public MatchPlayer getPlayer1() {
        return mPlayer1;
    }

    public void setPlayer1(MatchPlayer player1) {
        this.mPlayer1 = player1;
    }

    public MatchPlayer getPlayer2() {
        return mPlayer2;
    }

    public void setPlayer2(MatchPlayer player2) {
        this.mPlayer2 = player2;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        this.mStatus = status;
    }

    public Integer getSets() {
        return mSets;
    }

    public void setSets(Integer sets) {
        this.mSets = sets;
    }

    public Integer getCompletedSets() {
        return mCompletedSets;
    }

    public void setCompletedSets(Integer completedSets) {
        this.mCompletedSets = completedSets;
    }

    public Boolean getIsTieBreak() {
        return mIsTieBreak;
    }

    public void setIsTieBreak(Boolean isTieBreak) {
        this.mIsTieBreak = isTieBreak;
    }

    public Boolean getIsCompleted() {
        return mIsCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.mIsCompleted = isCompleted;
    }
}
