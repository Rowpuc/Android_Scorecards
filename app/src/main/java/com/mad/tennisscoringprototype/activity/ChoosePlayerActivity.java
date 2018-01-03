package com.mad.tennisscoringprototype.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.tennisscoringprototype.R;
import com.mad.tennisscoringprototype.adapter.UserAdapter;
import com.mad.tennisscoringprototype.model.User;

/**
 * Created by rowdo on 13/09/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The ChoosePlayerActivity is started when he user is creating a new match and is choosing a
 *          player to play against. A RecyclerView is used to display a list of the user's friends
 *          using the UserAdapter class. Selecting a particular user will select that user as the player
 *          the user will play against in the new match.
 */
public class ChoosePlayerActivity extends AppCompatActivity {

    private RecyclerView mFriendsRv;
    private DatabaseReference mDatabase;
    private DatabaseReference mFriendsDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private static String USER_ID_EXTRA = "USER_ID_EXTRA";

    /**
     * onCreate method which sets up to the ActionBar. Gets the current FirebaseAuth user
     * and gets the database reference to the friends node for the current user.
     * Also sets up the Friends RecyclerView.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_player);

        // Get support ActionBar and enable Up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Store current user from FirebaseUser
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        // Get root Firebase databse reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFriendsDatabase = mDatabase.child("friends").child(mCurrentUser.getUid());

        // Get friends RecyclerView and set it
        mFriendsRv = (RecyclerView) findViewById(R.id.friendsRv);
        mFriendsRv.setHasFixedSize(true);
        mFriendsRv.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * onStart method which creates a new FirebaseRecyclerAdapter which is used to get all the
     * information about each friend that the current user has and uses the UserAdapter to populate
     * each item in the view.
     */
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, UserAdapter.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserAdapter.ViewHolder>(
                User.class, R.layout.user_item, UserAdapter.ViewHolder.class, mFriendsDatabase
        ) {
            /**
             * Used to populate each friend item with the username and email TextView.
             * Sets a click listener for each item to get the ID of the selected user and
             * return it to the NewMatchActivity
             *
             * @param viewHolder
             * @param model
             * @param position
             */
            @Override
            protected void populateViewHolder(UserAdapter.ViewHolder viewHolder, User model, int position) {

                // Bind username, email and display image to relevant fields on the specific friend
                viewHolder.setUsername(model.getUsername());
                viewHolder.setEmail(model.getEmail());

                // Hide Add Friend Image View
                viewHolder.hideFriendIv();

                // Userid of selected player
                final String userId = getRef(position).getKey();

                // OnCLickListener so that the ID of the selected player is passed when the
                // corresponding itemview is clicked
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get choosePlayerIntent and store userId in it from the selected player
                        Intent choosePlayerIntent = getIntent();
                        choosePlayerIntent.putExtra(USER_ID_EXTRA, userId);

                        // Set result and finish activity
                        setResult(RESULT_OK, choosePlayerIntent);
                        finish();
                    }
                });
            }
        };
        // Bind RecyclerView with Friends RecyclerView
        mFriendsRv.setAdapter(firebaseRecyclerAdapter);
    }


}
