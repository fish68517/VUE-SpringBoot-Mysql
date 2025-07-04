<!--
 * @Author: kaix
 * @Date: 2023-03-12 18:20:24
 * @LastEditTime: 2023-05-22 16:05:47
 * @LastEditors: kaix
 * @Description:
-->

# CoolV-comps 可视化低代码插拔式 vue3 组件库 - webpack

### echarts 数据的格式

```ts
dataset: {
  dimensions: Array<string>
  source: Array<DataProps>
}
{
  "dimensions": ["product", "data1", "data2"],
  "source": [
    {
      "product": "Mon",
      "data1": 120,
      "data2": 130
    },
    {
      "product": "Tue",
      "data1": 200,
      "data2": 130
    }
  ]
}
```

## 代码提交

- feat: 新功能
- fix: 修复 Bug
- docs: 文档修改
- perf: 性能优化
- revert: 版本回退
- ci: CICD 集成相关
- test: 添加测试代码
- refactor: 代码重构
- build: 影响项目构建或依赖修改
- style: 不影响程序逻辑的代码修改
- chore: 不属于以上类型的其他类型(日常事务)
