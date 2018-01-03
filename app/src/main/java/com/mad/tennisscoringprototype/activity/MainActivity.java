package com.mad.tennisscoringprototype.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.tennisscoringprototype.R;
import com.mad.tennisscoringprototype.model.User;

/**
 * Created by rowdo on 13/09/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The MainActivity is the activity that is launched once the user is logged in.
 *          It does not display anything within itself.
 *          It uses a Navigation Drawer which inflates Fragments based on which ever item is selected
 *          from the Navigation Drawer. The default Fragment is the Scorecards Fragment.
 */
public class MainActivity extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private User mCurrentUser;
    private Toolbar mToolbar;
    private View mHeader;

    private static final String TAG = "MAIN_ACTIVITY";

    /**
     * onCreate method used to setup the current user, database reference, toolbar and
     * also the MatchesFragment which is the inital Fragment used in the Activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Toolbar and set title
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.matches_title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mHeader = navigationView.getHeaderView(0);

        // Set AuthStateListener which checks if user is signed in
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

        // Get root Firebase databse reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Store current user from FirebaseUser
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        mCurrentUser = new User(user.getDisplayName(), user.getEmail());

        // Add user to Firebase users database
        mDatabase.child("users").child(user.getUid()).setValue(mCurrentUser);

        // Get username TextView for drawer and set it
        TextView usernameTv = (TextView) mHeader.findViewById(R.id.usernameTv);
        usernameTv.setText(mCurrentUser.getUsername());

        // Get email TextView for drawer and set it
        TextView emailTv = (TextView) mHeader.findViewById(R.id.emailTv);
        emailTv.setText(mCurrentUser.getEmail());

        // New FragmentTransaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Get new MatchesFragment
        Fragment matchesFragment = new MatchesFragment();

        // Replace the existing Fragment with the MatchesFragment
        fragmentTransaction.replace(R.id.content_main, matchesFragment);
        fragmentTransaction.commit();
    }

    /**
     * on Start method which checks if the user is logged in and sends the user back to the
     * LoginActivity if they are not.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // If not user is logged in, go back to LoginActivity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLoginActivity();
        }
    }

    /**
     * Creates a new Intent to send the user back to the LoginActivity.
     */
    protected void sendToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    /**
     * Default method used to open and shut the navigation drawer
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Default method used to create options menu if there is one. Which there is not.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Default method used to get the currently selected option.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Replaces the existing Fragment one with the one selected from the NavigationDrawer
     * and then sets the ActionBar Title.
     *
     * @param item Selected NavigationDrawer item.
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Create new FragmentTransaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Create new Fragment
        Fragment fragment;

        if (id == R.id.matches) {

            // Set Toolbar title
            mToolbar.setTitle(R.string.matches_title);

            // Get relevant Fragment
            fragment = new MatchesFragment();

            // Replace current fragment with the new one
            fragmentTransaction.replace(R.id.content_main, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.allUsers) {

            // Set Toolbar title
            mToolbar.setTitle(R.string.all_users_title);

            // Get relevant Fragment
            fragment = new AllUsersFragment();

            // Replace current fragment with the new one
            fragmentTransaction.replace(R.id.content_main, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.friends) {

            // Set Toolbar title
            mToolbar.setTitle(R.string.friends_title);

            // Get relevant Fragment
            fragment = new FriendsFragment();

            // Replace current fragment with the new one
            fragmentTransaction.replace(R.id.content_main, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.logout) {
            // Sign the user out and return to LoginActivity
            FirebaseAuth.getInstance().signOut();
            sendToLoginActivity();
            finish();
        }
        // Close the Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
