import axios, { AxiosRequestConfig, AxiosResponse } from "axios";

// 创建 axios 实例
const service = axios.create({
  baseURL: 'http://23.210.227.35:23343/',
  timeout: 50000,
  headers: { "Content-Type": "application/json;charset=utf-8" },
});
// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    if (response.data) {
      return response.data;
    }
    // 响应数据为二进制流处理()
    if (response.data instanceof ArrayBuffer) {
      return response;
    }
  },
  (error: any) => {
    if (error.response.data) {
      const { code, msg } = error.response;
      // token 过期,重新登录
      if (error.response.status === 401) {
        // dialog.warning({
        //   title: '警告',
        //   content: "当前模块失效，请重刷新页面",
        //   positiveText: '确定',
        //   draggable: true,
        //   onPositiveClick: () => {
        //
        //   },
        //   onNegativeClick: () => {
        //     message.error('不确定')
        //   }
        // })
      } else {
        // message.error(msg || "系统出错");
      }
    }
    return Promise.reject(error);
  }
);

// 导出 axios 实例
export default service;
