import axios from 'axios';
import { ElMessage } from 'element-plus';

const service = axios.create();
const NETWORK_ERROR = '网络错误，请检查网络连接';
// 添加请求拦截器
service.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    return config;
  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

// 添加响应拦截器
service.interceptors.response.use((res) => {
    (res) => {
        const {code, data, msg} = res.data;
        if (code === 200) {
            return data;
        } else {
            ElMessage.error(msg || NETWORK_ERROR); 
            return Promise.reject(msg || NETWORK_ERROR);
        }
    }
});