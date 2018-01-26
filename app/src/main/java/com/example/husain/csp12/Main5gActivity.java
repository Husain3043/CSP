package com.example.husain.csp12;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static java.lang.System.exit;

public class Main5gActivity extends AppCompatActivity {
    Bitmap image;
    private StorageReference mstr;
    String size, loc;
    int i=0;
    Uri ui;



    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5g);
        Bundle bundle = getIntent().getExtras();
        size = bundle.getString("size");
        loc = bundle.getString("loc");
       // ui=bundle.getParcelable("img");

        byte[] byteArray = bundle.getByteArray("picture");
        image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(image);
        TextView location = (TextView) findViewById(R.id.loc);
        location.setText("Your Location :" + loc);
        TextView volu = (TextView) findViewById(R.id.vol);
        Double vol = Double.valueOf(size);
        vol = vol * vol * vol;
        vol = Math.ceil(vol);
        size = String.valueOf(vol);
        volu.setText("Approximate volume of garbage is :" + size);
        Button btn = (Button)findViewById(R.id.upload);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    if(i==0)
                    {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                        Vector vector = new Vector(2);
                        vector.add("Volume : " + size);
                        vector.add("Location : " + loc);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Garbage");
                        String uid = myRef.push().getKey();

                        myRef.child(user.getDisplayName()).child(uid).setValue(vector);
                        i++;



                    }
                    else
                    {
                        Toast.makeText(getApplicationContext() , "Thanc for uploading prblm" , Toast.LENGTH_SHORT).show();
                    }
            }
        });





    }


}


