/*
 * @Author: wangcong
 * @Date: 2025-06-09 20:57:39
 * @LastEditTime: 2025-06-11 10:02:08
 * @LastEditors: wangcong
 * @Description: 
 */
const baseUrl = 'http://23.210.227.13:25082'

// 查询设备信息
export const getPointPageApi = (authorization: string, data: Object) => {
  return window.fetch(baseUrl + '/prod-api/esQuery/getPointPage', {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer ' + authorization,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  }).then(res => res.json()).then(res => res)
}

// 获取监控视频流
export const getMonitorUrApil = (authorization: string, deviceId: string) => {
  return window.fetch(baseUrl + `/prod-api/screen/geMonitorListByDeviceId?deviceids=${deviceId}`, {
    method: 'get',
    headers: {
      'Authorization': 'Bearer ' + authorization,
      'Content-Type': 'application/json'
    },
  }).then(res => res.json()).then(res => res)
}