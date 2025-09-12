import request from '@/utils/request'

export default {
  //生成订单
  createOrders(courseId) {
    return request({
      url: '/edeorder/order/createOrder/' + courseId,
      method: 'post'
    })
  },
  //根据订单id查询订单信息
  getOrdersInfo(id) {
    return request({
      url: '/edeorder/order/getOrderInfo/' + id,
      method: 'get'
    })
  },
  payAlipay(orderNo, subject, totalAmount) {
    return request({
      url: '/edeorder/order/pay',
      method: 'get',
      params: {
        subject: subject,
        traceNo: orderNo,
        totalAmount: totalAmount
      },
      responseType: 'text',
      maxRedirects: 5, // 关键修改：允许跟随重定向
      validateStatus: () => true // 接受所有状态码
    })
  },

  // 查询支付状态
  queryAlipayStatus(orderNo) {
    return request({
      url: '/edeorder/order/status',
      method: 'get',
      params: {
        orderId: orderNo
      }
    })
  }
}
