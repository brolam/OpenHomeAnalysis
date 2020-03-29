import { useState, useEffect } from 'react';
import { getToken, removeToken } from './OhaLocalStore'
import apiSensor from './OhaApiSensor'

export const UserLoginStatus = {};

UserLoginStatus.isLogin = function () {
  return getToken() != null;
}

UserLoginStatus.getToken = function () {
  return getToken();
}

export function AppConsoleStatus(token, sensorId) {
  const [sensorListData, setSensorListData] = useState([]);
  const [summaryCostDay, setSummaryCostDay] = useState({});
  const [sensorSeriesData, setSensorSeriesData] = useState([]);
  const [sensorRecentLogsData, setSensorRecentLogsData] = useState([]);
  const [seconds, setSeconds] = useState(0);

  function JsonOrLogoff(response, then) {
    if (response.status >= 400) {
      removeToken();
      return
    }

    response.json().then((json) => {
      then(json);
    })
  };

  setTimeout(() => setSeconds(seconds + 1), 15000);

  useEffect(() => {
    const today = new Date();
    const [todayYear, todayMonth, todayDay] = [today.getFullYear(), today.getMonth() + 1, today.getDate()]
    if (!UserLoginStatus.isLogin()) {
      document.location.reload(true);
      return;
    }

    apiSensor.getSensorList(token).then(res => JsonOrLogoff(res, (json) => {
      setSensorListData(json);
    }));

    apiSensor.getSensorSeriesPerHour(token, sensorId, todayYear, todayMonth, todayDay).then(res => JsonOrLogoff(res, (json) => {
      setSensorSeriesData(json);
    }));

    apiSensor.getSensorSummaryCostDay(token, sensorId, todayYear, todayMonth, todayDay).then(res => JsonOrLogoff(res, (json) => {
      setSummaryCostDay(json);
    }));

    apiSensor.getSensorRecentLogs(token, sensorId).then(res => JsonOrLogoff(res, (json) => {
      setSensorRecentLogsData(json);
    }));

  }, [token, seconds]);

  return { sensorListData, summaryCostDay, sensorSeriesData, sensorRecentLogsData };
}