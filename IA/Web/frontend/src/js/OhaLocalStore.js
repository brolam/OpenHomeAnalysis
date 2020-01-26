const TOKEN_KEY = "@oha-auth-Token";

export const getToken = () => localStorage.getItem(TOKEN_KEY);

export const saveToken = token => {
  localStorage.setItem(TOKEN_KEY, token);
};
export const removeToken = () => {
  localStorage.removeItem(TOKEN_KEY);
};