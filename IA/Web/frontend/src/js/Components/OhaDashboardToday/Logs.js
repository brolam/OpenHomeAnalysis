import React from 'react';
import Link from '@material-ui/core/Link';
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Title from './Title';

// Generate Order Data
function createData(id, hour, phase1, phase2, phase3, amount) {
  return { id, hour, phase1, phase2, phase3, amount };
}

const rows = [
  createData(0, '22:52:10', 312.44, 312.44, 312.44, 312.44),
  createData(1, '22:52:20', 866.99, 312.44, 312.44, 312.44),
  createData(2, '22:52:30', 100.81, 312.44, 312.44, 312.44),
  createData(3, '22:52:40', 654.39, 312.44, 312.44, 312.44),
  createData(4, '22:52:50', 212.79, 312.44, 312.44, 312.44),
];

function preventDefault(event) {
  event.preventDefault();
}

const useStyles = makeStyles(theme => ({
  seeMore: {
    marginTop: theme.spacing(3),
  },
}));

export default function Logs() {
  const classes = useStyles();
  return (
    <React.Fragment>
      <Title>Latest Logs</Title>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Hour</TableCell>
            <TableCell>Phase 01</TableCell>
            <TableCell>Phase 02</TableCell>
            <TableCell>Phase 03</TableCell>
            <TableCell align="right">Amount</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map(row => (
            <TableRow key={row.id}>
              <TableCell>{row.hour}</TableCell>
              <TableCell>{row.phase1}</TableCell>
              <TableCell>{row.phase2}</TableCell>
              <TableCell>{row.phase3}</TableCell>
              <TableCell align="right">{row.amount}</TableCell>
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