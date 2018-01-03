package com.mad.tennisscoringprototype.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.tennisscoringprototype.R;

/**
 * Created by rowdo on 26/09/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The UserAdapter class is used to help populate a RecyclerView which contains user objects
 *          from the Firebase Database.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    /**
     * Default method.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    /**
     * Default method.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder holder, int position) {

    }

    /**
     * Gets the amount of users.
     *
     * @return The amount of users.
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * This inner classs is used to set all the TextViews and the ImageViews for each user item
     * in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView mUsernameTv, mEmailTv, mYouTv;
        ImageView mFriendIv;
        Boolean mIsFriend;

        /**
         * Constructor used to get the view and instantiate all the TextViews.
         *
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mUsernameTv = (TextView) mView.findViewById(R.id.usernameTv);
            mEmailTv = (TextView) mView.findViewById(R.id.emailTv);
            mFriendIv = (ImageView) mView.findViewById(R.id.friendIv);
            mYouTv = (TextView) mView.findViewById(R.id.youTv);

            mIsFriend = false;
        }

        /**
         * Set the username for each friend in the RecyclerView
         *
         * @param username
         */
        public void setUsername(String username) {
            mUsernameTv.setText(username);
        }

        /**
         * Set the email for each friend in the RecyclerView
         *
         * @param email
         */
        public void setEmail(String email) {
            mEmailTv.setText(email);
        }

        public ImageView getFriendIv() {
            return mFriendIv;
        }

        /**
         * Set ImageView to reflect that the user is already a friend.
         */
        public void setRemoveFriendImageView() {
            mFriendIv.setImageDrawable(mView.getResources().getDrawable(R.drawable.remove_friend));
        }

        /**
         * Set ImageView to reflect that the user is not a friend.
         */
        public void setAddFriendImageView() {
            mFriendIv.setImageDrawable(mView.getResources().getDrawable(R.drawable.add_friend));
        }

        public Boolean getIsFriend() {
            return mIsFriend;
        }

        public void setIsFriend(Boolean mIsFriend) {
            this.mIsFriend = mIsFriend;
        }

        /**
         * Removes the Friend ImageView and sets the TextView for the current user item.
         */
        public void makeCurrentUser() {
            // Remove the Add Friend ImageView
            mFriendIv.setVisibility(View.GONE);

            // Show the "You" TextView
            mYouTv.setVisibility(View.VISIBLE);
        }

        /**
         * Hides the Friend ImageView.
         */
        public void hideFriendIv() {
            // Remove the Add Friend ImageView
            mFriendIv.setVisibility(View.GONE);
        }
    }
}
