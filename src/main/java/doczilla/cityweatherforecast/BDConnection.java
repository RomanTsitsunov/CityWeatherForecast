package doczilla.cityweatherforecast;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class BDConnection {
    public static Jedis getConnection() {
        try (JedisPool pool = new JedisPool(AppProperties.get("redis.host"), Integer.parseInt(AppProperties.get("redis.port")))) {
            return pool.getResource();
        }
    }
}