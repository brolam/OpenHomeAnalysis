import React from 'react';
import Link from '@material-ui/core/Link';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Title from './Title';
import Box from '@material-ui/core/Box';
import CostIcon from '@material-ui/icons/MonetizationOn';
import KwhIcon from '@material-ui/icons/FlashOnOutlined';
import HoursIcon from '@material-ui/icons/AccessTimeOutlined';
import DaysIcon from '@material-ui/icons/DateRangeOutlined';



function preventDefault(event) {
  event.preventDefault();
}

const useStyles = makeStyles(theme => ({
  depositContext: {
    flex: 1,
  },
  box: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-start',
    overflow: 'auto',
  },
}));

export default function Cost(props) {
  const classes = useStyles();
  const { summaryCost } = props
  console.log('summaryCost', summaryCost)
  return (
    <React.Fragment className={classes.depositContext}>
      <Title>Today</Title>
      <Typography component="p" variant="h6">
        <Box display="flex" flexWrap="wrap" alignContent="flex-start" p={0} m={0}>
          <Box className={classes.box}>
            <CostIcon /> {summaryCost.cost_day}
          </Box>
          <Box className={classes.box}>
            <KwhIcon /> {summaryCost.kwh_day}
          </Box>
          <Box className={classes.box}>
            <HoursIcon /> {summaryCost.hours_day}
          </Box>
        </Box>
      </Typography>
      <Title>{summaryCost.title}</Title>
      <Typography component="p" variant="h6">
        <Box display="flex" flexWrap="wrap" alignContent="flex-start" p={0} m={0}>
          <Box className={classes.box}>
            <CostIcon className={classes.box} /> {summaryCost.cost_month}
          </Box>
          <Box className={classes.box}>
            <KwhIcon className={classes.box} /> {summaryCost.kwh_month}
          </Box>
          <Box className={classes.box}>
            <DaysIcon className={classes.item} /> {summaryCost.days_month}
          </Box>
        </Box>

      </Typography>
      <div>
        <Link color="primary" href="#" onClick={preventDefault}>
          More
        </Link>
      </div>
    </React.Fragment>
  );
}