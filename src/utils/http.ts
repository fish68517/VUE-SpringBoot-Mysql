// @ts-ignore
import request from '@/package/request';
import { AxiosRequestConfig } from 'axios'

const getHeaders = (config) => {
  return {
    // "Content-Type": "application/json;charset=UTF-8",
    // "Authorization": unref(jczzToken),
    ...(config?.headers || {})
  }
}

export default {
  get: (url: string, params?: any, config?: AxiosRequestConfig) => {
    return new Promise((resolve, reject) => {
      request
      .get(url, { params, ...(config || {}), headers: getHeaders(config) })
      .then((res) => resolve(res))
      .catch((error) => {
        reject(error);
      });
    });
  },
  post: (url: string, params?: any, config?: AxiosRequestConfig) => {
    return new Promise((resolve, reject) => {
      request
      .post(url, params, { ...config, headers: getHeaders(config) })
      .then((res) => {
        resolve(res)
      })
      .catch((error) => {
        reject(error);
      });
    });
  },
  put: (url: string, params?: any, config?: AxiosRequestConfig) => {
    return new Promise((resolve, reject) => {
      request
      .put(url, params, { ...config, headers: getHeaders(config) })
      .then((res) => resolve(res))
      .catch((error) => {
        reject(error);
      });
    });
  },
  delete: (url: string, params?: any, config?: AxiosRequestConfig) => {
    return new Promise((resolve, reject) => {
      request
      .delete(url, { params, ...(config || {}), headers: getHeaders(config) })
      .then((res) => resolve(res))
      .catch((error) => {
        reject(error);
      });
    });
  },
};