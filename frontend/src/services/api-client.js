import axios from "axios";

const apiClient = axios.create({
  baseURL: "https://rest-mart-drf.vercel.app/api/v1/",
});

export default apiClient;
