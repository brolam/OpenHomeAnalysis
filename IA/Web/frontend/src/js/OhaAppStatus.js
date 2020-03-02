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
  const [summaryCostDay, setSummaryCostDay] = useState({});
  const [sensorSeriesData, setSensorSeriesData] = useState([]);
  const [sensorRecentLogsData, setSensorRecentLogsData] = useState([]);
  const [seconds, setSeconds] = useState(0);

  setTimeout(() => setSeconds(seconds + 1), 15000);

  useEffect(() => {
    apiSensor.getSensorList(token).then(res => {
      setSensorListData(res);
      console.log('Sensores :', res);
    });
  }, [token]);

  useEffect(() => {

    apiSensor.getSensorSeriesPerHour(token, '96c9286b-7c30-42a9-863e-b60965845a66', 2020, 2, 1).then(res => {
      setSensorSeriesData(res);
      console.log('Sensores :', res);
    });

    apiSensor.getSensorSummaryCostDay(token, '96c9286b-7c30-42a9-863e-b60965845a66', 2020, 2, 1).then(res => {
      setSummaryCostDay(res);
      console.log('summaryCostDay :', res);
    });

    apiSensor.getSensorRecentLogs(token, '96c9286b-7c30-42a9-863e-b60965845a66').then(res => {
      setSensorRecentLogsData(res);
      console.log('sensorRecentLogsData :', res);
    });
  }, [token, seconds]);

  return { sensorListData, summaryCostDay, sensorSeriesData, sensorRecentLogsData };
}