package com.example.colormaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;



public class ColorPicker extends AppCompatActivity {
//initialize variable
    ImageView imgView;
    TextView mColorValues, mColorName;
    View mColorViews;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);


        /*Lines of code for color picker*/
        //assign variable

        imgView=(ImageView)findViewById(R.id.colorPickers);
        mColorValues=(TextView)findViewById(R.id.displayValues);
        mColorName=(TextView)findViewById(R.id.colorName);
        mColorViews=(View)findViewById(R.id.displayColors);

        imgView.setDrawingCacheEnabled(true);
        imgView.buildDrawingCache(true);
        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
                    bitmap=imgView.getDrawingCache();
                    int pixels=bitmap.getPixel((int)event.getX(), (int)event.getY());

                    int red= Color.red(pixels);
                    int green= Color.green(pixels);
                    int blue= Color.blue(pixels);

                    String hex="#"+Integer.toHexString(pixels);
                    mColorViews.setBackgroundColor(Color.rgb(red,green,blue));
                    mColorValues.setText("RGB:"+"("+red+","+green+","+blue+")"+"\n HEX:" +hex);

                }

                return false;
            }
        });




        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //set Color Picker selected
        bottomNavigationView.setSelectedItemId(R.id.importImage);
        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.colorPicker:
                        return true;
                    case R.id.importImage:
                        startActivity(new Intent(getApplicationContext(),ImportImage.class));
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

    }
}