INSERT IGNORE INTO sys_user
    (id, username, password, nickname, phone, email, user_type, status)
VALUES
    (1, 'admin', 'admin123', '平台管理员', '13800000001', 'admin@example.com', 1, 1),
    (2, 'zhangsan', '123456', '张三', '13800000002', 'zhangsan@example.com', 0, 1),
    (3, 'lisi', '123456', '李四', '13800000003', 'lisi@example.com', 0, 1),
    (4, 'disabled_user', '123456', '禁用账号', NULL, NULL, 0, 0);

INSERT IGNORE INTO region
    (id, parent_id, region_code, region_name, region_level, sort_no, status)
VALUES
    (1, 0, '330000', '浙江省', 1, 1, 1),
    (2, 1, '330300', '温州市', 2, 1, 1),
    (3, 1, '330100', '杭州市', 2, 2, 1),
    (4, 2, '330302', '鹿城区', 3, 1, 1),
    (5, 2, '330304', '瓯海区', 3, 2, 1),
    (6, 2, '330303', '龙湾区', 3, 3, 1),
    (7, 3, '330106', '西湖区', 3, 1, 1),
    (8, 3, '330105', '拱墅区', 3, 2, 1);

INSERT IGNORE INTO charging_station
    (id, region_id, station_code, station_name, address, longitude, latitude, station_type, open_time, operator_name, parking_desc, fee_desc, service_facilities, status)
VALUES
    (1, 4, 'WZ-LC-001', '温州滨江商务区充电站', '温州市鹿城区滨江街道会展路', 120.699361, 27.994267, 0, '00:00-24:00', '城投新能源', '前2小时免费', '电费按峰谷计价，另收服务费', '卫生间,便利店,休息区', 1),
    (2, 4, 'WZ-LC-002', '温州南塘公共充电站', '温州市鹿城区南塘街', 120.670800, 27.985600, 0, '06:00-23:00', '特来电', '停车费按场地标准', '详见现场公示', '餐饮,卫生间', 1),
    (3, 4, 'WZ-LC-003', '温州火车站停车场充电站', '温州市鹿城区温州大道', 120.684600, 27.980300, 0, '00:00-24:00', '国家电网', '充电车辆首小时免费', '峰谷电价', '候车区,便利店', 2),
    (4, 5, 'WZ-OH-001', '温州南站西广场充电站', '温州市瓯海区潘桥街道', 120.586300, 27.966200, 0, '00:00-24:00', '国家电网', '停车场正常收费', '峰谷电价', '卫生间,候车区', 1),
    (5, 5, 'WZ-OH-002', '瓯海奥体中心充电站', '温州市瓯海区娄桥街道', 120.620800, 27.972900, 1, '07:00-22:00', '小桔充电', '活动期间可能限流', '统一服务费', '体育场馆,休息区', 1),
    (6, 6, 'WZ-LW-001', '龙湾国际机场充电站', '温州市龙湾区机场大道', 120.849400, 27.912200, 0, '00:00-24:00', '星星充电', '按机场停车标准', '详见运营商页面', '卫生间,餐饮,候机楼', 1),
    (7, 6, 'WZ-LW-002', '龙湾万达广场充电站', '温州市龙湾区永中街道', 120.813200, 27.932600, 0, '08:00-23:00', '特来电', '消费可抵扣停车费', '商业时段统一价', '商场,餐饮,卫生间', 1),
    (8, 7, 'HZ-XH-001', '杭州西湖文化广场充电站', '杭州市西湖区文三路', 120.132400, 30.276200, 0, '00:00-24:00', '国家电网', '停车场正常收费', '峰谷电价', '便利店,卫生间', 1),
    (9, 7, 'HZ-XH-002', '杭州黄龙体育中心充电站', '杭州市西湖区黄龙路', 120.140300, 30.273900, 1, '06:00-23:00', '城投新能源', '赛事期间可能限流', '统一服务费', '体育场馆,餐饮', 1),
    (10, 8, 'HZ-GS-001', '杭州运河商务区充电站', '杭州市拱墅区小河路', 120.150900, 30.320600, 0, '00:00-24:00', '小桔充电', '前1小时免费', '详见现场公示', '便利店,休息区', 1);

