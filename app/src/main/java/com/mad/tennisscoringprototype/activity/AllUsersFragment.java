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
import com.google.android.gms.tasks.Task;
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
 *          The AllUsersFragment is a Fragment of the MainActivity which is inflated when the user presses
 *          the All Users Button from the Navigation Drawer.
 *          <p>
 *          This Fragment shows all the users who have logged into the app in a RecyclerView which is populated
 *          from the users node on the Firebase Database.
 *          <p>
 *          Each user row has a love heart Image View next to it which either adds or removes the user as
 *          a friend. Friends are stored in the friends node on the Firebase Database.
 */
public class AllUsersFragment extends Fragment {

    private RecyclerView mAllUsersRv;
    private DatabaseReference mDatabase;
    private DatabaseReference mAllUsersDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private View mRootView;

    /**
     * onCreate default method
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * onCreateView method which inflates the Fragment view. It gets the current FirebaseAuth User
     * and the Firebase Database reference to the users node. It also sets up the AllUsers RecyclerVIew.
     *
     * @param inflater           Used to inflate the Fragment View
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Get fragment view
        mRootView = inflater.inflate(R.layout.fragment_all_users, container, false);

        // Get root Firebase databse reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get users Firebase databse reference
        mAllUsersDatabase = mDatabase.child("users");

        // Store current user from FirebaseUser
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        // Get and set the AllUsers RecyclerView
        mAllUsersRv = (RecyclerView) mRootView.findViewById(R.id.allUsersRv);
        mAllUsersRv.setHasFixedSize(true);
        mAllUsersRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        return mRootView;
    }

    /**
     * onViewCreated default method
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
     * all users stored in the database. Uses the UserAdapter to set the name and email TextViews
     * and the ImageViews.
     */
    @Override
    public void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<User, UserAdapter.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserAdapter.ViewHolder>(
                User.class, R.layout.user_item, UserAdapter.ViewHolder.class, mAllUsersDatabase
        ) {
            /**
             * Populate the current user item with the name and email TextViews and the ImageViews
             * for adding and removing friends.
             *
             * @param viewHolder Current viewHolder.
             * @param model Current user object.
             * @param position Current user item position.
             */
            @Override
            protected void populateViewHolder(final UserAdapter.ViewHolder viewHolder, final User model, int position) {

                // Bind username, email and display image to relevant fields on the specific friend
                viewHolder.setUsername(model.getUsername());
                viewHolder.setEmail(model.getEmail());

                // Userid of selected player
                final String userId = getRef(position).getKey();

                // Set the current user so that the current user can't add themselves as a friend
                if (userId.equals(mCurrentUser.getUid())) {
                    viewHolder.makeCurrentUser();
                }

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
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            // Find all users which are already friends
                            if (user.getKey().equals(userId)) {
                                // Change the ImageView from add friend to remove friend and set isFriend to true
                                viewHolder.setRemoveFriendImageView();
                                viewHolder.setIsFriend(true);
                            }
                        }
                    }

                    /**
                     * onCancelled default method.
                     * @param databaseError
                     */
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Set OnClickListener for addFriend ImageView
                viewHolder.getFriendIv().setOnClickListener(new View.OnClickListener() {
                    /**
                     * onClick listener for Friend ImageVIew. Either adds user or removes user as
                     * a friend depending on the current state and displays the relevant information
                     * in a SnackBar
                     *
                     * @param v Current view.
                     */
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.getIsFriend()) {
                            // Remove user from Firebase friends database
                            mDatabase.child("friends").child(mCurrentUser.getUid()).child(userId).removeValue();

                            // Set Add friend image for the ImageView
                            viewHolder.setAddFriendImageView();

                            // Set Add friend to false
                            viewHolder.setIsFriend(false);

                            // Notify the user that the user has been removed as a friend
                            Snackbar.make(mRootView, R.string.friend_removed_iv, Snackbar.LENGTH_LONG).show();
                        } else {
                            // Add user to Firebase friends database
                            mDatabase.child("friends").child(mCurrentUser.getUid()).child(userId).setValue(model);

                            // Notify the user that the user has been added as a friend
                            Snackbar.make(mRootView, R.string.friend_added_iv, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        // Set the adapter for the RecyclerView
        mAllUsersRv.setAdapter(firebaseRecyclerAdapter);
    }
}
