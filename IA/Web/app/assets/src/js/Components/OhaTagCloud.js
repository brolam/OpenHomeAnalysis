import React from 'react'
import { TagCloud } from 'react-tagcloud'
import { makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';

const useStyles = makeStyles(theme => ({
  margin: {
    margin: theme.spacing(2),
  },
  padding: {
    padding: theme.spacing(0, 2),
  },
}));

const data = [
  { value: 'Jantar', count: 23.94 },
  { value: 'Banho', count: 38.94 },
  { value: 'Almoço', count: 12.94 },
  { value: 'Café', count: 11.94 },
  { value: 'Escritório', count: 33.94 },
  { value: 'Ar Condicionado do Escritório', count: 20.94 },
  { value: 'Geladeria', count: 22.94 },
  { value: 'Televisão', count: 7.94 },
  { value: 'Airflay', count: 1.94 },
  { value: 'Ar Condicionado da Suite', count: 27.94 },
  { value: 'Chuveiro da Suite', count: 50.55 },
  { value: 'Forno Eletrico', count: 15.94 },
  { value: 'Fritadeira', count: 30.94 },
  { value: 'Ferro de passar roupa', count: 11.94 },
]

/* CSS:
@keyframes blinker {
  50% { opacity: 0.0; }
}
*/




export default function OhaTagCloud(props) {

  const classes = useStyles();
  // custom renderer is function which has tag, computed font size and
  // color as arguments, and returns react component which represents tag
  const customRenderer = (tag, size, color) => (
    <Button key={tag.value} variant="outlined"
      style={{
        animation: 'blinker 6s linear infinite',
        animationDelay: `${Math.random() * 2}s`,
        fontSize: `${size / 2}em`,
        border: `2px solid ${color}`,
        margin: '3px',
        padding: '3px',
        display: 'inline-block',
        color: 'black',
      }}
    >{tag.value}</Button>
    /*
    <span
      key={tag.value}
      style={{
        animation: 'blinker 6s linear infinite',
        animationDelay: `${Math.random() * 2}s`,
        fontSize: `${size / 2}em`,
        border: `2px solid ${color}`,
        margin: '3px',
        padding: '3px',
        display: 'inline-block',
        color: 'black',
      }}
    >
      {tag.value}
      <span
        style={{
          fontSize: `50%`,
          margin: '1px',
          padding: '1px',
          color: `${color}`,
        }}
      >
        {` R$ ${tag.count}`}
      </span>
    </span>
    */
  )
  return (< TagCloud tags={data}
    minSize={1} maxSize={8}
    renderer={customRenderer}
    onClick={tag => alert(`'${tag.value}' was selected!`)} />
  )
}