INSERT IGNORE INTO charging_pile
    (id, station_id, pile_code, pile_name, connector_type, rated_power, manufacturer, install_date, enable_status)
VALUES
    (1, 1, 'WZ-LC-001-DC01', '1号快充桩', 0, 120.00, '华为数字能源', '2025-01-15', 1),
    (2, 1, 'WZ-LC-001-DC02', '2号快充桩', 0, 120.00, '华为数字能源', '2025-01-15', 1),
    (3, 1, 'WZ-LC-001-AC01', '3号慢充桩', 1, 7.00, '万马新能源', '2025-01-15', 1),
    (4, 2, 'WZ-LC-002-DC01', '1号快充桩', 0, 60.00, '特来电', '2024-09-10', 1),
    (5, 2, 'WZ-LC-002-DC02', '2号快充桩', 0, 60.00, '特来电', '2024-09-10', 1),
    (6, 2, 'WZ-LC-002-AC01', '3号慢充桩', 1, 7.00, '特来电', '2024-09-10', 1),
    (7, 3, 'WZ-LC-003-DC01', '1号快充桩', 0, 120.00, '国电南瑞', '2023-11-20', 1),
    (8, 3, 'WZ-LC-003-DC02', '2号快充桩', 0, 120.00, '国电南瑞', '2023-11-20', 1),
    (9, 3, 'WZ-LC-003-AC01', '3号慢充桩', 1, 7.00, '国电南瑞', '2023-11-20', 1),
    (10, 4, 'WZ-OH-001-DC01', '1号快充桩', 0, 160.00, '国电南瑞', '2025-02-12', 1),
    (11, 4, 'WZ-OH-001-DC02', '2号快充桩', 0, 160.00, '国电南瑞', '2025-02-12', 1),
    (12, 4, 'WZ-OH-001-AC01', '3号慢充桩', 1, 7.00, '万马新能源', '2025-02-12', 1),
    (13, 5, 'WZ-OH-002-DC01', '1号快充桩', 0, 120.00, '小桔充电', '2024-07-18', 1),
    (14, 5, 'WZ-OH-002-DC02', '2号快充桩', 0, 120.00, '小桔充电', '2024-07-18', 1),
    (15, 5, 'WZ-OH-002-AC01', '3号慢充桩', 1, 7.00, '小桔充电', '2024-07-18', 1),
    (16, 6, 'WZ-LW-001-DC01', '1号快充桩', 0, 180.00, '星星充电', '2025-03-08', 1),
    (17, 6, 'WZ-LW-001-DC02', '2号快充桩', 0, 180.00, '星星充电', '2025-03-08', 1),
    (18, 6, 'WZ-LW-001-AC01', '3号慢充桩', 1, 7.00, '星星充电', '2025-03-08', 1),
    (19, 7, 'WZ-LW-002-DC01', '1号快充桩', 0, 120.00, '特来电', '2024-12-01', 1),
    (20, 7, 'WZ-LW-002-DC02', '2号快充桩', 0, 120.00, '特来电', '2024-12-01', 1),
    (21, 7, 'WZ-LW-002-AC01', '3号慢充桩', 1, 7.00, '特来电', '2024-12-01', 1),
    (22, 8, 'HZ-XH-001-DC01', '1号快充桩', 0, 120.00, '国电南瑞', '2024-06-16', 1),
    (23, 8, 'HZ-XH-001-DC02', '2号快充桩', 0, 120.00, '国电南瑞', '2024-06-16', 1),
    (24, 8, 'HZ-XH-001-AC01', '3号慢充桩', 1, 7.00, '国电南瑞', '2024-06-16', 1),
    (25, 9, 'HZ-XH-002-DC01', '1号快充桩', 0, 160.00, '华为数字能源', '2025-04-22', 1),
    (26, 9, 'HZ-XH-002-DC02', '2号快充桩', 0, 160.00, '华为数字能源', '2025-04-22', 1),
    (27, 9, 'HZ-XH-002-AC01', '3号慢充桩', 1, 7.00, '万马新能源', '2025-04-22', 1),
    (28, 10, 'HZ-GS-001-DC01', '1号快充桩', 0, 120.00, '小桔充电', '2024-10-11', 1),
    (29, 10, 'HZ-GS-001-DC02', '2号快充桩', 0, 120.00, '小桔充电', '2024-10-11', 1),
    (30, 10, 'HZ-GS-001-AC01', '3号慢充桩', 1, 7.00, '小桔充电', '2024-10-11', 1);

