/*
 * @Author: wangcong
 * @Date: 2023-09-18 14:26:50
 * @LastEditTime: 2024-12-05 15:40:56
 * @LastEditors: likang
 * @Description:
 */

const baseUrl = window.location.href.includes('localhost') ? 'http://10.3.240.211:808/api/' : '/api/'
export const getColorByType = (type: 0 | 1, token: string): Promise<any> => {
  return window
    .fetch(baseUrl + `api/Color/find/${type}`, {
      method: 'get',
      headers: {
        Authorization: token
      }
    })
    .then(res => res.json())
    .then(res => {
      return Promise.resolve(res)
    })
}

export const addColorByType = (data: FormData, token: string): Promise<any> => {
  return window
    .fetch(baseUrl + `api/Color/add`, {
      method: 'post',
      body: data,
      headers: {
        Authorization: token
      }
    })
    .then(res => res.json())
    .then(res => {
      return Promise.resolve(res)
    })
}

export const delColorByID = (id: string, token: string): Promise<any> => {
  return window
    .fetch(baseUrl + `api/Color/delete/${id}`, {
      method: 'delete',
      headers: {
        Authorization: token
      }
    })
    .then(res => res.json())
    .then(res => {
      return Promise.resolve(res)
    })
}
