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
import android.widget.TextView;
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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.System.exit;

public class Main4pActivity extends AppCompatActivity {

    ImageView imageView1 , imageView2;
    private StorageReference mstr;
    BaseLoaderCallback mLoaderCallback;
    double area ,  max  ;
    int m=0;
    String size  , loc;
    Bitmap image , bitmap ;
    Uri uri;
    Bitmap histBitmap;
    TextView text1 , text2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4p);
        Bundle bundle=getIntent().getExtras();
         loc =bundle.getString("loc");


        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);

        text1 = (TextView)findViewById(R.id.text1);
        text2 = (TextView)findViewById(R.id.text2);

        Button sub= (Button)findViewById(R.id.button5);

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                histBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                final byte[] byteArray = stream.toByteArray();


                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream2);
                final byte[] byteArray2 = stream2.toByteArray();


                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                StorageReference riversRef = storageRef.child("Potholes" + "/" + user.getDisplayName() );

                riversRef.putBytes(byteArray)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            }
                        });

                StorageReference riversRef2 = storageRef.child("Potholes" + "/"+user.getDisplayName() + " Location :"+loc + " size :" + size + " inches");


                riversRef2.putBytes(byteArray2)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
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

         bitmap = (Bitmap) data.getExtras().get("data");
        uri =data.getData() ;
        image = bitmap;
        imageView1.setImageBitmap(bitmap);
        Bitmap bmp = bitmap;
        Bitmap depth = bitmap;
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
        max=Math.sqrt(area);
        max = Math.ceil(max);
        size =String.valueOf(max);
        Toast.makeText(getApplicationContext() , "Calculating size and depth of pothole" , Toast.LENGTH_SHORT).show();


        Mat rgba = new Mat();
        Utils.bitmapToMat(depth , rgba);

        Size rgbaSize = rgba.size();

        int histSize=256;
        MatOfInt histogramSize = new MatOfInt(histSize);

        int histogramHeight = (int)rgbaSize.height;
        int binWidth=5;

        MatOfFloat histogramRange = new MatOfFloat(0f , 256f );

        Scalar [] colorsRgb = new Scalar[]{new Scalar(200 , 0 ,0 , 255) , new Scalar(0 , 200 , 0 , 255) ,new Scalar(0 , 0 , 200, 255)};

        MatOfInt[] channels = new MatOfInt[]{new MatOfInt(0), new MatOfInt(1), new MatOfInt(2)};

        Mat[] histograms = new Mat[]{new Mat(), new Mat(), new Mat()};
        Mat histMatBitmap = new Mat(rgbaSize, rgba.type());



        for (int i = 0; i < channels.length; i++) {
            Imgproc.calcHist(Collections.singletonList(rgba), channels[i], new Mat(), histograms[i], histogramSize, histogramRange);
            Core.normalize(histograms[i], histograms[i], histogramHeight, 0, Core.NORM_INF);
            for (int j = 0; j < histSize; j++) {
                Point p1 = new Point(binWidth * (j - 1), histogramHeight - Math.round(histograms[i].get(j - 1, 0)[0]));
                Point p2 = new Point(binWidth * j, histogramHeight - Math.round(histograms[i].get(j, 0)[0]));
                Imgproc.line(histMatBitmap, p1, p2, colorsRgb[i], 2, 8, 0);
            }


            for (int j = 0; j < histSize; j++) {
                Point p1 = new Point(binWidth * (j - 1), histogramHeight - Math.round(histograms[i].get(j - 1, 0)[0]));
                Point p2 = new Point(binWidth * j, histogramHeight - Math.round(histograms[i].get(j, 0)[0]));
                Imgproc.line(histMatBitmap, p1, p2, colorsRgb[i], 2, 8, 0);
            }

        }







         histBitmap = Bitmap.createBitmap(histMatBitmap.cols(), histMatBitmap.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(histMatBitmap, histBitmap);

        imageView2.setImageBitmap(histBitmap);

        text1.setText(loc);
        text2.setText(size + "  inches");



    }
}