INSERT INTO pile_realtime_status (pile_id, work_status, current_power, alarm_code, status_time, source_type)
SELECT p.id,
       CASE MOD(p.id, 10) WHEN 0 THEN 4 WHEN 1 THEN 1 WHEN 2 THEN 1 WHEN 3 THEN 2 ELSE 0 END,
       CASE WHEN MOD(p.id, 10) IN (1, 2) THEN ROUND(p.rated_power * 0.72, 2) ELSE 0 END,
       CASE WHEN MOD(p.id, 10) = 0 THEN 'E1001' ELSE NULL END,
       NOW(),
       0
FROM charging_pile p
WHERE NOT EXISTS (SELECT 1 FROM pile_realtime_status s WHERE s.pile_id = p.id);

INSERT IGNORE INTO charging_usage_record
    (id, pile_id, user_id, start_time, end_time, duration_min, energy_kwh, service_fee, total_amount, record_status)
VALUES
    (1, 1, 2, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 23 HOUR), 60, 42.50, 8.50, 54.60, 1),
    (2, 4, 3, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 47 HOUR), 60, 35.20, 7.04, 45.68, 1),
    (3, 10, 2, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 71 HOUR), 60, 48.80, 9.76, 63.42, 1),
    (4, 13, 3, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 95 HOUR), 60, 39.60, 7.92, 51.48, 1),
    (5, 16, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 119 HOUR), 60, 56.30, 11.26, 73.19, 1),
    (6, 19, 3, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 143 HOUR), 60, 41.20, 8.24, 53.56, 1),
    (7, 22, 2, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 167 HOUR), 60, 44.70, 8.94, 58.11, 1),
    (8, 25, 3, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 191 HOUR), 60, 51.80, 10.36, 67.34, 1),
    (9, 28, 2, DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 215 HOUR), 60, 43.10, 8.62, 56.03, 1),
    (10, 2, 3, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 239 HOUR), 60, 38.90, 7.78, 50.57, 1);

INSERT IGNORE INTO region_statistics
    (id, region_id, stat_date, station_count, pile_count, free_count, using_count, fault_count, usage_count, energy_kwh, usage_rate)
VALUES
    (1, 4, CURDATE(), 3, 9, 5, 2, 1, 18, 642.50, 22.22),
    (2, 5, CURDATE(), 2, 6, 4, 1, 0, 11, 386.40, 16.67),
    (3, 6, CURDATE(), 2, 6, 4, 1, 1, 13, 451.80, 16.67),
    (4, 7, CURDATE(), 2, 6, 4, 1, 0, 15, 520.30, 16.67),
    (5, 8, CURDATE(), 1, 3, 2, 1, 0, 8, 278.60, 33.33);

INSERT IGNORE INTO region_statistics
    (id, region_id, stat_date, station_count, pile_count, free_count, using_count,
     fault_count, usage_count, energy_kwh, usage_rate)
SELECT 100 + region_data.region_id * 10 + day_data.day_no,
       region_data.region_id,
       DATE_SUB(CURDATE(), INTERVAL day_data.day_no DAY),
       region_data.station_count,
       region_data.pile_count,
       region_data.pile_count - GREATEST(1, MOD(region_data.region_id + day_data.day_no, 4))
           - CASE WHEN MOD(region_data.region_id + day_data.day_no, 5) = 0 THEN 1 ELSE 0 END,
       GREATEST(1, MOD(region_data.region_id + day_data.day_no, 4)),
       CASE WHEN MOD(region_data.region_id + day_data.day_no, 5) = 0 THEN 1 ELSE 0 END,
       7 + region_data.region_id + day_data.day_no * 2,
       ROUND((7 + region_data.region_id + day_data.day_no * 2) * 31.75, 2),
       ROUND(GREATEST(1, MOD(region_data.region_id + day_data.day_no, 4)) / region_data.pile_count * 100, 2)
