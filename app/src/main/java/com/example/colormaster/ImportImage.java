package com.example.colormaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class ImportImage extends AppCompatActivity {

    //initialize variable
    Button btn;
    ImageView imageView;
    TextView textView;
    TextView textView1;
    View view;
    TextToSpeech textToSpeech;
    Bitmap bitmap;
    int SELECT_IMAGE_CODE = 1;
    String[] file_color_name = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_image);

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //set ImportImage selected
        bottomNavigationView.setSelectedItemId(R.id.importImage);
        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.importImage:
                        return true;
                    case R.id.colorPicker:
                        startActivity(new Intent(getApplicationContext(),ColorPicker.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.scanner:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
//Lines of codes for accessing the internal file storage
        //assign variable
        btn = (Button) findViewById(R.id.btn_importImage);
        imageView = (ImageView) findViewById(R.id.pickedImage);
         textView = (TextView) findViewById(R.id.colorValues);
        textView1 = (TextView) findViewById(R.id.colorName);
        view = (View) findViewById(R.id.displayColor);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Title"),SELECT_IMAGE_CODE);
            }
        });
        // create an object textToSpeech and adding features into it
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            //checking if there is no error
            public void onInit(int status) {
                if (status!=TextToSpeech.ERROR){
                    //choose the language
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Uri uri  = data.getData();
            imageView.setImageURI(uri);
            btn.setText("Image Imported");
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache(true);
            //setOnTouchListener for the imported image on the imageView
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction()  == MotionEvent.ACTION_DOWN || event.getAction()== MotionEvent.ACTION_MOVE){
                        bitmap = imageView.getDrawingCache();
                        int pixels = bitmap.getPixel((int)event.getX(),(int) event.getY());
                        int red = Color.red(pixels);
                        int green = Color.green(pixels);
                        int blue = Color.blue(pixels);
                        String hex ="#"+Integer.toHexString(pixels);
                        view.setBackgroundColor(Color.rgb(red,green,blue));
                        textView.setText("RGB:"+"("+red+","+green+","+blue+")"+"\n HEX:" +hex);
                        textView.setTextColor(Color.rgb(255,255,255));
                        file_color_name = ColorName.getColorName(red,green,blue);
                        textView1.setText("Color Name:  " + file_color_name[0].toString().toUpperCase());
                        textView.setTextColor(Color.rgb(255,255,255));
                        textToSpeech.speak(textView1.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                    return false;
                }
            });

        }

    }
}