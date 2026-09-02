<!--

-->
<template>
  <n-config-provider style="width: 100%; height: 100%" :theme-overrides="overridesTheme" :theme="darkTheme">
    <n-message-provider>
      <MessageContent />
    </n-message-provider>
    <div class="playground">
      <div class="title">
        <div class="label" v-if="optionData">
          <span>组件调试playground，</span>
          <span class="size">组件宽度: <input type="text" v-model="optionData.attr.w" />，</span>
          <span class="size">组件高度: <input type="text" v-model="optionData.attr.h" /></span>
        </div>
        <div class="nav-section">
          <div class="nav-buttons" style="display: flex; gap: 10px; flex-wrap: wrap;">
            <n-button style="padding: 0 10px; width: 120px;" @click="goToBus" type="warning">全局参数-事件测试</n-button>
          </div>
        </div>
        <div class="select">
          <NButton class="btn" @click="randomData">随机数据</NButton>
          <NButton class="btn_allData" @click="allData('南京市')">模拟全量</NButton>
          <NButton class="btn_allData2" @click="allData('苏州市')">模拟全量2</NButton>
          <NButton class="btn_error" @click="handleError">数据错误</NButton>
          <NButton class="btn_editPublc" @click="editPublicParam">修改公共参数</NButton>
          <NButton class="btn_hide" @click="showHide(false)">隐藏组件</NButton>
          <NButton class="btn_show" @click="showHide(true)">显示组件</NButton>
          <NTooltip trigger="hover">
            <template #trigger>
              <NButton class="btn2" @click="imitateMap">模拟映射</NButton>
            </template>
            <span>dimensions: {{ optionData.option.dataset.dimensions }}</span>
          </NTooltip>
          <NTooltip trigger="hover">
            <template #trigger>
              <NButton class="btn3" @click="getToken(true)">获取token</NButton>
            </template>
            <span>token: {{ token ? token : '未获取' }}</span>
          </NTooltip>
          <NButton class="btn4" @click="addDataset">系列+1</NButton>
          <NButton class="btn5" @click="removeDataset">系列-1</NButton>
          <NButton class="btn6" @click="addDataSource">数据+1</NButton>
          <NButton class="btn7" @click="updateDataSource">更新数据</NButton>
          <span>请选择组件：</span>
          <NSelect
            class="nSelect"
            :options="options"
            filterable
            v-model:value="selectComponent"
            placeholder="请选择组件"
          />
          <ColorSelect class="pick" @transTheme="changeColor" :optionData="null" :new-attr="_newAttr" />
        </div>
      </div>
      <div class="content">
        <div class="box" v-if="optionData">
          <!-- 组件 -->
          <template v-if="optionData.chartConfig.chartFrame !== 'map'">
            <VueDraggableResizable
              :x="0"
              :y="0"
              :draggable="true"
              :w="optionData.attr.w"
              :h="optionData.attr.h"
              @resizing="onResize"
              :parent="true"
            >
              <component
                v-if="componentName && token && !optionData.status.hide"
                :is="componentName"
                @finishedFn="finishedFn"
                @change="test"
                @changePage="test"
                @clickRow="clickRow"
                @clickCell="clickCell"
                @clickItem="test"
                @clickButton="test"
                :chartConfig="optionData"
                :themeColor="globalColor"
                :publicParamList="paramList"
                @getIframMessage="getIframMessage"
                @select="test"
                @customDblclick="test"
                @customClick="test"
                @handleDblclick="test"
                @mouseoverFn="test"
                @mouseoutFn="test"
                @rightclickFn="test"
                @clickText="test"
                @mouseEnters="test"
                @mouseLeaves="test"
                @right="test"
                :key="optionData.key"
                :projectId="'aaaaa'"
              />
            </VueDraggableResizable>
          </template>
          <template v-else>
            <component
              v-if="componentName && token"
              :is="componentName"
              @finishedFn="finishedFn"
              @dblclick="chartClick($event)"
              @changePage="test"
              @clickRow="clickRow"
              @clickCell="clickCell"
              :chartConfig="optionData"
              :themeColor="globalColor"
              :projectId="'aaaaa'"
            />
          </template>
        </div>
        <div class="config">
          <n-scrollbar>
            <!-- 配置项 -->
            <component
              v-if="configComponentName && token"
              :is="configComponentName"
              :optionData="option"
              :newAttr="newAttr"
              :themeColor="globalColor"
              :token="token"
              :uploadUrl="uploadUrl()"
              :paramList="paramList"
            />
          </n-scrollbar>
        </div>
      </div>
    </div>
  </n-config-provider>
</template>

