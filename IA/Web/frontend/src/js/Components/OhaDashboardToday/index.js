import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';
import Chart from './Chart';
import Bill from './Bill';
import Logs from './Logs';

const useStyles = makeStyles(theme => ({
  
  paper: {
    padding: theme.spacing(2),
    display: 'flex',
    overflow: 'auto',
    flexDirection: 'column',
  },
}));

export default function Index(props) {
  const classes = useStyles();
  const fixedHeightPaper = props.fixedHeightPaper;

  return (
    <Grid container spacing={3}>
      {/* Chart */}
      <Grid item xs={12} md={8} lg={9}>
        <Paper className={fixedHeightPaper}>
          <Chart /> 
        </Paper>
      </Grid>
      {/* Recent Bill */}
      <Grid item xs={12} md={4} lg={3}>
        <Paper className={fixedHeightPaper}>
          <Bill />
        </Paper>
      </Grid>
      {/* Recent Logs */}
      <Grid item xs={12}>
        <Paper className={classes.paper}>
          <Logs />
        </Paper>
      </Grid>
    </Grid>
  );
}