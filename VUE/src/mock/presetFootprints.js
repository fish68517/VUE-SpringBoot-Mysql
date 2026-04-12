const presetUserFootprints = [
  {
    userId: 1,
    username: '旅行者1号',
    footprints: [
      { id: 101, travel_record_id: 1001, location_name: '北京故宫', latitude: 39.916345, longitude: 116.397155, visit_date: '2026-01-12', created_at: '2026-01-12 10:20:00' },
      { id: 102, travel_record_id: 1001, location_name: '北京天坛', latitude: 39.882181, longitude: 116.406605, visit_date: '2026-01-13', created_at: '2026-01-13 14:30:00' },
      { id: 103, travel_record_id: 1002, location_name: '北京颐和园', latitude: 39.99991, longitude: 116.275522, visit_date: '2026-01-14', created_at: '2026-01-14 09:15:00' },
      { id: 104, travel_record_id: 1002, location_name: '什刹海', latitude: 39.943115, longitude: 116.37925, visit_date: '2026-01-14', created_at: '2026-01-14 18:40:00' }
    ]
  },
  {
    userId: 2,
    username: '旅行者2号',
    footprints: [
      { id: 201, travel_record_id: 2001, location_name: '上海外滩', latitude: 31.240021, longitude: 121.490317, visit_date: '2026-02-03', created_at: '2026-02-03 18:20:00' },
      { id: 202, travel_record_id: 2001, location_name: '上海豫园', latitude: 31.227238, longitude: 121.492531, visit_date: '2026-02-04', created_at: '2026-02-04 11:10:00' },
      { id: 203, travel_record_id: 2002, location_name: '上海迪士尼', latitude: 31.143374, longitude: 121.657303, visit_date: '2026-02-05', created_at: '2026-02-05 15:40:00' },
      { id: 204, travel_record_id: 2002, location_name: '陆家嘴中心绿地', latitude: 31.236237, longitude: 121.503869, visit_date: '2026-02-06', created_at: '2026-02-06 09:20:00' },
      { id: 205, travel_record_id: 2003, location_name: '静安寺', latitude: 31.223029, longitude: 121.445124, visit_date: '2026-02-06', created_at: '2026-02-06 16:55:00' }
    ]
  },
  {
    userId: 3,
    username: '旅行者3号',
    footprints: [
      { id: 301, travel_record_id: 3001, location_name: '杭州西湖', latitude: 30.243378, longitude: 120.150053, visit_date: '2026-03-08', created_at: '2026-03-08 08:50:00' },
      { id: 302, travel_record_id: 3001, location_name: '灵隐寺', latitude: 30.242938, longitude: 120.104004, visit_date: '2026-03-08', created_at: '2026-03-08 13:25:00' },
      { id: 303, travel_record_id: 3002, location_name: '西溪湿地', latitude: 30.26737, longitude: 120.063698, visit_date: '2026-03-09', created_at: '2026-03-09 16:05:00' },
      { id: 304, travel_record_id: 3002, location_name: '河坊街', latitude: 30.241423, longitude: 120.174665, visit_date: '2026-03-09', created_at: '2026-03-09 20:10:00' }
    ]
  },
  {
    userId: 4,
    username: '旅行者4号',
    footprints: [
      { id: 401, travel_record_id: 4001, location_name: '南京夫子庙', latitude: 32.020138, longitude: 118.792199, visit_date: '2026-03-18', created_at: '2026-03-18 19:00:00' },
      { id: 402, travel_record_id: 4001, location_name: '中山陵', latitude: 32.064735, longitude: 118.847629, visit_date: '2026-03-19', created_at: '2026-03-19 09:40:00' },
      { id: 403, travel_record_id: 4002, location_name: '玄武湖', latitude: 32.078281, longitude: 118.802422, visit_date: '2026-03-19', created_at: '2026-03-19 17:05:00' },
      { id: 404, travel_record_id: 4002, location_name: '老门东', latitude: 32.01598, longitude: 118.798759, visit_date: '2026-03-20', created_at: '2026-03-20 10:30:00' },
      { id: 405, travel_record_id: 4003, location_name: '明孝陵', latitude: 32.061825, longitude: 118.85096, visit_date: '2026-03-20', created_at: '2026-03-20 14:20:00' },
      { id: 406, travel_record_id: 4003, location_name: '总统府', latitude: 32.048289, longitude: 118.792373, visit_date: '2026-03-20', created_at: '2026-03-20 18:05:00' }
    ]
  },
  {
    userId: 5,
    username: '旅行者5号',
    footprints: [
      { id: 501, travel_record_id: 5001, location_name: '苏州拙政园', latitude: 31.32683, longitude: 120.631318, visit_date: '2026-04-02', created_at: '2026-04-02 10:45:00' },
      { id: 502, travel_record_id: 5001, location_name: '平江路', latitude: 31.320626, longitude: 120.634871, visit_date: '2026-04-02', created_at: '2026-04-02 14:00:00' },
      { id: 503, travel_record_id: 5002, location_name: '金鸡湖', latitude: 31.319774, longitude: 120.730758, visit_date: '2026-04-03', created_at: '2026-04-03 18:10:00' },
      { id: 504, travel_record_id: 5002, location_name: '山塘街', latitude: 31.330763, longitude: 120.609855, visit_date: '2026-04-04', created_at: '2026-04-04 09:50:00' }
    ]
  },
  {
    userId: 6,
    username: '旅行者6号',
    footprints: [
      { id: 601, travel_record_id: 6001, location_name: '广州塔', latitude: 23.108526, longitude: 113.319926, visit_date: '2026-04-15', created_at: '2026-04-15 20:30:00' },
      { id: 602, travel_record_id: 6001, location_name: '沙面岛', latitude: 23.108151, longitude: 113.239961, visit_date: '2026-04-16', created_at: '2026-04-16 11:00:00' },
      { id: 603, travel_record_id: 6002, location_name: '白云山', latitude: 23.186312, longitude: 113.298786, visit_date: '2026-04-17', created_at: '2026-04-17 09:20:00' },
      { id: 604, travel_record_id: 6002, location_name: '永庆坊', latitude: 23.115571, longitude: 113.238469, visit_date: '2026-04-17', created_at: '2026-04-17 16:10:00' },
      { id: 605, travel_record_id: 6003, location_name: '陈家祠', latitude: 23.12581, longitude: 113.244942, visit_date: '2026-04-18', created_at: '2026-04-18 10:25:00' }
    ]
  },
  {
    userId: 7,
    username: '旅行者7号',
    footprints: [
      { id: 701, travel_record_id: 7001, location_name: '深圳湾公园', latitude: 22.510565, longitude: 113.939579, visit_date: '2026-05-02', created_at: '2026-05-02 17:40:00' },
      { id: 702, travel_record_id: 7001, location_name: '世界之窗', latitude: 22.540999, longitude: 113.974692, visit_date: '2026-05-03', created_at: '2026-05-03 12:10:00' },
      { id: 703, travel_record_id: 7002, location_name: '大梅沙海滨公园', latitude: 22.597203, longitude: 114.318238, visit_date: '2026-05-04', created_at: '2026-05-04 15:45:00' },
      { id: 704, travel_record_id: 7002, location_name: '莲花山公园', latitude: 22.547727, longitude: 114.059563, visit_date: '2026-05-05', created_at: '2026-05-05 08:40:00' }
    ]
  },
  {
    userId: 8,
    username: '旅行者8号',
    footprints: [
      { id: 801, travel_record_id: 8001, location_name: '成都宽窄巷子', latitude: 30.673676, longitude: 104.055731, visit_date: '2026-05-21', created_at: '2026-05-21 16:10:00' },
      { id: 802, travel_record_id: 8001, location_name: '成都大熊猫繁育研究基地', latitude: 30.739803, longitude: 104.149146, visit_date: '2026-05-22', created_at: '2026-05-22 09:00:00' },
      { id: 803, travel_record_id: 8002, location_name: '锦里古街', latitude: 30.650592, longitude: 104.048235, visit_date: '2026-05-22', created_at: '2026-05-22 19:30:00' },
      { id: 804, travel_record_id: 8002, location_name: '望江楼公园', latitude: 30.630992, longitude: 104.103214, visit_date: '2026-05-23', created_at: '2026-05-23 10:20:00' },
      { id: 805, travel_record_id: 8003, location_name: '杜甫草堂', latitude: 30.666465, longitude: 104.028648, visit_date: '2026-05-23', created_at: '2026-05-23 14:30:00' },
      { id: 806, travel_record_id: 8003, location_name: '东郊记忆', latitude: 30.675581, longitude: 104.130746, visit_date: '2026-05-23', created_at: '2026-05-23 20:15:00' }
    ]
  },
  {
    userId: 9,
    username: '旅行者9号',
    footprints: [
      { id: 901, travel_record_id: 9001, location_name: '西安钟楼', latitude: 34.261102, longitude: 108.948024, visit_date: '2026-06-09', created_at: '2026-06-09 18:15:00' },
      { id: 902, travel_record_id: 9001, location_name: '大雁塔', latitude: 34.225643, longitude: 108.964458, visit_date: '2026-06-10', created_at: '2026-06-10 10:50:00' },
      { id: 903, travel_record_id: 9002, location_name: '秦始皇兵马俑', latitude: 34.384554, longitude: 109.273315, visit_date: '2026-06-11', created_at: '2026-06-11 13:35:00' },
      { id: 904, travel_record_id: 9002, location_name: '西安城墙永宁门', latitude: 34.24555, longitude: 108.942258, visit_date: '2026-06-11', created_at: '2026-06-11 19:40:00' }
    ]
  },
  {
    userId: 10,
    username: '旅行者10号',
    footprints: [
      { id: 1001, travel_record_id: 10001, location_name: '武汉黄鹤楼', latitude: 30.544592, longitude: 114.302776, visit_date: '2026-06-28', created_at: '2026-06-28 11:40:00' },
      { id: 1002, travel_record_id: 10001, location_name: '东湖绿道', latitude: 30.560941, longitude: 114.403145, visit_date: '2026-06-29', created_at: '2026-06-29 08:25:00' },
      { id: 1003, travel_record_id: 10002, location_name: '户部巷', latitude: 30.544855, longitude: 114.309245, visit_date: '2026-06-29', created_at: '2026-06-29 19:20:00' },
      { id: 1004, travel_record_id: 10002, location_name: '武汉大学', latitude: 30.536112, longitude: 114.364743, visit_date: '2026-06-30', created_at: '2026-06-30 10:05:00' },
      { id: 1005, travel_record_id: 10003, location_name: '汉口江滩', latitude: 30.595087, longitude: 114.301322, visit_date: '2026-06-30', created_at: '2026-06-30 18:10:00' }
    ]
  },
  {
    userId: 11,
    username: '旅行者11号',
    footprints: [
      { id: 1101, travel_record_id: 11001, location_name: '重庆洪崖洞', latitude: 29.56301, longitude: 106.580457, visit_date: '2026-07-06', created_at: '2026-07-06 19:10:00' },
      { id: 1102, travel_record_id: 11001, location_name: '解放碑', latitude: 29.558176, longitude: 106.577091, visit_date: '2026-07-07', created_at: '2026-07-07 10:20:00' },
      { id: 1103, travel_record_id: 11002, location_name: '磁器口古镇', latitude: 29.587842, longitude: 106.448449, visit_date: '2026-07-08', created_at: '2026-07-08 15:05:00' },
      { id: 1104, travel_record_id: 11002, location_name: '鹅岭二厂', latitude: 29.553106, longitude: 106.519757, visit_date: '2026-07-08', created_at: '2026-07-08 20:15:00' }
    ]
  },
  {
    userId: 12,
    username: '旅行者12号',
    footprints: [
      { id: 1201, travel_record_id: 12001, location_name: '长沙岳麓山', latitude: 28.180248, longitude: 112.944302, visit_date: '2026-07-15', created_at: '2026-07-15 09:15:00' },
      { id: 1202, travel_record_id: 12001, location_name: '橘子洲', latitude: 28.190261, longitude: 112.963255, visit_date: '2026-07-15', created_at: '2026-07-15 17:40:00' },
      { id: 1203, travel_record_id: 12002, location_name: '太平老街', latitude: 28.194864, longitude: 112.972168, visit_date: '2026-07-16', created_at: '2026-07-16 20:10:00' },
      { id: 1204, travel_record_id: 12002, location_name: '湖南省博物馆', latitude: 28.213478, longitude: 112.990144, visit_date: '2026-07-17', created_at: '2026-07-17 09:30:00' },
      { id: 1205, travel_record_id: 12003, location_name: '杜甫江阁', latitude: 28.188557, longitude: 112.976458, visit_date: '2026-07-17', created_at: '2026-07-17 19:00:00' }
    ]
  },
  {
    userId: 13,
    username: '旅行者13号',
    footprints: [
      { id: 1301, travel_record_id: 13001, location_name: '青岛栈桥', latitude: 36.060291, longitude: 120.320241, visit_date: '2026-07-23', created_at: '2026-07-23 08:50:00' },
      { id: 1302, travel_record_id: 13001, location_name: '八大关', latitude: 36.048261, longitude: 120.357359, visit_date: '2026-07-24', created_at: '2026-07-24 14:25:00' },
      { id: 1303, travel_record_id: 13002, location_name: '五四广场', latitude: 36.066151, longitude: 120.382621, visit_date: '2026-07-25', created_at: '2026-07-25 18:30:00' },
      { id: 1304, travel_record_id: 13002, location_name: '小鱼山', latitude: 36.059521, longitude: 120.332394, visit_date: '2026-07-26', created_at: '2026-07-26 10:20:00' }
    ]
  },
  {
    userId: 14,
    username: '旅行者14号',
    footprints: [
      { id: 1401, travel_record_id: 14001, location_name: '济南趵突泉', latitude: 36.658827, longitude: 117.008868, visit_date: '2026-08-02', created_at: '2026-08-02 09:35:00' },
      { id: 1402, travel_record_id: 14001, location_name: '大明湖', latitude: 36.676977, longitude: 117.025853, visit_date: '2026-08-02', created_at: '2026-08-02 15:05:00' },
      { id: 1403, travel_record_id: 14002, location_name: '千佛山', latitude: 36.641419, longitude: 117.041926, visit_date: '2026-08-03', created_at: '2026-08-03 10:50:00' },
      { id: 1404, travel_record_id: 14002, location_name: '泉城广场', latitude: 36.656936, longitude: 117.021421, visit_date: '2026-08-03', created_at: '2026-08-03 14:45:00' },
      { id: 1405, travel_record_id: 14003, location_name: '黑虎泉', latitude: 36.662118, longitude: 117.032929, visit_date: '2026-08-03', created_at: '2026-08-03 18:05:00' },
      { id: 1406, travel_record_id: 14003, location_name: '山东博物馆', latitude: 36.651262, longitude: 117.12187, visit_date: '2026-08-04', created_at: '2026-08-04 11:15:00' }
    ]
  },
  {
    userId: 15,
    username: '旅行者15号',
    footprints: [
      { id: 1501, travel_record_id: 15001, location_name: '厦门鼓浪屿', latitude: 24.448091, longitude: 118.067562, visit_date: '2026-08-10', created_at: '2026-08-10 11:20:00' },
      { id: 1502, travel_record_id: 15001, location_name: '南普陀寺', latitude: 24.438516, longitude: 118.09454, visit_date: '2026-08-11', created_at: '2026-08-11 09:40:00' },
      { id: 1503, travel_record_id: 15002, location_name: '环岛路', latitude: 24.432588, longitude: 118.164779, visit_date: '2026-08-11', created_at: '2026-08-11 17:15:00' },
      { id: 1504, travel_record_id: 15002, location_name: '曾厝垵', latitude: 24.418636, longitude: 118.127509, visit_date: '2026-08-12', created_at: '2026-08-12 18:20:00' }
    ]
  },
  {
    userId: 16,
    username: '旅行者16号',
    footprints: [
      { id: 1601, travel_record_id: 16001, location_name: '昆明滇池', latitude: 24.957615, longitude: 102.665859, visit_date: '2026-08-18', created_at: '2026-08-18 16:40:00' },
      { id: 1602, travel_record_id: 16001, location_name: '翠湖公园', latitude: 25.050291, longitude: 102.704538, visit_date: '2026-08-19', created_at: '2026-08-19 10:10:00' },
      { id: 1603, travel_record_id: 16002, location_name: '云南民族村', latitude: 24.969598, longitude: 102.659994, visit_date: '2026-08-20', created_at: '2026-08-20 14:45:00' },
      { id: 1604, travel_record_id: 16002, location_name: '海埂大坝', latitude: 24.95074, longitude: 102.651646, visit_date: '2026-08-20', created_at: '2026-08-20 18:10:00' },
      { id: 1605, travel_record_id: 16003, location_name: '官渡古镇', latitude: 24.953366, longitude: 102.759108, visit_date: '2026-08-21', created_at: '2026-08-21 11:30:00' }
    ]
  },
  {
    userId: 17,
    username: '旅行者17号',
    footprints: [
      { id: 1701, travel_record_id: 17001, location_name: '贵阳甲秀楼', latitude: 26.573236, longitude: 106.715381, visit_date: '2026-08-27', created_at: '2026-08-27 19:25:00' },
      { id: 1702, travel_record_id: 17001, location_name: '黔灵山公园', latitude: 26.597388, longitude: 106.693955, visit_date: '2026-08-28', created_at: '2026-08-28 09:55:00' },
      { id: 1703, travel_record_id: 17002, location_name: '花果园湿地公园', latitude: 26.556945, longitude: 106.690417, visit_date: '2026-08-28', created_at: '2026-08-28 16:20:00' }
    ]
  },
  {
    userId: 18,
    username: '旅行者18号',
    footprints: [
      { id: 1801, travel_record_id: 18001, location_name: '南宁青秀山', latitude: 22.806542, longitude: 108.397059, visit_date: '2026-09-04', created_at: '2026-09-04 10:30:00' },
      { id: 1802, travel_record_id: 18001, location_name: '三街两巷', latitude: 22.819492, longitude: 108.318702, visit_date: '2026-09-05', created_at: '2026-09-05 14:00:00' },
      { id: 1803, travel_record_id: 18002, location_name: '南湖公园', latitude: 22.808418, longitude: 108.346595, visit_date: '2026-09-05', created_at: '2026-09-05 18:20:00' },
      { id: 1804, travel_record_id: 18002, location_name: '广西民族博物馆', latitude: 22.818041, longitude: 108.392355, visit_date: '2026-09-06', created_at: '2026-09-06 10:45:00' }
    ]
  },
  {
    userId: 19,
    username: '旅行者19号',
    footprints: [
      { id: 1901, travel_record_id: 19001, location_name: '海口骑楼老街', latitude: 20.047559, longitude: 110.341312, visit_date: '2026-09-12', created_at: '2026-09-12 20:05:00' },
      { id: 1902, travel_record_id: 19001, location_name: '假日海滩', latitude: 20.00718, longitude: 110.25174, visit_date: '2026-09-13', created_at: '2026-09-13 16:50:00' },
      { id: 1903, travel_record_id: 19002, location_name: '万绿园', latitude: 20.033543, longitude: 110.311023, visit_date: '2026-09-14', created_at: '2026-09-14 09:10:00' },
      { id: 1904, travel_record_id: 19002, location_name: '白沙门公园', latitude: 20.055307, longitude: 110.360442, visit_date: '2026-09-14', created_at: '2026-09-14 16:15:00' },
      { id: 1905, travel_record_id: 19003, location_name: '海口钟楼', latitude: 20.045972, longitude: 110.343974, visit_date: '2026-09-15', created_at: '2026-09-15 10:10:00' }
    ]
  },
  {
    userId: 20,
    username: '旅行者20号',
    footprints: [
      { id: 2001, travel_record_id: 20001, location_name: '哈尔滨中央大街', latitude: 45.782073, longitude: 126.628448, visit_date: '2026-09-20', created_at: '2026-09-20 18:35:00' },
      { id: 2002, travel_record_id: 20001, location_name: '圣索菲亚教堂广场', latitude: 45.775195, longitude: 126.640703, visit_date: '2026-09-21', created_at: '2026-09-21 11:15:00' },
      { id: 2003, travel_record_id: 20002, location_name: '太阳岛风景区', latitude: 45.794882, longitude: 126.603458, visit_date: '2026-09-22', created_at: '2026-09-22 14:55:00' },
      { id: 2004, travel_record_id: 20002, location_name: '防洪纪念塔', latitude: 45.788956, longitude: 126.626847, visit_date: '2026-09-22', created_at: '2026-09-22 19:25:00' },
      { id: 2005, travel_record_id: 20003, location_name: '哈尔滨大剧院', latitude: 45.80961, longitude: 126.577085, visit_date: '2026-09-23', created_at: '2026-09-23 10:40:00' },
      { id: 2006, travel_record_id: 20003, location_name: '斯大林公园', latitude: 45.787135, longitude: 126.618332, visit_date: '2026-09-23', created_at: '2026-09-23 16:35:00' }
    ]
  }
]

export const getPresetFootprintUsers = () => presetUserFootprints

export const getPresetFootprintsByUserId = (userId) => {
  const matchedUser = presetUserFootprints.find((item) => item.userId === Number(userId))
  return matchedUser ? matchedUser.footprints.map((footprint) => ({ ...footprint })) : []
}

export const getPresetFootprintOwnerByUserId = (userId) => {
  return presetUserFootprints.find((item) => item.userId === Number(userId)) || null
}
