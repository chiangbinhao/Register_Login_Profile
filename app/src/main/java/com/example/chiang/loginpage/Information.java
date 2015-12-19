package com.example.chiang.loginpage;

/**
 * Created by Chiang on 13/11/2015.
 */
public class Information {
    private int _id;
    private String _username, _password;

    public Information() {

    }

    public Information(int _id, String _username, String _password) {
        this._id = _id;
        this._username = _username;
        this._password = _password;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public int getId() {
        return this._id;
    }

    public void setUsername(String _username) {
        this._username = _username;
    }

    public String getUsername() {
        return this._username;
    }

    public void setPassword(String _password) {
        this._password = _password;
    }

    public String getPassword() {
        return this._password;
    }
}