<script lang="ts" setup>
import { ref, watch, shallowRef, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ColorSelect } from '@/components/Pages/ChartItemSetting/components/index'
import { GlobalThemeOverrides, darkTheme } from 'naive-ui'
import { MessageContent } from '@/plugins/MessageContent'
import VueDraggableResizable from 'vue-draggable-resizable-gorkys/src/components/vue-draggable-resizable.vue'
import 'vue-draggable-resizable-gorkys/dist/VueDraggableResizable.css'
import { getUUID } from '@/utils'
import allDatas from './allDatas.json'
import { paramType } from '@/types/public.d'

const router = useRouter()
const overridesTheme: GlobalThemeOverrides = {
  common: {
    borderRadius: '6px',
    primaryColor: '#3a89ff',
    primaryColorHover: 'hsl(215.89999999999998, 100%, 67.5%)',
    primaryColorPressed: 'hsl(215.89999999999998, 100%, 67.5%)',
    primaryColorSuppl: '#3a89ff'
  },
  LoadingBar: {
    colorLoading: '#3a89ff'
  },
  Pagination: {
    jumperTextColor: '#AAAAAA'
  },
  Switch: {
    railColorActive: '#10151f',
    railColor: '#10151f',
    buttonColor: '#3a89ff',
    iconColor: '#808792'
  },
  Select: {
    peers: {
      InternalSelection: {
        textColor: '#fff',
        color: '#10151F',
        border: '1px solid #222831'
      },
      InternalSelectMenu: {
        optionTextColorActive: '#fff',
        optionColorActivePending: '#3a89ff',
        optionColorPending: '#3a89ff',
        optionCheckColor: '#fff'
        // actionTextColorPending: '#fff'
      }
    }
  },
  Popover: {
    color: 'rgba(24, 29, 39, 0.8)',
    textColor: '#bcc9d4'
  },
  Tabs: {
    tabTextColorActiveSegment: '#fff'
  },
  Input: {
    color: '#10151f',
    border: '1px solid #222831'
  },
  InputNumber: {
    peers: {
      Input: {
        color: '#10151f',
        border: '1px solid #222831'
      }
    }
  },
  TreeSelect: {
    peers: {
      InternalSelection: {
        color: '#10151f'
      }
    }
  },
  Form: {
    labelTextColor: '#AAAAAA'
  },
  Upload: {
    draggerColor: '#10151f'
  },
  DataTable: {
    thColor: '#10151f',
    tdColor: 'rgba(255, 255, 255, 0.06)',
    tdColorStriped: 'transparent'
  },
  Cascader: {
    optionTextColor: '#fff',
    peers: {
      InternalSelection: {
        textColor: '#fff',
        color: '#10151F',
        border: '1px solid #222831'
      }
    }
  },
  Collapse: {
    textColor: '#fff'
  }
}

// 拖拽框的参数
const boxParams = ref({
  x: 100,
  y: 100,
  h: 100,
  w: 100,
  active: false
})

// @ts-ignore
const componentsList = COMPONENT_LIST

const token = ref<string | null>('123')
const getToken = (msg = false) => {
  // http://10.3.240.211:808
  // http://coolv.ctyun-devops.com
  // const ip = 'http://10.3.240.211:808'
  const ip = 'http://58.220.24.195:9098'
  window
    .fetch(`${ip}/api/api/system/loginNew`, {
      method: 'post',
      body: JSON.stringify({
        username: 'admin',
        password:
          'T2pTLNPhV/357zISNb9Gd0KAYvtjw3yb9lYQ1AXPEmgsm5zNARmvVQ0vz7BT8z18ZYVp0l/kMR9TtCvRbzFLqrzpVBgSd1MXfNk6CyVWkEyCDY7U5z8H/qrIIkC3u0sh/JrHJ74VPHmdUn/nILrtkTr2AMcGj6iwWeeKV9+W4kE=',
        rememberme: '0',
        tenantAccount: 'default'
      }),
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      }
    })
    .then(res => res.json())
    .then(res => {
      if (msg) {
        window.$message.success('token获取成功')
      }
      window.localStorage.setItem('token', res.token)
      token.value = res.token || 'aaa'
    })
}

// getToken()

const selectComponent = ref('')
const options = ref<Array<any>>([])
const componentName = shallowRef<any>('')
const configComponentName = shallowRef<any>('')

const optionData = ref<any>(null)

/**
 * ----------------------------------------------------------------
 * @description: 图表组件的自定义事件监听
 * @return {*}
 */
const finishedFn = () => {
  console.log('finished渲染完成 或 数据变化')
}

const chartClick = event => {
  console.log('event', event)
  console.log('当前组件信息：', optionData.value)
}

const clickRow = (row: any, index: number) => {
  console.log('点击行数', row)
}

