import React from 'react';
import Link from '@material-ui/core/Link';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Title from './Title';
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
  itens: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-start',
    paddingRight: theme.spacing(4),
    overflow: 'auto',
  },
  item: {
    marginRight: '8'
  },
}));

export default function Cost(props) {
  const classes = useStyles();
  const { summaryCost } = props
  console.log('summaryCost', summaryCost)
  return (
    <React.Fragment className={classes.depositContext}>
      <Title>Today</Title>
      <Typography component="p" variant="h6" className={classes.itens}>
        <CostIcon className={classes.item} /> {summaryCost.cost_day}
        <KwhIcon className={classes.item} /> {summaryCost.kwh_day}
        <HoursIcon className={classes.item} /> {summaryCost.hours_day}
      </Typography>
      <Title>{summaryCost.title}</Title>
      <Typography component="p" variant="h6" className={classes.itens} >
        <CostIcon className={classes.item} /> {summaryCost.cost_month}
        <KwhIcon className={classes.item} /> {summaryCost.kwh_month}
        <DaysIcon className={classes.item} /> {summaryCost.days_month}
      </Typography>
      <div>
        <Link color="primary" href="#" onClick={preventDefault}>
          More
        </Link>
      </div>
    </React.Fragment>
  );
}