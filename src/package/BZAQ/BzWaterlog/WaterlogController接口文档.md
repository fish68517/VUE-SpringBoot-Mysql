# WaterlogController 接口文档

## 概述
内涝相关功能的API接口，用于处理内涝风险点位、预警信息及相关管理功能。

## 基础路径
`/api/system/waterlog`

---

### 1. 获取token

**请求方法**: GET  
**请求路径**: `/token`  
**描述**: 获取token  

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "http://23.211.35.132:38001/subdivision/magicLogin?access-tk=saToken"
}
```

**响应字段说明**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| data | String | 登录token地址 |

---

### 2. 获取风险点位及关联摄像头信息

**请求方法**: GET  
**请求路径**: `/risk/carema`  
**描述**: 获取风险点位及关联摄像头信息  

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pointId | String | 是 | 点位ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "string",
      "name": "string",
      "countyCode": "string",
      "county": "string",
      "community": "string",
      "communityCode": "string",
      "townCode": "string",
      "riskTownCode": "string",
      "riskName": "string",
      "longitude": "BigDecimal",
      "latitude": "BigDecimal",
      "devicesInfo": "String"
    }
  ]
}
```

**响应字段说明（QueryRiskWaterPointEntity）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 主键ID |
| name | String | 名称 |
| countyCode | String | 区县编码 |
| county | String | 区县名称 |
| community | String | 社区名称 |
| communityCode | String | 社区编码 |
| townCode | String | 镇街编码 |
| riskTownCode | String | 风险镇街编码 |
| riskName | String | 风险名称 |
| longitude | BigDecimal | 经度 |
| latitude | BigDecimal | 纬度 |
| devicesInfo | String | 设备信息（JSON） |

---

### 3. 摄像头编码集

**请求方法**: GET  
**请求路径**: `/risk/carema/list`  
**描述**: 摄像头编码集  

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    "string"
  ]
}
```

**响应字段说明**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| data | List\<String\> | 摄像头编码列表 |

---

### 4. 获取内涝风险点位

**请求方法**: GET  
**请求路径**: `/risk/point`  
**描述**: 获取内涝风险点位  

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "string",
      "name": "string",
      "address": "string",
      "county": "string",
      "countyCode": "string",
      "town": "string",
      "townCode": "string",
      "community": "string",
      "communityCode": "string",
      "longitude": "BigDecimal",
      "latitude": "BigDecimal",
      "cspsflaqResponsible": "string",
      "csnlzlResponsible": "string",
      "sdzdResponsible": "string",
      "ghdwResponsible": "string",
      "cameraInfos": "string",
      "liquidLevelInfos": "string",
      "threshold": "BigDecimal",
      "unit": "string",
      "createTime": "string",
      "updateTime": "string",
      "pointLevel": "string",
      "reportor": "string",
      "pointRiskType": "string",
      "pendingRemediation": "string"
    }
  ]
}
```

**响应字段说明（QueryPointRecordDto）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 点位ID |
| name | String | 风险点位名称 |
| address | String | 地址 |
| county | String | 区县名称 |
| countyCode | String | 区县编码 |
| town | String | 镇街名称 |
| townCode | String | 镇街编码 |
| community | String | 社区名称 |
| communityCode | String | 社区编码 |
| longitude | BigDecimal | 经度 |
| latitude | BigDecimal | 纬度 |
| cspsflaqResponsible | String | 市级责任人 |
| csnlzlResponsible | String | 区级责任人 |
| sdzdResponsible | String | 镇街责任人 |
| ghdwResponsible | String | 管护责任人 |
| cameraInfos | String | 摄像头信息列表（JSON） |
| liquidLevelInfos | String | 液位计信息列表（JSON） |
| threshold | BigDecimal | 点位阈值(mm/2h) |
| unit | String | 责任单位 |
| createTime | String | 点位创建时间 |
| updateTime | String | 点位更新时间 |
| pointLevel | String | 点位风险等级 |
| reportor | String | 报告人员 |
| pointRiskType | String | 点位风险类型 |
| pendingRemediation | String | 点位状态(0启用/1禁用) |

---

### 5. 城市内涝风险点关联降雨量信息

