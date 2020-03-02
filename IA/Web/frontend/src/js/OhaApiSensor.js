const url = "http://localhost:8000/api/"
let apiSensor = {};
const fetchOption = (method, token) => ({
  method,
  headers: {
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    'X-CSRFToken': '{{csrf_token}}',
    'Authorization': `Token ${token}`
  }
})

function encodeData(data) {
  return Object.keys(data).map((key) => {
    return [key, data[key]].map(encodeURIComponent).join("=");
  }).join("&");
}

function encodeJson(data) {
  return JSON.stringify(data)
}

apiSensor.getSensorList = (token) =>
  fetch(`${url}sensor/simple_list/`, fetchOption('GET', token)
  ).then(res => res.json())

apiSensor.getSensorSummaryCostDay = (token, sensorId, year, month, day) =>
  fetch(`${url}sensor/${sensorId}/summary_cost_day/${year}/${month}/${day}`, fetchOption('GET', token)
  ).then(res => res.json())


apiSensor.getSensorSeriesPerHour = (token, sensorId, year, month, day) =>
  fetch(`${url}sensor/${sensorId}/series_per_hour/${year}/${month}/${day}`, fetchOption('GET', token)
  ).then(res => res.json())

apiSensor.getSensorRecentLogs = (token, sensorId) =>
  fetch(`${url}sensor/${sensorId}/recent_logs`, fetchOption('GET', token)
  ).then(res => res.json())


export default apiSensor;