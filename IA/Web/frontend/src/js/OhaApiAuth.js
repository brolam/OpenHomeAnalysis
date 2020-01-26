const url = "http://localhost:8000/"
let apiAuth = {};
const fetchOption = (method, body) => ({
  method,
  headers: {
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    'X-CSRFToken': '{{csrf_token}}'
  },
  body: body
})

function encodeData(data) {
  return Object.keys(data).map((key) => {
    return [key, data[key]].map(encodeURIComponent).join("=");
  }).join("&");
}

function encodeJson(data) {
  return JSON.stringify(data)
}

apiAuth.login = (userName, password) =>
  fetch(`${url}token-auth/`, fetchOption(
    'POST',
    encodeJson({ username: userName, password: password }))
  ).then(res => res.json())

apiAuth.tokenRefresh = (token) =>
  fetch(`${url}token-auth-refresh/`, fetchOption(
    'POST',
    encodeJson({ token: token }))
  ).then(res => res.json())


export default apiAuth;