const clickCell = (row: number, key: number | string) => {
  console.log(`点击${row}行字段为${key}`)
}
const test = e => {
  console.log(e)
  // console.log(optionData.value.option);
}

const allData = key => {
  // console.log(key);
  optionData.value.option.allDatas = allDatas
  optionData.value.option.datasetParamsMaps.interactiveParams = key
  optionData.value.option.datasetParamsMaps.dataKey = key
  // console.log(allDatas);
}

/**
 * ----------------------------------------------------------------
 * @description: 图表组件的自定义事件监听
 * @return {*}
 */

const globalColor = ref({
  color: ['#04bcfa', '#0454cb', '#056ff1', '#47dea2', '#16b8d6', '#f1b736']
})
const _newAttr = ref({
  themeColor: {
    selfTheme: {
      value: 'technology'
    }
  }
})

for (let i = 0; i < componentsList.length; i++) {
  const el = componentsList[i]
  options.value.push({
    label: el.name,
    value: el.name
  })
}

const option = ref({})

const newAttr = ref({})

watch(
  () => selectComponent.value,
  val => {
    setComponent(val)
  }
)

// 根据key选择组件
const setComponent = async (key: string) => {
  const el = componentsList.find(it => it.name === key)
  // const chart = await import(el!.entry.replace('src', '..'))
  // @ts-ignore
  const entryPath = el!.entry
    .replace(/[\\]/g, '/')
    .replace('src', '..')
    .replace('../package', '')
    .replace('export.ts', '')
  // webpack的bug 动态导入不能使用变量 模板字符串可以
  const chart = await import(`../package${entryPath}export.ts`)
  console.log('chart', chart, key)
  componentName.value = chart[key].component
  configComponentName.value = chart[key].configVue

  const config = new chart[key].config.default()
  // console.log('playgroundConfig\n', config)
  optionData.value = config
  console.log('组件配置项', optionData.value)
  option.value = config.option
  newAttr.value = config.newAttr
}

// 默认值
setComponent('BarCommon')


const changeColor = (val: { color: string[]; val: string }) => {
  globalColor.value.color = val.color
  _newAttr.value.themeColor.selfTheme.value = val.val
}

const randomData = () => {
  try {
    const {
      option: {
        dataset: { dimensions, source }
      }
    } = optionData.value as any
    if (!dimensions || !source) {
      throw new Error('不能生成随机数据')
    }
    const getRandom = () => {
      return Math.ceil(Math.random() * 300)
    }
    dimensions.forEach((key, i) => {
      if (i > 0) {
        source.forEach(item => {
          item[key] = getRandom() + ''
        })
      }
    })
    console.log(optionData.value)
  } catch (e) {
    console.log(e)
  }
}

const addDataSource = () => {
  try {
    const {
      option: {
        dataset: { dimensions, source }
      }
    } = optionData.value as any
    if (!dimensions || !source) {
      throw new Error('不能生成随机数据')
    }
    const getRandom = () => {
      return Math.ceil(Math.random() * 300)
    }
    const obj = dimensions.reduce((pre, cur) => {
      if (cur === 'product') {
        pre[cur] = 'product' + getRandom()
      } else {
        pre[cur] = getRandom()
      }
      return pre
    }, {})

    console.log('obj', obj)
    source.push(obj)
  } catch (e) {
    console.log(e)
  }
}

const updateDataSource = () => {
  optionData.value.option.dataset.source = [
    {
      product: 'A',
      data1: 120
    },
    {
      product: 'B',
      data1: 620
    },
    {
      product: 'C',
      data1: 430
    },
    {
      product: 'D',
      data1: 780
    },
    {
      product: 'E',
      data1: 580
    }
  ]
}

const addDataset = () => {
  try {
    const {
      option: {
        dataset: { dimensions, source }
      }
    } = optionData.value as any
    if (!dimensions || !source) {
      throw new Error('不能生成随机数据')
    }
    const getRandom = () => {
      return Math.ceil(Math.random() * 300)
    }
    const newKey = getUUID()
    dimensions.push(newKey)
    source.forEach(item => [(item[newKey] = getRandom())])
  } catch (e) {
    console.log(e)
  }
}

const removeDataset = () => {
  try {
    const {
      option: {
        dataset: { dimensions, source }
      }
    } = optionData.value as any
    if (!dimensions || !source) {
      throw new Error('不能生成随机数据')
    }
    const getRandom = () => {
      return Math.ceil(Math.random() * 300)
    }
    const newKey = getUUID()
    dimensions.pop()
    source.forEach(item => [(item[newKey] = getRandom())])
  } catch (e) {
    console.log(e)
  }
}

