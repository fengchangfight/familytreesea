import axios from 'axios'

export const AXIOS = axios.create({
  // baseURL: 'http://www.fengchang.cc/money',
  baseURL: 'http://localhost:8865',
  //baseURL: '',
  withCredentials: true
})
