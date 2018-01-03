package com.mad.tennisscoringprototype.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.tennisscoringprototype.R;
import com.mad.tennisscoringprototype.adapter.UserAdapter;
import com.mad.tennisscoringprototype.model.User;

/**
 * Created by rowdo on 15/10/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The FriendsFragment show a list of all the current user's friends in a RecyclerView which
 *          uses the UserAdapter and displays the name and email in TextViews and allows the user to
 *          remove a user as a friend by selecting the heart ImageView.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsRv;
    private DatabaseReference mDatabase;
    private DatabaseReference mFriendsDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private View mRootView;

    /**
     * onCreateView method which inflates the Fragment view. It gets the current FirebaseAuth User
     * and the Firebase Database reference to the friends node for the current user.
     * It also sets up the Friends RecyclerVIew.
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
        mRootView = inflater.inflate(R.layout.fragment_friends, container, false);

        // Store current user from FirebaseUser
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        // Get root Firebase databse reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get friends database
        mFriendsDatabase = mDatabase.child("friends").child(mCurrentUser.getUid());

        // Sets up the Friends RecyclerView
        mFriendsRv = (RecyclerView) mRootView.findViewById(R.id.friendsRv);
        mFriendsRv.setHasFixedSize(true);
        mFriendsRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        return mRootView;
    }

    /**
     * Default onViewCreated
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * onStart method which creates a new FirebaseRecyclerAdapter in order to display the list of
     * friends for the current user stored in the database. Uses the UserAdapter to set the name and email TextViews
     * and the ImageViews.
     */
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, UserAdapter.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserAdapter.ViewHolder>(
                User.class, R.layout.user_item, UserAdapter.ViewHolder.class, mFriendsDatabase
        ) {
            /**
             * Populate the current user item with the name and email TextViews and the ImageViews
             * for adding and removing friends.
             *
             * @param viewHolder
             * @param model
             * @param position
             */
            @Override
            protected void populateViewHolder(final UserAdapter.ViewHolder viewHolder, User model, int position) {

                // Bind username, email and display image to relevant fields on the specific friend
                viewHolder.setUsername(model.getUsername());
                viewHolder.setEmail(model.getEmail());

                // Userid of selected player
                final String userId = getRef(position).getKey();

                // Get changes for the current users' friends
                mDatabase.child("friends").child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    /**
                     * Finds all the users that are friends in the friends node of the database for the current user,
                     * and set the ImageView to reflect this.
                     *
                     * @param dataSnapshot
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Get user1 and bind username to textview
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            if (user.getKey().equals(userId)) {
                                viewHolder.setRemoveFriendImageView();
                                viewHolder.setIsFriend(true);
                            }
                        }

                    }

                    /**
                     * onCancelled default method.
                     *
                     * @param databaseError
                     */
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Set OnClickListener for addFriend ImageView
                viewHolder.getFriendIv().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.getIsFriend()) {
                            // Remove user from Firebase friends database
                            mDatabase.child("friends").child(mCurrentUser.getUid()).child(userId).removeValue();

                            // Notify the user that the user has been removed as a friend
                            Snackbar.make(mRootView, R.string.friend_added_iv, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        // Sets the adapter for the RecyclerView
        mFriendsRv.setAdapter(firebaseRecyclerAdapter);
    }
}
