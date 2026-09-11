# 防汛预警地图第三方数据说明

## 行政区边界

- 项目：Supeset/China-GeoData
- 固定提交：`5822c4c0a0bdfd73327f9454976c8661bfd6ad9f`
- 原始文件：`geojson/china_province_city_full.geojson`
- 许可证：MIT
- 本项目处理：筛选重庆市 38 个区县，裁剪到离线瓦片范围，简化并只保留显示字段。
- 注意：该公开工程数据集不等同于行政主管部门的正式发布，生产对外使用前需要复核行政区版本。

## 河网

- 数据：OpenStreetMap 中国省级 PBF 提取数据
- 下载服务：Geofabrik、download.openstreetmap.fr
- 许可证：Open Database License 1.0（ODbL 1.0）
- 必须署名：`© OpenStreetMap contributors`
- 许可说明：https://www.openstreetmap.org/copyright
- 本项目处理：提取 `waterway=river|stream|canal`，裁剪到瓦片范围，按 OSM way ID 去重、简化，并从 WGS84 转为用于高德瓦片显示的 GCJ-02 工程坐标。

具体下载地址、时间、文件大小和 SHA-256 见：

`scripts/map-sources.lock.json`

## 生成文件

- `waterlog-admin.gcj02.geojson`
- `waterlog-rivers.gcj02.geojson`

上述文件均为本地静态资源，浏览器运行时不会请求第三方地图数据服务。
