import React from 'react';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import ListSubheader from '@material-ui/core/ListSubheader';
import DashboardIcon from '@material-ui/icons/Dashboard';
import Sensor from '@material-ui/icons/SettingsRemote';
import Bill from '@material-ui/icons/MonetizationOn';
import Logs from '@material-ui/icons/Update';
import LayersIcon from '@material-ui/icons/Layers';
import HistoryIcon from '@material-ui/icons/History';


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
        <Sensor/>
      </ListItemIcon>
      <ListItemText primary="Sensor" />
    </ListItem>
    <ListItem button>
      <ListItemIcon>
        <Bill />
      </ListItemIcon>
      <ListItemText primary="Bill" />
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

export const secondaryListItems = (
  <div>
    <ListSubheader inset>Bills History</ListSubheader>
    <ListItem button>
      <ListItemIcon>
        <HistoryIcon />
      </ListItemIcon>
      <ListItemText primary="Current month" />
    </ListItem>
    <ListItem button>
      <ListItemIcon>
        <HistoryIcon />
      </ListItemIcon>
      <ListItemText primary="Last month" />
    </ListItem>
    <ListItem button>
      <ListItemIcon>
        <HistoryIcon />
      </ListItemIcon>
      <ListItemText primary="Last Year" />
    </ListItem>
  </div>
);