**请求方法**: GET  
**请求路径**: `/risk/point/rain`  
**描述**: 城市内涝风险点关联降雨量信息  

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "tong_id": "Integer",
      "point_id": "String",
      "create_time": "String",
      "rian": "String",
      "tong_time": "String",
      "op": "String"
    }
  ]
}
```

**响应字段说明（CityWaterloggingPointRain）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| tong_id | Integer | 流水主键 |
| point_id | String | 关联风险点位ID |
| create_time | String | 降雨采集时间 |
| rian | String | 降雨量 |
| tong_time | String | 外部数据插入时间 |
| op | String | 操作类型 |

---

### 6. 区域预警详情

**请求方法**: GET  
**请求路径**: `/area/early/warning`  
**描述**: 区域预警详情  

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNumber | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页大小 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "tongId": "string",
        "id": "string",
        "code": "string",
        "warningPeriodStart": "LocalDateTime",
        "warningPeriodEnd": "LocalDateTime",
        "warningContent": "string",
        "warningBasis": "string",
        "warningType": "string",
        "warningLevel": "string",
        "warningStatus": "string",
        "periodsNumber": "string",
        "pdfFilePreviewUrl": "string",
        "placeCode": "string",
        "cyPlace": "string",
        "op": "string",
        "earlyWarningId": "string"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 1,
    "pages": 0
  }
}
```

**响应字段说明（DmWaterEventArea）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| tongId | String | 流水ID |
| id | String | 主键ID |
| code | String | 编码 |
| warningPeriodStart | LocalDateTime | 预警开始时间 |
| warningPeriodEnd | LocalDateTime | 预警结束时间 |
| warningContent | String | 预警内容 |
| warningBasis | String | 预警依据 |
| warningType | String | 预警类型 |
| warningLevel | String | 预警等级 |
| warningStatus | String | 预警状态 |
| periodsNumber | String | 轮次编号 |
| pdfFilePreviewUrl | String | PDF文件预览地址 |
| placeCode | String | 地点编码 |
| cyPlace | String | 叫应地点 |
| op | String | 操作类型(insert/update/delete) |
| earlyWarningId | String | 预警ID |

---

### 7. 雨前预警详情

**请求方法**: GET  
**请求路径**: `/dot/early/warning`  
**描述**: 雨前预警详情  

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNumber | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页大小 |
| warningStatus | Integer | 否 | 预警状态 |
| round | String | 是 | 轮次 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "tongId": "string",
        "id": "string",
        "code": "string",
        "longitude": "string",
        "latitude": "string",
        "level": "string",
        "createDate": "string",
        "riskPoint": "string",
        "districtName": "string",
        "streetName": "string",
        "nextPrecipitation": "string",
        "warningPeriodStart": "string",
        "warningPeriodEnd": "string",
        "warningContent": "string",
        "warningBasis": "string",
        "warningType": "string",
        "warningLevel": "string",
        "warningStatus": "string",
        "createPersonName": "string",
        "warningThreshold": "string",
        "pdfUrl": "string",
        "op": "string",
        "pointId": "string",
        "name": "string",
        "countyCode": "string",
        "county": "string",
        "townCode": "string",
        "town": "string",
        "communityCode": "string",
        "community": "string",
        "eventNum": "string",
        "cyWaringName": "string",
        "publishTime": "string",
        "publishUnit": "string",
        "twoHourRain": "string",
        "cyRainNum": "string",
        "earlyWarningId": "string"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 1,
    "pages": 0
  }
}
```

**响应字段说明（DmWaterEventDot）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| tongId | String | 流水ID |
| id | String | 主键ID |
| code | String | 编码 |
| longitude | String | 经度 |
| latitude | String | 纬度 |
| level | String | 等级 |
| createDate | String | 创建日期 |
| riskPoint | String | 风险点位 |
| districtName | String | 区县名称 |
| streetName | String | 街道名称 |
| nextPrecipitation | String | 未来降雨量 |
| warningPeriodStart | String | 预警开始时间 |
| warningPeriodEnd | String | 预警结束时间 |
| warningContent | String | 预警内容 |
| warningBasis | String | 预警依据 |
| warningType | String | 预警类型 |
| warningLevel | String | 预警等级 |
| warningStatus | String | 预警状态 |
| createPersonName | String | 创建人 |
| warningThreshold | String | 预警阈值 |
| pdfUrl | String | PDF地址 |
| op | String | 操作类型(insert/update/delete) |
| pointId | String | 点位ID |
| name | String | 点位名称 |
| countyCode | String | 区县编码 |
| county | String | 区县 |
| townCode | String | 街道编码 |
| town | String | 街道 |
| communityCode | String | 社区编码 |
| community | String | 社区 |
| eventNum | String | 事件编号 |
| cyWaringName | String | 叫应名称 |
| publishTime | String | 发布时间 |
| publishUnit | String | 发布单位/人 |
| twoHourRain | String | 2小时降雨量 |
| cyRainNum | String | 叫应降雨量 |
| earlyWarningId | String | 预警ID |

---

### 8. 预警处置详情

**请求方法**: GET  
**请求路径**: `/disposition/early/warning`  
**描述**: 预警处置详情  

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNumber | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页大小 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "tongId": "string",
        "name": "string",
        "phone": "string",
        "state": "string",
        "operatePerson": "string",
        "op": "string",
        "taskId": "string",
        "riskPointId": "string",
        "riskPoint": "string",
        "countyCode": "string",
        "county": "string",
        "townCode": "string",
        "town": "string",
        "communityCode": "string",
        "community": "string",
        "personnelNumber": "string",
        "specialVehiclesNumber": "string",
        "pumpingEquipmentQuantity": "string",
        "extractionCapacity": "string",
        "disposalMatter": "string",
        "arrivalSystemFile": "string",
        "disposeSystemFile": "string",
        "id": "string",
        "createTime": "string"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 1,
    "pages": 0
  }
}
```