FROM (
    SELECT 4 AS region_id, 3 AS station_count, 9 AS pile_count
    UNION ALL SELECT 5, 2, 6
    UNION ALL SELECT 6, 2, 6
    UNION ALL SELECT 7, 2, 6
    UNION ALL SELECT 8, 1, 3
) region_data
CROSS JOIN (
    SELECT 1 AS day_no
    UNION ALL SELECT 2
    UNION ALL SELECT 3
    UNION ALL SELECT 4
    UNION ALL SELECT 5
    UNION ALL SELECT 6
    UNION ALL SELECT 7
) day_data;

INSERT IGNORE INTO user_favorite (id, user_id, station_id)
VALUES (1, 2, 1), (2, 2, 4), (3, 3, 6);

INSERT IGNORE INTO user_feedback
    (id, user_id, feedback_type, title, content, contact, process_status, reply_content, process_time)
VALUES
    (1, 2, 0, '建议增加夜间照明说明', '希望详情页能够显示站点夜间照明情况。', '13800000002', 0, NULL, NULL),
    (2, 3, 1, '站点地址需要更新', '龙湾某站点入口已调整，建议核实。', 'lisi@example.com', 1, '已安排管理员核对现场信息。', NULL),
    (3, 2, 0, '建议增加夜间筛选', '希望地图可以快速筛选全天营业的站点。', '13800000002', 2, '已在站点列表中展示营业时间。', NOW()),
    (4, 3, 1, '充电桩状态显示异常', '某个充电桩现场空闲但页面显示使用中。', 'lisi@example.com', 0, NULL, NULL),
    (5, 2, 2, '停车场入口指引不清楚', '建议在站内导览中突出显示车辆入口。', '13800000002', 1, '已联系站点运营人员补充入口说明。', NULL);

INSERT IGNORE INTO guide_point
    (id, station_id, point_name, point_type, x_ratio, y_ratio, description, sort_no, status)
VALUES
    (1, 1, '车辆入口', 0, 0.1200, 0.8200, '从会展路辅路进入停车场', 1, 1),
    (2, 1, '快充区', 2, 0.5200, 0.4600, '1号和2号快充桩所在区域', 2, 1),
    (3, 1, '休息区', 3, 0.8200, 0.2800, '设有便利店和卫生间', 3, 1),
    (4, 4, '西侧入口', 0, 0.0800, 0.7000, '从西广场停车场入口进入', 1, 1),
    (5, 4, '充电区', 2, 0.5600, 0.4200, '充电车辆专用车位', 2, 1),
    (6, 4, '候车区', 3, 0.8600, 0.2300, '步行可到达高铁候车大厅', 3, 1),
    (7, 6, '停车场入口', 0, 0.1000, 0.7800, '按机场停车指引进入', 1, 1),
    (8, 6, '快充区', 2, 0.5000, 0.5000, '位于停车场B区', 2, 1),
    (9, 6, '候机楼通道', 3, 0.8800, 0.2600, '步行通往航站楼', 3, 1);

INSERT IGNORE INTO guide_point
    (id, station_id, point_name, point_type, x_ratio, y_ratio, description, sort_no, status)
