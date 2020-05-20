import React from 'react';
import { useTheme } from '@material-ui/core/styles';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import Title from './Title';

export default function Chart(props) {
  const theme = useTheme();
  const { sensorSeriesData } = props

  return (
    <React.Fragment>
      <Title>Today KWH</Title>
      <ResponsiveContainer >
        <LineChart
          data={sensorSeriesData}
          margin={{
            top: 16,
            right: 16,
            bottom: 0,
            left: 0,
          }}
        >
          <XAxis dataKey="x" stroke={theme.palette.text.secondary} interval="preserveStartEnd" />
          <YAxis stroke={theme.palette.text.secondary} interval="preserveStartEnd" />
          <Tooltip />
          <Line type="monotone" dataKey="y" stroke={theme.palette.primary.main} dot={false} />
        </LineChart>
      </ResponsiveContainer>
    </React.Fragment>
  );
}