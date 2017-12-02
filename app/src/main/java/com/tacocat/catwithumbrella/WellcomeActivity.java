package com.tacocat.catwithumbrella;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WellcomeActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    private TextView addressTxtV;
    private TextView weatherTxtV;
    private ApiInterface apiInterface;
    private LocationCallback mLocationCallback;
    private static LocationRequest locationRequest = createLocationRequest();


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);
        addressTxtV = findViewById(R.id.current_location);
        weatherTxtV = findViewById(R.id.weatherTxtV);
        createLocationCallback();
        apiInterface = ApiClient.Companion.getClient().create(ApiInterface.class);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
            return;
        }

        setLocation();
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("Tag", "onLocationResult");
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);

                    Address address = getLocationName(location);
                    if (address != null) {
                        addressTxtV.setText(address.getAddressLine(0));
                        getWeather(address.getLatitude(), address.getLongitude());
                    }
                }
            }
        };
    }

    private void setLocation() {
        Log.d("Tag", "setLocation called");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null );
    }

    private Address getLocationName(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
            return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
          return null;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            int[] grantResults){
        setLocation();
    }

    public void getWeather(double lat, double lng) {
        Log.d("Tag", "getWeather called");
        Call<WeatherResponse> call = apiInterface.getWeather(lat, lng, "a9f07b3e58aa83b04d6299075004c1ce");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d("Tag", "getWeather response: " + response.body());
                WeatherResponse weatherResponse = response.body();
                WeatherResponse.WeatherInfo weatherInfo = weatherResponse.getWeatherInfoList().get(0);
                WeatherResponse.Weather weather1 = weatherInfo.getWeatherList().get(0);
                weatherTxtV.setText(weather1.getMain());
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
               call.cancel();
            }
        });
    }

    protected static LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        return mLocationRequest;
    }
}
