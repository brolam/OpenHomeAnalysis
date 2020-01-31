import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import MenuItem from '@material-ui/core/MenuItem';
import Menu from '@material-ui/core/Menu';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import PropTypes from 'prop-types'

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


export default function OhaSensorsSelect(props) {
  const classes = useStyles();
  const {sensorListData} = props 
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [selectedIndex, setSelectedIndex] = React.useState(null);
  const sensorSelectName = selectedIndex == null? "": sensorListData[selectedIndex].name

  const handleClickListItem = event => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuItemClick = (event, index) => {
    setSelectedIndex(index);
    setAnchorEl(null);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <div className={classes.root}>
      <IconButton type="submit" className={classes.iconButton} aria-label="Select a Sensor" onClick={handleClickListItem}>
        <MenuIcon />
      </IconButton>
      <div className={classes.item}>
        <Typography variant="body1">Sensor</Typography>
        <Typography variant="h5" component="h2" gutterBottom >{sensorSelectName} </Typography>
      </div>
      <Menu
        id="lock-menu"
        anchorEl={anchorEl}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        {sensorListData.map((sensor, index) => (
          <MenuItem
            key={sensor.url}
            selected={index === selectedIndex}
            onClick={event => handleMenuItemClick(event, index)}
          >
            {sensor.name}
          </MenuItem>
        ))}
      </Menu>
    </div>
  );
}

OhaSensorsSelect.propTypes = {
  sensorListData: PropTypes.array.isRequired
}