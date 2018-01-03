package com.mad.tennisscoringprototype.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.tennisscoringprototype.R;
import com.mad.tennisscoringprototype.model.Match;
import com.mad.tennisscoringprototype.model.MatchPlayer;
import com.mad.tennisscoringprototype.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rowdo on 13/09/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The MatchActivity gets all the match information based on the selected matchId and
 *          displays it all by setting the text of TextViews.
 *          <p>
 *          It displays the player names, match status, current score, and overall game scores using
 *          a series of TextViews set from the current Match object.
 *          <p>
 *          It allows the user to increase the score for either player using a series of four buttons
 *          for each player to either score a winner, error, ace or fault.
 *          <p>
 *          When the current game is completed, the player ImageView will be changed to reflect the current
 *          server by showing a Ball image.
 *          <p>
 *          When the match has been completed, the scoring Buttons will disappear and the player ImageViews
 *          will be changed to reflect the winner by showing a Trophy image.
 */
public class MatchActivity extends AppCompatActivity implements View.OnClickListener {

    private MatchPlayer mPlayer1;
    private MatchPlayer mPlayer2;
    private User mUser1;
    private User mUser2;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Match mCurrentMatch;
    private ImageView mPlayer1Iv;
    private ImageView mPlayer2Iv;
    private Button mPlayer1AceBtn;
    private Button mPlayer1FaultBtn;
    private Button mPlayer1WinnerBtn;
    private Button mPlayer1ErrorBtn;
    private Button mPlayer2AceBtn;
    private Button mPlayer2FaultBtn;
    private Button mPlayer2WinnerBtn;
    private Button mPlayer2ErrorBtn;
    private String mMatchId;

    private static String MATCH_ID_EXTRA = "MATCH_ID_EXTRA";

