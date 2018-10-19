package angel.weatherpoint.fragments;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import angel.weatherpoint.API.API;
import angel.weatherpoint.API.WeatherService;
import angel.weatherpoint.R;
import angel.weatherpoint.models.Weather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, View.OnClickListener {

    private View rootView;

    private MapView mapView;
    private GoogleMap gMap;
    private Marker weatherMarker;
    private List<Address> addresses;
    private Geocoder geoCoder;

    private EditText inputSearchBar;
    private ImageButton btnSearchBar;
    private Button btnSearch;
    private ImageView imgIcon;
    private TextView textTemperature;
    private TextView textMinTemp;
    private TextView textMaxTemp;
    private TextView textCity;
    private TextView textInfo;
    private LinearLayout weatherInfoWrapper;
    private Button btnUnits;

    private boolean isCelsius;
    private WeatherService service;
    private Call<Weather> call;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        service = API.getAPI().create(WeatherService.class);
        setUI();
        isCelsius = true;
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = rootView.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        weatherMarker = null;
        gMap.setOnMapLongClickListener(this);
        gMap.setOnMapClickListener(this);
        geoCoder = new Geocoder(getContext(), Locale.getDefault());
    }

    private void setUI() {
        inputSearchBar = rootView.findViewById(R.id.inputSearchBar);
        btnSearchBar = rootView.findViewById(R.id.btnSearchBar);
        btnSearch = rootView.findViewById(R.id.btnSearch);
        imgIcon = rootView.findViewById(R.id.imgIcon);
        textTemperature = rootView.findViewById(R.id.textTemperature);
        textMinTemp = rootView.findViewById(R.id.textMinTemp);
        textMaxTemp = rootView.findViewById(R.id.textMaxTemp);
        textCity = rootView.findViewById(R.id.textCity);
        textInfo = rootView.findViewById(R.id.textInfo);
        weatherInfoWrapper = rootView.findViewById(R.id.weatherInfoWrapper);
        btnUnits = rootView.findViewById(R.id.btnUnits);

        btnUnits.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnSearchBar.setOnClickListener(this);

        inputSearchBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    onClick(btnSearchBar);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        setMarker(latLng);
        if (btnSearch.getVisibility() == View.VISIBLE)
            btnSearch.setVisibility(View.INVISIBLE);
        weatherInfoWrapper.setVisibility(View.VISIBLE);
        loadWeather(null);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        setMarker(latLng);
        if (weatherInfoWrapper.getVisibility() == View.VISIBLE)
            weatherInfoWrapper.setVisibility(View.INVISIBLE);
        btnSearch.setText("Weather in " + getAddress(latLng));
        btnSearch.setVisibility(View.VISIBLE);
    }

    public void setMarker(LatLng latLong) {
        if (weatherMarker == null) {
            weatherMarker = gMap.addMarker(new MarkerOptions().position(latLong).title(getAddress(latLong)));
        } else {
            weatherMarker.remove();
            weatherMarker = gMap.addMarker(new MarkerOptions().position(latLong).title(getAddress(latLong)));
        }
    }

    private String getAddress(LatLng latLong) {
        String address = "";
        try {
            addresses = geoCoder.getFromLocation(latLong.latitude, latLong.longitude, 1);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error getting address, please try again", Toast.LENGTH_LONG).show();
        }
        if (addresses.size() == 0)
            address = "the middle of the ocean";
        else {
            if (addresses.get(0).getLocality() == null)
                address = "a random place in ";
            else
                address = addresses.get(0).getLocality() + ", ";
            address += addresses.get(0).getCountryName();
        }
        return address;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSearchBar:
                loadWeather(inputSearchBar.getText().toString());
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.btnSearch:
                loadWeather(null);
                break;
            case R.id.btnUnits:
                swapUnits();
                break;
        }
    }

    private void setResults(Weather weather) {
        String unit;
        if (isCelsius)
            unit = " ºC";
        else unit = "ºF";
        textCity.setText(weather.getCity());
        textInfo.setText(weather.getDescription());
        textMinTemp.setText(weather.getMinTemp() + unit);
        textMaxTemp.setText(weather.getMaxTemp() + unit);
        textTemperature.setText(weather.getTemperature() + unit);
        weatherInfoWrapper.setVisibility(View.VISIBLE);
        Picasso.get().load(API.BASE_URL_ICONS + weather.getIcon() + ".png").into(imgIcon);
    }

    private void swapUnits() {
        if (!isCelsius) {
            isCelsius = true;
            btnUnits.setText("ºF");
            Toast.makeText(getContext(), "Temperature will be shown in Celsius", Toast.LENGTH_SHORT).show();
            return;
        } else {
            isCelsius = false;
            btnUnits.setText("ºC");
            Toast.makeText(getContext(), "Temperature will be shown in Fahrenheit", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadWeather(String city) {
        if (city != null) {
            if (isCelsius)
                call = service.getWeather(city, API.APPKEY, "metric");
            else call = service.getWeather(city, API.APPKEY);
        } else {
            if (isCelsius)
                call = service.getWeather(weatherMarker.getPosition().latitude, weatherMarker.getPosition().longitude, API.APPKEY, "metric");
            else call = service.getWeather(weatherMarker.getPosition().latitude, weatherMarker.getPosition().longitude, API.APPKEY);
        }
        call.enqueue(new Callback<Weather>() {//LLAMADA ASINCRONA
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                setResults(response.body());
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Toast.makeText(getContext(), "Connection error", Toast.LENGTH_LONG).show();
            }
        });
    }
}