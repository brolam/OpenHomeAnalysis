import React from 'react';
import ReactDOM from 'react-dom';
import App from './Components/OhaApp'
import UserSingIn from './Components/OhaUserSignIn'
import { useLoingIn } from './OhaAppStatus'


function Index(props) {
  const isUserLoginIn = useLoingIn('123');
  if (isUserLoginIn) {
    return (<App />)
  }
  return (<UserSingIn />)
}

ReactDOM.render(<Index />, document.getElementById('react-app'));
