package angel.weatherpoint.API;

import com.google.gson.GsonBuilder;

import angel.weatherpoint.models.Weather;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String APPKEY = "a3854fa6d05b55a5809e2920f0a81abf";
    public static final String BASE_URL_ICONS = "http://openweathermap.org/img/w/";
    private static Retrofit retrofit;

    public static Retrofit getAPI() {
        if (retrofit == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Weather.class, new WeatherDeserializer());

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(builder.create()))
                    .build();
        }
        return retrofit;
    }
}
