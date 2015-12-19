package com.example.chiang.loginpage;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class ProfilePage extends AppCompatActivity {

    private static int RESULT_LOAD_IMG = 2;
    ImageView userDisplayIV;
    Button changeDisplayBtn, changePwdBtn, logoutBtn;
    TextView usernameTV, pwdTV;
    Drawable displayPic;
    String username, password;
    String filepath;
    Bitmap bitmap;
    DBHandler dbHandler = new DBHandler(this, null, null, 1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        userDisplayIV = (ImageView) this.findViewById(R.id.userDisplayIV);
        changeDisplayBtn = (Button) this.findViewById(R.id.changeDisplayBtn);
        changePwdBtn = (Button) this.findViewById(R.id.changePwdBtn);
        logoutBtn = (Button) this.findViewById(R.id.logoutBtn);
        usernameTV = (TextView) this.findViewById(R.id.usernameTV);
        pwdTV = (TextView) this.findViewById(R.id.pwdTV);
        username = getIntent().getExtras().getString("username");
        password = getIntent().getExtras().getString("password");
        displayPic = getDisplayPic(username);
        usernameTV.setText(username);
        pwdTV.setText(convertPwdToAsterisk(password));
        userDisplayIV.setImageDrawable(displayPic);
        changeDisplayBtn.setEnabled(false);
        changeDisplayBtn.setOnClickListener(btnlistener);
        changePwdBtn.setOnClickListener(btnlistener);
        logoutBtn.setOnClickListener(btnlistener);
        userDisplayIV.setOnClickListener(btnlistener);
    }

    View.OnClickListener btnlistener = new View.OnClickListener(){
        public void onClick(View v){
            switch(v.getId()){
                case R.id.changeDisplayBtn:
                    saveImageToInternalStorage(bitmap, username);
                    Toast.makeText(ProfilePage.this, username + ", You have successfully changed your profile picture!", Toast.LENGTH_SHORT).show();
                    changeDisplayBtn.setEnabled(false);
                    break;
                case R.id.changePwdBtn:
                    Intent intent1 = new Intent(ProfilePage.this, change_PWD.class);
                    intent1.putExtra("username", username);
                    intent1.putExtra("password", password);
                    startActivity(intent1);
                    finish();
                    break;
                case R.id.logoutBtn:
                    Toast.makeText(ProfilePage.this, username + " , You have successfully logged out.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfilePage.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.userDisplayIV:
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // Start the Intent
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                filepath = getRealPathFromURI(selectedImage);
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodeString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                //displayIV.setImageBitmap(BitmapFactory.decodeFile(imgDecodeString));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                bitmap = BitmapFactory.decodeFile(imgDecodeString);
                int height = bitmap.getHeight(), width = bitmap.getWidth();
                if (height > 1280 && width > 960) {
                    bitmap = BitmapFactory.decodeFile(imgDecodeString, options);
                }
                try {
                    ExifInterface exif = new ExifInterface(filepath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
                    userDisplayIV.setImageBitmap(bitmap);
                    changeDisplayBtn.setEnabled(true);
                } catch (Exception e) {
                    Toast.makeText(this, "Rotate Fail", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public Drawable getDisplayPic(String filename) {
        Drawable image = null;
            try {
                filename = filename + ".png";
                File filePath = this.getFileStreamPath(filename);
                image = Drawable.createFromPath(filePath.toString());
            } catch (Exception ex) {
                Toast.makeText(this,"something went wrong",Toast.LENGTH_SHORT).show();
            }
        return image;
    }

    @Override
    public void onBackPressed() {

    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public boolean saveImageToInternalStorage(Bitmap image, String filename) {
        try {
            // Use the compress method on the Bitmap object to write image to
            // the OutputStream
            FileOutputStream fos = this.openFileOutput(filename + ".png", Context.MODE_PRIVATE);
            // Writing the bitmap to the output stream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }

    public String convertPwdToAsterisk(String pwd){
        String asteriskPwd="";
        for(int a=0; a<pwd.length(); a++){
            asteriskPwd = asteriskPwd + "*";
        }
        return asteriskPwd;
    }
}
