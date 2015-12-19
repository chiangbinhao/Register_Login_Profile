package com.example.chiang.loginpage;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;

public class RegisterActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMG = 1;
    String filepath;
    EditText usernameET, pwdET;
    CheckBox showCB;
    Button registerBtn;
    ImageView displayIV;
    Bitmap bitmap, display;
    DBHandler dbHandle = new DBHandler(this, null, null, 1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameET = (EditText) this.findViewById(R.id.usernameET);
        pwdET = (EditText) this.findViewById(R.id.passwordET);
        showCB = (CheckBox) this.findViewById(R.id.showCheckBox);
        registerBtn = (Button) this.findViewById(R.id.registerBtn);
        displayIV = (ImageView) this.findViewById(R.id.displayIV);
        showCB.setOnClickListener(listener);
        registerBtn.setOnClickListener(listener);
        displayIV.setOnClickListener(listener);
        display = BitmapFactory.decodeResource(this.getResources(), R.drawable.pic);
        displayIV.setImageBitmap(display);
        filepath = "display";
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.showCheckBox:
                    if(showCB.isChecked()){
                        pwdET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    else{
                        pwdET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    break;
                case R.id.registerBtn:
                    if(usernameET.getText().toString().equals("")){
                        Toast.makeText(getBaseContext(), "Please enter a username!", Toast.LENGTH_SHORT).show();
                    }
                    else if(pwdET.getText().toString().equals("")){
                        Toast.makeText(getBaseContext(), "Please enter a password!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String username = usernameET.getText().toString();
                        String pwd = pwdET.getText().toString();
                        if(dbHandle.findUsername(username) == null){
                            //nobody uses this username
                            Information info = new Information();
                            info.setUsername(username);
                            info.setPassword(pwd);
                            dbHandle.addUser(info);
                            if(filepath.equals("display")){
                                saveImageToInternalStorage(display, username);
                            }
                            else{
                                saveImageToInternalStorage(bitmap, username);
                            }
                            Toast.makeText(getBaseContext(), username + ", Welcome to the community! You can login now", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(getBaseContext(), "Please enter another username! This had been taken!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                case R.id.displayIV:
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
                    displayIV.setImageBitmap(bitmap);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
