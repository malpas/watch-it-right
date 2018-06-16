import axios from "axios";

axios.interceptors.request.use(config => {
  var token = localStorage.getItem("token");
  if (token !== null) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
