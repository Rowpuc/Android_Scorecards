package com.mad.tennisscoringprototype.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.tennisscoringprototype.R;

import org.w3c.dom.Text;

/**
 * Created by rowdo on 13/09/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The LoginActivity allows the user to login to the app either using an email and password
 *          combination or by using the Google Account by pressing the Sign In Google Button.
 *          The user can also register a new account by pressing the Register Button.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDataBase;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;
    private EditText mEmailEt;
    private EditText mPasswordEt;
    private Button mRegisterBtn;
    private ProgressDialog mLoginPd;
    private AlertDialog mLoginAd;
    private String mUsername;

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "SIGN_IN";
    private static final int RC_REGISTER = 2;
    private static final String REGISTER_EXTRA = "REGISTER_EXTRA";

    /**
     * onCreate method sets up the Buttons, alert and progress dialogs and Signin methods
     * through either Username and Password combination or by the Google Account Signin method.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up login ProgressDialog with title and message
        mLoginPd = new ProgressDialog(this);
        mLoginPd.setTitle(getString(R.string.login_progress_bar));
        mLoginPd.setMessage(getString(R.string.login_pd));
        mLoginPd.setCanceledOnTouchOutside(false);

        // Create new AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        // Set AlertDialog title
        builder.setTitle(R.string.invalid_login_ad);

        // Set OnClickListener for AlertDialog
        builder.setPositiveButton(R.string.ok_ad, new DialogInterface.OnClickListener() {
            /**
             * OnCLick for the alert dialog
             *
             * @param dialog
             * @param which
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Create the login AlertDialog
        mLoginAd = builder.create();

        // Get the Google SignInButton
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);

        // Get the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Get current user
        mCurrentUser = mAuth.getCurrentUser();

        // Create AuthStateListener which recognises when the user is authenticated/signed in
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            /**
             * Detects when a user is logged in and changes the username to the inputted one
             * on the RegisterActivity and sends the user to the MainActivity.
             * @param firebaseAuth
             */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    // Call method to add username that was inputted on the RegisterActivity
                    changeUsername(user);

                    // Send the user to the MainActivity
                    sendToMainActivity();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Google SignIn Failed", Toast.LENGTH_LONG).show();
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Google SignInButton onClickListener
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginPd.show();
                signIn();
            }
        });

        // Get Email and password EditText fields
        mEmailEt = (EditText) findViewById(R.id.emailEt);
        mPasswordEt = (EditText) findViewById(R.id.passwordEt);

        // Get register button
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);

        // Set onClickListener for Register button which starts a new RegisterActivity
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(registerIntent, RC_REGISTER);
            }
        });
    }

    /**
     * Sets the user with the entered username if it doesn't already have one
     *
     * @param user
     */
    protected void changeUsername(FirebaseUser user) {
        if (!TextUtils.isEmpty(mUsername)) {
            // Create UserProfileChangeRequest to add the username to the current user
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(mUsername)
                    .build();
            // Add username to current user
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            }
                        }
                    });
        }
    }

    /**
     * Creates a new intent which goes to the MainActivity
     */
    protected void sendToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    /**
     * Returns whether or not the entered email is in a valid format or not
     *
     * @return True if email is valid. False if not.
     */
    protected Boolean isValidEmail() {
        String email = mEmailEt.getText().toString();

        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    /**
     * Returns whether the password is in a valid format or not.
     *
     * @return True if password is valid. False if not.
     */
    protected Boolean isValidPassword() {
        String password = mPasswordEt.getText().toString();

        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return true;
    }

    /**
     * onStart set AuthStateListener and gets current user.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    /**
     * onStop removes AuthStateListener
     */
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    /**
     * Calls method to login user if details are valid.
     *
     * @param view Current view.
     */
    public void loginAction(View view) {
        if (!isValidEmail()) {
            // Email invalid
            mLoginAd.setMessage(getString(R.string.enter_valid_email_ad));
            mLoginAd.show();
        } else if (!isValidPassword()) {
            // Password invalid
            mLoginAd.setMessage(getString(R.string.enter_valid_password_ad));
            mLoginAd.show();
        } else {
            // Login is valid
            mLoginPd.show();
            signIn(mEmailEt.getText().toString(), mPasswordEt.getText().toString());
        }
    }

    /**
     * Attempt to sign user in using username and password.
     */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Gets the entered details from the RegisterActivity and attempts to create a new account
     * using those details.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        } else if (requestCode == RC_REGISTER) {
            if (resultCode == RESULT_OK) {
                // Create new account from RegisterActivity details
                mLoginPd.show();
                String[] registerIntent = data.getStringArrayExtra(REGISTER_EXTRA);
                mUsername = registerIntent[0];
                createAccount(registerIntent[1], registerIntent[2]);
            }
        }
    }

    /**
     * Default method used to sign the user in with their Google Account.
     *
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.authentication_failed_pd,
                                    Toast.LENGTH_SHORT).show();
                        }
                        mLoginPd.dismiss();
                    }
                });
    }

    /**
     * Default method used to create a new FirebaseAuth account using the email and password entered.
     *
     * @param email    Inputed email from RegisterActivity
     * @param password Inputed password from RegisterActivity
     */
    protected void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        mLoginPd.dismiss();
                    }
                });
    }

    /**
     * Default Sigin method used to sign the user in with the email and password entered from the
     * RegisterActivity.
     *
     * @param email    Inputed email from RegisterActivity
     * @param password Inputed password from RegisterActivity
     */
    protected void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        mLoginPd.dismiss();
                    }
                });
    }
}
