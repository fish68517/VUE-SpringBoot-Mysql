/**
 * 本地联调用水情站点响应。
 *
 * 注意：MOCK-* 编码和补齐的阈值只用于 UI/算法调试，不能作为真实水文数据使用。
 */
export const USE_STATION_MOCK = true

export const MOCK_ALL_HYDROLOGY_RESPONSE = {
  traceId: 'waterlog-station-mock',
  time: 0,
  code: '200',
  message: 'mock success',
  data: {
    stations: [
      { stcd: 'MOCK-JL-YZ', stnm: '鸭嘴', rvnm: '嘉陵江', lon: 106.2685, lat: 29.9907, dist: 95.2, ivhz: 204.93, wrz: 204.93, grz: 206.93, z: 203.20 },
      { stcd: 'MOCK-JL-DJT', stnm: '东津沱', rvnm: '嘉陵江', lon: 106.2853, lat: 29.9755, dist: 90.7, ivhz: 206.50, wrz: 206.50, grz: 208.50, z: 205.10 },
      { stcd: 'MOCK-JL-BB3', stnm: '北碚(三)', rvnm: '嘉陵江', lon: 106.4493, lat: 29.8193, dist: 55.1, ivhz: 194.50, wrz: 194.50, grz: 199.00, z: 193.40 },
      { stcd: 'MOCK-JL-CQK', stnm: '磁器口', rvnm: '嘉陵江', lon: 106.4649, lat: 29.5751, dist: 16.0, ivhz: 182.64, wrz: 182.64, grz: 185.64, z: 181.72 },
      { stcd: 'MOCK-JL-HLQ', stnm: '化龙桥', rvnm: '嘉陵江', lon: 106.5059, lat: 29.5567, dist: 10.9, ivhz: 181.80, wrz: 181.80, grz: 184.80, z: 180.45 },
      { stcd: 'MOCK-JL-DXG', stnm: '大溪沟', rvnm: '嘉陵江', lon: 106.5498, lat: 29.5695, dist: 7.3, ivhz: 181.60, wrz: 181.60, grz: 184.60, z: 180.30 },
      { stcd: 'MOCK-JL-QSM', stnm: '千厮门', rvnm: '嘉陵江', lon: 106.5771, lat: 29.5663, dist: 2.3, ivhz: 181.90, wrz: 181.90, grz: 184.90, z: 180.55 },
      { stcd: 'MOCK-YZ-LZ3', stnm: '泸州(三)', rvnm: '长江', lon: 105.4211, lat: 28.8742, dist: 245.9, ivhz: 240.50, wrz: 240.50, grz: 242.00, z: 239.20 },
      { stcd: 'MOCK-YZ-TP', stnm: '塔坪', rvnm: '长江', lon: 106.2502, lat: 29.2990, dist: 70.0, ivhz: 201.00, wrz: 201.00, grz: 204.00, z: 199.80 },
      { stcd: 'MOCK-YZ-DEZ', stnm: '钓二嘴', rvnm: '长江', lon: 106.5057, lat: 29.4275, dist: 25.5, ivhz: 184.80, wrz: 184.80, grz: 187.80, z: 183.40 },
      { stcd: 'MOCK-YZ-LJT', stnm: '李家沱', rvnm: '长江', lon: 106.5405, lat: 29.4733, dist: 16.5, ivhz: 181.00, wrz: 181.00, grz: 185.00, z: 179.80 },
      { stcd: 'MOCK-YZ-CYB', stnm: '菜园坝', rvnm: '长江', lon: 106.5496, lat: 29.5514, dist: 7.1, ivhz: 183.00, wrz: 183.00, grz: 186.00, z: 181.40 },
      { stcd: 'MOCK-YZ-XTM', stnm: '玄坛庙', rvnm: '长江', lon: 106.590662, lat: 29.567781, dist: 2.7, ivhz: 180.30, wrz: 180.30, grz: 183.30, z: 179.20 },
      { stcd: 'MOCK-CTM', stnm: '朝天门', rvnm: '长江', lon: 106.5847, lat: 29.5708, dist: 0, ivhz: 182.00, wrz: 182.00, grz: 185.00, z: 180.80 },
      { stcd: 'MOCK-JL-GJT', stnm: '郭家沱', rvnm: '嘉陵江', lon: 106.6745, lat: 29.5689, dist: 14.0, ivhz: 179.50, wrz: 179.50, grz: 182.50, z: 178.20 }
    ],
    total: 15
  }
}
