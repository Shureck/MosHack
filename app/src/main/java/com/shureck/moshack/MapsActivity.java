package com.shureck.moshack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.shureck.moshack.Preview;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        View.OnClickListener,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    public ArrayList<View> surveyButtons;
    public ArrayList<Boolean> selectedGenres;
    public ArrayList<SurveyButtonContent> buttonContents;
    public ArrayList<String> genresToSend;
    String str;
    Button button;
    int tappedButtons;

    LinearLayout liner;

    GoogleMap mMap;
    private String token;

    public LocationManager locationManager;
    public LocationListener locationListener;

    public static Location moment_loc;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        WorkWithToken workWithToken = new WorkWithToken(MapsActivity.this);
        token = workWithToken.readToken();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = ll;

        new IOAsyncTask().execute("http://192.168.31.187:8083/user/preview?page=0&size=20");

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        // TODO: Before enabling the My Location layer, you must request
        // location permission from the user. This sample does not include
        // a request for location permission.
        mMap = map;

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMarkerClickListener(this);

        Location location = getLastKnownLocation();
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 11);
            mMap.animateCamera(cameraUpdate);
            moment_loc = location;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
                .show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        Preview preview = (Preview) marker.getTag();

        liner = findViewById(R.id.singleLiner);
        ScrollView scrollView = findViewById(R.id.mapsEventsContainer);
        scrollView.scrollTo(0,0);

        liner.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(liner.getContext());

        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(getApplicationContext());

        selectedGenres = new ArrayList<>();
        genresToSend = new ArrayList<>();

        tappedButtons = 0;

        surveyButtons = new ArrayList<>();
        View newGenreButton = inflater.inflate(R.layout.element, null);

        ImageView genreImage = newGenreButton.findViewById(R.id.eventImageView);
        TextView dateTextView = newGenreButton.findViewById(R.id.dateTextView);
        TextView sphereTextView = newGenreButton.findViewById(R.id.sphereTextView);
        TextView freeTextView = newGenreButton.findViewById(R.id.freeTextView);
        TextView eventHeader = newGenreButton.findViewById(R.id.eventHeader);

        imageLoader.displayImage(preview.jpgUrl, genreImage);
        sphereTextView.setText(preview.sphere.get(0));
        freeTextView.setText(preview.free.toString());

        SimpleDateFormat sddd = new SimpleDateFormat("d MMMM");
        dateTextView.setText(sddd.format(new Date(Long.valueOf(preview.date_from_timestamp) * 1000)));

        if (preview.free) {
            freeTextView.setText("Бесплатно");
        } else {
            freeTextView.setText("");
        }
        eventHeader.setText(preview.title);


        surveyButtons.add(newGenreButton.findViewById(R.id.imageButton));
        selectedGenres.add(false);

        newGenreButton.setId(preview.idItem);
        newGenreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, FullInfoActivity.class);
                intent.putExtra("id", String.valueOf(v.getId()));
                startActivity(intent);
            }
        });

        liner.addView(newGenreButton);

        return false;
    }

    final LocationListener ll = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            moment_loc = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onClick(View v) {

    }

    class IOAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return sendData(params[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            String strr = response;
            Gson gson = new Gson();
            List<Preview> previews = stringToArray(strr, Preview[].class);
            System.out.println("DDD "+previews.get(0).date);
            setData(previews);
        }
    }

    private void setData(List<Preview> previews) {
        liner = findViewById(R.id.liner);

        LayoutInflater inflater = LayoutInflater.from(liner.getContext());

        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(getApplicationContext());

        selectedGenres = new ArrayList<>();
        genresToSend = new ArrayList<>();

        tappedButtons = 0;

        surveyButtons = new ArrayList<>();
        for (int i=0; i<previews.size(); i++) {
            View newGenreButton = inflater.inflate(R.layout.element, null);

            ImageView genreImage = newGenreButton.findViewById(R.id.eventImageView);
            TextView dateTextView = newGenreButton.findViewById(R.id.dateTextView);
            TextView sphereTextView = newGenreButton.findViewById(R.id.sphereTextView);
            TextView freeTextView = newGenreButton.findViewById(R.id.freeTextView);
            TextView eventHeader = newGenreButton.findViewById(R.id.eventHeader);

            imageLoader.displayImage(previews.get(i).jpgUrl, genreImage);
            sphereTextView.setText(previews.get(i).sphere.get(0));
            freeTextView.setText(previews.get(i).free.toString());

            SimpleDateFormat sddd = new SimpleDateFormat("d MMMM");
            dateTextView.setText(sddd.format(new Date(Long.valueOf(previews.get(i).date_from_timestamp) * 1000)));

            if (previews.get(i).free){
                freeTextView.setText("Бесплатно");
            }
            else {
                freeTextView.setText("");
            }
            eventHeader.setText(previews.get(i).title);


            surveyButtons.add(newGenreButton.findViewById(R.id.imageButton));
            selectedGenres.add(false);

            newGenreButton.setId(previews.get(i).idItem);
            newGenreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MapsActivity.this, FullInfoActivity.class);
                    intent.putExtra("id", String.valueOf(v.getId()));
                    startActivity(intent);
                }
            });

            mMap.addMarker(new MarkerOptions().position(new LatLng(previews.get(i).lat, previews.get(i).lon)).title(previews.get(i).title)).setTag(previews.get(i));

            liner.addView(newGenreButton);
        }
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    public String sendData(String str){
        try {
//            RequestBody formBody = new FormBody.Builder()
//                    .add("LatLong", str)
//                    .build();

            Request request = new Request.Builder()
                    .url(str)
                    .header("Authorization","Bearer "+token)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}