package com.example.husain.csp12;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Main5gActivity extends AppCompatActivity {
    Bitmap image ;
    String size , loc ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5g);
        Bundle bundle=getIntent().getExtras();
        size = bundle.getString("size");
        loc =bundle.getString("loc");
        byte[] byteArray = bundle.getByteArray("picture");
        image= BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(image);
        TextView location = (TextView)findViewById(R.id.loc);
        location.setText("Your Location :"+loc);
        TextView volu=(TextView)findViewById(R.id.vol);
        Double vol=Double.valueOf(size);
        vol = vol*vol*vol;
        vol=Math.ceil(vol);
        size=String.valueOf(vol);
        volu.setText("Approximate volume of garbage is :"+ size);



    }
}