**响应字段说明（DmWaterDisposition）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| tongId | String | 流水ID |
| name | String | 处置人员姓名 |
| phone | String | 联系电话（加密） |
| state | String | 状态 |
| operatePerson | String | 操作人 |
| op | String | 操作类型(insert/update/delete) |
| taskId | String | 任务ID |
| riskPointId | String | 风险点位ID |
| riskPoint | String | 风险点位名称 |
| countyCode | String | 区县编码 |
| county | String | 区县 |
| townCode | String | 街道编码 |
| town | String | 街道 |
| communityCode | String | 社区编码 |
| community | String | 社区 |
| personnelNumber | String | 人员数量 |
| specialVehiclesNumber | String | 特种车辆数量 |
| pumpingEquipmentQuantity | String | 抽水设备数量 |
| extractionCapacity | String | 抽水能力(m³/h) |
| disposalMatter | String | 处置事项（JSON） |
| arrivalSystemFile | String | 到场系统文件路径 |
| disposeSystemFile | String | 处置系统文件路径 |
| id | String | 主键ID |
| createTime | String | 创建时间 |

---

### 9. 积水处置详情

**请求方法**: GET  
**请求路径**: `/standing/early/warning`  
**描述**: 积水处置详情  

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNumber | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页大小 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "tongId": "string",
        "name": "string",
        "phone": "string",
        "state": "string",
        "operatePerson": "string",
        "op": "string",
        "taskId": "string",
        "riskPointId": "string",
        "riskPoint": "string",
        "countyCode": "string",
        "county": "string",
        "townCode": "string",
        "town": "string",
        "communityCode": "string",
        "community": "string",
        "personnelNumber": "string",
        "specialVehiclesNumber": "string",
        "pumpingEquipmentQuantity": "string",
        "extractionCapacity": "string",
        "disposalMatter": "string",
        "arrivalSystemFile": "string",
        "disposeSystemFile": "string",
        "id": "string",
        "createTime": "string"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 1,
    "pages": 0
  }
}
```

**响应字段说明（DmWaterDisposition）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| tongId | String | 流水ID |
| name | String | 处置人员姓名 |
| phone | String | 联系电话（加密） |
| state | String | 状态 |
| operatePerson | String | 操作人 |
| op | String | 操作类型(insert/update/delete) |
| taskId | String | 任务ID |
| riskPointId | String | 风险点位ID |
| riskPoint | String | 风险点位名称 |
| countyCode | String | 区县编码 |
| county | String | 区县 |
| townCode | String | 街道编码 |
| town | String | 街道 |
| communityCode | String | 社区编码 |
| community | String | 社区 |
| personnelNumber | String | 人员数量 |
| specialVehiclesNumber | String | 特种车辆数量 |
| pumpingEquipmentQuantity | String | 抽水设备数量 |
| extractionCapacity | String | 抽水能力(m³/h) |
| disposalMatter | String | 处置事项（JSON） |
| arrivalSystemFile | String | 到场系统文件路径 |
| disposeSystemFile | String | 处置系统文件路径 |
| id | String | 主键ID |
| createTime | String | 创建时间 |

---

### 10. 区域预警统计

**请求方法**: GET  
**请求路径**: `/area/early/warning/statistics`  
**描述**: 区域预警统计  

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 0,
    "solved": 0,
    "unsolve": 0
  }
}
```