const imitateMap = () => {
  try {
    const dimensions = JSON.parse(JSON.stringify((optionData.value as any).option.dataset.dimensions))
    if (!dimensions) {
      throw new Error('不能模拟映射')
    }
    // optionData.value.option.dataset.dimensions[1] = dimensions[2];
    // optionData.value.option.dataset.dimensions[2] = dimensions[1];
    optionData.value.option.dataset.dimensions = [dimensions[0], dimensions[1]]
  } catch (e) {
    console.log(e)
  }
}

const uploadUrl = () => {
  const baseUrl = window.location.href.includes('localhost') ? 'http://58.220.24.195:9098/api/' : '/api/'
  return baseUrl + 'api/projectManage/pageDataManage/upload'
}

const onResize = (x, y, w, h) => {
  optionData.value.attr.w = w
  optionData.value.attr.h = h
}

const handleError = () => {
  optionData.value.dataset = {
    dimensions: ['error']
    // source:
  }
  console.log(optionData.value)
}

// 模拟公共参数
const paramList = ref<paramType[]>([
  {
    id: '123',
    name: 'token',
    content: 'abc',
    variableType: 1
  },
  {
    id: '456',
    name: 'name',
    content: 'a',
    variableType: 1
  },
  {
    id: '789',
    name: 'pid',
    content: '1',
    variableType: 2
  }
])
const editPublicParam = () => {
  paramList.value[0].content = getUUID()
}
const getIframMessage = e => {
  console.log(e)
}

const showHide = (type: boolean) => {
  optionData.value.status.hide = !type
  // 模拟下拉菜单组件 初始化
  if (type && optionData.value.chartConfig.key === 'Select') {
    // 模拟接口
    setTimeout(() => {
      optionData.value.option.dataset = {
        dimensions: ['a', 'b'],
        source: [
          {a: 'aa', b: 'aa1'},
          {a: 'bb', b: 'bb1'}
        ]
      }
    }, 100)
  }
}

// 路由导航方法
const goToBus = () => {
  router.push('/bus')
}
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';

.playground {
  width: 100%;
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: rgb(16, 16, 20);
  color: #fff;

  .title {
    .label {
      font-size: 18px;
      line-height: 3;
      text-align: center;

      .size {
        display: inline-block;
        font-size: 14px;

        input {
          width: 60px;
        }
      }
    }

    .nav-section {
      display: flex;
      justify-content: center;
      margin-bottom: 10px;
      
      .nav-buttons {
        display: flex;
        gap: 10px;
      }
    }

    .select {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;

      .btn {
        position: absolute;
        top: 0;
        left: 100px;
      }
      .btn8 {
        position: absolute;
        top: 40px;
        left: 100px;
      }
      .btn_allData {
        position: absolute;
        top: -40px;
        left: 100px;
      }
      .btn_allData2 {
        position: absolute;
        top: -40px;
        left: 200px;
      }
      .btn_error {
        position: absolute;
        top: -40px;
        left: 300px;
      }
      .btn_editPublc {
        position: absolute;
        top: -40px;
        left: 400px;
      }
      .btn_hide {
        position: absolute;
        top: -40px;
        left: 500px;
      }
      .btn_show {
        position: absolute;
        top: -40px;
        left: 600px;
      }

      .btn2 {
        position: absolute;
        top: 0;
        left: 200px;
      }

      .btn3 {
        position: absolute;
        top: 0;
        left: 300px;
      }

      .btn4 {
        position: absolute;
        top: 0;
        left: 400px;
      }

      .btn5 {
        position: absolute;
        top: 0;
        left: 500px;
      }

      .btn6 {
        position: absolute;
        top: 0;
        left: 600px;
      }

      .btn7 {
        position: absolute;
        top: 0;
        left: 700px;
      }

      span {
        text-align: right;
        margin-right: 10px;
      }

      .nSelect {
        width: 200px;
      }

      .pick {
        width: 300px;
        position: absolute;
        top: 0;
        right: 100px;
        background: rgba(0, 0, 0, 0.8);

        ::v-deep .dialog {
          background: rgba(0, 0, 0, 0.8);
          z-index: 999;
        }
      }
    }
  }

  .content {
    display: flex;
    height: 100%;
    overflow: auto;
    align-items: center;
    flex: 1;

    .box {
      flex: 1;
      height: 90%;
      // padding: 20px;
      background: rgba(136, 136, 136, 0.33);
      overflow: auto;
      // display: flex;
      // justify-content: center;
      // align-items: center;
    }

    .config {
      width: 290px;
      height: 100%;
      border: 1px solid #ddd;
      background: rgb(24, 24, 28);
      overflow: auto;
    }
  }
}
</style>