VALUES
    (10, 2, '南塘街入口', 0, 0.1000, 0.8000, '从南塘街停车场入口进入', 1, 1),
    (11, 2, '快充车位', 2, 0.5000, 0.4600, '1号和2号直流快充桩', 2, 1),
    (12, 2, '餐饮休息区', 3, 0.8300, 0.2500, '附近设有餐饮和卫生间', 3, 1),
    (13, 3, '停车场入口', 0, 0.0900, 0.7900, '从温州大道进入停车场', 1, 1),
    (14, 3, '维护充电区', 2, 0.5200, 0.4500, '部分设备维护中，请查看现场提示', 2, 1),
    (15, 3, '候车服务区', 3, 0.8500, 0.2400, '步行前往候车区和便利店', 3, 1),
    (16, 5, '奥体中心入口', 0, 0.1000, 0.8100, '活动期间按现场交通指引进入', 1, 1),
    (17, 5, '充电专用区', 2, 0.5300, 0.4600, '快充和慢充车位集中区域', 2, 1),
    (18, 5, '场馆休息区', 3, 0.8400, 0.2500, '靠近体育场馆休息区域', 3, 1),
    (19, 7, '商场停车入口', 0, 0.1100, 0.8000, '从万达停车场入口进入', 1, 1),
    (20, 7, '新能源车位', 2, 0.5200, 0.4700, '充电车辆专用车位', 2, 1),
    (21, 7, '商场服务区', 3, 0.8500, 0.2300, '连接商场餐饮和卫生间', 3, 1),
    (22, 8, '文三路入口', 0, 0.1000, 0.8100, '从文三路进入公共停车区', 1, 1),
    (23, 8, '充电设备区', 2, 0.5100, 0.4600, '快充和慢充设备所在区域', 2, 1),
    (24, 8, '便利服务区', 3, 0.8300, 0.2500, '设有便利店和卫生间', 3, 1),
    (25, 9, '黄龙路入口', 0, 0.0900, 0.8000, '从黄龙路停车入口进入', 1, 1),
    (26, 9, '体育中心充电区', 2, 0.5200, 0.4500, '赛事期间可能实施交通管制', 2, 1),
    (27, 9, '场馆餐饮区', 3, 0.8500, 0.2400, '步行前往体育场馆和餐饮区', 3, 1),
    (28, 10, '小河路入口', 0, 0.1000, 0.8000, '从小河路进入商务区停车场', 1, 1),
    (29, 10, '运河充电区', 2, 0.5200, 0.4600, '充电车辆集中停放区域', 2, 1),
    (30, 10, '商务休息区', 3, 0.8500, 0.2500, '设有便利店和休息区', 3, 1);

INSERT IGNORE INTO notice
    (id, title, content, notice_type, publish_status, publish_time, create_by)
VALUES
    (1, '平台试运行公告', '汽车充电桩信息与可视化导览平台现已进入学习演示阶段。', 0, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), 1),
    (2, '温州火车站充电站维护提示', '部分充电桩正在维护，请优先选择附近其他站点。', 1, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), 1),
    (3, '充电安全使用提示', '请在充电前检查接口状态，并按照现场指引规范停车。', 2, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), 1),
    (4, '高峰时段出行建议', '工作日18:00至21:00为充电高峰，建议提前查询空闲数量。', 2, 1, NOW(), 1),
    (5, '后台草稿示例', '该公告处于草稿状态，前台不展示。', 0, 0, NULL, 1),
    (6, '地图与导览功能上线', '站点地图、区域统计和站内可视化导览功能已完成，欢迎体验。', 0, 1, NOW(), 1);

INSERT IGNORE INTO region_statistics
    (id, region_id, stat_date, station_count, pile_count, free_count, using_count,
     fault_count, usage_count, energy_kwh, usage_rate)
SELECT 1000 + region_data.region_id * 100 + day_data.day_no,
       region_data.region_id,
       DATE_SUB(CURDATE(), INTERVAL day_data.day_no DAY),
       region_data.station_count,
       region_data.pile_count,
       region_data.pile_count - GREATEST(1, MOD(region_data.region_id + day_data.day_no, 4))
           - CASE WHEN MOD(region_data.region_id + day_data.day_no, 11) = 0 THEN 1 ELSE 0 END,
       GREATEST(1, MOD(region_data.region_id + day_data.day_no, 4)),
       CASE WHEN MOD(region_data.region_id + day_data.day_no, 11) = 0 THEN 1 ELSE 0 END,
       8 + region_data.region_id + MOD(day_data.day_no * 3, 19),
       ROUND((8 + region_data.region_id + MOD(day_data.day_no * 3, 19)) * 31.75, 2),
       ROUND(GREATEST(1, MOD(region_data.region_id + day_data.day_no, 4)) / region_data.pile_count * 100, 2)
FROM (
    SELECT 4 AS region_id, 3 AS station_count, 9 AS pile_count
    UNION ALL SELECT 5, 2, 6
    UNION ALL SELECT 6, 2, 6
    UNION ALL SELECT 7, 2, 6
    UNION ALL SELECT 8, 1, 3
) region_data
CROSS JOIN (
    SELECT 8 AS day_no UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11
    UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15
    UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19
    UNION ALL SELECT 20 UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23
    UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL SELECT 26 UNION ALL SELECT 27
    UNION ALL SELECT 28 UNION ALL SELECT 29
) day_data;
