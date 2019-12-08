import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';

const useStyles = makeStyles(theme => ({
  root: {
    padding: theme.spacing(1),
    display: 'flex',
    alignItems: 'center',
  },
  item: {
    marginLeft: theme.spacing(1),
    flex: 1,
  },
  iconButton: {
    padding: 8,
  },
}));

export default function OhaSensorsSatus(props) {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <div className={classes.item}>
        <Typography variant="body1">Running</Typography>
        <Typography variant="h5" component="h2" gutterBottom >Last updated at 11:45hs</Typography>
      </div>
    </div>

  );
}