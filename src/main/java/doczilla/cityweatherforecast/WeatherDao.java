package doczilla.cityweatherforecast;

import com.fasterxml.jackson.core.JsonProcessingException;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

public class WeatherDao {
    private final Jedis jedis = BDConnection.getConnection();

    /**
     * @param key запрос в виде "http://localhost:8080/weather?city={city}"
     * @return map, где ко ключу time хранятся следующие 24 часа, по ключу temperature_2m хранятся температуры в каждый из этих часов
     */
    public Map<String, List<String>> loadWeatherInfo(String key) throws JsonProcessingException {
        List<String> timeInfo = jedis.lrange(key + ":time", 0, -1);
        List<String> temperatureInfo = jedis.lrange(key + ":temperature_2m", 0, -1);
        if (timeInfo.isEmpty() || temperatureInfo.isEmpty()) {
            return null;
        }
        return Map.of("time", timeInfo, "temperature_2m", temperatureInfo);
    }

    /**
     * Сохраняет по ключу key + ":time" часы, по ключу key + ":temperature_2m" температуру, где key запрос в виде "http://localhost:8080/weather?city={city}"
     */
    public void saveWeatherInfo(String key, List<String> timeInfo, List<Double> temperatureInfo) {
        for (String time : timeInfo) {
            jedis.rpush(key + ":time", time);
        }
        jedis.expire(key + ":time", 60 * 15);
        for (Double temperature : temperatureInfo) {
            jedis.rpush(key + ":temperature_2m", String.valueOf(temperature));
        }
        jedis.expire(key + ":temperature_2m", 60 * 15);
    }
}
