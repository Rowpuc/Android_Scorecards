package com.mad.tennisscoringprototype.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.tennisscoringprototype.R;

/**
 * Created by rowdo on 24/09/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The MatchAdapter is used to populate each Match item in the RecyclerView with the players
 *          name, match status and score and set the visibility of the ball and trophy imageviews.
 */
public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    /**
     * Default method.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MatchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    /**
     * Default method.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MatchAdapter.ViewHolder holder, int position) {

    }

    /**
     * Returns the number of items in the RecyclerView. i.e. number of matches
     *
     * @return number of matches for the current user.
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * This inner classs is used to set all the TextViews and the ImageViews for each Match item
     * in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView mPlayer1NameTv, mPlayer2NameTv, mPlayer1ScoreTv, mPlayer2ScoreTv,
                mPlayer1GamesTv, mPlayer2GamesTv, mStatusTv;
        ImageView mPlayer1Iv, mPlayer2Iv;

        /**
         * Conatructor used to set up all the TextViews and ImageViews.
         *
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mPlayer1NameTv = (TextView) mView.findViewById(R.id.player1NameTv);
            mPlayer2NameTv = (TextView) mView.findViewById(R.id.player2NameTv);
            mPlayer1ScoreTv = (TextView) mView.findViewById(R.id.player1ScoreTv);
            mPlayer2ScoreTv = (TextView) mView.findViewById(R.id.player2ScoreTv);
            mPlayer1GamesTv = (TextView) mView.findViewById(R.id.player1GamesTv);
            mPlayer2GamesTv = (TextView) mView.findViewById(R.id.player2GamesTv);
            mStatusTv = (TextView) mView.findViewById(R.id.statusTv);
            mPlayer1Iv = (ImageView) mView.findViewById(R.id.player1Iv);
            mPlayer2Iv = (ImageView) mView.findViewById(R.id.player2Iv);
        }

        public void setPlayer1Name(String name) {
            mPlayer1NameTv.setText(name);
        }

        public void setPlayer2Name(String name) {
            mPlayer2NameTv.setText(name);
        }

        public void setPlayer1Games(String games) {
            mPlayer1GamesTv.setText(games);
        }

        public void setPlayer2Games(String games) {
            mPlayer2GamesTv.setText(games);
        }

        public void setPlayer1Score(String score) {
            mPlayer1ScoreTv.setText(score);
        }

        public void setPlayer2Score(String score) {
            mPlayer2ScoreTv.setText(score);
        }

        public void setmStatusTv(String status) {
            mStatusTv.setText(status);
        }

        /**
         * Set visibility of ball ImageViews based on which player is serving.
         *
         * @param player1IsServe
         * @param player2IsServe
         */
        public void setBallVisibility(Boolean player1IsServe, Boolean player2IsServe) {
            if (player1IsServe) {
                mPlayer2Iv.setVisibility(View.INVISIBLE);
                mPlayer1Iv.setVisibility(View.VISIBLE);
            } else if (player2IsServe) {
                mPlayer1Iv.setVisibility(View.INVISIBLE);
                mPlayer2Iv.setVisibility(View.VISIBLE);
            } else {
                // Remove serving ball for both players
                mPlayer1Iv.setImageDrawable(null);
                mPlayer2Iv.setImageDrawable(null);
            }
        }

        /**
         * Sets trophy as the Player ImageView depending on which player has won.
         *
         * @param player1IsWinner True if player 1 has won the match.
         * @param player2IsWinner True if player 2 has won the match.
         */
        public void setTrophy(Boolean player1IsWinner, Boolean player2IsWinner) {
            if (player1IsWinner) {
                mPlayer1Iv.setImageDrawable(itemView.getResources().getDrawable(R.drawable.trophy));
            } else if (player2IsWinner) {
                mPlayer2Iv.setImageDrawable(itemView.getResources().getDrawable(R.drawable.trophy));
            }
        }
    }
}
