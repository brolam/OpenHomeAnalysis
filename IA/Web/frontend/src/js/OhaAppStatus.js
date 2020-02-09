import { useState, useEffect } from 'react';
import { getToken } from './OhaLocalStore'
import apiSensor from './OhaApiSensor'

export const UserLoginStatus = {};

UserLoginStatus.isLogin = function () {
  return getToken() != null;
}

UserLoginStatus.getToken = function () {
  return getToken();
}

export function AppConsoleStatus(token) {
  const [sensorListData, setSensorListData] = useState([]);
  const [sensorSeriesData, setSensorSeriesData] = useState([]);

  useEffect(() => {

    apiSensor.getSensorList(token).then(res => {
      setSensorListData(res);
      console.log('Sensores :', res);
    });
  }, [token]);

  useEffect(() => {

    apiSensor.getSensorSeries(token).then(res => {
      setSensorSeriesData(res);
      console.log('Sensores :', res);
    });
  }, [token]);

  return { sensorListData, sensorSeriesData };
}