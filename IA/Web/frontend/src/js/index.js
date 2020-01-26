import React from 'react';
import ReactDOM from 'react-dom';
import App from './Components/OhaApp'
import UserSingIn from './Components/OhaUserSignIn'
import { isUserLoginIn } from './OhaAppStatus'
import { BrowserRouter, Route, Switch, Redirect } from "react-router-dom";


function Index(props) {

  const PrivateRoute = ({ component: Component, ...rest }) => (
    <Route
      {...rest}
      render={props =>
        isUserLoginIn() ? (
          <Component {...props} />
        ) : (
            <Redirect to={{ pathname: "/app/login", state: { from: props.location } }} />
          )
      }
    />
  );

  return (
    <BrowserRouter>
      <Switch>
        <PrivateRoute exact path="/" component={() => <App {...props} />} />
        <PrivateRoute exact path="/app" component={() => <App {...props} />} />
        <Route exact path="/app/login" component={() => <UserSingIn {...props} />} />
      </Switch>
    </BrowserRouter>
  )
}

ReactDOM.render(<Index />, document.getElementById('react-app'));