    /**
     * onCreate method which gets the current Intent and retrieves the ID of the selected match.
     * It gets the current FirebaseAuth and FirebaseDatabase root reference.
     * Calls the displayMatch() method which displays the match information.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // Get FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Get existing intent and matchId from extra
        Intent matchIntent = getIntent();
        mMatchId = matchIntent.getStringExtra(MATCH_ID_EXTRA);

        // Get database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Call method to display all the match information
        displayMatch();
    }

    /**
     * This method gets the current match from the FirebasebaseDatabase and sets a ValueEventListener
     * on it so that any changes that are made to the match object are automatically updated in the
     * database.
     * It sets the player name, status and score TextViews based on the current match object.
     * It creates listeners for the scoring Buttons for each of the users.
     * It changes the ImageViews for each player indicating who is serving.
     */
    private void displayMatch() {
        // Sets a ValueEventListener on the current Match object in the database
        mDatabase.child("matches").child(mAuth.getCurrentUser().getUid()).child(mMatchId).addValueEventListener(new ValueEventListener() {
            /**
             * Gets the Match object and both Players. Sets another ValueEventListener on the
             * users node in the database to retrieve the usernames of both players and sets
             * the name TextViews for both the scoring and score sections of the activity.
             *
             * @param dataSnapshot Contains snapshot of current match node
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get the current match
                mCurrentMatch = dataSnapshot.getValue(Match.class);

                // Get both MatchPlayers
                mPlayer1 = mCurrentMatch.getPlayer1();
                mPlayer2 = mCurrentMatch.getPlayer2();

                // Sets a ValueEventListener for the users node in the database
                mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                    /**
                     * Retrieves both Users based on the Player IDs and sets the relevant names for both
                     * the scoring and score TextViews.
                     * @param dataSnapshot Contains snapshot of the users node
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Get user 1
                        mUser1 = dataSnapshot.child(mPlayer1.getUserId()).getValue(User.class);

                        // Set the Player 1 name TextView from the user1 username
                        TextView player1NameTv = (TextView) findViewById(R.id.player1NameTv);
                        player1NameTv.setText(mUser1.getUsername());

                        // Set the Player 1 name TextView for the scoring section
                        TextView player1Name2Tv = (TextView) findViewById(R.id.player1Name2Tv);
                        player1Name2Tv.setText(mUser1.getUsername());

                        // Get user 2
                        mUser2 = dataSnapshot.child(mPlayer2.getUserId()).getValue(User.class);

                        // Set the Player 2 name TextView from the user2 username
                        TextView player2NameTv = (TextView) findViewById(R.id.player2NameTv);
                        player2NameTv.setText(mUser2.getUsername());

                        // Set the Player 2 name TextView for the scoring section
                        TextView player2Name2Tv = (TextView) findViewById(R.id.player2Name2Tv);
                        player2Name2Tv.setText(mUser2.getUsername());

                        // Sets the current match status using a TextView
                        setStatus();
                    }

                    /**
                     * Sets the TextView for the match status depending on the state of the match.
                     */
                    private void setStatus() {
                        String status;

                        if (mPlayer1.getIsWinner()) {
                            // Player 1 is winner
                            status = getString(R.string.winner_status_tv) + mUser1.getUsername();
                        } else if (mPlayer2.getIsWinner()) {
                            // Player 2 is winner
                            status = getString(R.string.winner_status_tv) + mUser2.getUsername();
                        } else {
                            // Neither player is winner. Match is incomplete
                            status = getString(R.string.incomplete_status_tv);
                        }

                        // Sets the TextView
                        TextView statusTv = (TextView) findViewById(R.id.statusTv);
                        statusTv.setText(status);
                    }

                    /**
                     * onCancelled default method
                     *
                     * @param databaseError Database error
                     */
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Sets the score TextViews
                setScore();

                // Get ball ImageViews
                mPlayer1Iv = (ImageView) findViewById(R.id.player1Iv);
                mPlayer2Iv = (ImageView) findViewById(R.id.player2Iv);

                // Set the OnClickListeners for all Buttons
                setButtonClickListeners();

                // Update Player ImageView to reflect current server
                updateServer();

                // Set trophy for ImageView for player who wins the match
                if (mPlayer1.getIsWinner()) {
                    // Player 1 is winner
                    // Set trophy image for player 1
                    mPlayer1Iv.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.trophy));
                } else if (mPlayer2.getIsWinner()) {
                    // Player 2 is winner
                    // Set trophy image for player 2
                    mPlayer2Iv.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.trophy));
                }
            }

            /**
             * Sets the OnClickListeners for all scoring Buttons
             */
            private void setButtonClickListeners() {
                // Get player1 buttons and set OnClickListeners
                mPlayer1AceBtn = (Button) findViewById(R.id.player1AceBtn);
                mPlayer1AceBtn.setOnClickListener(MatchActivity.this);
                mPlayer1FaultBtn = (Button) findViewById(R.id.player1FaultBtn);
                mPlayer1FaultBtn.setOnClickListener(MatchActivity.this);
                mPlayer1WinnerBtn = (Button) findViewById(R.id.player1WinnerBtn);
                mPlayer1WinnerBtn.setOnClickListener(MatchActivity.this);
                mPlayer1ErrorBtn = (Button) findViewById(R.id.player1ErrorBtn);
                mPlayer1ErrorBtn.setOnClickListener(MatchActivity.this);

                // Get player2 scoring buttons and set OnClickListeners
                mPlayer2AceBtn = (Button) findViewById(R.id.player2AceBtn);
                mPlayer2AceBtn.setOnClickListener(MatchActivity.this);
                mPlayer2FaultBtn = (Button) findViewById(R.id.player2FaultBtn);
                mPlayer2FaultBtn.setOnClickListener(MatchActivity.this);
                mPlayer2WinnerBtn = (Button) findViewById(R.id.player2WinnerBtn);
                mPlayer2WinnerBtn.setOnClickListener(MatchActivity.this);
                mPlayer2ErrorBtn = (Button) findViewById(R.id.player2ErrorBtn);
                mPlayer2ErrorBtn.setOnClickListener(MatchActivity.this);
            }

            /**
             * Sets the scoring TextViews for either the current score or tiebreak score.
             */
            private void setScore() {
                // Set the Player 1 games
                TextView player1GamesTv = (TextView) findViewById(R.id.player1GamesTv);
                player1GamesTv.setText(mPlayer1.getGames());

                // Set the Player 2 games
                TextView player2GamesTv = (TextView) findViewById(R.id.player2GamesTv);
                player2GamesTv.setText(mPlayer2.getGames());

                // Get score TextViews for both players
                TextView player1Score = (TextView) findViewById(R.id.player1ScoreTv);
                TextView player2Score = (TextView) findViewById(R.id.player2ScoreTv);

                if (!mCurrentMatch.getIsTieBreak()) {
                    // Set the Player 1 and player 2 scores
                    player1Score.setText(mPlayer1.getScore().getStringScore());
                    player2Score.setText(mPlayer2.getScore().getStringScore());
                } else {
                    // Set the player 1 and player 2 tiebreak scores
                    player1Score.setText(mPlayer1.getTiebreak().toString());
                    player2Score.setText(mPlayer2.getTiebreak().toString());
                }
            }

            /**
             * onCancelled default method
             *
             * @param databaseError Database error
             */
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Sets the visibility of the serving ball images and the serve scoring Buttons depending
     * on which player is current serving. If the match is complete, hide the LinearLayout
     * holding all the scoring Buttons and hide both serving balls.
     */
    protected void updateServer() {
        if (mPlayer1.getIsServe()) {
            // Player 1 serve
            updateBallImageView(mPlayer1Iv, mPlayer2Iv);
            updateServingButtons(mPlayer1AceBtn, mPlayer1FaultBtn, mPlayer2AceBtn, mPlayer2FaultBtn);

        } else if (mPlayer2.getIsServe()) {
            // Player 2 serve
            updateBallImageView(mPlayer2Iv, mPlayer1Iv);
            updateServingButtons(mPlayer2AceBtn, mPlayer2FaultBtn, mPlayer1AceBtn, mPlayer1FaultBtn);
        } else {
            // Remove serving ball for both players
            mPlayer1Iv.setImageDrawable(null);
            mPlayer2Iv.setImageDrawable(null);

            // Make both ImageView visible
            mPlayer1Iv.setVisibility(View.VISIBLE);
            mPlayer2Iv.setVisibility(View.VISIBLE);

            // Hide all scoring Buttons for both players
            LinearLayout player1ScoringLl = (LinearLayout) findViewById(R.id.player1ScoringLl);
            player1ScoringLl.setVisibility(View.GONE);
            LinearLayout player2ScoringLl = (LinearLayout) findViewById(R.id.player2ScoringLl);
            player2ScoringLl.setVisibility(View.GONE);
        }
    }

    /**
     * Enables or disables the serve scoring buttons depending on who is serving
     *
     * @param serverAceBtn     Server Ace Button
     * @param serverFaultBtn   Server Fault Button
     * @param receiverAceBtn   Receiver Ace Button
     * @param receiverFaultBtn Receiver Fault Button
     */
    private void updateServingButtons(Button serverAceBtn, Button serverFaultBtn, Button receiverAceBtn, Button receiverFaultBtn) {
        // Enable serve buttons for server
        serverAceBtn.setEnabled(true);
        serverFaultBtn.setEnabled(true);

        // Disable serve buttons for receiver
        receiverAceBtn.setEnabled(false);
        receiverFaultBtn.setEnabled(false);
    }

    /**
     * Makes only the server's ball image visible
     *
     * @param server   Server ImageView
     * @param receiver Receiver ImageView
     */
    protected void updateBallImageView(ImageView server, ImageView receiver) {
        receiver.setVisibility(View.INVISIBLE);
        server.setVisibility(View.VISIBLE);
    }

    /**
     * Creates a HashMap for the match for both users and then updates the children so that
     * all the data for the match is up to date for both users.
     *
     * @param matchId The MatchId of the current match
     */
    protected void updateMatch(String matchId) {
        Map<String, Object> matchValues = mCurrentMatch.toMap();

        // Create a new HashMap to store the match for current user and for the selected player
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/matches/" + mPlayer1.getUserId() + "/" + matchId, mCurrentMatch);
        childUpdates.put("/matches/" + mPlayer2.getUserId() + "/" + matchId, mCurrentMatch);

        // Update the Firebase database accordingly
        mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d("MATCH_LOG", databaseError.getMessage().toString());
                }
            }
        });
    }

    /**
     * onStart default method.
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * onClick method which calls methods to update the score depending on which scoring Buttons
     * are pressed. E.g. pressing the Ace Button for player 1 will increase the score for player1
     * by calling the relevant method.
     *
     * @param v The current view. Used to get the ID of the buttons
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == mPlayer1AceBtn.getId() || v.getId() == mPlayer1WinnerBtn.getId()
                || v.getId() == mPlayer2ErrorBtn.getId() || (v.getId() == mPlayer2FaultBtn.getId() && mPlayer2.getIsSecondServe())) {
            // Score point for Player1
            mCurrentMatch.score(mPlayer1, mPlayer2);
            updateMatch(mMatchId);
            resetFaultBtns();
        } else if (v.getId() == mPlayer2AceBtn.getId() || v.getId() == mPlayer2WinnerBtn.getId()
                || v.getId() == mPlayer1ErrorBtn.getId() || (v.getId() == mPlayer1FaultBtn.getId() && mPlayer1.getIsSecondServe())) {
            // Score point for Player2
            mCurrentMatch.score(mPlayer2, mPlayer1);
            updateMatch(mMatchId);
            resetFaultBtns();
        } else if (v.getId() == mPlayer1FaultBtn.getId()) {
            // Player1 serves first fault
            mPlayer1.setIsSecondServe(true);
            mPlayer1FaultBtn.setText(R.string.double_fault_tv);
        } else if (v.getId() == mPlayer2FaultBtn.getId()) {
            // Player2 serves first fault
            mPlayer2.setIsSecondServe(true);
            mPlayer2FaultBtn.setText(R.string.double_fault_tv);
        }
    }

    /**
     * Sets both player Fault Buttons back to "Fault"
     */
    public void resetFaultBtns() {
        mPlayer1FaultBtn.setText(R.string.fault_tv);
        mPlayer2FaultBtn.setText(R.string.fault_tv);
    }
}
