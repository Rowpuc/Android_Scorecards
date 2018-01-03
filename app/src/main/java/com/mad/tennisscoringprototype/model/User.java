package com.mad.tennisscoringprototype.model;

/**
 * Created by rowdo on 13/09/2017.
 */

/**
 * @author rowdo
 * @version 1.0
 *          <p>
 *          The User class contains the ID, username and email of the current user.
 */
public class User {

    private String mId;
    private String mUsername;
    private String mEmail;

    /**
     * Empty constructor
     */
    public User() {
    }

    /**
     * Constructor which takes the username and email.
     *
     * @param username User's username.
     * @param email    User's email.
     */
    public User(String username, String email) {
        this.mUsername = username;
        this.mEmail = email;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }
}

