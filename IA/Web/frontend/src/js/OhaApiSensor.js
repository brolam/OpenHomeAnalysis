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

export default apiSensor;