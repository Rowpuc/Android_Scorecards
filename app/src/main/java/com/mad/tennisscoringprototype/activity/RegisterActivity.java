package com.mad.tennisscoringprototype.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mad.tennisscoringprototype.R;

import java.lang.reflect.Array;

/**
 * Created by rowdo on 13/09/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The RegisterActivity is launched from the LoginActivity when the user presses the Register
 *          Button.
 *          This activity allows the user to enter in a username, 2 passwords and email address.
 *          If all the information is valid, it will be stored in an Extra which is then passed with the Intent
 *          back to the LoginActivity where the user account will be created.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText mUsernameEt;
    private EditText mEmailEt;
    private EditText mPasswordEt;
    private EditText mConfirmPasswordEt;
    private Button mRegisterBtn;
    private AlertDialog mLoginAd;
    private ProgressDialog mRegisterPd;

    private static final String REGISTER_EXTRA = "REGISTER_EXTRA";

    /**
     * Sets up the TextViews, validation, buttons, EditTexts, alert and progress dialogs.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get support ActionBar and enable Up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create new AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        // Set AlertDialog title
        builder.setTitle(R.string.invalid_registration_ad);

        // Set OnClickListener for AlertDialog
        builder.setPositiveButton(R.string.ok_ad, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Build AlertDialog
        mLoginAd = builder.create();

        // Get EditTexts for username, email, password and confirmation password
        mUsernameEt = (EditText) findViewById(R.id.usernameEt);
        mEmailEt = (EditText) findViewById(R.id.emailEt);
        mPasswordEt = (EditText) findViewById(R.id.passwordEt);
        mConfirmPasswordEt = (EditText) findViewById(R.id.confirmPasswordEt);

        // Get register Button
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);

        // Register button on click listener
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidUsername()) {
                    mLoginAd.setMessage(getString(R.string.enter_valid_username_ad));
                    mLoginAd.show();
                } else if (!isValidEmail()) {
                    mLoginAd.setMessage(getString(R.string.enter_valid_email_ad_2));
                    mLoginAd.show();
                } else if (!isValidPassword()) {
                    mLoginAd.setMessage(getString(R.string.enter_valid_password_ad_2));
                    mLoginAd.show();
                } else if (!doPasswordMatch()) {
                    mLoginAd.setMessage(getString(R.string.passwords_not_match_ad));
                    mLoginAd.show();
                } else {
                    new RegisterUser().execute();
                }
            }
        });

        // Create ProgressDialog with a title and message
        mRegisterPd = new ProgressDialog(this);
        mRegisterPd.setTitle(getString(R.string.register_progress_title));
        mRegisterPd.setMessage("Please wait while your account is created");
    }

    /**
     * Validates the username entered.
     *
     * @return true if username is valid or false if it is invalid.
     */
    protected Boolean isValidUsername() {
        String username = mUsernameEt.getText().toString();

        return !TextUtils.isEmpty(username);
    }

    /**
     * Validaes the email address entered.
     *
     * @return true if email is valid or false if it is invalid.
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
     * Validates the password entered.
     *
     * @return true if password is valid or false if it is invalid
     */
    protected Boolean isValidPassword() {
        String password = mPasswordEt.getText().toString();

        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether or not both passwords match.
     *
     * @return true if passwords match or false if they do not
     */
    protected Boolean doPasswordMatch() {
        String password = mPasswordEt.getText().toString();
        String confirmPassword = mConfirmPasswordEt.getText().toString();

        if (password.equals(confirmPassword)) {
            return true;
        }
        return false;
    }

    /**
     * Asynctask used to register a new user
     */
    private class RegisterUser extends AsyncTask<Void, Void, Void> {

        /**
         * Shiw ProgressDialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRegisterPd.show();
        }

        /**
         * Publish progress.
         *
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Void... params) {
            register();
            publishProgress();
            return null;
        }

        /**
         * Update progress.
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        /**
         * Default method.
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /**
     * Method is used to register a new user by sending back the entered data in an intent to the
     * LoginActivity.
     */
    protected void register() {
        String[] registerArray = {mUsernameEt.getText().toString(), mEmailEt.getText().toString(),
                mPasswordEt.getText().toString()};

        Intent registerIntent = new Intent();
        registerIntent.putExtra(REGISTER_EXTRA, registerArray);

        setResult(RESULT_OK, registerIntent);
        finish();
    }
}
