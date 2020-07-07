import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Title from './Title';
import CostIcon from '@material-ui/icons/MonetizationOn';
import KwhIcon from '@material-ui/icons/FlashOnOutlined';
import HoursIcon from '@material-ui/icons/AccessTimeOutlined';
import DaysIcon from '@material-ui/icons/DateRangeOutlined';
import Grid from '@material-ui/core/Grid';

const useStyles = makeStyles(theme => ({
  root: {
    flexGrow: 1,
  },
  depositContext: {
    flex: 1,
  },
  item: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'auto',
    padding: theme.spacing(0.1)
  },
}));

export default function Cost(props) {
  const classes = useStyles();
  const { summaryCost } = props
  console.log('summaryCost', summaryCost)
  return (
    <React.Fragment className={classes.depositContext}>
      <Typography component="p" variant="h6">
        <Grid container spacing={12}>
          <Grid item xs={6} className={classes.item}><Title>Today</Title></Grid>
          <Grid item xs={6} className={classes.item}><Title>{summaryCost.title}</Title></Grid>
          <Grid item xs={6} className={classes.item}><CostIcon />{summaryCost.cost_day}</Grid>
          <Grid item xs={6} className={classes.item}><CostIcon />{summaryCost.cost_month}</Grid>
          <Grid item xs={6} className={classes.item}><KwhIcon />{summaryCost.kwh_day}</Grid>
          <Grid item xs={6} className={classes.item}><KwhIcon />{summaryCost.kwh_month}</Grid>
          <Grid item xs={6} className={classes.item}><HoursIcon />{summaryCost.hours_day}</Grid>
          <Grid item xs={6} className={classes.item}><DaysIcon />{summaryCost.days_month}</Grid>
        </Grid>
      </Typography>
    </React.Fragment>
  );
}