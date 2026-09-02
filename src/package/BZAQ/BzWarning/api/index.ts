import request from '@/utils/request'

export const pickupcoordinateutil = (data: any) => {
    return request({
        url: 'http://23.210.227.34:23343/yztapi/cockpit/component/enterprise/pickupcoordinateutil',
        method: 'POST',
        data,
    })
}