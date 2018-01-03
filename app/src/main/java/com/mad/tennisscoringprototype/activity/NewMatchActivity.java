package com.mad.tennisscoringprototype.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
 *          The NewMatchActivity is started when the user presses the NewMatch FAB button at the bottom
 *          right of the MatchesFragment.
 *          This Activity allows the user to select the number of sets they wish to play from a Spinner,
 *          the player they want to play against by clicking the ChoosePlayer button which launches
 *          the ChoosePlayerActivity and the player to serve through the use of radio buttons.
 *          The Start Match Button allows the user to start a new Match and takes the user to the MatchActivity.
 */
public class NewMatchActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private Spinner mMatchSetsSpr;
    private Button mStartMatchBtn;
    private RadioGroup mPlayerToServeRg;
    private RadioButton mPlayer1ServeRbtn;
    private RadioButton mPlayer2ServeRbtn;
    private Button mSelectPlayer1Btn;
    private TextView mPlayer1NameTv;
    private MatchPlayer mPlayer1;
    private MatchPlayer mPlayer2;
    private Match mCurrentMatch;
    private String mCurrentUserId;
    private String mUserId;
    private Boolean mIsPlayer1Serve;
    private Boolean mIsPlayer2Serve;
    private int mSets;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static final int CHOOSE_PLAYER_RC = 1;
    private static final String TAG = "NEW_MATCH_ACTIVITY";
    private static String USER_ID_EXTRA = "USER_ID_EXTRA";
    private static String MATCH_ID_EXTRA = "MATCH_ID_EXTRA";

    /**
     * onCreate method which sets up the spinner for the sets, the button for selecting the other
     * player, the current user, the database reference and the radiobuttons for the player to serve.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_match);

        mPlayer1NameTv = (TextView) findViewById(R.id.player1NameTv);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        // Get database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mMatchSetsSpr = (Spinner) findViewById(R.id.matchSetsSpr);

        // Get Match and Choose Player Buttons
        mStartMatchBtn = (Button) findViewById(R.id.startMatchBtn);
        mSelectPlayer1Btn = (Button) findViewById(R.id.choosePlayer1Btn);

        // Starts a new ChoosePlayerActivity when the Select Player Button is pressed
        mSelectPlayer1Btn.setOnClickListener(new View.OnClickListener() {
            /**
             * Launches the ChoosePlayerActivity
             *
             * @param v Current view.
             */
            @Override
            public void onClick(View v) {
                Intent choosePlayerIntent = new Intent(NewMatchActivity.this, ChoosePlayerActivity.class);
                startActivityForResult(choosePlayerIntent, CHOOSE_PLAYER_RC);
            }
        });

        // Create new AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(NewMatchActivity.this);

        // Set AlertDialog title
        builder.setTitle(R.string.no_player_selected_ad);

        // Set OnClickListener for AlertDialog
        builder.setPositiveButton(R.string.ok_ad, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Build AlertDialog
        final AlertDialog playerAd = builder.create();

        // Set AlertDialog message
        playerAd.setMessage(getString(R.string.select_player_ad));

        // Starts a new match when the Start Match Button is pressed
        mStartMatchBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * Checks that a player and starts a new Match with the current settings if it is valid.
             * Launches the MatchActivity and passes the MatchId with the Intent extra.
             *
             * @param v Current view.
             */
            @Override
            public void onClick(View v) {
                if (mUserId != null) {
                    // Create a new match and go to MatchActivity
                    newMatch();
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    // Alert user that no player has been selected
                    playerAd.show();
                }
            }
        });

        // Get radio buttons for player to serve
        mPlayer1ServeRbtn = (RadioButton) findViewById(R.id.player1Rbtn);
        mPlayer2ServeRbtn = (RadioButton) findViewById(R.id.player2Rbtn);

        // Set player1 to serve as checked initially
        mPlayer1ServeRbtn.setChecked(true);

        // Set player1 to serve as default
        mIsPlayer1Serve = true;
        mIsPlayer2Serve = false;

        // Set OnCheckedChangeListener for player to serve RadioGroup
        mPlayerToServeRg = (RadioGroup) findViewById(R.id.playerToServeRg);
        mPlayerToServeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.player1Rbtn) {
                    // Player1 to serve
                    mIsPlayer1Serve = true;
                    mIsPlayer2Serve = false;
                } else if (checkedId == R.id.player2Rbtn) {
                    // Player 2 to serve
                    mIsPlayer2Serve = true;
                    mIsPlayer1Serve = false;
                }
            }
        });

        // Create match sets ArrayAdapter
        ArrayAdapter<CharSequence> matchSetsAdapter = ArrayAdapter.createFromResource(this,
                R.array.match_sets, android.R.layout.simple_spinner_dropdown_item);

        // Sets match sets ArrayAdapter
        matchSetsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMatchSetsSpr.setAdapter(matchSetsAdapter);

        // Get value of match sets Spinner
        mMatchSetsSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Sets the number of sets based on what is selected in the Spinner.
             *
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Store the value of the sets spinner
                String sets = parent.getItemAtPosition(position).toString().substring(0, 1);
                mSets = Integer.parseInt(sets);
            }

            /**
             * Default method.
             * @param parent
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Gets the ID of the user selected from the ChoosePlayerActivity and calls a method to set
     * both players usernames in the TextViews.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_PLAYER_RC) {
            if (resultCode == RESULT_OK) {
                // User id of selected user
                mUserId = data.getStringExtra(USER_ID_EXTRA);

                // Gets the user based on the username and sets the username
                setUsernames();
            }
        }
    }

    /**
     * Sets the usernames for both players in the TextViews.
     */
    private void setUsernames() {
        // Get both user objects and set the usernames for the relevant TextViews
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            /**
             * Based on the player ID, gets the user objects from the firEbase database
             * and sets the usernames for the TextViews.
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get the user1 object
                User user1 = dataSnapshot.child(mCurrentUserId).getValue(User.class);

                // Set the user1 username for the serve RadioButton
                RadioButton player1Rbtn = (RadioButton) findViewById(R.id.player1Rbtn);
                player1Rbtn.setText(user1.getUsername());

                // Get the user2 object
                User user2 = dataSnapshot.child(mUserId).getValue(User.class);

                // Set the user2 username for the TextView
                TextView player2NameTv = (TextView) findViewById(R.id.player2NameTv);
                player2NameTv.setText(user2.getUsername());

                // Set the user2 for the serve RadioButton
                RadioButton player2Rbtn = (RadioButton) findViewById(R.id.player2Rbtn);
                player2Rbtn.setText(user2.getUsername());
            }

            /**
             * Default method.
             *
             * @param databaseError
             */
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Creates both the new MatchPlayer objects and then creates the Match object using all the
     * entered information.
     * Then stores the Match in the Firebase databse for both users through a HashMap.
     * Then stores the MatchId in an Intent and starts a new MatchActivity with that Intent.
     */
    private void newMatch() {
        // Create new MatchPlayers based on the current user and the selected user
        mPlayer1 = new MatchPlayer(mCurrentUserId, mIsPlayer1Serve);
        mPlayer2 = new MatchPlayer(mUserId, mIsPlayer2Serve);

        // Create a new match using the two players, the number of sets and the player to serve
        mCurrentMatch = new Match(mPlayer1, mPlayer2, mSets);

        // Database Match push
        DatabaseReference userMatchPush = mDatabase.child("matches").push();

        // Store matchId from the key used in the database push
        String matchId = userMatchPush.getKey();

        // Create a new HashMap to store the match for current user and for the selected player
        Map newMatchMap = new HashMap();
        newMatchMap.put("/matches/" + mCurrentUserId + "/" + matchId, mCurrentMatch);
        newMatchMap.put("/matches/" + mUserId + "/" + matchId, mCurrentMatch);

        // Update the Firebase database accordingly
        mDatabase.updateChildren(newMatchMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d("MATCH_LOG", databaseError.getMessage().toString());
                }
            }
        });

        // Make a new intent for MatchActivity
        Intent matchIntent = new Intent(NewMatchActivity.this, MatchActivity.class);

        // Store matchId
        matchIntent.putExtra(MATCH_ID_EXTRA, matchId);

        // Start new MatchActivity
        startActivity(matchIntent);
        finish();
    }

}
