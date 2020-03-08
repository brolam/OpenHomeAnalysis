import React from 'react';
import ReactDOM from 'react-dom';
import App from './Components/OhaApp'
import UserSingIn from './Components/OhaUserSignIn'
import { UserLoginStatus } from './OhaAppStatus'
import { BrowserRouter, Route, Switch, Redirect } from "react-router-dom";


const PrivateRoute = ({ component: Component, ...rest }) => (
  <Route
    {...rest}
    render={props =>
      UserLoginStatus.isLogin() ? (
        <Component {...props} token={UserLoginStatus.getToken()} />
      ) : (
          <Redirect to={{ pathname: "/app/login", state: { from: props.location } }} />
        )
    }
  />
);


function Index(props) {

  return (
    <BrowserRouter>
      <Switch>
        <PrivateRoute exact path="/" component={App} />
        <PrivateRoute exact path="/app" component={App} />
        <Route exact path="/app/login" component={UserSingIn} />
      </Switch>
    </BrowserRouter>
  )
}

ReactDOM.render(<Index />, document.getElementById('react-app'));
