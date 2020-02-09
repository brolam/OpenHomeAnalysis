import React from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import IconButton from '@material-ui/core/IconButton'
import MoreIcon from '@material-ui/icons/MoreVert';
import OpenSenorsSelect from './OhaSensorsSelect'
import OhaSensoresStatus from './OhaSensorsStatus'
import { AppConsoleStatus } from '../OhaAppStatus'
import {
  ResponsiveContainer, BarChart, Bar, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend,
} from 'recharts';

const useStyles = makeStyles(theme => ({
  root: {
    display: 'flex',
    flexDirection: 'column',
    minHeight: '100vh',
  },
  main: {
    marginTop: theme.spacing(8),
    marginBottom: theme.spacing(2),
    alignItems: 'center',
    width: '100%',
    height: '100%'
  },
  footer: {
    padding: theme.spacing(1, 1),
    marginTop: 'auto',
    backgroundColor:
      theme.palette.type === 'dark' ? theme.palette.grey[800] : theme.palette.grey[200],
  },
  sensorRoot: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-between'

  },
  sensorMenu: {
    flex: 1
  },

  sensorStatus: {
    flex: 1
  },

  sensorOptions: {
    margin: theme.spacing(2),
    width: theme.spacing(7),
    height: theme.spacing(7),
  },

}));

export default function App(props) {
  const classes = useStyles();
  const appConsoleStatus = AppConsoleStatus(props.token)


  return (
    <div className={classes.root}>
      <CssBaseline />
      <Container component="main" className={classes.main} >
        <div style={{ width: '100%', height: 300 }}>
          <ResponsiveContainer>
            <BarChart
              width={500}
              height={300}
              data={appConsoleStatus.sensorSeriesData}
              margin={{
                top: 20, right: 30, left: 20, bottom: 5,
              }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="day" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="kwh1" stackId="a" fill="#8884d8" />
              <Bar dataKey="kwh2" stackId="a" fill="#82ca9d" />
              <Bar dataKey="kwh3" stackId="a" fill="#82rf8d" />

            </BarChart>
          </ResponsiveContainer>
        </div>

      </Container>
      <footer className={classes.footer}>
        <div className={classes.sensorRoot}>
          <OpenSenorsSelect className={classes.sensorMenu} sensorListData={appConsoleStatus.sensorListData} />
          <OhaSensoresStatus className={classes.sensorStatus} />
          <IconButton className={classes.sensorOptions} color="inherit">
            <MoreIcon />
          </IconButton>
        </div>
      </footer>
    </div>
  )
}
