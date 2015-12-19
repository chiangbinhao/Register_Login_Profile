package com.example.chiang.loginpage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class change_PWD extends AppCompatActivity {

    Button changePasswordBtn;
    EditText currentET, newPwdET, retypeET;
    String name, pwd;
    DBHandler dbHandler = new DBHandler(this, null, null, 1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__pwd);
        currentET = (EditText) this.findViewById(R.id.currentET);
        newPwdET = (EditText) this.findViewById(R.id.newPwdET);
        retypeET = (EditText) this.findViewById(R.id.retypeET);
        changePasswordBtn = (Button) this.findViewById(R.id.changePasswordBtn);
        changePasswordBtn.setOnClickListener(changelistener);
        name = getIntent().getExtras().getString("username");
        pwd = getIntent().getExtras().getString("password");
    }

    View.OnClickListener changelistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.changePasswordBtn:
                    if(!(newPwdET.getText().toString().equals(retypeET.getText().toString()))){
                        Toast.makeText(getBaseContext(), "Please make sure the new password and retype password is the same!", Toast.LENGTH_SHORT).show();
                    }
                    else if(currentET.getText().toString().equals(newPwdET.getText().toString())){
                        Toast.makeText(getBaseContext(), "You have entered the same Current Password and New Password! Try Again!", Toast.LENGTH_SHORT).show();
                    }
                    else if(currentET.getText().toString().equals(pwd)){
                        Information info = dbHandler.findUsername(name);
                        info.setPassword(newPwdET.getText().toString());
                        dbHandler.update(info);
                        Toast.makeText(getBaseContext(), name + ", You have successfully changed your password!", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(change_PWD.this, ProfilePage.class);
                        intent1.putExtra("username", name);
                        intent1.putExtra("password", newPwdET.getText().toString());
                        startActivity(intent1);
                        finish();
                    }
                    else{
                        Toast.makeText(getBaseContext(),"You have entered a wrong current password! Try again!", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(change_PWD.this, ProfilePage.class);
        intent.putExtra("username", name);
        intent.putExtra("password", pwd);
        startActivity(intent);
        this.finish();
    }
}
