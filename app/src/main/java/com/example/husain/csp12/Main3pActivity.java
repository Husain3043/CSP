package com.example.husain.csp12;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Main3pActivity extends AppCompatActivity implements LocationListener {

    private static final int REQUEST_PERMISSION_FINE_LOCATION_RESULT =1;
    private TextView locationText , strs;
    private Button getLocationBtn;
    private LocationManager locationManager;
    String str="00";
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3p);

        locationText = (TextView) findViewById(R.id.LocationText);

        strs = (TextView) findViewById(R.id.textView3);
        button = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // if (str == "00") {
              //      Toast.makeText(getApplicationContext(), "please give your Location", Toast.LENGTH_SHORT).show();
              //  } else {
                    Intent intent = new Intent(Main3pActivity.this , Main4pActivity.class);
                    intent.putExtra("loc",str);
                    startActivity(intent);
              //  }
            }
        });


                if (Build.VERSION.SDK_INT > -Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getLocation();
                    } else {
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                            Toast.makeText(getApplicationContext(), "Application requied to access loaction", Toast.LENGTH_SHORT).show();
                        }
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FINE_LOCATION_RESULT);
                    }
                } else {
                    getLocation();
                }
            }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_FINE_LOCATION_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), " Application Loading Location  ", Toast.LENGTH_SHORT).show();

            }
        }
    }


    void getLocation(){
        try {


            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }




    @Override
    public void onLocationChanged(Location location) {

        locationText.setText("\nLatitude"+location.getLatitude()+ " "+"Longitude"+location.getLongitude());
        try{
            Geocoder geocoder=new Geocoder(this);
            List<Address> addresses= geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            //locationText.setText(locationText.getText()+"\n"+addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));

            // str= addresses.get(0).getLocality()+" ";
            str=addresses.get(0).getAddressLine(0);
            strs.setText(str);


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

        Toast.makeText(getApplicationContext(), "Thanx for Enabling GPS",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
