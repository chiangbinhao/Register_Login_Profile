package com.example.chiang.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton, registerButton;
    CheckBox rememberCheckBox;
    String username, password;
    DBHandler dbHandler = new DBHandler(this, null, null, 1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEditText = (EditText) this.findViewById(R.id.UsernameEditText);
        passwordEditText = (EditText) this.findViewById(R.id.PasswordEditText);
        loginButton = (Button) this.findViewById(R.id.loginButton);
        registerButton = (Button) this.findViewById(R.id.registerBtn);
        rememberCheckBox = (CheckBox) this.findViewById(R.id.RememberCheckBox);
        loginButton.setOnClickListener(myButtonListener);
        registerButton.setOnClickListener(myButtonListener);
        rememberCheckBox.setOnClickListener(myButtonListener);
        SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String strUsername = prefs.getString("_username", "");
        String strPassword = prefs.getString("_password", "");
        Boolean checked = prefs.getBoolean("_rememberCB", false);
        passwordEditText.setText(strPassword);
        usernameEditText.setText(strUsername);
        rememberCheckBox.setChecked(checked);
    }
    public View.OnClickListener myButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginButton:
                    username = usernameEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    if (username.equals("")) {
                        Toast.makeText(getBaseContext(),
                                "Key in Username", Toast.LENGTH_SHORT).show();
                    } else if (password.equals("")) {
                        Toast.makeText(getBaseContext(),
                                "Key in password", Toast.LENGTH_SHORT).show();
                    } else if (dbHandler.findUsername(username) == null) {
                        Toast.makeText(getBaseContext(),
                                "User is not registered. Please register", Toast.LENGTH_SHORT).show();
                    } else if (dbHandler.info.getPassword().equals(password)) {
                        Intent intent = new Intent(MainActivity.this, ProfilePage.class);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(),"You had entered the wrong password!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.registerBtn:
                    startRegister();
                    finish();
                    break;
                case R.id.RememberCheckBox:
                    if (rememberCheckBox.isChecked()) {
                        SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("_rememberCB", true);
                        editor.putString("_username", usernameEditText.getText().toString());
                        editor.putString("_password", passwordEditText.getText().toString());
                        editor.commit();
                        Toast.makeText(getBaseContext(),
                                "Login Details saved", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("_rememberCB", false);
                        editor.putString("_username", "");
                        editor.putString("_password", "");
                        editor.commit();
                        Toast.makeText(getBaseContext(),
                                "Login Details cleared", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void startRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String strUsername = prefs.getString("_username", "");
        String strPassword = prefs.getString("_password", "");
        Boolean checked = prefs.getBoolean("_rememberCB", false);
        passwordEditText.setText(strPassword);
        usernameEditText.setText(strUsername);
        rememberCheckBox.setChecked(checked);
    }
}
