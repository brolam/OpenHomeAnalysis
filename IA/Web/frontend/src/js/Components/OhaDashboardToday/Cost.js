import React from 'react';
import Link from '@material-ui/core/Link';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Title from './Title';
import CostIcon from '@material-ui/icons/MonetizationOn';

function preventDefault(event) {
  event.preventDefault();
}

const useStyles = makeStyles({
  depositContext: {
    flex: 1,
  },
});

export default function Cost(props) {
  const classes = useStyles();
  const { summaryCost } = props
  console.log('summaryCost', summaryCost)
  return (
    <React.Fragment>
      <Title>Today</Title>
      <Typography component="p" variant="h4">
        <CostIcon /> {summaryCost.total_day}
      </Typography>
      <Title>{summaryCost.title}</Title>
      <Typography component="p" variant="h4">
        <CostIcon /> {summaryCost.total_month}
      </Typography>
      <div>
        <Link color="primary" href="#" onClick={preventDefault}>
          More
        </Link>
      </div>
    </React.Fragment>
  );
}