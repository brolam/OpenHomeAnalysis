//import { useState, useEffect } from 'react';
import { getToken } from './OhaLocalStore'

export function isUserLoginIn() {
  return getToken() != null;
}