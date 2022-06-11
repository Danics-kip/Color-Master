package com.example.colormaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener , CameraBridgeViewBase.CvCameraViewListener2{
    private CameraBridgeViewBase mOpencvCameraView;
    private Mat mRgba;
    private Scalar mBlobColorHsv;
    private  Scalar mBlobColorRgba;
    String[] file_color_name = new String[3];

    //define object for the application
    TextView touch_coordinates;
    TextView hex_color;
    TextView rgb_color;
    TextView color;
    View view;
    TextToSpeech textToSpeech;

    double x = -1;
    double y = -1;

    private BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface
                        .SUCCESS: {
                    mOpencvCameraView.enableView();
                    mOpencvCameraView.setOnTouchListener(MainActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);

                }
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //set Scanner selected
        bottomNavigationView.setSelectedItemId(R.id.scanner);
        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.scanner:
                        return true;
                    case R.id.colorPicker:
                        startActivity(new Intent(getApplicationContext(),ColorPicker.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.importImage:
                        startActivity(new Intent(getApplicationContext(),ImportImage.class));
                        overridePendingTransition(0,0);
                        return true;
                        }
                return false;
            }
        });

        //keeps the phone screen in on state
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //declare objects for color detector
        touch_coordinates = (TextView) findViewById(R.id.touch_coordinates);
        hex_color = (TextView) findViewById(R.id.hex_color);
        rgb_color = (TextView) findViewById(R.id.rgb_color);
        color = (TextView) findViewById(R.id.color);
        view = (View) findViewById(R.id.view);

        mOpencvCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_tutorial_activity_surface_view);
        mOpencvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpencvCameraView.setCvCameraViewListener(this);

        // create an object textToSpeech and adding features into it
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // if No error is found then only it will run
                if(status!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpencvCameraView != null)
            mOpencvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this,mLoaderCallBack);
        }else {
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
//destroys all camera operation and closes camera activity
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpencvCameraView != null)
            mOpencvCameraView.disableView();
    }

    /**
     * This method is invoked when camera preview has started. After this method is invoked
     * the frames will start to be delivered to client via the onCameraFrame() callback.
     *
     * @param width  -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
    }

    /**
     * This method is invoked when camera preview has been stopped for some reason.
     * No frames will be delivered via onCameraFrame() callback after this method is called.
     */
    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    /**
     * This method is invoked when delivery of the frame needs to be done.
     * The returned values - is a modified frame which needs to be displayed on the screen.
     * TODO: pass the parameters specifying the format of the frame (BPP, YUV or RGB and etc)
     *
     * @param inputFrame
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        return mRgba;
    }
    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        double yLow = (double)mOpencvCameraView.getHeight() * 0.2401961;
        double yHigh = (double)mOpencvCameraView.getHeight() * 0.7696078;

        double xScale = (double)cols / (double)mOpencvCameraView.getWidth();
        double yScale = (double)rows / (yHigh - yLow);

        x = event.getX();
        y = event.getY();

        y = y - yLow;

        x = x * xScale;
        y = y * yScale;

        if((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;


        Rect touchedRect = new Rect();

        touchedRect.x = (int)x;
        touchedRect.y = (int)y;

        touchedRect.width = 1;
        touchedRect.height = 1;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba,touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL );

        touch_coordinates.setText("X: " + Double.valueOf(x) + ", Y: " + Double.valueOf(y));

        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width * touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = convertScalarsHsv2Rgba(mBlobColorHsv);

        hex_color.setText("HEX: #" + String.format("%02X", (int)mBlobColorRgba.val[0])
                + String.format("%02X", (int)mBlobColorRgba.val[1])
                + String.format("%02X", (int)mBlobColorRgba.val[2]));

        //codes for getting rgb for the color
        int red = (int)mBlobColorRgba.val[0];
        int green =  (int)mBlobColorRgba.val[1];
        int blue = (int)mBlobColorRgba.val[2];


        rgb_color.setText("RGB:"+"(" +red+","+green+","+blue+')');
        file_color_name = ColorName.getColorName(red,green,blue);
        color.setText("Color Name:  " + file_color_name[0].toString().toUpperCase());
        textToSpeech.speak(color.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);

        /*For setting textColor to be displayed on the TextView and the background color of the view*/
        hex_color.setTextColor(Color.WHITE);
        rgb_color.setTextColor(Color.WHITE);
        touch_coordinates.setTextColor(Color.WHITE);
        view.setBackgroundColor(Color.rgb(red,green,blue));
        color.setTextColor(Color.WHITE);

        return false;
    }

    private Scalar convertScalarsHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }


}