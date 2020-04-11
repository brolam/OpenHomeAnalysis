import React from 'react';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import ListSubheader from '@material-ui/core/ListSubheader';
import DashboardIcon from '@material-ui/icons/Dashboard';
import Sensor from '@material-ui/icons/SettingsRemote';
import CostIcon from '@material-ui/icons/MonetizationOn';
import Logs from '@material-ui/icons/Update';
import LayersIcon from '@material-ui/icons/Layers';

export const mainListItems = (
  <div>
    <ListItem button>
      <ListItemIcon>
        <DashboardIcon />
      </ListItemIcon>
      <ListItemText primary="Dashboard" />
    </ListItem>
    <ListItem button>
      <ListItemIcon>
        <CostIcon />
      </ListItemIcon>
      <ListItemText primary="CostIcon" />
    </ListItem>
    <ListItem button>
      <ListItemIcon>
        <Logs />
      </ListItemIcon>
      <ListItemText primary="Logs" />
    </ListItem>
    <ListItem button>
      <ListItemIcon>
        <LayersIcon />
      </ListItemIcon>
      <ListItemText primary="Integrations" />
    </ListItem>
  </div>
);

export function SensorListItems(props) {
  const { sensorStatus, setSensorStatus } = props

  const onSelectSensor = (sensorId) => {
    const newSensorStatus = { data: sensorStatus.data, selectedId: sensorId };
    setSensorStatus(newSensorStatus)
  }

  return (
    <div>
      <ListSubheader inset>Sensors</ListSubheader>
      {sensorStatus.data.map(sensor => (
        <ListItem button key={sensor.id}
          selected={sensor.id == sensorStatus.selectedId}
          onClick={() => onSelectSensor(sensor.id)}
        >
          <ListItemIcon>
            <Sensor />
          </ListItemIcon>
          <ListItemText primary={sensor.name} />
        </ListItem>
      ))}
    </div>
  )
};