# 水位监控系统 - API 接口文档

> 版本: v1.0  
> 基础路径: `http://localhost:8080`  
> 数据格式: JSON

---

## 目录

1. [通用说明](#通用说明)
2. [监测站管理](#监测站管理)
3. [水位上报与查询](#水位上报与查询)
4. [警报管理](#警报管理)

---

## 通用说明

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200为成功，500为失败 |
| message | string | 响应消息 |
| data | object/array/null | 响应数据 |

### 错误响应示例

```json
{
  "code": 500,
  "message": "监测站不存在",
  "data": null
}
```

### 数据类型说明

| 类型 | 说明 | 示例 |
|------|------|------|
| Long | 长整型 | `1`, `100` |
| String | 字符串 | `"站点名称"` |
| BigDecimal | 精确小数(水位值) | `3.50`, `5.20` |
| Integer | 整数 | `0`, `1`, `2` |
| LocalDateTime | 日期时间 | `"2026-03-28 14:30:00"` |

---

## 监测站管理

### 1. 获取所有站点列表

**请求信息**
- **接口**: `GET /stations`
- **说明**: 获取所有状态为正常的监测站列表

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "一号监测站",
      "location": "城东河道A段",
      "warnLevel": 3.50,
      "dangerLevel": 5.00,
      "status": 1,
      "createdAt": "2026-03-28 10:00:00",
      "updatedAt": "2026-03-28 10:00:00"
    },
    {
      "id": 2,
      "name": "二号监测站",
      "location": "城南河道B段",
      "warnLevel": 4.00,
      "dangerLevel": 6.00,
      "status": 1,
      "createdAt": "2026-03-28 10:00:00",
      "updatedAt": "2026-03-28 10:00:00"
    }
  ]
}
```

**响应字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 站点ID |
| name | String | 站点名称 |
| location | String | 地理位置描述 |
| warnLevel | BigDecimal | 警戒水位(m) |
| dangerLevel | BigDecimal | 危险水位(m) |
| status | Integer | 状态: 1=正常, 0=停用 |
| createdAt | String | 创建时间 |
| updatedAt | String | 更新时间 |

---

### 2. 获取单个站点详情

**请求信息**
- **接口**: `GET /stations/{id}`
- **说明**: 根据ID获取站点详情

**路径参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 站点ID |

**响应示例 - 成功**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "一号监测站",
    "location": "城东河道A段",
    "warnLevel": 3.50,
    "dangerLevel": 5.00,
    "status": 1,
    "createdAt": "2026-03-28 10:00:00",
    "updatedAt": "2026-03-28 10:00:00"
  }
}
```

**响应示例 - 失败(站点不存在)**

```json
{
  "code": 500,
  "message": "站点不存在",
  "data": null
}
```

---

### 3. 新增监测站

**请求信息**
- **接口**: `POST /stations`
- **Content-Type**: `application/json`

**请求参数**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 站点名称 |
| location | String | 否 | 地理位置描述 |
| warnLevel | BigDecimal | 是 | 警戒水位(m)，必须>0 |
| dangerLevel | BigDecimal | 是 | 危险水位(m)，必须>warnLevel |

**请求示例**

```json
{
  "name": "四号监测站",
  "location": "城北河道D段",
  "warnLevel": 2.50,
  "dangerLevel": 4.00
}
```

**响应示例 - 成功**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 4. 修改站点信息

**请求信息**
- **接口**: `PUT /stations/{id}`
- **Content-Type**: `application/json`

**路径参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 站点ID |

**请求参数**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 否 | 站点名称 |
| location | String | 否 | 地理位置描述 |
| warnLevel | BigDecimal | 否 | 警戒水位(m) |
| dangerLevel | BigDecimal | 否 | 危险水位(m) |

**请求示例**

```json
{
  "name": "一号监测站(已修改)",
  "warnLevel": 3.80
}
```

**响应示例 - 成功**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 5. 停用站点

**请求信息**
- **接口**: `DELETE /stations/{id}`
- **说明**: 停用站点（软删除，status置为0）

**路径参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 站点ID |

**响应示例 - 成功**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 水位上报与查询

### 1. 上报水位数据【核心接口】

**请求信息**
- **接口**: `POST /water/report`
- **Content-Type**: `application/json`
- **说明**: 上报水位数据，系统会自动判断是否触发警报

**请求参数**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| stationId | Long | 是 | 监测站ID |
| waterLevel | BigDecimal | 是 | 当前水位(m)，必须为正数 |
| recordedAt | String | 否 | 采集时间，格式：yyyy-MM-dd HH:mm:ss，默认为当前时间 |

**请求示例 - 正常水位**

```json
{
  "stationId": 1,
  "waterLevel": 2.00,
  "recordedAt": "2026-03-28 15:30:00"
}
```

**请求示例 - 警戒水位**

```json
{
  "stationId": 1,
  "waterLevel": 3.80
}
```

**请求示例 - 危险水位**

```json
{
  "stationId": 1,
  "waterLevel": 6.50
}
```

**响应示例 - 成功**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**响应示例 - 失败(站点不存在)**

```json
{
  "code": 500,
  "message": "监测站不存在",
  "data": null
}
```

**响应示例 - 失败(站点已停用)**

```json
{
  "code": 500,
  "message": "监测站已停用",
  "data": null
}
```

**响应示例 - 参数校验失败**

```json
{
  "code": 400,
  "message": "站点ID不能为空, 水位值必须为正数",
  "data": null
}
```

**警报触发逻辑**

| 水位情况 | 触发行为 |
|----------|----------|
| waterLevel < warnLevel | 仅保存水位记录，不触发警报 |
| warnLevel ≤ waterLevel < dangerLevel | 保存记录 + 创建**警戒**警报(alert_type=1) |
| waterLevel ≥ dangerLevel | 保存记录 + 创建**危险**警报(alert_type=2) |

---

### 2. 查询历史水位记录

**请求信息**
- **接口**: `GET /water/records`
- **说明**: 支持按站点ID和时间范围查询，按采集时间倒序排列

**查询参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| stationId | Long | 否 | 站点ID，不填查询所有站点 |
| startTime | String | 否 | 开始时间，格式：yyyy-MM-dd HH:mm:ss |
| endTime | String | 否 | 结束时间，格式：yyyy-MM-dd HH:mm:ss |

**请求示例**

```
GET /water/records?stationId=1&startTime=2026-03-28 00:00:00&endTime=2026-03-28 23:59:59
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 100,
      "stationId": 1,
      "stationName": "一号监测站",
      "waterLevel": 5.20,
      "recordedAt": "2026-03-28 15:30:00",
      "createdAt": "2026-03-28 15:30:05"
    },
    {
      "id": 99,
      "stationId": 1,
      "stationName": "一号监测站",
      "waterLevel": 3.50,
      "recordedAt": "2026-03-28 14:00:00",
      "createdAt": "2026-03-28 14:00:03"
    }
  ]
}
```

**响应字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 记录ID |
| stationId | Long | 站点ID |
| stationName | String | 站点名称 |
| waterLevel | BigDecimal | 水位值(m) |
| recordedAt | String | 采集时间 |
| createdAt | String | 记录创建时间 |

---

### 3. 查询某站点最新水位

**请求信息**
- **接口**: `GET /water/latest/{stationId}`

**路径参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| stationId | Long | 是 | 站点ID |

**响应示例 - 成功**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 100,
    "stationId": 1,
    "stationName": "一号监测站",
    "waterLevel": 5.20,
    "recordedAt": "2026-03-28 15:30:00",
    "createdAt": "2026-03-28 15:30:05"
  }
}
```

**响应示例 - 无数据**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 警报管理

### 1. 查询警报列表

**请求信息**
- **接口**: `GET /alerts`
- **说明**: 支持按状态筛选，按创建时间倒序排列

**查询参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | Integer | 否 | 状态筛选：0=未处理，1=已处理，不填查询全部 |

**请求示例 - 查询未处理警报**

```
GET /alerts?status=0
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "stationId": 1,
      "stationName": "一号监测站",
      "recordId": 100,
      "alertType": 2,
      "alertTypeDesc": "危险",
      "waterLevel": 6.50,
      "status": 0,
      "statusDesc": "未处理",
      "handledAt": null,
      "createdAt": "2026-03-28 15:30:05"
    },
    {
      "id": 2,
      "stationId": 1,
      "stationName": "一号监测站",
      "recordId": 95,
      "alertType": 1,
      "alertTypeDesc": "警戒",
      "waterLevel": 3.80,
      "status": 0,
      "statusDesc": "未处理",
      "handledAt": null,
      "createdAt": "2026-03-28 14:15:02"
    }
  ]
}
```

**响应字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 警报ID |
| stationId | Long | 站点ID |
| stationName | String | 站点名称 |
| recordId | Long | 触发的水位记录ID |
| alertType | Integer | 警报类型：1=警戒，2=危险 |
| alertTypeDesc | String | 警报类型描述 |
| waterLevel | BigDecimal | 触发时的水位值 |
| status | Integer | 状态：0=未处理，1=已处理 |
| statusDesc | String | 状态描述 |
| handledAt | String/null | 处理时间 |
| createdAt | String | 创建时间 |

**警报类型枚举**

| 值 | 描述 |
|----|------|
| 1 | 警戒 |
| 2 | 危险 |

**警报状态枚举**

| 值 | 描述 |
|----|------|
| 0 | 未处理 |
| 1 | 已处理 |

---

### 2. 标记警报为已处理

**请求信息**
- **接口**: `PUT /alerts/{id}/handle`
- **说明**: 将未处理的警报标记为已处理

**路径参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 警报ID |

**响应示例 - 成功**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**响应示例 - 失败(警报不存在)**

```json
{
  "code": 500,
  "message": "警报记录不存在",
  "data": null
}
```

**响应示例 - 失败(已处理)**

```json
{
  "code": 500,
  "message": "该警报已处理",
  "data": null
}
```

---

## 测试用例示例

### 完整业务流程测试

```bash
# 1. 查看所有站点
curl http://localhost:8080/stations

