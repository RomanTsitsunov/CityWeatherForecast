document.getElementById('weatherForecastForm').addEventListener('submit', event => getWeatherForecast(event));
let weatherChart = null;

async function getWeatherForecast(event) {
    event.preventDefault();
    const city = document.getElementById('cityInput').value;
    if (!city) {
        alert('Укажите город');
    }
    const response = await fetch(`/weather?city=${city}`, {
        method: 'GET'
    });
    if (response.ok) {
        const weatherInfo = await response.json();
        drawChart(weatherInfo)
    } else {
        document.getElementById('grafic').setAttribute('hidden', '');
        if (response.status === 403) {
            alert('Неверно указан город');
        }
        if (response.status === 500) {
            alert('Ошибка на стороне сервера');
        }
    }
}

function drawChart(weatherInfo) {
    document.getElementById('grafic').removeAttribute('hidden');

    const hours = Object.keys(weatherInfo);
    const temps = Object.values(weatherInfo);

    if (weatherChart !== null) {
        weatherChart.destroy();
    }

    weatherChart = new Chart(document.getElementById('weatherChart'), {
        type: 'bar',
        data: {
            labels: hours,
            datasets: [{
                label: 'Temperature °C',
                data: temps
            }]
        },
        options: {
            responsive: false,
            scales: {
                y: { beginAtZero: false }
            }
        }
    });
}