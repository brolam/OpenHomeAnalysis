import React from 'react';
import Link from '@material-ui/core/Link';
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Title from './Title';

function preventDefault(event) {
  event.preventDefault();
}

const useStyles = makeStyles(theme => ({
  seeMore: {
    marginTop: theme.spacing(3),
  },
}));

export default function Logs(props) {
  const classes = useStyles();
  const { sensorRecentLogsData } = props
  return (
    <React.Fragment>
      <Title>Latest Logs</Title>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>When</TableCell>
            <TableCell>Duration</TableCell>
            <TableCell>Phase 01</TableCell>
            <TableCell>Phase 02</TableCell>
            <TableCell>Phase 03</TableCell>
            <TableCell align="right">Total Watts</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {sensorRecentLogsData.map(row => (
            <TableRow key={row.id}>
              <TableCell>{row.datetime}</TableCell>
              <TableCell>{row.duration}</TableCell>
              <TableCell>{row.phase1}</TableCell>
              <TableCell>{row.phase2}</TableCell>
              <TableCell>{row.phase3}</TableCell>
              <TableCell align="right">{row.total}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <div className={classes.seeMore}>
        <Link color="primary" href="#" onClick={preventDefault}>
          See more orders
        </Link>
      </div>
    </React.Fragment>
  );
}