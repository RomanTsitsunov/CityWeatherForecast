# Геокодинг города

## Описание
Программа представляет собой сервис, который будет получать текущий прогноз погоды для указанного города, используя открытое API (https://open-meteo.com). 
Сервис должен кэшировать данные о погоде в Redis, а также визуализировать данные о температуре в виде графика, отображающего изменения температуры по часам

### Frontend
- Стек: Vanilla JS, CSS, HTML
- Визуализировать изменения температуры в виде графика, показывающего температуру по часам в течение дня.

### Backend
- Стек: Java 21 (Maven)
- Кеширование данных о прогнозе погоды в Redis с временем жизни 15 минут

## Запуск
1. Клонирование проекта:
   ```bash
   git clone https://github.com/RomanTsitsunov/CityWeatherForecast
   cd CityWeatherForecast
   ```
   
2. Проверьте, что Redis запущен:
   ```bash
   redis-cli ping
   ```
   В ответ должны получить:
   ```
   PONG
   ```
   
3. Запуск проекта:
   ```bash
   mvn clean compile exec:java -Dexec.mainClass="doczilla.cityweatherforecast.Main"
   ```
   
4. Открыть http://localhost:8080/