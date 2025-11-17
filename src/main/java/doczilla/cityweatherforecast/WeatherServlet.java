package doczilla.cityweatherforecast;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class WeatherServlet extends HttpServlet {
    private final WeatherDao weatherDao = new WeatherDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String city = req.getParameter("city");
        if (city.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String reqPath = String.valueOf(((Request) req).getHttpURI());
        ObjectMapper objectMapper = new ObjectMapper();

        List<String> timeInfo;
        List<Double> temperatureInfo;
        Map<String, List<String>> weatherInfo = weatherDao.loadWeatherInfo(reqPath);
        if (weatherInfo == null) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format(AppProperties.get("city_geocoding_api"), city)))
                    .build();
            HttpResponse<String> response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            Map<String, Object> json = objectMapper.readValue(response.body(), Map.class);
            if (json.get("results") == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            Map<String, Object> cityInfo = (Map<String, Object>) ((List) json.get("results")).get(0);
            String latitude = String.valueOf(cityInfo.get("latitude"));
            String longitude = String.valueOf(cityInfo.get("longitude"));

            request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format(AppProperties.get("weather_report_api"), latitude, longitude)))
                    .build();
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            json = objectMapper.readValue(response.body(), Map.class);
            timeInfo = (List<String>) ((Map) json.get("hourly")).get("time");
            temperatureInfo = (List<Double>) ((Map) json.get("hourly")).get("temperature_2m");
            weatherDao.saveWeatherInfo(reqPath, timeInfo, temperatureInfo);
        } else {
            timeInfo = weatherInfo.get("time");
            temperatureInfo = weatherInfo.get("temperature_2m")
                    .stream()
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
        }

        Map<String, Double> dayWeatherInfo = convertToMap(timeInfo, temperatureInfo);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.println(objectMapper.writeValueAsString(dayWeatherInfo));
    }

    /**
     * @param timeInfo        часы
     * @param temperatureInfo температура
     * @return map, где ключ это часы, а значение это температура в этот час
     */
    private Map<String, Double> convertToMap(List<String> timeInfo, List<Double> temperatureInfo) {
        Map<String, Double> result = new TreeMap<>();
        for (int i = 0; i < timeInfo.size(); i++) {
            result.put(timeInfo.get(i), temperatureInfo.get(i));
        }
        return result;
    }
}
