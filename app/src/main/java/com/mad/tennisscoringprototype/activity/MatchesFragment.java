package com.mad.tennisscoringprototype.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.tennisscoringprototype.R;
import com.mad.tennisscoringprototype.adapter.MatchAdapter;
import com.mad.tennisscoringprototype.model.Match;
import com.mad.tennisscoringprototype.model.MatchPlayer;
import com.mad.tennisscoringprototype.model.User;

/**
 * Created by rowdo on 14/10/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The MatchesFragment is the default Fragment which is inflated in the MainActivity when the user
 *          is first logged into the app.
 *          It shows the list of all matches the user has played in and created through a RecyclerView
 *          which gets the data from the matches node for the current user.
 *          The MatchAdapter is used to populate the itemview with the names of both users, the score,
 *          status and current server for the match using TextViews and ItemViews.
 *          The FAB in bottom right is used to create a new match.
 */
public class MatchesFragment extends Fragment {

    private DatabaseReference mDatabase;
    private DatabaseReference mMatchesDatabase;
    private FirebaseAuth mAuth;
    private User mCurrentUser;
    private User mUser1;
    private User mUser2;
    private String mStatus;
    private RecyclerView mMatchesRv;

    private static String MATCH_ID_EXTRA = "MATCH_ID_EXTRA";

    /**
     * onCreateView is used to setup the current user, the FAB, match database reference and the
     * matches RecyclerView.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Get fragment view
        View rootView = inflater.inflate(R.layout.fragment_matches, container, false);

        // Set Options menu
        setHasOptionsMenu(true);

        // get FAB and create an OnClickListener for it
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * Takes the user to the NewMatchActivity to create a new match.
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                // Start new match
                startActivity(new Intent(getActivity(), NewMatchActivity.class));
            }
        });

        // Get root Firebase databse reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Store current user from FirebaseUser
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mCurrentUser = new User(user.getDisplayName(), user.getEmail());

        // Get database reference for users matches
        mMatchesDatabase = mDatabase.child("matches").child(mAuth.getCurrentUser().getUid());

        // Get and set Matches RecyclerView
        mMatchesRv = (RecyclerView) rootView.findViewById(R.id.matchesRv);
        mMatchesRv.setHasFixedSize(true);
        mMatchesRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        //return inflater.inflate(R.layout.fragment_matches, container, false);
        return rootView;
    }

    /**
     * Default method.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Default method.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Options menu
        setHasOptionsMenu(true);
    }

    /**
     * onStart method used to display all the matches for the current user in a RecyclerView
     * and populate it with all the relevant names, scores and imageViews.
     * Also creates a click listener which gets the ID of the selected match and passes it to the
     * MatchActivity to display in full.
     */
    @Override
    public void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<Match, MatchAdapter.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Match, MatchAdapter.ViewHolder>(
                Match.class, R.layout.match_item, MatchAdapter.ViewHolder.class, mMatchesDatabase
        ) {
            /**
             * Populates each match item in the RecyclerView with the player name, score and status
             * TextViews.
             * Displays the ball image view depending on the server or the trophy image for the
             * winner if the match is complete.
             *
             * @param viewHolder ViewHolder for current match.
             * @param model Current Match model.
             * @param position Position of current match.
             */
            @Override
            protected void populateViewHolder(final MatchAdapter.ViewHolder viewHolder, Match model, int position) {

                // Get both MatchPlayers from the selected Match
                final MatchPlayer player1 = model.getPlayer1();
                final MatchPlayer player2 = model.getPlayer2();

                // Create a new database reference
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                // Get changes for the current match for both players
                database.child("users").addValueEventListener(new ValueEventListener() {
                    /**
                     * Gets both player objects from the Firebase users database based on the ID
                     * of each player and sets the name TextViews for both.
                     * Also sets the status depending of if there is a winner or if the match
                     * is incomplete.
                     *
                     * @param dataSnapshot Users node of database.
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Get user1 and bind username to textview
                        mUser1 = dataSnapshot.child(player1.getUserId()).getValue(User.class);
                        viewHolder.setPlayer1Name(mUser1.getUsername());

                        // Get user2 and bind username to textview
                        mUser2 = dataSnapshot.child(player2.getUserId()).getValue(User.class);
                        viewHolder.setPlayer2Name(mUser2.getUsername());

                        // Sets match status
                        if (player1.getIsWinner()) {
                            mStatus = getString(R.string.winner_status_tv) + mUser1.getUsername();
                        } else if (player2.getIsWinner()) {
                            mStatus = getString(R.string.winner_status_tv) + mUser2.getUsername();
                        } else {
                            mStatus = getString(R.string.incomplete_status_tv);
                        }
                        // Bind match status
                        viewHolder.setmStatusTv(mStatus);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Bind usernames and game scores for both player 1 and player 2
                viewHolder.setPlayer1Games(player1.getGames());
                viewHolder.setPlayer2Games(player2.getGames());

                if (!model.getIsTieBreak()) {
                    // Bind scores for both player 1 and player 2
                    viewHolder.setPlayer1Score(player1.getScore().getStringScore());
                    viewHolder.setPlayer2Score(player2.getScore().getStringScore());
                } else {
                    // Bind tiebreak scores for both player 1 and player 2
                    viewHolder.setPlayer1Score(player1.getTiebreak().toString());
                    viewHolder.setPlayer2Score(player2.getTiebreak().toString());
                }


                // Set visiblity of tennis ball ImageView depending on whose serve it is
                viewHolder.setBallVisibility(player1.getIsServe(), player2.getIsServe());

                // Set trophy icon depending on who wins the match
                viewHolder.setTrophy(player1.getIsWinner(), player2.getIsWinner());

                // Get the ID of the current Match
                final String matchId = getRef(position).getKey();

                // Sets onClickListener for when the user clicks a particular match
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Starts a new MatchActivity intent and store the matchid
                        Intent matchIntent = new Intent(getActivity(), MatchActivity.class);
                        matchIntent.putExtra(MATCH_ID_EXTRA, matchId);
                        startActivity(matchIntent);
                    }
                });
            }
        };
        // Set the adapter for the match RecyclerView
        mMatchesRv.setAdapter(firebaseRecyclerAdapter);
    }
}