# 2. 上报正常水位
curl -X POST http://localhost:8080/water/report \
  -H "Content-Type: application/json" \
  -d '{"stationId":1,"waterLevel":2.5}'

# 3. 上报警戒水位（触发警报）
curl -X POST http://localhost:8080/water/report \
  -H "Content-Type: application/json" \
  -d '{"stationId":1,"waterLevel":4.0}'

# 4. 上报危险水位（触发警报）
curl -X POST http://localhost:8080/water/report \
  -H "Content-Type: application/json" \
  -d '{"stationId":1,"waterLevel":6.5}'

# 5. 查询警报列表
curl http://localhost:8080/alerts

# 6. 查询未处理警报
curl "http://localhost:8080/alerts?status=0"

# 7. 处理警报（假设警报ID为1）
curl -X PUT http://localhost:8080/alerts/1/handle

# 8. 查询水位历史
curl "http://localhost:8080/water/records?stationId=1"

# 9. 查询最新水位
curl http://localhost:8080/water/latest/1
```

---

## 数据库表结构参考

### station（监测站表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(64) | 站点名称 |
| location | VARCHAR(128) | 地理位置 |
| warn_level | DECIMAL(6,2) | 警戒水位 |
| danger_level | DECIMAL(6,2) | 危险水位 |
| status | TINYINT | 状态：1正常，0停用 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### water_record（水位记录表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| station_id | BIGINT | 站点ID |
| water_level | DECIMAL(6,2) | 水位值 |
| recorded_at | DATETIME | 采集时间 |
| created_at | DATETIME | 创建时间 |

### alert_record（警报记录表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| station_id | BIGINT | 站点ID |
| record_id | BIGINT | 水位记录ID |
| alert_type | TINYINT | 警报类型：1警戒，2危险 |
| water_level | DECIMAL(6,2) | 触发水位 |
| status | TINYINT | 状态：0未处理，1已处理 |
| handled_at | DATETIME | 处理时间 |
| created_at | DATETIME | 创建时间 |

---

*文档生成时间: 2026-03-28*
