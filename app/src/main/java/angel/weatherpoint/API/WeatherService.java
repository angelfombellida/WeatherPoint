package angel.weatherpoint.API;

import angel.weatherpoint.models.Weather;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("weather")
    Call<Weather> getWeather(@Query("q") String city, @Query("appid") String key);

    @GET("weather")
    Call<Weather> getWeather(@Query("q") String city, @Query("appid") String key, @Query("units") String units);

    @GET("weather")
    Call<Weather> getWeather(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String key);

    @GET("weather")
    Call<Weather> getWeather(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String key, @Query("units") String units);
}
