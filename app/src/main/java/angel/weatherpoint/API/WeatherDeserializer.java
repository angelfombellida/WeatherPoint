package angel.weatherpoint.API;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import angel.weatherpoint.models.Weather;

public class WeatherDeserializer implements JsonDeserializer<Weather> {
    @Override
    public Weather deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int temperature = json.getAsJsonObject().get("main").getAsJsonObject().get("temp").getAsInt();
        int minTemp = json.getAsJsonObject().get("main").getAsJsonObject().get("temp_min").getAsInt();
        int maxTemp = json.getAsJsonObject().get("main").getAsJsonObject().get("temp_max").getAsInt();
        String description = json.getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();
        String icon = json.getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString();
        String city = json.getAsJsonObject().get("name").getAsString();

        return new Weather(temperature, description, minTemp, maxTemp, icon, city);
    }
}
