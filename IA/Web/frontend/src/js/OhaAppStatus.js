import React, { useState, useEffect } from 'react';

export function useLoingIn(token) {
  const [isLogin, setOsLogin] = useState(false);

  useEffect(() => {
    function handleStatusChange(status) {
      setOsLogin(status);
    }

    handleStatusChange(false);
    return () => {

    };
  });

  return isLogin;
}