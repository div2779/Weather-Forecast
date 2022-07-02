package com.example.weatherforecast;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    SearchView searchView;
    String jsonData;
    TextView date1, temp1, min1, max1, hum1, weather1;
    TextView date2, temp2, min2, max2, hum2, weather2;
    TextView date3, temp3, min3, max3, hum3, weather3;
    CardView card1, card2, card3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        
        card1 = findViewById(R.id.Card1);
        date1 = findViewById(R.id.Date1);
        temp1 = findViewById(R.id.Temp1Field);
        min1 = findViewById(R.id.min1Field);
        max1 = findViewById(R.id.max1Field);
        hum1 = findViewById(R.id.hum1Field);
        weather1 = findViewById(R.id.weather1Field);

        card2 = findViewById(R.id.Card2);
        date2 = findViewById(R.id.Date2);
        temp2 = findViewById(R.id.Temp2Field);
        min2 = findViewById(R.id.min2Field);
        max2 = findViewById(R.id.max2Field);
        hum2 = findViewById(R.id.hum2Field);
        weather2 = findViewById(R.id.weather2Field);

        card3 = findViewById(R.id.Card3);
        date3 = findViewById(R.id.Date3);
        temp3 = findViewById(R.id.Temp3Field);
        min3 = findViewById(R.id.min3Field);
        max3 = findViewById(R.id.max3Field);
        hum3 = findViewById(R.id.hum3Field);
        weather3 = findViewById(R.id.weather3Field);



        searchView = findViewById(R.id.idSearchView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();

                List<Address> addressList = null;

                if (location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        Toast.makeText(MapsActivity.this, "Invalid place!! Please re-enter", Toast.LENGTH_SHORT).show();;
                    }

                    if(addressList.size()==0){
                        Toast.makeText(MapsActivity.this, "Invalid place!! Please re-enter", Toast.LENGTH_SHORT).show();;
                        return false;
                    }
                    Address address = addressList.get(0);

                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                    fillData(address.getLongitude(), address.getLatitude());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void fillData(double longitude, double latitude){
        okhttp3.OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/forecast?lat="
                        +latitude+"&lon="+longitude+"&appid="+BuildConfig.WEATHERMAP_API_KEY)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseString = response.body().string();
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            jsonData = responseString;
                            if (!jsonData.isEmpty()) {
                                JsonElement jsonElement = new JsonParser().parse(jsonData);
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                JsonArray list = jsonObject.getAsJsonArray("list");
                                JsonObject quote0 = list.get(0).getAsJsonObject();
                                JsonObject quote1 = list.get(8).getAsJsonObject();
                                JsonObject quote2 = list.get(16).getAsJsonObject();

                                JsonObject main0 = quote0.getAsJsonObject("main");
                                JsonObject Weather0 = quote0.getAsJsonArray("weather").get(0).getAsJsonObject();
                                String DateTime0 = quote0.get("dt_txt").toString();
                                JsonObject main1 = quote1.getAsJsonObject("main");
                                JsonObject Weather1 = quote1.getAsJsonArray("weather").get(0).getAsJsonObject();
                                String DateTime1 = quote1.get("dt_txt").toString();
                                JsonObject main2 = quote2.getAsJsonObject("main");
                                JsonObject Weather2 = quote2.getAsJsonArray("weather").get(0).getAsJsonObject();
                                String DateTime2 = quote2.get("dt_txt").toString();

                                setCards(card1,date1, temp1, min1, max1, hum1, weather1, DateTime0, main0, Weather0);
                                setCards(card2,date2, temp2, min2, max2, hum2, weather2, DateTime1, main1, Weather1);
                                setCards(card3,date3, temp3, min3, max3, hum3, weather3, DateTime2, main2, Weather2);
                            } else {
                                Log.d("json not read", "sad");
                            }
                        }
                    });
                } else {
                    System.out.println("Error");
                }
            }
        });
        client.connectionPool().evictAll();
    }

    private void setCards(CardView card, TextView date, TextView temp, TextView min,
                          TextView max, TextView hum, TextView weather, String DateTime, JsonObject Main, JsonObject Weather){
        DateTime = DateTime.replaceAll("^\"|\"$", "");
        date.setText(DateTime.substring(0, DateTime.length()-9));
        temp.setText(Main.get("temp").toString().replaceAll("^\"|\"$", ""));
        min.setText(Main.get("temp_min").toString().replaceAll("^\"|\"$", ""));
        max.setText(Main.get("temp_max").toString().replaceAll("^\"|\"$", ""));
        String humidity = Main.get("humidity").toString().replaceAll("^\"|\"$", "");
        humidity = humidity + '%';
        hum.setText(humidity);
        weather.setText(Weather.get("description").toString().replaceAll("^\"|\"$", ""));

        card.setVisibility(View.VISIBLE);
    }
}
