package com.example.husain.csp12;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Main4gActivity extends AppCompatActivity {

    ImageView imageView;
    BaseLoaderCallback mLoaderCallback;
    double area ,  max;
    int m=0;
    String size , loc;
    Bitmap image ;
    Uri uri;
    private StorageReference mstr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4g);

        Bundle bundle=getIntent().getExtras();
         loc=bundle.getString("loc");



        imageView = (ImageView) findViewById(R.id.imageView);
        Button sub = (Button)findViewById(R.id.button5);

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                mstr = FirebaseStorage.getInstance().getReference();
                StorageReference up = mstr.child("GARBAGE PROBLEM").child(user.getDisplayName()+ "  ## Location : " + loc + "  ## VOLUME : "+ size);
                up.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri dw = taskSnapshot.getDownloadUrl();
                        Toast.makeText(getApplicationContext() , "Thank you for uploading the problem " , Toast.LENGTH_SHORT).show();

                    }
                });



            }
        });



                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);


        mLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        Log.i("OpenCV", "OpenCV loaded successfully");
                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }
        };

    }

    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        m=1;
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        uri =data.getData();
        image = bitmap;
        imageView.setImageBitmap(bitmap);
        Bitmap bmp = bitmap;
        Mat gray = new Mat();
        Mat color = new Mat();
        Mat draw = new Mat();
        Mat wide = new Mat();
        Utils.bitmapToMat(bmp, color);
        Imgproc.cvtColor(color, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(gray, wide, 50, 150, 3, false);
        wide.convertTo(draw, CvType.CV_8U);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(draw, contours, gray, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            Imgproc.drawContours(color, contours, contourIdx, new Scalar(0, 0, 255), -1);



        }
        MatOfPoint2f approxCurve = new MatOfPoint2f();

        //For each contour found
        for (int i=0; i<contours.size(); i++)
        {
            //Convert contours(i) from MatOfPoint to MatOfPoint2f
            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);

            Imgproc.rectangle(draw, rect.tl(), rect.br(), new Scalar(255, 0, 0),1, 8,0);

        }
        area=-1;
        for(int i=0 ; i<contours.size() ; i++){
            max=Imgproc.contourArea(contours.get(i));
            if(area<max){
                area=max;
            }

        }
        area=area/19;
        max =Math.sqrt(area);
        max=max*max*max;
        max=Math.ceil(max);
        size=String.valueOf(max);
        Toast.makeText(getApplicationContext() , "Calculating Volume of Garbage......." , Toast.LENGTH_SHORT).show();




    }
}