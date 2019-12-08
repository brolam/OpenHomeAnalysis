import React from 'react';
import ReactDOM from 'react-dom';
import CssBaseline from '@material-ui/core/CssBaseline';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import OpenSenorsSelect from './Components/OhaSensorsSelect'
import IconButton from '@material-ui/core/IconButton'
import MoreIcon from '@material-ui/icons/MoreVert';
import OhaSensoresStatus from './Components/OhaSensorsStatus'

const useStyles = makeStyles(theme => ({
  root: {
    display: 'flex',
    flexDirection: 'column',
    minHeight: '100vh',
  },
  main: {
    marginTop: theme.spacing(8),
    marginBottom: theme.spacing(2),
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

function App() {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <CssBaseline />
      <Container component="main" className={classes.main} >
        <Typography variant="h2" component="h1" gutterBottom>Open Home Analysis</Typography>
        <Typography variant="h5" component="h2" gutterBottom>Tell me.</Typography>
        <Typography variant="body1">What I can do for you?</Typography>
      </Container>
      <footer className={classes.footer}>
        <div className={classes.sensorRoot}>
          <OpenSenorsSelect className={classes.sensorMenu} />
          <OhaSensoresStatus className={classes.sensorStatus} />
          <IconButton className={classes.sensorOptions} color="inherit">
            <MoreIcon />
          </IconButton>
        </div>
      </footer>
    </div>
  )
}

ReactDOM.render(<App />, document.getElementById('react-app'));