**响应字段说明（WarningStatusDto）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| total | Integer | 总数 |
| solved | Integer | 已处置 |
| unsolve | Integer | 未处置 |

---

### 11. 雨前预警统计

**请求方法**: GET  
**请求路径**: `/dot/early/warning/statistics`  
**描述**: 雨前预警统计  

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| round | String | 是 | 轮次 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 0,
    "solved": 0,
    "unsolve": 0
  }
}
```

**响应字段说明（WarningStatusDto）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| total | Integer | 总数 |
| solved | Integer | 已处置 |
| unsolve | Integer | 未处置 |

---

### 12. 积水预警统计

**请求方法**: GET  
**请求路径**: `/disposition/early/warning/statistics`  
**描述**: 积水预警统计  

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 0,
    "solved": 0,
    "unsolve": 0
  }
}
```

**响应字段说明（WarningStatusDto）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| total | Integer | 总数 |
| solved | Integer | 已处置 |
| unsolve | Integer | 未处置 |

---

### 13. 查询轮次信息

**请求方法**: GET  
**请求路径**: `/warning/round`  
**描述**: 查询轮次信息  

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "label": "string",
      "year": "string",
      "no": "string",
      "startTime": "string",
      "endTime": "string"
    }
  ]
}
```

**响应字段说明（RoundDataResponse）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| label | String | 轮次标签 |
| year | String | 年份 |
| no | String | 轮次编号 |
| startTime | String | 开始时间 |
| endTime | String | 结束时间 |

---

### 14. 查询预警详情

**请求方法**: GET  
**请求路径**: `/warning/detail`  
**描述**: 查询预警详情  

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| round | String | 是 | 轮次 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "qs": "Integer",
      "round": "String",
      "mc": "String",
      "sdStart": "String",
      "sdEnd": "String",
      "time": "String",
      "filename": "String",
      "filePreviewUri": "String",
      "reportId": "String",
      "cyReqState": "String",
      "cyResState": "String",
      "cyWaringDetail": "String",
      "eventNum": "String",
      "id": "String",
      "cyWaringLevel": "String"
    }
  ]
}
```

**响应字段说明（WaterloggingWarningItem）**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| qs | Integer | 期数 |
| round | String | 轮次 |
| mc | String | 名称 |
| sdStart | String | 开始时间 |
| sdEnd | String | 结束时间 |
| time | String | 时间 |
| filename | String | 文件名 |
| filePreviewUri | String | 文件预览地址 |
| reportId | String | 报告ID |
| cyReqState | String | 叫应请求状态 |
| cyResState | String | 叫应响应状态 |
| cyWaringDetail | String | 叫应预警详情 |
| eventNum | String | 事件编号 |
| id | String | 主键ID |
| cyWaringLevel | String | 叫应预警等级 |

---

### 15. 查询三级贯通

**请求方法**: GET  
**请求路径**: `/three/response`  
**描述**: 查询三级贯通  

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| warningId | String | 是 | 预警ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "key1": "value1",
    "key2": "value2"
  }
}
```

**响应字段说明**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| data | Map\<String, Object\> | 三级贯通数据（动态键值对） |

---

### 16. 查询三级贯通统计

**请求方法**: GET  
**请求路径**: `/three/response/statistics`  
**描述**: 查询三级贯通统计  

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| warningId | String | 是 | 预警ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "key1": "value1",
    "key2": "value2"
  }
}
```

**响应字段说明**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| data | Map\<String, Object\> | 三级贯通统计数据（动态键值对） |

---

### 17. 查询期数列表

**请求方法**: GET  
**请求路径**: `/list/qs`  
**描述**: 查询期数列表  

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| round | String | 是 | 轮次 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    "string"
  ]
}
```

**响应字段说明**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| data | List\<String\> | 期数列表（如：["1", "2", "3"]） |
