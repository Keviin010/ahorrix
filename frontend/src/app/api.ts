const enLocal = location.hostname === 'localhost' || location.hostname === '127.0.0.1';

export const API = enLocal
  ? `http://${location.hostname}:8081/api`
  : 'https://ahorrix-api.onrender.com/api';
