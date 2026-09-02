<template>
  <div class="yz-warning-wrap" :style="warningWrapStyle" @click="handleWrapperClick">
    <!-- <div class="top-dispatch-row">
      <span class="top-dispatch-btn" @click.stop="openDispatchIframe">调度</span>
    </div> -->
    <!-- <div class="yz-warning">
      <div
          v-for="item in warningList"
          :key="item.value"
          class="yz-warning__item"
          :class="{ 'yz-warning__item--active': activeWarning === item.value }"
          @click="handleWarningClick(item)"
      >
        <img class="yz-warning__icon" :src="activeWarning === item.value ? item.activeIcon : item.icon" alt=""/>
        <span class="yz-warning__label">{{ item.label }}</span>
        <span v-if="item.noticeCount && item.noticeCount > 0" class="yz-warning__badge">
          {{ item.noticeCount }}
        </span>
      </div>
    </div> -->

    <!-- <div v-show="showWeatherPanel" class="weather-risk-panel">
      <div class="weather-risk-panel__top"></div>
      <div class="weather-risk-panel__body">
        <div class="weather-risk-panel__title">天气</div>

        <div class="weather-block">
          <div class="weather-block__heading">
            <span class="weather-block__arrow"></span>
            <span>天气预报</span>
          </div>
          <div class="weather-days">
            <div v-for="item in weatherDays" :key="item.date" class="weather-day">
              <div class="weather-day__date">{{ item.date }}</div>
              <img class="weather-day__icon" :src="item.icon" alt=""/>
              <div class="weather-day__text">{{ item.weather }}</div>
              <div class="weather-day__temp">{{ item.temp }}</div>
            </div>
          </div>
        </div>

        <div class="weather-block__heading">
            <span class="weather-block__arrow"></span>
            <span>风速</span>
          </div>
        <div class="wind-table">

          <div class="wind-table__row wind-table__row--level">
            <div class="wind-table__label">等级</div>
            <div v-for="item in windLevels" :key="item.level" class="wind-table__cell">
              <div>{{ item.level }}</div>
              <div>{{ item.speed }}</div>
            </div>
          </div>
          <div class="wind-table__row">
            <div class="wind-table__label">站数</div>
            <div v-for="item in windLevels" :key="item.level" class="wind-table__cell wind-table__cell--count">
              {{ item.count }}
            </div>
          </div>
        </div>

        <div class="weather-block__heading">
          <span class="weather-block__arrow"></span>
          <span>小时降雨</span>
        </div>
        <div class="rain-table">
          <div class="rain-table__row rain-table__row--level">
            <div class="rain-table__label">等级</div>
            <div v-for="item in rainLevels" :key="item.level" class="rain-table__cell">
              <div>{{ item.level }}</div>
              <div>{{ item.rainfall }}</div>
            </div>
          </div>
          <div class="rain-table__row">
            <div class="rain-table__label">站数</div>
            <div v-for="item in rainLevels" :key="item.level" class="rain-table__cell rain-table__cell--count">
              {{ item.count }}
            </div>
          </div>
        </div>

        <div class="today-warning">
          <div class="today-warning__heading">
            <span class="weather-block__arrow"></span>
            <span>今日预警</span>
          </div>
          <div class="today-warning__meta">
            <div class="today-warning__level">
              <span>预警等级：</span>
              <img :src="warningLevelIcon" alt=""/>
              <span class="today-warning__badge">高温红色预警</span>
            </div>
            <div>影响镇街：11</div>
            <div>预警时段：14日 09:53～20:00</div>
          </div>
          <div class="today-warning__content">
            渝中区应急管理局提醒：重庆市气象台2026年7月14日9时20分发布“高温红色预警信号”，预计14日13:00-20:00，菜园坝、大坪、两路口、上清寺、大溪沟、南纪门、七星岗、解放碑、化龙桥、朝天门、石油路等11个街道，日最高气温将升至40-42℃。
          </div>
        </div>
      </div>
      <div class="weather-risk-panel__bottom"></div>
    </div> -->

    <!-- <div v-show="showDisasterPanel" class="disaster-risk-panel">
      <div class="disaster-risk-panel__top"></div>
      <div class="disaster-risk-panel__body">
        <div class="disaster-risk-panel__title">危岩地灾</div>

        <div class="disaster-section__heading">
          <span class="weather-block__arrow"></span>
          <span>风险预警（7.20）</span>
        </div>
        <div class="disaster-risk-table">
          <div class="disaster-risk-table__row disaster-risk-table__row--head">
            <div>影响乡镇</div>
            <div>风险等级</div>
          </div>
          <div v-for="item in disasterRisks" :key="item.level" class="disaster-risk-table__row">
            <div class="disaster-risk-table__towns">{{ item.towns }}</div>
            <div class="disaster-risk-table__level" :class="`disaster-risk-table__level--${item.type}`">
              {{ item.level }}
            </div>
          </div>
        </div>

        <div class="disaster-section__heading disaster-section__heading--dispose">
          <span class="weather-block__arrow"></span>
          <span>告警处置</span>
        </div>
        <div class="disaster-dispose">
          <div v-for="item in disasterDisposals" :key="item.title" class="disaster-dispose__card">
            <div class="disaster-dispose__title">
              <img :src="item.icon" alt=""/>
              <span>{{ item.title }}</span>
              <strong>{{ item.total }}</strong>
            </div>
            <div class="disaster-dispose__status">
              <div class="disaster-dispose__status-item">
                <span>正在处置</span>
                <strong class="disaster-dispose__count--pending">{{ item.pending }}</strong>
              </div>
              <div class="disaster-dispose__status-item">
                <span>已完成</span>
                <strong>{{ item.finished }}</strong>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="disaster-risk-panel__bottom"></div>
    </div> -->

    <!-- <div v-show="showFirePanel" class="fire-risk-panel">
      <div class="fire-risk-panel__top"></div>
      <div class="fire-risk-panel__body">
        <div class="fire-risk-panel__title">火灾风险</div>

        <div class="fire-section__heading">
          <span class="weather-block__arrow"></span>
          <span>城市火情（7.14-7.17）</span>
        </div>
        <div class="fire-stat">
          <div v-for="item in fireStats" :key="item.label" class="fire-stat__item">
            <span>{{ item.label }}</span>
            <strong :class="{'fire-stat__num--danger': item.danger}">{{ item.value }}</strong>
          </div>
        </div>

        <div class="fire-section__heading fire-section__heading--forest">
          <span class="weather-block__arrow"></span>
          <span>森林火灾</span>
        </div>
        <div class="forest-fire">
          <div class="forest-fire__meta">
            <div>
              <span>风险等级：</span>
              <span class="forest-fire__tag">风险高</span>
            </div>
            <div>
              <span>预警等级：</span>
              <img :src="fireWarningIcon" alt=""/>
              <span class="forest-fire__badge">森林草原火险橙色预警</span>
            </div>
            <div>
              <span>影响镇街：</span>
              <strong>3</strong>
            </div>
          </div>
          <div class="forest-fire__content">
            渝中区森林防灭火指挥部、渝中区应急管理局、渝中区林业局、渝中区气象局2026年7月14日08时00分联合发布“森林草原火险橙色预警信号”，我区两路口、上清寺、大溪沟等3个街道已出现4级森林草原火险气象等级，预计7月14日到16日我区还将持续4级及以上森林草原火险气象等级，高度危险，引发森林草原火灾的可能性较大。
          </div>
        </div>
      </div>
      <div class="fire-risk-panel__bottom"></div>
    </div> -->

    <div v-show="showFloodPanel" class="flood-risk-panel">
      <!-- 调度按钮 -->
      <span class="flood-dispatch-btn" @click.stop="openDispatchIframe">调度</span>
      <!-- 草街水库今日放水量指标 -->
      <div
        class="water-discharge-indicator"
        @mouseenter="showDischargePopup = true"
        @mouseleave="showDischargePopup = false"
      >
        <div class="water-discharge-indicator__title">草街水库今日放水量</div>
        <div class="water-discharge-indicator__value">
          <span class="water-discharge-indicator__num">{{ dischargeValue }}</span>
          <span class="water-discharge-indicator__unit">m³</span>
        </div>
        <!-- 悬浮弹框：放水量和时间折线图 -->
        <div v-show="showDischargePopup" class="water-discharge-popup">
          <div class="water-discharge-popup__title">草街水库放水量趋势</div>
          <div ref="dischargeChartRef" class="water-discharge-popup__chart"></div>
        </div>
        <!-- 连接线：从草街指标到东津沱和北碚之间 -->
        <span class="water-discharge-connector" :class="{ 'water-discharge-connector--tall': !isTripleScreen }"></span>
      </div>
      <!-- 河道线条图例 -->
      <div class="river-legend">
        <div class="river-legend__item">
          <span class="river-legend__line river-legend__line--jlj"></span>
          <span class="river-legend__label">嘉陵江侧</span>
        </div>
        <div class="river-legend__item">
          <span class="river-legend__line river-legend__line--cjj"></span>
          <span class="river-legend__label">长江侧</span>
        </div>
      </div>
      <!-- <div class="flood-risk-panel__top"></div> -->
      <div class="flood-risk-panel__body">
        <!-- <div class="flood-risk-panel__title">水患风险</div> -->

        <!-- <div class="flood-section__heading">
          <span class="weather-block__arrow"></span>
          <span>河道水位</span>
        </div> -->
        <div class="river-level" :class="{ 'river-level--tall': !isTripleScreen }" :style="riverLevelStyle">
          <div v-for="river in riverLevels" :key="river.name" class="river-level__row">
            <div class="river-level__stations">
              <span
                  v-for="line in river.lines"
                  :key="line.className"
                  class="river-level__line"
                  :class="line.className"
              ></span>
              <!-- 相邻站点距离标注 -->
              <div
                  v-for="(d, idx) in getAdjacentDistances(river.stations, river.name)"
                  :key="`dist-${idx}`"
                  class="river-level__dist-label"
                  :style="{ left: `${d.x}px`, top: `${d.y}px` }"
              >{{ d.dist }}</div>
              <!-- 玄坛庙→朝天门跨行距离标注（仅长江侧显示） -->
              <div
                  v-if="river.name === '长江侧' && crossRiverDist"
                  class="river-level__dist-label"
                  :style="{ left: `${crossRiverDist.x}px`, top: `${crossRiverDist.y}px` }"
              >{{ crossRiverDist.dist }}</div>
              <div
                  v-for="station in river.stations"
                  :key="`${river.name}-${station.name}-${station.x}-${station.y}`"
                  class="river-level__station"
                  :class="{
                    'river-level__station--active': activeStationKey === `${river.name}-${station.name}`,
                    'river-level__station--jlj': river.name === '嘉陵江侧'
                  }"
                  :style="{ left: `${station.x}px`, top: `${station.y}px` }"
                  @click.stop="handleStationClick(river.name, station)"
              >
                <div class="river-level__point" :class="`river-level__point--${station.status}`">
                  <img :src="getStationIcon(station)" alt=""/>
                </div>
                <span class="river-level__station-name" :class="`river-level__station-name--${station.status}`">
                  {{ station.name }}
                </span>
                <span v-if="getStationWaterLevelText(station)" class="river-level__station-level" :class="`river-level__station-level--${station.status}`">
                  {{ getStationWaterLevelText(station) }}
                </span>
                <!-- 洪峰倒计时气泡（仅朝天门站显示） -->
                <div
                    v-if="station.name === '朝天门' && floodCountdownText"
                    class="river-level__flood-bubble"
                >
                  <span class="river-level__flood-bubble-text">洪峰预计{{ floodCountdownText }}后到达</span>
                  <span class="river-level__flood-bubble-arrow"></span>
                </div>
                <!-- <div v-if="station.inTime || station.outTime" class="river-level__flood">
                  <div class="river-level__flood-title">洪峰过境</div>
                  <div v-if="station.inTime" class="river-level__flood-in">入：{{ station.inTime }}</div>
                  <div v-if="station.outTime" class="river-level__flood-out">出：{{ station.outTime }}</div>
                </div> -->
                <div
                    v-if="false"
                    class="river-level__tooltip"
                    :class="`river-level__tooltip--${getRiverTooltipPlacement(station.name)}`"
                >
                  <button
                      class="river-level__tooltip-close"
                      type="button"
                      @click.stop="activeStationKey = ''"
                  >×</button>
                  <div class="river-level__tooltip-title">{{ station.name }}</div>
                  <template v-if="stationLoadingMap[`${river.name}-${station.name}`]">
                    <div class="river-level__tooltip-loading">加载中...</div>
                  </template>
                  <template v-else-if="stationDetailMap[`${river.name}-${station.name}`]">
                    <div class="river-level__tooltip-row river-level__tooltip-row--warning">
                      <span>{{ stationDetailMap[`${river.name}-${station.name}`].warningInfo || '无' }}</span>
                    </div>
                    <div class="river-level__tooltip-row river-level__tooltip-row--levels">
                      <span><label>实时水位：</label><span>{{ stationDetailMap[`${river.name}-${station.name}`].realTimeLevel }}</span></span>
                      <span><label>警戒水位：</label><span>{{ stationDetailMap[`${river.name}-${station.name}`].warningLevel }}</span></span>
                      <span><label>保证水位：</label><span>{{ stationDetailMap[`${river.name}-${station.name}`].guaranteeLevel }}</span></span>
                    </div>
                    <div
                        class="river-level__tooltip-chart"
                        :ref="(el) => setChartRef(el, `${river.name}-${station.name}`)"
                    ></div>
                  </template>
                  <template v-else>
                    <div class="river-level__tooltip-loading">暂无数据</div>
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- <div class="flood-section__heading flood-section__heading--waterlog">
          <span class="weather-block__arrow"></span>
          <span>内涝（7.14-7.17）</span>
        </div>
        <div class="waterlog-stat">
          <div v-for="item in waterlogStats" :key="item.label" class="waterlog-stat__item">
            <img :src="item.icon" alt=""/>
            <div class="waterlog-stat__content">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}<em>个</em></strong>
            </div>
          </div>
        </div> -->
      </div>
      <!-- <div class="flood-risk-panel__bottom"></div> -->

      <div
          v-if="activeStationContext"
          class="river-level__tooltip river-level__tooltip--center"
          @click.stop
      >
        <button
            class="river-level__tooltip-close"
            type="button"
            @click.stop="activeStationKey = ''"
        >×</button>
        <button
            v-if="activeStationContext.station.name === '化龙桥'"
            class="river-level__tooltip-detail"
            type="button"
            @click.stop="openHlqVideoDetail"
        >查看详情</button>
        <div class="river-level__tooltip-title">{{ activeStationContext.station.name }}</div>
        <template v-if="stationLoadingMap[activeTooltipKey]">
          <div class="river-level__tooltip-loading">加载中...</div>
        </template>
        <template v-else-if="stationDetailMap[activeTooltipKey]">
          <div class="river-level__tooltip-row river-level__tooltip-row--warning">
            <span>{{ stationDetailMap[activeTooltipKey].warningInfo || '无' }}</span>
          </div>
          <div class="river-level__tooltip-row river-level__tooltip-row--levels">
            <span><label>实时水位：</label><span>{{ stationDetailMap[activeTooltipKey].realTimeLevel }}</span></span>
            <span><label>警戒水位：</label><span>{{ stationDetailMap[activeTooltipKey].warningLevel }}</span></span>
            <span><label>保证水位：</label><span>{{ stationDetailMap[activeTooltipKey].guaranteeLevel }}</span></span>
          </div>
          <div
              class="river-level__tooltip-chart"
              :ref="(el) => setChartRef(el, activeTooltipKey)"
          ></div>
        </template>
        <template v-else>
          <div class="river-level__tooltip-loading">暂无数据</div>
        </template>
      </div>
    </div>

    <!-- <div v-show="showLinkagePanel" class="linkage-risk-panel">
      <div class="linkage-risk-panel__top"></div>
      <div class="linkage-risk-panel__body">
        <div class="linkage-risk-panel__title">三情联动</div>

        <div class="linkage-table">
          <div class="linkage-table__row linkage-table__row--head">
            <div>时间</div>
            <div>事件</div>
            <div>状态</div>
          </div>
          <div v-for="item in linkageEvents" :key="`${item.time}-${item.status}`" class="linkage-table__row">
            <div class="linkage-table__time">
              <span v-if="item.isRecent" class="linkage-table__dot"></span>
              <span>{{ item.time }}</span>
            </div>
            <div class="linkage-table__event">{{ item.event }}</div>
            <div class="linkage-table__action">
              <span class="linkage-table__status" :class="`linkage-table__status--${item.type}`">
                {{ item.status }}
              </span>
              <button class="linkage-table__view" type="button" @click.stop="handleLinkageView(item)">查看</button>
            </div>
          </div>
        </div>
      </div>
      <div class="linkage-risk-panel__bottom"></div>
    </div> -->
    <!-- 化龙桥监控视频弹窗 -->
    <div v-if="showHlqVideoDialog" class="hlq-video-dialog" :class="{ 'hlq-video-dialog--fullscreen': hlqVideoFullscreen }" @click.stop>
      <div class="hlq-video-dialog__header">
        <span class="hlq-video-dialog__title">化龙桥监控视频</span>
        <button class="hlq-video-dialog__close" @click.stop="closeHlqVideoDialog">×</button>
      </div>
      <div class="hlq-video-dialog__body">
        <video
          ref="hlqDialogVideoRef"
          class="hlq-video-dialog__video"
          autoplay muted playsinline
        ></video>
      </div>
      <button
        v-if="!hlqVideoFullscreen"
        class="hlq-video-dialog__enlarge"
        @click.stop="toggleHlqFullscreen"
      >放大</button>
      <button
        v-else
        class="hlq-video-dialog__enlarge"
        @click.stop="toggleHlqFullscreen"
      >缩小</button>
    </div>

    <!-- 隐藏的video和canvas用于后台抽帧 -->
    <video ref="hiddenVideoRef" autoplay muted playsinline style="display:none;"></video>
    <canvas ref="hiddenCanvasRef" style="display:none;"></canvas>
  </div>
</template>

<script lang="ts" setup>
import 'element-plus/dist/index.css'
import {ref, defineProps, reactive, onMounted, onUnmounted, nextTick, provide, watch, computed} from 'vue'
import type {GlobalParams, EventBus} from '@/package/index.d'
import axios from "axios";
import {ElLoading} from "element-plus";
import {pickupcoordinateutil} from './api/index'
import {copyToClip} from "@/package/components/rightClick/utils/tools";
import tianqiHuang from './img/tianqi_huang.png'
import tianqiLan from './img/tianqi_lan.png'
import dizaiHuang from './img/dizai_huang.png'
import dizaiLan from './img/dizai_lan.png'
import huozaiHuang from './img/huozai_huang.png'
import huozaiLan from './img/huozai_lan.png'
import neilaoHuang from './img/neilao_huang.png'
import neilaoLan from './img/neilao_lan.png'
import jiaotongHuang from './img/jiaotong_huang.png'
import jiaotongLan from './img/jiaotong_lan.png'
import sanqingliandongHuang from './img/sanqingliandong_huang.png'
import sanqingliandongLan from './img/sanqingliandong_lan.png'
import qingIcon from './img/weather_icon/qing.png'
import duoyunIcon from './img/weather_icon/duoyun.png'
import xiaoyuIcon from './img/weather_icon/xiaoyu.png'
import zhengyuIcon from './img/weather_icon/zhengyu.png'
import warningLevelIcon from './img/warn_type_icon/Hightemperature_RED.gif'
import fireWarningIcon from './img/warn_type_icon/Forestfire_ORANGE.gif'
import disasterDeviceIcon from './img/weiyandizai_shebeigaojin.png'
import disasterGridIcon from './img/weiyandizai_wanggexuncha.png'
import riverGreenIcon from './img/shuihuan_hd_lv.png'
import riverYellowIcon from './img/shuihuan_hd_huang.png'
import riverRedIcon from './img/shuihuan_hd_hong.png'
import hlqGreenIcon from './img/hlqGreen.png'
import hlqYellowIcon from './img/hlqYellow.png'
import hlqRedIcon from './img/hlqRed.png'
import waterlogWarnIcon from './img/shuihuan_yjdw.png'
import waterlogProcessingIcon from './img/shuihuan_czz.png'
import waterlogPatrolIcon from './img/shuihuan_xcz.png'
import waterlogDoneIcon from './img/shuihuan_wccz.png'
import riverInIcon from './img/shuihuan_ru.png'
import riverOutIcon from './img/shuihuan_chu.png'
import floodBgIcon from './img/bg_list_data.png'

const sourceName = 'BzWarning'
const isDevelopment = process.env.NODE_ENV === 'development'
const mapReady = ref(false)
const cesiumContainer = ref(null)

const coordinatesystem = ref<string>('CGCS2000')
type ActiveItem = {
  addressName?: string
  active?: boolean
}
const list = ref<Array<ActiveItem>>([])

type WarningItem = {
  label: string
  value: string
  icon: string
  activeIcon: string
  noticeCount?: number
}

type WeatherDay = {
  date: string
  icon: string
  weather: string
  temp: string
}

type WindLevel = {
  level: string
  speed: string
  count: number
}

type RainLevel = {
  level: string
  rainfall: string
  count: number
}

type DisasterRisk = {
  towns: string
  level: string
  type: 'higher' | 'some' | 'high' | 'very-high'
}

type DisasterDisposal = {
  title: string
  icon: string
  total: number
  pending: number
  finished: number
}

type FireStat = {
  label: string
  value: number
  danger?: boolean
}

type RiverStationStatus = 'green' | 'yellow' | 'red'

type RiverStation = {
  name: string
  status: RiverStationStatus
  x: number
  y: number
  dist?: number
  inTime?: string
  outTime?: string
  checkTime?: string
  waterLevel?: string
  trend?: string
  overWarning?: string
  overGuarantee?: string
  district?: string
  warningInfo?: string
  realTimeLevel?: string
  warningLevel?: string
  guaranteeLevel?: string
  chartData?: Array<{ time: string; value: number }>
}

type RiverLevel = {
  name: string
  lines: Array<{className: string}>
  stations: Array<RiverStation>
}

type WaterlogStat = {
  label: string
  value: number
  icon: string
}

type LinkageStatus = 'processing' | 'finished' | 'pending'

type LinkageEvent = {
  time: string
  event: string
  status: string
  type: LinkageStatus
  isRecent?: boolean
}

const activeWarning = ref<string>('neilao')
// const showWeatherPanel = ref<boolean>(false)
// const showDisasterPanel = ref<boolean>(false)
// const showFirePanel = ref<boolean>(false)
const showFloodPanel = ref<boolean>(true)
// const showLinkagePanel = ref<boolean>(false)
// const warningList = ref<Array<WarningItem>>([
//   {label: '天气', value: 'tianqi', icon: tianqiLan, activeIcon: tianqiHuang},
//   {label: '危岩地灾', value: 'dizai', icon: dizaiLan, activeIcon: dizaiHuang, noticeCount: 489},
//   {label: '火灾风险', value: 'huozai', icon: huozaiLan, activeIcon: huozaiHuang, noticeCount: 39},
//   {label: '水患风险', value: 'neilao', icon: neilaoLan, activeIcon: neilaoHuang},
//   {label: '交通', value: 'jiaotong', icon: jiaotongLan, activeIcon: jiaotongHuang},
//   {label: '三情联动', value: 'sanqingliandong', icon: sanqingliandongLan, activeIcon: sanqingliandongHuang}
// ])

// const weatherDays = ref<Array<WeatherDay>>([
//   {date: '7月14日', icon: qingIcon, weather: '晴', temp: '32-41℃'},
//   {date: '7月15日', icon: zhengyuIcon, weather: '阴', temp: '32-41℃'},
//   {date: '7月16日', icon: xiaoyuIcon, weather: '大雨', temp: '33-41℃'},
//   {date: '7月17日', icon: xiaoyuIcon, weather: '小雨', temp: '26-38℃'},
//   {date: '7月18日', icon: duoyunIcon, weather: '多云', temp: '27-35℃'},
//   {date: '7月19日', icon: xiaoyuIcon, weather: '阵雨', temp: '28-37℃'},
//   {date: '7月20日', icon: qingIcon, weather: '晴', temp: '28-39℃'}
// ])

// const windLevels = ref<Array<WindLevel>>([
//   {level: '13级及以上', speed: '≥37.0', count: 0},
//   {level: '11-12级', speed: '28.5~36.9', count: 0},
//   {level: '9-10级', speed: '20.8~28.4', count: 0},
//   {level: '7-8级', speed: '13.9~20.7', count: 0},
//   {level: '6级及以下', speed: '≤13.8', count: 1224}
// ])

// const rainLevels = ref<Array<RainLevel>>([
//   {level: '特大暴雨', rainfall: '≥250', count: 0},
//   {level: '大暴雨', rainfall: '100~249.9', count: 0},
//   {level: '暴雨', rainfall: '50~99.9', count: 0},
//   {level: '大雨', rainfall: '25~49.9', count: 0},
//   {level: '中雨', rainfall: '10~24.9', count: 0},
//   {level: '小雨', rainfall: '0.1~9.9', count: 0}
// ])

// const disasterRisks = ref<Array<DisasterRisk>>([
//   {
//     towns: '华龙桥、大坪',
//     level: '风险较高',
//     type: 'higher'
//   },
//   {
//     towns: '平安镇、龙溪镇、鹿鸣乡、岩东乡、长生镇、保家镇、万足镇',
//     level: '有一定风险',
//     type: 'some'
//   },
//   {
//     towns: '平安镇、龙溪镇、鹿鸣乡、岩东乡、长生镇、保家镇、万足镇',
//     level: '风险高',
//     type: 'high'
//   },
//   {
//     towns: '平安镇、龙溪镇、鹿鸣乡、岩东乡、长生镇、保家镇、万足镇',
//     level: '风险很高',
//     type: 'very-high'
//   }
// ])

// const disasterDisposals = ref<Array<DisasterDisposal>>([
//   {title: '设备告警', icon: disasterDeviceIcon, total: 1, pending: 0, finished: 1},
//   {title: '网格巡查', icon: disasterGridIcon, total: 0, pending: 0, finished: 0}
// ])

// const fireStats = ref<Array<FireStat>>([
//   {label: '正在处置', value: 0},
//   {label: '已完成处置', value: 2},
//   {label: '火灾舆情', value: 3, danger: true}
// ])

const stationIconMap: Record<RiverStationStatus, string> = {
  green: riverGreenIcon,
  yellow: riverYellowIcon,
  red: riverRedIcon
}

const hlqIconMap: Record<RiverStationStatus, string> = {
  green: hlqGreenIcon,
  yellow: hlqYellowIcon,
  red: hlqRedIcon
}

const getStationIcon = (station: RiverStation): string => {
  if (station.name === '化龙桥') {
    return hlqIconMap[station.status]
  }
  return stationIconMap[station.status]
}

const getStationWaterLevelText = (station: RiverStation): string => {
  const level = station.waterLevel || station.realTimeLevel
  return level ? `${level}m` : ''
}

const withRiverInfo = (station: RiverStation): RiverStation => {
  return {
    ...station
  }
}

const riverTooltipDownStations = ['鸭嘴', '东津沱', '北碚(三)', '磁器口', '大溪沟', '千厮门']
const riverTooltipLeftStations = ['朝天门', '郭家沱']

const getRiverTooltipPlacement = (stationName: string) => {
  if (riverTooltipDownStations.includes(stationName)) return 'down'
  if (riverTooltipLeftStations.includes(stationName)) return 'left'
  return 'top'
}

const riverLevels = ref<Array<RiverLevel>>([
  {
    name: '嘉陵江侧',
    lines: [
      {className: 'river-level__line--top-main'},
      {className: 'river-level__line--top-branch'},
      {className: 'river-level__line--top-right'}
    ],
    stations: [
      {name: '鸭嘴', status: 'green', x: -30, y: 15},
      {name: '东津沱', status: 'green', x: 30, y: 15},
      {name: '北碚(三)', status: 'green', x: 90, y: 15},
      {name: '磁器口', status: 'green', x: 150, y: 15},
      {name: '化龙桥', status: 'green', x: 210, y: 15},
      {name: '大溪沟', status: 'green', x: 270, y: 15, inTime: '2h', outTime: '2h'},
      {name: '千厮门', status: 'green', x: 330, y: 15, inTime: '2h', outTime: '2h'},
      {name: '朝天门', status: 'green', x: 382, y: 75, inTime: '2h', outTime: '2h'},
      {name: '郭家沱', status: 'green', x: 434, y: 75}
    ].map(withRiverInfo)
  },
  {
    name: '长江侧',
    lines: [
      {className: 'river-level__line--bottom-main'},
      {className: 'river-level__line--bottom-branch'}
    ],
    stations: [
      {name: '泸州(三)', status: 'green', x: -30, y: -15},
      {name: '塔坪', status: 'green', x: 43, y: -15},
      {name: '钓二嘴', status: 'green', x: 116, y: -15},
      {name: '李家沱', status: 'green', x: 189, y: -15},
      {name: '菜园坝', status: 'green', x: 262, y: -15, inTime: '2h', outTime: '2h'},
      {name: '玄坛庙', status: 'green', x: 335, y: -15, inTime: '2h', outTime: '2h'}
    ].map(withRiverInfo)
  }
])

// ECharts 折线图
const echartsLib = (window as any).echarts
const chartRefs: Record<string, HTMLElement | null> = {}
const chartInstances: Map<string, any> = new Map()

// 草街水库今日放水量
const dischargeValue = ref('0')
const showDischargePopup = ref(false)
const dischargeChartRef = ref<HTMLElement | null>(null)
let dischargeChartInstance: any = null
const dischargeChartData = ref<{ time: string; value: number }[]>([])

const renderDischargeChart = () => {
  if (!dischargeChartRef.value || !echartsLib) return
  if (!dischargeChartInstance) {
    dischargeChartInstance = echartsLib.init(dischargeChartRef.value)
  }
  dischargeChartInstance.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: {
      type: 'category',
      data: dischargeChartData.value.map(d => d.time),
      axisLabel: { color: '#8BBCE6', fontSize: 10 },
      axisLine: { lineStyle: { color: '#3a6a9a' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#8BBCE6', fontSize: 10 },
      axisLine: { lineStyle: { color: '#3a6a9a' } },
      splitLine: { lineStyle: { color: 'rgba(58,106,154,0.3)' } }
    },
    series: [{
      name: '放水量',
      type: 'line',
      smooth: true,
      data: dischargeChartData.value.map(d => d.value),
      lineStyle: { color: '#5FEAFF', width: 2 },
      itemStyle: { color: '#5FEAFF' },
      areaStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(95,234,255,0.3)' },
            { offset: 1, color: 'rgba(95,234,255,0)' }
          ]
        }
      }
    }]
  })
}

// 获取草街水库放水量数据
const fetchDischargeData = async () => {
  try {
    const now = new Date()
    const y = now.getFullYear()
    const m = String(now.getMonth() + 1).padStart(2, '0')
    const d = String(now.getDate()).padStart(2, '0')
    const startTime = `${y}-${m}-${d} 00:00:00`
    const endTime = `${y}-${m}-${d} 23:59:59`

    const res = await axios.post(RESERVOIR_STATION_URL, { startTime, endTime })
    const data = res?.data?.data ?? {}
    const historyList = Array.isArray(data?.historyList) ? data.historyList : []

    if (historyList.length > 0) {
      dischargeChartData.value = historyList.map((item: any) => ({
        time: item.tm ? String(item.tm).slice(11, 16) : '--:--',
        value: Number(item.otq) || 0
      }))
      const total = dischargeChartData.value.reduce((sum, item) => sum + item.value, 0)
      dischargeValue.value = total.toLocaleString()
    } else {
      dischargeChartData.value = []
      dischargeValue.value = '0'
    }
  } catch (error) {
    console.error('获取草街水库放水量失败:', error)
    dischargeChartData.value = []
    dischargeValue.value = '0'
  }
}

// 悬浮弹框显示时渲染图表
watch(showDischargePopup, (val) => {
  if (val) {
    nextTick(() => renderDischargeChart())
  }
})

const setChartRef = (el: any, key: string) => {
  chartRefs[key] = el
}

// 站点水文信息接口
const HYDROLOGY_STATION_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/common/getHydrologyStationInfo'

// 草街水库站点综合信息接口
const RESERVOIR_STATION_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/common/getReservoirStationInfo'

// 当前激活的站点（点击触发）
const activeStationKey = ref<string>('')
const activeTooltipKey = computed(() => activeStationKey.value)
const activeStationContext = computed(() => {
  for (const river of riverLevels.value) {
    const station = river.stations.find(item => `${river.name}-${item.name}` === activeStationKey.value)
    if (station) return { riverName: river.name, station }
  }
  return null
})
// 站点详情数据缓存
const stationDetailMap = reactive<Record<string, {
  warningInfo: string
  realTimeLevel: string
  warningLevel: string
  guaranteeLevel: string
  historyList: any[]
}>>({})
// 站点加载状态
const stationLoadingMap = reactive<Record<string, boolean>>({})

// 所有站点信息接口
const ALL_STATION_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/common/getAllHydrologyStationInfo'

// ===================== IRS视频流抽帧+水位识别 =====================
// IRS平台接口基础地址
const IRS_BASE_URL = 'http://100.92.3.205:11001'

// 隐藏的video和canvas引用
const hiddenVideoRef = ref<HTMLVideoElement | null>(null)
const hiddenCanvasRef = ref<HTMLCanvasElement | null>(null)

// 化龙桥监控视频弹窗
const hlqDialogVideoRef = ref<HTMLVideoElement | null>(null)
const showHlqVideoDialog = ref(false)
const hlqVideoFullscreen = ref(false)

// IRS视频流地址
const irsStreamUrl = ref<string>('')
// HLS播放器实例（用any避免类型冲突）
let hlsPlayer: any = null
// 抽帧定时器
let captureTimer: ReturnType<typeof setInterval> | null = null
// 抽帧间隔（秒）
const CAPTURE_INTERVAL = 5

// 连接视频流（参考index.html逻辑）
const connectStream = (url: string) => {
  const video = hiddenVideoRef.value
  if (!video) return

  // 销毁旧播放器
  if (hlsPlayer) {
    hlsPlayer.destroy()
    hlsPlayer = null
  }
  video.removeAttribute('src')
  video.load()

  const isHls = url.toLowerCase().includes('.m3u8') || url.toLowerCase().includes('/live/')
  const isFlv = url.toLowerCase().includes('.flv')

  console.log('[IRS] 视频流地址:', url, '协议判断: HLS=', isHls, 'FLV=', isFlv)

  if (isHls || !isFlv) {
    tryHls(url)
  } else if (isFlv) {
    // FLV需要flv.js，项目中未安装，降级为原生
    console.warn('[IRS] FLV协议需要flv.js，尝试原生播放')
    tryNative(url)
  } else {
    tryNative(url)
  }
}

// 尝试HLS播放（使用window上的Hls对象，与index.html一致）
const tryHls = (url: string) => {
  const video = hiddenVideoRef.value
  if (!video) return

  const HlsLib = (window as any).Hls
  if (HlsLib && HlsLib.isSupported()) {
    console.log('[IRS] 尝试HLS协议...')
    hlsPlayer = new HlsLib({
      enableWorker: true,
      lowLatencyMode: true,
      maxBufferLength: 10,
      maxMaxBufferLength: 30,
    })
    hlsPlayer.loadSource(url)
    hlsPlayer.attachMedia(video)
    hlsPlayer.on(HlsLib.Events.MANIFEST_PARSED, function () {
      video.play().catch(function () {})
      console.log('[IRS] HLS连接成功，后台静默播放中')
      // 连接成功后立即抽帧一次
      setTimeout(captureAndParse, 2000)
    })
    hlsPlayer.on(HlsLib.Events.ERROR, function (event: any, data: any) {
      console.error('[IRS] HLS错误:', data.details)
      if (data.fatal) {
        hlsPlayer?.destroy()
        hlsPlayer = null
        console.warn('[IRS] HLS致命错误，尝试原生播放')
        tryNative(url)
      }
    })
  } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
    console.log('[IRS] Safari原生HLS...')
    tryNative(url)
  } else {
    console.warn('[IRS] HLS不可用，尝试原生播放')
    tryNative(url)
  }
}

// 尝试原生播放
const tryNative = (url: string) => {
  const video = hiddenVideoRef.value
  if (!video) return

  console.log('[IRS] 尝试原生video播放...')
  video.crossOrigin = 'anonymous'
  video.src = url
  video.load()
  video.onloadeddata = function () {
    video.play().catch(function () {})
    console.log('[IRS] 原生播放成功，后台静默播放中')
    setTimeout(captureAndParse, 2000)
  }
  video.onerror = function () {
    console.error('[IRS] 所有播放方式均失败')
  }
}

// 抽帧+水位识别
const captureAndParse = async () => {
  const video = hiddenVideoRef.value
  const canvas = hiddenCanvasRef.value
  if (!video || !canvas) return

  if (video.readyState < 2) {
    console.warn('[IRS] 视频未就绪，跳过抽帧')
    return
  }

  const w = video.videoWidth || 640
  const h = video.videoHeight || 360
  canvas.width = w
  canvas.height = h

  try {
    const ctx = canvas.getContext('2d')
    if (!ctx) return
    ctx.drawImage(video, 0, 0, w, h)
    const dataUrl = canvas.toDataURL('image/png')
    const imageBase64 = dataUrl.split(',')[1] // 去掉 data:image/png;base64, 前缀

    console.log('[IRS] 抽帧成功，发送给后端识别水位...')

    const res = await axios.post(`${IRS_BASE_URL}/api/boot/system/irs/parse`, { imageBase64 })
    const result = res?.data
    console.log('[IRS] 水位识别结果:', result)

    if (result && result.code === '1000' && result.data) {
      // 识别到水位，更新化龙桥站点数据
      const waterLevel = result.data // 如 "12.35m"
      const waterLevelNum = parseFloat(waterLevel) // 提取数字

      riverLevels.value.forEach(river => {
        river.stations.forEach(station => {
          if (station.name === '化龙桥') {
            station.waterLevel = String(waterLevelNum)
          }
        })
      })

      console.log('[IRS] 化龙桥水位已更新:', waterLevel)
    } else {
      // 未识别到水位，继续等待下一次抽帧
      console.log('[IRS] 未识别到水位，等待下次抽帧')
    }
  } catch (e: any) {
    console.error('[IRS] 抽帧或识别失败:', e.message)
  }
}

// 初始化IRS视频流
const initIrsStream = async () => {
  try {
    const res = await axios.get(`${IRS_BASE_URL}/api/boot/system/irs/url`)
    const url = res?.data?.data
    if (url) {
      irsStreamUrl.value = url
      console.log('[IRS] 获取视频流地址成功:', url)
      // 等待DOM渲染完成
      await nextTick()
      connectStream(url)
      // 启动定时抽帧
      if (captureTimer) clearInterval(captureTimer)
      captureTimer = setInterval(captureAndParse, CAPTURE_INTERVAL * 1000)
    } else {
      console.warn('[IRS] 获取视频流地址失败:', res?.data)
    }
  } catch (e: any) {
    console.error('[IRS] 获取视频流地址异常:', e.message)
  }
}

// 打开化龙桥监控视频弹窗
const openHlqVideoDialog = () => {
  showHlqVideoDialog.value = true
  nextTick(() => {
    const dialogVideo = hlqDialogVideoRef.value
    if (dialogVideo && irsStreamUrl.value) {
      // 在弹窗中也用HLS播放
      const HlsLib = (window as any).Hls
      if (HlsLib && HlsLib.isSupported()) {
        const hls = new HlsLib({
          enableWorker: true,
          lowLatencyMode: true,
        })
        hls.loadSource(irsStreamUrl.value)
        hls.attachMedia(dialogVideo)
        hls.on(HlsLib.Events.MANIFEST_PARSED, function () {
          dialogVideo.play().catch(function () {})
        })
        // 保存实例用于销毁
        ;(dialogVideo as any)._hls = hls
      } else if (dialogVideo.canPlayType('application/vnd.apple.mpegurl')) {
        dialogVideo.src = irsStreamUrl.value
        dialogVideo.play().catch(function () {})
      }
    }
  })
}

// 关闭化龙桥监控视频弹窗
const closeHlqVideoDialog = () => {
  showHlqVideoDialog.value = false
  hlqVideoFullscreen.value = false
  const dialogVideo = hlqDialogVideoRef.value
  if (dialogVideo) {
    const hls = (dialogVideo as any)._hls
    if (hls) {
      hls.destroy()
      ;(dialogVideo as any)._hls = null
    }
    dialogVideo.removeAttribute('src')
    dialogVideo.load()
  }
}

// 放大/缩小监控视频
const toggleHlqFullscreen = () => {
  hlqVideoFullscreen.value = !hlqVideoFullscreen.value
}

// ===================== 模拟模式（本地调试用） =====================
// 设为 true 使用模拟数据，设为 false 调用真实接口
const MOCK_FLOOD_PEAK = false

// 模拟站点数据：大溪沟和磁器口为红色（超过保证水位），朝天门为绿色
// 大溪沟 dist=5km（最近），磁器口 dist=15km
// 流速 6.2 m/s → 5000m / 6.2 = 806s ≈ 13m26s 后到达朝天门
const mockStationData = {
  data: {
    stations: [
      { stnm: '鸭嘴', z: 170, wrz: 185, grz: 190, dist: 25 },
      { stnm: '东津沱', z: 172, wrz: 185, grz: 190, dist: 22 },
      { stnm: '北碚(三)', z: 174, wrz: 185, grz: 190, dist: 20 },
      { stnm: '磁器口', z: 195, wrz: 185, grz: 190, dist: 15 },   // 红色
      { stnm: '化龙桥', z: 180, wrz: 185, grz: 190, dist: 10 },
      { stnm: '大溪沟', z: 192, wrz: 185, grz: 190, dist: 5 },     // 红色（最近）
      { stnm: '千厮门', z: 180, wrz: 185, grz: 190, dist: 2 },
      { stnm: '朝天门', z: 180, wrz: 185, grz: 190, dist: 0 },     // 绿色
      { stnm: '郭家沱', z: 175, wrz: 185, grz: 190, dist: 8 },
      { stnm: '泸州(三)', z: 160, wrz: 175, grz: 180, dist: 120 },
      { stnm: '塔坪', z: 162, wrz: 175, grz: 180, dist: 100 },
      { stnm: '钓二嘴', z: 164, wrz: 175, grz: 180, dist: 80 },
      { stnm: '李家沱', z: 166, wrz: 175, grz: 180, dist: 60 },
      { stnm: '菜园坝', z: 170, wrz: 175, grz: 180, dist: 30 },
      { stnm: '玄坛庙', z: 168, wrz: 175, grz: 180, dist: 40 }
    ]
  }
}

// 处理站点数据（公共逻辑，真实接口和模拟数据共用）
const processStationData = (stations: any[]) => {
  const stationStatusMap: Record<string, RiverStationStatus> = {}
  const stationDistMap: Record<string, number | undefined> = {}
  const stationWaterLevelMap: Record<string, string | undefined> = {}
  const redStations: Array<{ name: string; dist?: number }> = []
  stations.forEach(station => {
    const current = station.z ?? station.sw ?? null
    const warningLevel = station.ivhz ?? station.wrz ?? null  // 警戒水位
    const guaranteeLevel = station.grz ?? null  // 保证水位
    let status: RiverStationStatus = 'green'
    if (current != null && guaranteeLevel != null && current >= guaranteeLevel) {
      status = 'red'
    } else if (current != null && warningLevel != null && current >= warningLevel) {
      status = 'yellow'
    }
    const stnm = station.stnm || ''
    const dist = station.dist != null ? Number(station.dist) : undefined
    if (stnm) {
      stationStatusMap[stnm] = status
      stationDistMap[stnm] = dist
      if (current != null) {
        stationWaterLevelMap[stnm] = String(current)
      }
    }
    if (status === 'red') {
      redStations.push({ name: stnm, dist })
    }
    console.log(`站点: ${stnm || '--'}, 当前水位: ${current}, 警戒水位: ${warningLevel}, 保证水位: ${guaranteeLevel}, 状态: ${status}, 距离朝天门: ${dist}km`)
  })
  // 更新 riverLevels 中匹配站点的 status 和 dist
  riverLevels.value.forEach(river => {
    river.stations.forEach(station => {
      if (stationStatusMap[station.name]) {
        station.status = stationStatusMap[station.name]
      }
      if (stationDistMap[station.name] != null) {
        station.dist = stationDistMap[station.name]
      }
      if (stationWaterLevelMap[station.name] != null) {
        station.waterLevel = stationWaterLevelMap[station.name]
      }
    })
  })
  // 检查洪峰状态并计算倒计时
  checkFloodPeak(redStations)
}

// 计算相邻站点之间的距离（公里）
const getAdjacentDistances = (stations: Array<RiverStation>, riverName?: string): Array<{ x: number; y: number; dist: string }> => {
  const result: Array<{ x: number; y: number; dist: string }> = []
  for (let i = 0; i < stations.length - 1; i++) {
    const a = stations[i]
    const b = stations[i + 1]
    if (a.dist != null && b.dist != null) {
      const diff = Math.abs(a.dist - b.dist)
      const x = a.x + 45
      // 嘉陵江侧距离标注在线条下方，长江侧在线条上方
      const y = riverName === '嘉陵江侧' ? a.y + 20 : a.y - 12
      result.push({ x, y, dist: `${diff.toFixed(1)}km` })
    }
  }
  return result
}

// 玄坛庙→朝天门跨行距离
const crossRiverDist = computed(() => {
  const lower = riverLevels.value[1]?.stations
  const upper = riverLevels.value[0]?.stations
  if (!lower || !upper) return null
  const xuantan = lower.find(s => s.name === '玄坛庙')
  const chaotian = upper.find(s => s.name === '朝天门')
  if (!xuantan || !chaotian || xuantan.dist == null || chaotian.dist == null) return null
  const diff = Math.abs(xuantan.dist - chaotian.dist)
  return {
    x: xuantan.x + 45,
    y: xuantan.y - 27,
    dist: `${diff.toFixed(1)}km`
  }
})

// 获取所有站点信息并更新状态
const fetchAllStationInfo = () => {
  if (MOCK_FLOOD_PEAK) {
    console.log('[模拟模式] 使用模拟站点数据')
    const stations = mockStationData.data.stations
    processStationData(stations)
    return
  }
  axios.post(ALL_STATION_URL)
    .then(res => {
      console.log('getAllHydrologyStationInfo 返回结果:', res)
      const stations = res.data?.data?.stations || []
      processStationData(stations)
    })
    .catch(err => {
      console.error('getAllHydrologyStationInfo 请求失败:', err)
    })
}

// 站点信息轮询定时器（3分钟）
let stationPollTimer: ReturnType<typeof setInterval> | null = null

// 获取单个站点水文信息
const fetchStationInfo = async (stnm: string) => {
  try {
    const res = await axios.post(HYDROLOGY_STATION_URL, { stnm })
    return res.data?.data || null
  } catch (error) {
    console.error('获取站点水文信息失败:', error)
    return null
  }
}

// ===================== 洪峰倒计时逻辑 =====================
// 洪峰流速（米/秒），从组件配置读取，默认6.2
const floodPeakVelocity = computed(() => Number(props.chartConfig?.floodPeakVelocity) || 6.2)

// 洪峰倒计时文本，如 "1h12m23s"
const floodCountdownText = ref<string>('')

// 洪峰到达时间戳（毫秒）
let floodArrivalTime: number | null = null
// 倒计时定时器
let floodCountdownTimer: ReturnType<typeof setInterval> | null = null

// 格式化倒计时文本
const formatCountdown = (ms: number): string => {
  if (ms <= 0) return '0s'
  const totalSeconds = Math.floor(ms / 1000)
  const h = Math.floor(totalSeconds / 3600)
  const m = Math.floor((totalSeconds % 3600) / 60)
  const s = totalSeconds % 60
  let text = ''
  if (h > 0) text += `${h}h`
  if (m > 0 || h > 0) text += `${m}m`
  text += `${s}s`
  return text
}

// 更新倒计时显示
const updateCountdown = () => {
  if (floodArrivalTime === null) {
    floodCountdownText.value = ''
    return
  }
  const remaining = floodArrivalTime - Date.now()
  if (remaining <= 0) {
    floodCountdownText.value = ''
    if (floodCountdownTimer) {
      clearInterval(floodCountdownTimer)
      floodCountdownTimer = null
    }
    return
  }
  floodCountdownText.value = formatCountdown(remaining)
}

// 启动倒计时
const startCountdown = (arrivalMs: number) => {
  floodArrivalTime = arrivalMs
  updateCountdown()
  if (floodCountdownTimer) clearInterval(floodCountdownTimer)
  floodCountdownTimer = setInterval(updateCountdown, 1000)
}

// 取消倒计时
const cancelCountdown = () => {
  floodArrivalTime = null
  floodCountdownText.value = ''
  if (floodCountdownTimer) {
    clearInterval(floodCountdownTimer)
    floodCountdownTimer = null
  }
}

// 检查洪峰状态并计算倒计时
// redStations: 红色站点列表，每项包含 { name, dist }
// dist 单位: 公里，floodPeakVelocity 单位: 米/秒
const checkFloodPeak = (redStations: Array<{ name: string; dist?: number }>) => {
  // 红色站点超过2个才计算
  if (redStations.length < 2) {
    cancelCountdown()
    return
  }

  // 找出距离朝天门最近的红色站点（排除朝天门自身）
  const upstreamRedStations = redStations.filter(s => s.name !== '朝天门' && s.dist != null && s.dist > 0)
  if (upstreamRedStations.length === 0) {
    cancelCountdown()
    return
  }

  // 按距离升序排序，取最近的
  upstreamRedStations.sort((a, b) => (a.dist! - b.dist!))
  const nearest = upstreamRedStations[0]

  // 距离: 公里 → 米
  const distanceMeters = nearest.dist! * 1000
  // 流速: 米/秒
  const velocity = floodPeakVelocity.value
  // 到达时间: 秒
  const arrivalSeconds = distanceMeters / velocity
  // 到达时间戳
  const arrivalMs = Date.now() + arrivalSeconds * 1000

  console.log(`洪峰计算: 最近红色站点=${nearest.name}, 距离=${nearest.dist}km=${distanceMeters}m, 流速=${velocity}m/s, 到达时间=${arrivalSeconds}s`)

  startCountdown(arrivalMs)
}

// 点击站点
const handleStationClick = async (riverName: string, station: RiverStation) => {
  const key = `${riverName}-${station.name}`

  // 再次点击同一站点则关闭
  if (activeStationKey.value === key) {
    activeStationKey.value = ''
    closeHlqVideoDialog()
    return
  }

  // 关闭之前的化龙桥视频弹窗（如果打开着）
  if (showHlqVideoDialog.value) {
    closeHlqVideoDialog()
  }

  activeStationKey.value = key

  // 每次点击都重新请求接口
  stationLoadingMap[key] = true
  const rawData = await fetchStationInfo(station.name)
  stationLoadingMap[key] = false

  if (rawData) {
    const stationDetail = rawData.stationDetail || {}
    const historyList = rawData.historyList || []
    const latest = historyList.length > 0 ? historyList[historyList.length - 1] : {}

    const realTimeLevel = latest.z ?? null
    const warningLevel = stationDetail.ivhz ?? null
    const guaranteeLevel = stationDetail.grz ?? null

    let warningInfo = ''
    if (realTimeLevel != null && guaranteeLevel != null && realTimeLevel >= guaranteeLevel) {
      warningInfo = '超保证水位预警'
    } else if (realTimeLevel != null && warningLevel != null && realTimeLevel >= warningLevel) {
      warningInfo = '超警戒水位预警'
    }

    stationDetailMap[key] = {
      warningInfo,
      realTimeLevel: realTimeLevel != null ? realTimeLevel.toFixed(2) : '--',
      warningLevel: warningLevel != null ? warningLevel.toFixed(2) : '--',
      guaranteeLevel: guaranteeLevel != null ? guaranteeLevel.toFixed(2) : '--',
      historyList
    }
  }

  // 渲染历史水位图表
  await nextTick()
  renderStationChart(riverName, station)
}

// 点击空白区域关闭tooltip
const handleWrapperClick = () => {
  activeStationKey.value = ''
}

const openHlqVideoDetail = () => {
  eventBus.emit('YZVideoPop:openPop', ['50010300991328050455'])
}

// 渲染站点历史水位图表
const renderStationChart = (riverName: string, station: RiverStation) => {
  const key = `${riverName}-${station.name}`
  const el = chartRefs[key]
  if (!el || !echartsLib) return

  const detail = stationDetailMap[key]
  if (!detail || !detail.historyList || detail.historyList.length === 0) return

  // 旧实例绑定在已销毁的 DOM 上，需释放后重新初始化
  let instance = chartInstances.get(key)
  if (instance) {
    instance.dispose()
  }
  instance = echartsLib.init(el)
  chartInstances.set(key, instance)

  const data = detail.historyList.map((item: any) => ({
    time: item.tm ? new Date(item.tm).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) : '',
    date: item.tm ? new Date(item.tm).toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }) : '',
    value: item.z || 0
  }))

  // 计算 Y 轴范围：比最小值低10，比最大值高10
  const values = data.map((d: any) => d.value)
  const dataMin = Math.min(...values)
  const dataMax = Math.max(...values)
  const yMin = dataMin - 10
  const yMax = dataMax + 10

  // 计算每个拐点的趋势（上升/下降）并构建 markPoint 数据
  const markPointData: any[] = []
  // 线条箭头 SVG path：箭头头部 + 箭杆（ECharts 使用 path:// 语法）
  const arrowUp = 'path://M-4,-2 L0,-7 L4,-2 L1.5,-2 L1.5,5 L-1.5,5 L-1.5,-2 Z'
  const arrowDown = 'path://M-4,2 L0,7 L4,2 L1.5,2 L1.5,-5 L-1.5,-5 L-1.5,2 Z'
  for (let i = 1; i < data.length; i++) {
    const prev = data[i - 1].value
    const curr = data[i].value
    if (curr > prev) {
      // 上升 - 红色向上箭头
      markPointData.push({
        coord: [data[i].time, curr],
        symbol: arrowUp,
        symbolSize: 12,
        symbolOffset: [0, -14],
        itemStyle: { color: '#F84444' }
      })
    } else if (curr < prev) {
      // 下降 - 绿色向下箭头
      markPointData.push({
        coord: [data[i].time, curr],
        symbol: arrowDown,
        symbolSize: 12,
        symbolOffset: [0, 14],
        itemStyle: { color: '#00FF88' }
      })
    }
  }

  const option = {
    grid: { left: 36, right: 8, top: 8, bottom: 18 },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const p = Array.isArray(params) ? params[0] : params
        const d = data[p.dataIndex]
        let arrow = ''
        if (p.dataIndex > 0) {
          const prev = data[p.dataIndex - 1].value
          const diff = Math.abs(p.value - prev).toFixed(2)
          if (p.value > prev) arrow = ` <span style="color:#F84444">↑${diff}m</span>`
          else if (p.value < prev) arrow = ` <span style="color:#00FF88">↓${diff}m</span>`
        }
        return `${d.date}<br/>水位：${p.value}m${arrow}`
      }
    },
    xAxis: {
      type: 'category',
      data: data.map((d: any) => d.time),
      axisLine: { lineStyle: { color: '#2B7BBD' } },
      axisLabel: { color: '#8EC9FF', fontSize: 9, interval: 'auto' },
      splitLine: { show: false }
    },
    yAxis: {
      type: 'value',
      name: 'm',
      min: yMin,
      max: yMax,
      nameTextStyle: { color: '#8EC9FF', fontSize: 9 },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#8EC9FF', fontSize: 9 },
      splitLine: { lineStyle: { color: 'rgba(43, 123, 189, 0.3)', type: 'dashed' } }
    },
    series: [{
      data: data.map((d: any) => d.value),
      type: 'line',
      smooth: true,
      symbolSize: 4,
      lineStyle: { color: '#45C8FF', width: 2 },
      itemStyle: { color: '#45C8FF' },
      areaStyle: {
        color: new echartsLib.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(69, 200, 255, 0.3)' },
          { offset: 1, color: 'rgba(69, 200, 255, 0)' }
        ])
      },
      markPoint: {
        data: markPointData,
        label: { show: false }
      }
    }]
  }

  instance.setOption(option, true)
}

// const waterlogStats = ref<Array<WaterlogStat>>([
//   {label: '预警点位', value: 5, icon: waterlogWarnIcon},
//   {label: '处置中', value: 0, icon: waterlogProcessingIcon},
//   {label: '巡查中', value: 4, icon: waterlogPatrolIcon},
//   {label: '完成处置', value: 0, icon: waterlogDoneIcon}
// ])

// const linkageEvents = ref<Array<LinkageEvent>>([
//   {time: '2026/7/22 10:00:01', event: '这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容这是事件内容', status: '处置中', type: 'processing', isRecent: true},
//   {time: '2026/7/20 10:00:01', event: '这是事件内容这是事件内容这是事件内容', status: '已办结', type: 'finished', isRecent: false},
//   {time: '2026/7/19 10:00:01', event: '这是事件内容这是事件内容这是事件内容', status: '未处置', type: 'pending'}
// ])


const isClick = ref(false)
const showClick = () => {
  isClick.value = true
}
const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  },
  // 全局参数
  globalParams: {
    type: Object as () => GlobalParams,
    required: false,
    default: () => ({
      params: reactive({
        yzt_tab: '',
        yzt_block: {class1: '', class2: '', class3: ''},
        yzt_map_ready: true,
        yzt_activeDispatch: {}
      })
    })
  },
  bus: {
    type: Object as () => EventBus,
    required: false
  }
})

const warningWrapStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '540px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '270px'
  }
})

const isTripleScreen = computed(() => {
  return props.chartConfig?.option?.tripleScreen !== false
})

const riverLevelStyle = computed(() => {
  return {
    height: isTripleScreen.value ? '200px' : '240px'
  }
})

// 非三联屏时朝天门/郭家沱的 y 坐标下移 25px
watch(isTripleScreen, (val) => {
  riverLevels.value.forEach(river => {
    river.stations.forEach(station => {
      if (station.name === '朝天门' || station.name === '郭家沱') {
        station.y = val ? 60 : 80
      }
    })
  })
}, { immediate: true })

const FLOOD_DISPATCH_TOKEN_URL = 'http://23.99.16.179:11001/api/boot/system/flood/token'

function getDispatchIframeUrl(data) {
  return 'http://23.213.62.37:28081/freeLogin'
  // const result = data?.data ?? data
  // if (typeof result === 'string') return result
  // if (!result || typeof result !== 'object') return ''
  // return result.url || result.iframeUrl || result.src || result.link || result.token || ''
}

// 事件总线定义，方便组件间通信，本地暂时不能触发，只能上传测试环境测试,注意emit中的source是组件名称，方便区分事件来源
const eventBus = {
  on: (event: string, callback: (data: any) => void) => {
    props.bus?.on(event, ({source, data}) => {
      console.log('🚀 ~ eventBus on:', event, source, data)
      if (source === sourceName) return
      callback(data)
    })
  },
  off: (event: string, callback: (eventData?: any) => void) => {
    props.bus?.off(event, callback)
  },
  emit: (event: string, data?: any) => {
    console.log('🚀 ~ eventBus emit:', event, data)
    props.bus?.emit(event, {data, source: sourceName})
  }
}

async function openDispatchIframe() {
  try {
    const res = await axios.get(FLOOD_DISPATCH_TOKEN_URL)
    const url = getDispatchIframeUrl(res?.data)
    if (!url) return
    eventBus.emit('CLOSE_DIA', null)
    eventBus.emit('OPEN_IFRAME_DIA', { url, type: 'flood' })
  } catch (error) {
    console.error('获取调度页面地址失败:', error)
  }
}

// const handleWarningClick = (item: WarningItem) => {
//   if (item.value === 'tianqi') {
//     showWeatherPanel.value = activeWarning.value !== item.value || !showWeatherPanel.value
//     showDisasterPanel.value = false
//     showFirePanel.value = false
//     showFloodPanel.value = false
//     showLinkagePanel.value = false
//   } else if (item.value === 'dizai') {
//     showDisasterPanel.value = activeWarning.value !== item.value || !showDisasterPanel.value
//     showWeatherPanel.value = false
//     showFirePanel.value = false
//     showFloodPanel.value = false
//     showLinkagePanel.value = false
//   } else if (item.value === 'huozai') {
//     showFirePanel.value = activeWarning.value !== item.value || !showFirePanel.value
//     showWeatherPanel.value = false
//     showDisasterPanel.value = false
//     showFloodPanel.value = false
//     showLinkagePanel.value = false
//   } else if (item.value === 'neilao') {
//     showFloodPanel.value = activeWarning.value !== item.value || !showFloodPanel.value
//     showWeatherPanel.value = false
//     showDisasterPanel.value = false
//     showFirePanel.value = false
//     showLinkagePanel.value = false
//   } else if (item.value === 'sanqingliandong') {
//     showLinkagePanel.value = activeWarning.value !== item.value || !showLinkagePanel.value
//     showWeatherPanel.value = false
//     showDisasterPanel.value = false
//     showFirePanel.value = false
//     showFloodPanel.value = false
//   } else {
//     showWeatherPanel.value = false
//     showDisasterPanel.value = false
//     showFirePanel.value = false
//     showFloodPanel.value = false
//     showLinkagePanel.value = false
//   }
//   activeWarning.value = item.value
//   eventBus.emit('YZWarning:warning_click', item)
// }

// const handleLinkageView = (item: LinkageEvent) => {
//   eventBus.emit('YZWarning:linkage_view', item)
// }

const eventListeners = {
  // 地图加载完成
  'CMap:yzt_map_ready': () => {
    mapReady.value = true
    nextTick(() => {

      console.log('111111111111111')
      // getLineData()
      // dataMappingRef.value?.setEnabledContextMenu(contextMenuKeys.value)
      // 获取全局配置的固定围栏
      // geoFences.value = props.globalParams.params.areaConfig.map(item => JSON.stringify(item.data))
      // isDomShow.value = true
      // buildInvokeInit()

    })
  }
}
window.eventListeners = eventListeners

const mapLoaded = () => {
  console.log('测试环境可以进入')
  mapReady.value = true
  viewer.scene.debugShowFramesPerSecond = isDevelopment
}

onMounted(() => {
  console.log('进入步骤2---------------------')

  // manager = new PointManager(window.viewer)
  // awmanager.load()

  // 调用水文站点接口，根据实时水位更新站点颜色，并检查洪峰状态
  fetchAllStationInfo()
  // 站点信息接口三分钟调用一次
  stationPollTimer = setInterval(fetchAllStationInfo, 3 * 60 * 1000)

  // 初始化IRS视频流（化龙桥监控），获取视频流地址并启动后台抽帧
  initIrsStream()

  // 获取草街水库今日放水量
  fetchDischargeData()
})

onUnmounted(() => {
  chartInstances.forEach(instance => instance.dispose())
  chartInstances.clear()
  if (dischargeChartInstance) {
    dischargeChartInstance.dispose()
    dischargeChartInstance = null
  }
  if (stationPollTimer) {
    clearInterval(stationPollTimer)
    stationPollTimer = null
  }
  cancelCountdown()
  // 清理IRS视频流相关资源
  if (captureTimer) {
    clearInterval(captureTimer)
    captureTimer = null
  }
  if (hlsPlayer) {
    hlsPlayer.destroy()
    hlsPlayer = null
  }
  // 清理弹窗中的HLS实例
  const dialogVideo = hlqDialogVideoRef.value
  if (dialogVideo) {
    const hls = (dialogVideo as any)._hls
    if (hls) {
      hls.destroy()
      ;(dialogVideo as any)._hls = null
    }
  }
})

// 房屋数量接口
// const House_Num_URL = 'http://23.99.16.86:11001/api/v1/system/management/allHouseManagement'

//建筑详情
// const Build_URL = 'http://23.99.16.86:11001/api/v1/system/map/mapList/addressList'

///////////////////////////////////////////////////////////////////
/////////////////////////图标显示代码////////////////////////////////
///////////////////////////////////////////////////////////////////


</script>

<script lang="ts">
export default {
  name: 'BzWarning',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.yz-warning-wrap {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  overflow: visible;
}

.top-dispatch-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
  height: 24px;
  flex-shrink: 0;
  box-sizing: border-box;
  padding-right: 8px;
}

.top-dispatch-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0 8px;
  color: #fff;
  background: rgba(26, 120, 194, 0.8);
  border: 1px solid #3bb3ff;
  border-radius: 3px;
  font-size: 12px;
  line-height: 18px;
  cursor: pointer;
  user-select: none;
}

.yz-warning {
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  width: 650px;
  height: 45px;
  padding: 2px 26px 0 50px;
  background: url('./img/bg.png') center / 100% 100% no-repeat;
  user-select: none;

  &__item {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 36px;
    gap: 7px;
    cursor: pointer;
    white-space: nowrap;
    font-family: Microsoft YaHei, PingFang SC, sans-serif;
    font-size: 16px;
    font-weight: 700;
    color: #ffffff;
    line-height: 1;
    text-shadow: 0 1px 4px rgba(2, 35, 73, 0.65);
    transition: transform 0.18s ease, filter 0.18s ease;

    &:hover {
      filter: brightness(1.12);
      transform: translateY(-1px);
    }

    &--active {
      color: #fff675;
    }
  }

  &__icon {
    display: block;
    width: 25px;
    height: 22px;
    object-fit: contain;
    flex: 0 0 auto;
  }

  &__item:first-child &__icon {
    width: 29px;
    height: 27px;
  }

  &__badge {
    position: absolute;
    top: -14px;
    right: -18px;
    min-width: 26px;
    height: 18px;
    box-sizing: border-box;
    padding: 0 6px;
    border-radius: 999px;
    border: 1px solid rgba(255, 255, 255, 0.86);
    background: linear-gradient(180deg, #ef3d43 0%, #c9152d 100%);
    box-shadow: 0 2px 6px rgba(5, 18, 46, 0.45), inset 0 1px 2px rgba(255, 255, 255, 0.35);
    color: #ffffff;
    font-family: Arial, Microsoft YaHei, sans-serif;
    font-size: 13px;
    font-weight: 700;
    line-height: 16px;
    text-align: center;
    text-shadow: none;
    pointer-events: none;
  }
}

.weather-risk-panel {
  position: absolute;
  top: 56px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  width: 620px;
  color: #d8efff;
  font-family: Microsoft YaHei, PingFang SC, sans-serif;
  text-shadow: 0 1px 3px rgba(2, 20, 50, 0.75);

  &__top {
    width: 620px;
    height: 63px;
    background: url('./img/pop_box_top.png') center / 100% 100% no-repeat;
  }

  &__body {
    width: 620px;
    min-height: 610px;
    box-sizing: border-box;
    padding: 0 21px 8px;
    margin-top: -35px;
    background: url('./img/pop_box_body.png') center top / 100% 100% no-repeat;
  }

  &__bottom {
    width: 620px;
    height: 12px;
    background: url('./img/pop_box_bottom.png') center / 100% 100% no-repeat;
  }

  &__title {
    height: 31px;
    margin-bottom: 21px;
    text-align: center;
    font-size: 22px;
    font-weight: 700;
    line-height: 31px;
    color: #ffffff;
  }
}

.disaster-risk-panel {
  position: absolute;
  top: 56px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  width: 620px;
  color: #d8efff;
  font-family: Microsoft YaHei, PingFang SC, sans-serif;
  text-shadow: 0 1px 3px rgba(2, 20, 50, 0.75);

  &__top {
    width: 620px;
    height: 63px;
    background: url('./img/pop_box_top.png') center / 100% 100% no-repeat;
  }

  &__body {
    width: 620px;
    min-height: 374px;
    box-sizing: border-box;
    padding: 0 21px 8px;
    margin-top: -35px;
    background: url('./img/pop_box_body.png') center top / 100% 100% no-repeat;
  }

  &__bottom {
    width: 620px;
    height: 12px;
    background: url('./img/pop_box_bottom.png') center / 100% 100% no-repeat;
  }

  &__title {
    height: 31px;
    margin-bottom: 21px;
    text-align: center;
    font-size: 22px;
    font-weight: 700;
    line-height: 31px;
    color: #ffffff;
  }
}

.disaster-section {
  &__heading {
    display: flex;
    align-items: center;
    height: 22px;
    gap: 6px;
    font-size: 17px;
    font-weight: 700;
    color: #d8efff;

    &--dispose {
      margin-top: 19px;
    }
  }
}

.disaster-risk-table {
  margin-top: 11px;
  border-top: 1px solid rgba(20, 79, 146, 0.7);
  border-left: 1px solid rgba(20, 79, 146, 0.56);

  &__row {
    display: grid;
    grid-template-columns: 1fr 150px;
    min-height: 39px;
    background: rgba(0, 74, 157, 0.22);
    border-bottom: 1px solid rgba(20, 79, 146, 0.56);

    > div {
      display: flex;
      align-items: center;
      justify-content: center;
      box-sizing: border-box;
      border-right: 1px solid rgba(20, 79, 146, 0.56);
      font-size: 14px;
      font-weight: 700;
    }

    &--head {
      min-height: 29px;
      background: rgba(0, 109, 188, 0.38);

      > div {
        color: rgba(221, 241, 255, 0.86);
      }
    }
  }

  &__towns {
    justify-content: flex-start !important;
    padding: 0 24px;
    color: rgba(218, 238, 255, 0.76);
    letter-spacing: 0;
  }

  &__level {
    &--higher {
      color: #d7ff23;
    }

    &--some {
      color: #40bbff;
    }

    &--high {
      color: #ffb11d;
    }

    &--very-high {
      color: #ff1d2b;
    }
  }
}

.disaster-dispose {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-top: 12px;

  &__card {
    box-sizing: border-box;
    height: 80px;
    padding: 8px 8px;
    border: 1px solid rgba(40, 129, 201, 0.55);
    background: rgba(0, 73, 154, 0.2);
  }

  &__title {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 29px;
    gap: 7px;
    font-size: 18px;
    font-weight: 700;
    color: #ffffff;

    img {
      width: 20px;
      height: 20px;
      object-fit: contain;
    }

    strong {
      color: #36c7ff;
      font-size: 20px;
    }
  }

  &__status {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 11px;
    margin-top: 6px;
  }

  &__status-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 32px;
    box-sizing: border-box;
    padding: 0 13px;
    background: url('./img/weiyandizai_second.png') center / 100% 100% no-repeat;
    font-size: 15px;
    font-weight: 700;
    color: #ffffff;

    strong {
      color: #41e8ff;
      font-size: 20px;
    }
  }

  &__count--pending {
    color: #ffe02a !important;
  }
}

.fire-risk-panel {
  position: absolute;
  top: 56px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  width: 620px;
  color: #d8efff;
  font-family: Microsoft YaHei, PingFang SC, sans-serif;
  text-shadow: 0 1px 3px rgba(2, 20, 50, 0.75);

  &__top {
    width: 620px;
    height: 63px;
    background: url('./img/pop_box_top.png') center / 100% 100% no-repeat;
  }

  &__body {
    width: 620px;
    min-height: 314px;
    box-sizing: border-box;
    padding: 0 21px 8px;
    margin-top: -35px;
    background: url('./img/pop_box_body.png') center top / 100% 100% no-repeat;
  }

  &__bottom {
    width: 620px;
    height: 12px;
    background: url('./img/pop_box_bottom.png') center / 100% 100% no-repeat;
  }

  &__title {
    height: 31px;
    margin-bottom: 21px;
    text-align: center;
    font-size: 22px;
    font-weight: 700;
    line-height: 31px;
    color: #ffffff;
  }
}

.fire-section {
  &__heading {
    display: flex;
    align-items: center;
    height: 22px;
    gap: 6px;
    font-size: 17px;
    font-weight: 700;
    color: #d8efff;

    &--forest {
      margin-top: 19px;
    }
  }
}

.fire-stat {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 13px;

  &__item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 41px;
    box-sizing: border-box;
    padding: 0 12px;
    border: 1px solid rgba(40, 129, 201, 0.58);
    background: url('./img/weiyandizai_second.png') center / 100% 100% no-repeat;
    font-size: 17px;
    font-weight: 700;
    color: #ffffff;

    strong {
      color: #5fffd8;
      font-size: 21px;
    }
  }

  &__num--danger {
    color: #ff6b58 !important;
  }
}

.forest-fire {
  margin-top: 11px;

  &__meta {
    display: grid;
    grid-template-columns: 178px 1fr 107px;
    align-items: center;
    height: 34px;
    font-size: 13px;
    font-weight: 700;
    color: rgba(220, 240, 255, 0.7);

    > div {
      display: flex;
      align-items: center;
    }

    img {
      width: 26px;
      height: 22px;
      margin-right: 6px;
      object-fit: contain;
    }

    strong {
      color: #ff6b58;
      font-size: 16px;
    }
  }

  &__tag,
  &__badge {
    display: inline-flex;
    align-items: center;
    height: 21px;
    padding: 0 7px;
    color: #ffffff;
    border: 1px solid rgba(255, 176, 70, 0.74);
    background: rgba(178, 91, 25, 0.72);
  }

  &__content {
    box-sizing: border-box;
    min-height: 95px;
    padding: 12px 13px;
    border: 1px solid rgba(44, 127, 205, 0.65);
    background: rgba(0, 76, 160, 0.24);
    color: #ffffff;
    font-size: 14px;
    font-weight: 700;
    line-height: 25px;
    text-align: justify;
  }
}

.flood-risk-panel {
  position: relative;
  width: 540px;
  color: #d8efff;
  font-family: Microsoft YaHei, PingFang SC, sans-serif;
  text-shadow: 0 1px 3px rgba(2, 20, 50, 0.75);

  &__top {
    width: 540px;
    height: 55px;
    background: url('./img/pop_box_top.png') center / 100% 100% no-repeat;
  }

  &__body {
    width: 540px;
    box-sizing: border-box;
    padding: 0 10px 8px;
    overflow: visible;
  }

  &__bottom {
    width: 540px;
    height: 11px;
    background: url('./img/pop_box_bottom.png') center / 100% 100% no-repeat;
  }

  &__title {
    height: 27px;
    margin-bottom: 18px;
    text-align: center;
    font-size: 19px;
    font-weight: 700;
    line-height: 27px;
    color: #ffffff;
  }
}

.flood-section {
  &__heading {
    display: flex;
    align-items: center;
    height: 22px;
    gap: 6px;
    font-size: 17px;
    font-weight: 700;
    color: #d8efff;

    &--waterlog {
      margin-top: 14px;
    }
  }
}

// 调度按钮
.flood-dispatch-btn {
  position: absolute;
  top: -35px;
  right: 35px;
  z-index: 101;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 8px;
  height: 22px;
  color: #fff;
  background: rgba(26, 120, 194, 0.8);
  border: 1px solid #3bb3ff;
  border-radius: 3px;
  font-size: 12px;
  line-height: 18px;
  cursor: pointer;
  user-select: none;
}

// 草街水库今日放水量指标
.water-discharge-indicator {
  position: absolute;
  top: 35%;
  left: 0;
  transform: translateY(-50%);
  z-index: 100;
  padding: 3px 8px;
  background: linear-gradient(135deg, rgba(4,35,82,0.92), rgba(10,60,120,0.88));
  border: 1px solid rgba(95,234,255,0.4);
  border-radius: 3px;
  cursor: pointer;
  text-align: center;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 4px;

  &__title {
    font-size: 11px;
    color: #8BBCE6;
    line-height: 14px;
    white-space: nowrap;
  }

  &__value {
    display: flex;
    align-items: baseline;
    justify-content: center;
    gap: 1px;
  }

  &__num {
    font-size: 15px;
    font-weight: 700;
    color: #5FEAFF;
    line-height: 18px;
  }

  &__unit {
    font-size: 10px;
    color: #8BBCE6;
  }
}

// 草街指标连接线：从指标顶部发射连接到东津沱和北碚之间的河道线条
.water-discharge-connector {
  position: absolute;
  left: 54%;
  top: 0;
  width: 2px;
  height: 28px;
  background: linear-gradient(180deg, rgba(95,234,255,0.4), rgba(95,234,255,0.7));
  transform: translate(-50%, -100%);
  pointer-events: none;
  z-index: 99;

  &::after {
    content: '';
    position: absolute;
    top: -2px;
    left: 50%;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: rgba(95,234,255,0.8);
    border: 1px solid rgba(255,255,255,0.5);
    transform: translate(-50%, -100%);
    box-shadow: 0 0 6px rgba(95,234,255,0.6);
  }
}

// 非三联屏：连接线高度从28px增加到41px
.water-discharge-connector--tall {
  height: 41px;
}

.water-discharge-popup {
  position: absolute;
  top: 50%;
  left: 100%;
  margin-left: 4px;
  transform: translateY(-50%);
  width: 320px;
  height: 130px;
  background: rgba(4,35,82,0.95);
  border: 1px solid rgba(95,234,255,0.4);
  border-radius: 4px;
  z-index: 50;

  &__title {
    font-size: 13px;
    color: #dff6ff;
    text-align: center;
    padding: 4px 0 2px;
  }

  &__chart {
    width: 100%;
    height: 100px;
  }
}

// 河道线条图例
.river-legend {
  position: absolute;
  bottom: 60px;
  right: 30px;
  z-index: 90;
  display: flex;
  flex-direction: column;
  gap: 4px;
  pointer-events: none;

  &__item {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  &__line {
    display: inline-block;
    width: 24px;
    border-top-style: solid;
  }

  &__line--jlj {
    border-top-width: 6px;
    border-top-color: #8BBCE6;
  }

  &__line--cjj {
    border-top-width: 12px;
    border-top-color: #098CFF;
  }

  &__label {
    font-size: 12px;
    color: #FFF;
    white-space: nowrap;
  }
}

.river-level {
  position: relative;
  height: 200px;
  margin-top: 8px;
  overflow: visible;

  &__row {
    position: absolute;
    left: 0;
    width: 100%;

    &:first-child {
      top: 0;
      height: 85px;
    }

    &:nth-child(2) {
      top: 120px;
      height: 80px;
    }
  }

  &__name {
    display: flex;
    align-items: center;
    justify-content: center;
    position: absolute;
    left: 5px;
    top: 0;
    box-sizing: border-box;
    width: 28px;
    height: 104px;
    padding: 12px 0;
    background: url('./img/shuihuan_second3.png') center / 100% 100% no-repeat;
    color: #ffffff;
    font-size: 15px;
    font-weight: 700;
    line-height: 19px;
    letter-spacing: 3px;
    text-align: center;
    text-shadow: 0 2px 5px rgba(3, 20, 50, 0.8);
    writing-mode: vertical-rl;
  }

  &__stations {
    position: relative;
    height: 100%;
  }

  &__dist-label {
    position: absolute;
    z-index: 0;
    transform: translate(0, -50%);
    color: #ffffff;
    font-size: 10px;
    font-weight: 600;
    white-space: nowrap;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
    pointer-events: none;
  }

  &__station {
    position: absolute;
    z-index: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 66px;

    img {
      display: block;
      width: 12px;
      height: 12px;
      margin-bottom: 4px;
      object-fit: contain;
    }

    > .river-level__station-name {
      width: 100%;
      overflow: hidden;
      color: #ffffff;
      font-size: 13px;
      font-weight: 700;
      line-height: 18px;
      text-align: center;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    &--jlj {
      > .river-level__station-name {
        position: absolute;
        left: 50%;
        width: 78px;
        transform: translateX(-50%);
        text-align: center;
        top: -20px;
      }

      > .river-level__station-level {
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        top: -56px;
      }
    }

    &:hover {
      z-index: 20;
    }

    &--active {
      z-index: 20;

      .river-level__tooltip {
        opacity: 1;
        visibility: visible;
        transform: translate(-50%, -8px);
      }

      .river-level__tooltip--down {
        transform: translate(-50%, 8px);
      }

      .river-level__tooltip--left {
        transform: translate(-8px, -50%);
      }
    }
  }

  &__row:nth-child(2) &__name {
    height: 93px;
  }

  &__point {
    position: relative;
    height: 12px;
    margin-bottom: 4px;

    &::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      width: 18px;
      height: 18px;
      border-radius: 50%;
      transform: translate(-50%, -50%);
      animation: station-breathing 2s ease-in-out infinite;
    }

    &--green::before {
      background: radial-gradient(circle, rgba(0, 255, 100, 0.85) 0%, rgba(0, 255, 100, 0) 70%);
    }

    &--yellow::before {
      background: radial-gradient(circle, rgba(255, 220, 0, 0.85) 0%, rgba(255, 220, 0, 0) 70%);
    }

    &--red::before {
      background: radial-gradient(circle, rgba(255, 30, 30, 0.85) 0%, rgba(255, 30, 30, 0) 70%);
    }
  }

  &__line {
    position: absolute;
    z-index: 0;
    border-top: 1px solid rgba(106, 169, 234, 0.78);
    transform-origin: left center;

    &::after {
      content: '';
      position: absolute;
      left: 0;
      top: -1px;
      width: 100%;
      height: 2px;
      background: linear-gradient(90deg, rgba(95, 211, 255, 0) 0 45%, rgba(95, 211, 255, 0.95) 50%, rgba(95, 211, 255, 0) 55% 100%);
      background-size: 40px 2px;
      animation: river-flow 1.35s linear infinite;
      filter: drop-shadow(0 0 4px rgba(95, 211, 255, 0.75));
    }
  }

  &__line--top-main {
    top: 18px;
    left: 0;
    width: 363px;
    border-top-width: 6px;
    border-top-color: #8BBCE6;

    &::after {
      height: 6px;
      top: -5px;
    }
  }

  &__line--top-branch {
    top: 18px;
    left: 363px;
    width: 70px;
    transform: rotate(41deg);
    border-top-width: 6px;
    border-top-color: #8BBCE6;

    &::after {
      height: 6px;
      top: -5px;
    }
  }

  &__line--top-right {
    top: 60px;
    left: 415px;
    width: 52px;
    border-top-width: 12px;
    border-top-color: #098CFF;

    &::after {
      height: 12px;
      top: -11px;
    }
  }

  &__line--bottom-main {
    top: -15px;
    left: 0;
    width: 368px;
    border-top-width: 12px;
    border-top-color: #098CFF;

    &::after {
      height: 12px;
      top: -11px;
    }
  }

  &__line--bottom-branch {
    top: -15px;
    left: 368px;
    width: 64px;
    transform: rotate(-44deg);
    border-top-width: 12px;
    border-top-color: #098CFF;

    &::after {
      height: 12px;
      top: -11px;
    }
  }

  &__station-name--yellow {
    color: #ffe713 !important;
  }

  &__station-level {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 50px;
    height: 32px;
    box-sizing: border-box;
    background: url('./img/swbg.png') center / 100% 100% no-repeat;
    font-size: 9px;
    font-weight: 600;
    line-height: 1.2;
    white-space: nowrap;
    margin-top: 1px;
  }

  &__station-level--red {
    color: #F84444;
  }

  &__station-level--yellow {
    color: #ffe713;
  }

  &__station-level--green {
    color: #5FEAFF;
  }

  &__flood {
    margin-top: 5px;
    padding: 4px 6px;
    background-image: url('./img/bg_list_data.png');
    background-size: 100% 100%;
    background-repeat: no-repeat;
    text-align: center;
    line-height: 1.5;
  }

  &__flood-title {
    font-size: 10px;
    font-weight: 700;
    color: #5FBCFF;
  }

  &__flood-in {
    font-size: 10px;
    font-weight: 600;
    color: #F84444;
  }

  &__flood-out {
    font-size: 10px;
    font-weight: 600;
    color: #12DD60;
  }

  &__tooltip {
    position: absolute;
    z-index: 200;
    left: 50%;
    bottom: calc(100% + 8px);
    width: 320px;
    box-sizing: border-box;
    padding: 8px 10px;
    border: 1px solid rgba(75, 181, 255, 0.72);
    background: rgba(4, 35, 82, 0.94);
    box-shadow: 0 0 12px rgba(19, 145, 230, 0.42), inset 0 0 16px rgba(37, 150, 233, 0.18);
    color: #dff6ff;
    font-size: 11px;
    line-height: 17px;
    text-align: left;
    text-shadow: none;
    opacity: 0;
    visibility: hidden;
    transform: translate(-50%, 0);
    transition: opacity 0.16s ease, transform 0.16s ease, visibility 0.16s ease;
    pointer-events: auto;

    &::after {
      content: '';
      position: absolute;
      left: 50%;
      bottom: -6px;
      width: 10px;
      height: 10px;
      border-right: 1px solid rgba(75, 181, 255, 0.72);
      border-bottom: 1px solid rgba(75, 181, 255, 0.72);
      background: rgba(4, 35, 82, 0.94);
      transform: translateX(-50%) rotate(45deg);
    }
  }

  &__tooltip--center {
    position: absolute;
    left: 50%;
    top: 30%;
    bottom: auto;
    z-index: 200;
    opacity: 1;
    visibility: visible;
    transform: translate(-50%, -50%);

    &::after {
      display: none;
    }
  }

  &__tooltip-title {
    font-size: 13px;
    font-weight: 700;
    color: #ffffff;
    text-align: center;
    margin-bottom: 6px;
    padding-bottom: 4px;
    border-bottom: 1px solid rgba(75, 181, 255, 0.3);
  }

  &__tooltip-close {
    position: absolute;
    top: 4px;
    right: 4px;
    width: 18px;
    height: 18px;
    box-sizing: border-box;
    border: 1px solid rgba(75, 181, 255, 0.5);
    border-radius: 50%;
    background: rgba(4, 35, 82, 0.6);
    color: #b8dfff;
    font-size: 14px;
    font-weight: 400;
    line-height: 14px;
    text-align: center;
    cursor: pointer;
    pointer-events: auto;
    transition: color 0.15s ease, border-color 0.15s ease, background 0.15s ease;

    &:hover {
      color: #ffffff;
      border-color: rgba(135, 230, 255, 0.95);
      background: rgba(18, 139, 217, 0.75);
    }
  }

  &__tooltip-detail {
    position: absolute;
    top: 4px;
    right: 28px;
    height: 18px;
    box-sizing: border-box;
    padding: 0 8px;
    border: 1px solid rgba(75, 181, 255, 0.65);
    border-radius: 9px;
    background: rgba(4, 70, 132, 0.78);
    color: #dff8ff;
    font-size: 11px;
    font-weight: 700;
    line-height: 16px;
    cursor: pointer;
    pointer-events: auto;
    transition: color 0.15s ease, border-color 0.15s ease, background 0.15s ease;

    &:hover {
      color: #ffffff;
      border-color: rgba(135, 230, 255, 0.95);
      background: rgba(18, 139, 217, 0.82);
    }
  }

  &__tooltip-row {
    display: flex;
    align-items: center;
    gap: 2px;
    margin-bottom: 4px;

    label {
      flex: 0 0 auto;
      color: rgba(216, 239, 255, 0.72);
      font-size: 11px;
      font-weight: 400;
    }

    span {
      color: #ffffff;
      font-size: 12px;
      font-weight: 700;
    }

    &--warning {
      justify-content: center;

      span {
        font-size: 12px;
      }
    }

    &--levels {
      justify-content: space-between;
      gap: 4px;
      margin-bottom: 6px;

      > span {
        display: flex;
        align-items: center;
        gap: 2px;

        label {
          font-size: 10px;
        }

        span {
          font-size: 11px;
        }
      }
    }
  }

  &__tooltip-chart {
    width: 100%;
    height: 80px;
  }

  &__tooltip-loading {
    text-align: center;
    padding: 16px 0;
    color: rgba(216, 239, 255, 0.6);
    font-size: 12px;
  }

  &__tooltip--down {
    top: calc(100% + 8px);
    bottom: auto;

    &::after {
      top: -6px;
      bottom: auto;
      border-right: 0;
      border-bottom: 0;
      border-left: 1px solid rgba(75, 181, 255, 0.72);
      border-top: 1px solid rgba(75, 181, 255, 0.72);
      transform: translateX(-50%) rotate(45deg);
    }
  }

  &__tooltip--left {
    left: auto;
    right: calc(100% + 8px);
    top: 50%;
    bottom: auto;
    transform: translate(0, -50%);

    &::after {
      left: auto;
      right: -6px;
      top: 50%;
      bottom: auto;
      transform: translateY(-50%) rotate(-45deg);
    }
  }

  &__flood-bubble {
    position: absolute;
    z-index: 40;
    left: 50%;
    bottom: calc(100% + 6px);
    transform: translateX(-50%);
    padding: 6px 12px;
    background: rgba(4, 35, 82, 0.95);
    border: 1px solid rgba(255, 80, 80, 0.8);
    border-radius: 6px;
    box-shadow: 0 0 10px rgba(255, 50, 50, 0.5), inset 0 0 8px rgba(255, 60, 60, 0.15);
    white-space: nowrap;
    pointer-events: none;
    animation: flood-bubble-pulse 1.5s ease-in-out infinite;
  }

  &__flood-bubble-text {
    font-size: 12px;
    font-weight: 700;
    color: #ff6b6b;
    text-shadow: 0 0 6px rgba(255, 80, 80, 0.6);
  }

  &__flood-bubble-arrow {
    position: absolute;
    left: 50%;
    bottom: -5px;
    width: 8px;
    height: 8px;
    background: rgba(4, 35, 82, 0.95);
    border-right: 1px solid rgba(255, 80, 80, 0.8);
    border-bottom: 1px solid rgba(255, 80, 80, 0.8);
    transform: translateX(-50%) rotate(45deg);
  }
}

// 非三联屏模式：容器变高，站点行间距与连接线同步放大
.river-level--tall {
  .river-level__row:nth-child(2) {
    top: 160px;
  }

  .river-level__line--top-branch {
    width: 84px;
    transform: rotate(53deg);
  }

  .river-level__line--bottom-branch {
    width: 77px;
    transform: rotate(-55deg);
  }

  .river-level__line--top-right {
    top: 80px;
  }
}

@keyframes river-flow {
  from {
    background-position: 0 0;
  }

  to {
    background-position: 40px 0;
  }
}

@keyframes station-breathing {
  0%, 100% {
    opacity: 0.4;
    transform: translate(-50%, -50%) scale(0.6);
  }

  50% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1.2);
  }
}

@keyframes flood-bubble-pulse {
  0%, 100% {
    box-shadow: 0 0 6px rgba(255, 50, 50, 0.4), inset 0 0 6px rgba(255, 60, 60, 0.1);
    border-color: rgba(255, 80, 80, 0.6);
  }

  50% {
    box-shadow: 0 0 14px rgba(255, 50, 50, 0.8), inset 0 0 10px rgba(255, 60, 60, 0.25);
    border-color: rgba(255, 100, 100, 1);
  }
}

.waterlog-stat {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px 31px;
  margin-top: 18px;

  &__item {
    position: relative;
    display: flex;
    align-items: center;
    height: 80px;
  }

  img {
    position: relative;
    z-index: 2;
    width: 57px;
    height: 65px;
    object-fit: contain;
  }

  &__content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 206px;
    height: 65px;
    box-sizing: border-box;
    padding: 0 20px 0 44px;
    margin-left: -25px;
    background: url('./img/shuihuan_second1.png') center / 100% 100% no-repeat;

    span {
      font-size: 16px;
      font-weight: 700;
      color: #d8efff;
    }

    strong {
      color: #45dfff;
      font-size: 32px;
      font-weight: 700;
      line-height: 1;
    }

    em {
      margin-left: 2px;
      color: #d8efff;
      font-size: 13px;
      font-style: normal;
    }
  }
}

.linkage-risk-panel {
  position: absolute;
  top: 56px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  width: 620px;
  color: #d8efff;
  font-family: Microsoft YaHei, PingFang SC, sans-serif;
  text-shadow: 0 1px 3px rgba(2, 20, 50, 0.75);

  &__top {
    width: 620px;
    height: 63px;
    background: url('./img/pop_box_top.png') center / 100% 100% no-repeat;
  }

  &__body {
    width: 620px;
    min-height: 286px;
    box-sizing: border-box;
    padding: 0 21px 8px;
    margin-top: -35px;
    background: url('./img/pop_box_body.png') center top / 100% 100% no-repeat;
  }

  &__bottom {
    width: 620px;
    height: 12px;
    background: url('./img/pop_box_bottom.png') center / 100% 100% no-repeat;
  }

  &__title {
    height: 31px;
    margin-bottom: 21px;
    text-align: center;
    font-size: 22px;
    font-weight: 700;
    line-height: 31px;
    color: #ffffff;
  }
}

.linkage-table {
  margin-top: 6px;
  border-top: 1px solid rgba(20, 79, 146, 0.7);
  border-left: 1px solid rgba(20, 79, 146, 0.56);

  &__row {
    display: grid;
    grid-template-columns: 166px 1fr 128px;
    min-height: 38px;
    background: rgba(0, 74, 157, 0.22);
    border-bottom: 1px solid rgba(20, 79, 146, 0.56);

    > div {
      display: flex;
      align-items: center;
      justify-content: center;
      box-sizing: border-box;
      min-width: 0;
      padding: 0 10px;
      border-right: 1px solid rgba(20, 79, 146, 0.56);
      overflow: hidden;
      font-size: 14px;
      font-weight: 700;
      color: rgba(218, 238, 255, 0.76);
    }

    &--head {
      min-height: 29px;
      background: rgba(0, 109, 188, 0.38);

      > div {
        color: rgba(221, 241, 255, 0.9);
      }
    }
  }

  &__event {
    justify-content: flex-start !important;
    align-items: flex-start !important;
    padding: 9px 14px 9px 42px !important;
    line-height: 20px;
    text-align: left;
    white-space: normal;
    word-break: break-all;
  }

  &__time {
    position: relative;
    gap: 6px;
    white-space: nowrap;
  }

  &__dot {
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: #ff2035;
    box-shadow: 0 0 6px rgba(255, 32, 53, 0.9);
    flex: 0 0 auto;
  }

  &__action {
    justify-content: space-between !important;
    gap: 7px;
    padding: 0 8px !important;
  }

  &__status {
    flex: 1;
    text-align: center;

    &--processing {
      color: #d7ff23 !important;
    }

    &--finished {
      color: #58f18e !important;
    }

    &--pending {
      color: #ffb11d !important;
    }
  }

  &__view {
    flex: 0 0 auto;
    height: 24px;
    box-sizing: border-box;
    padding: 0 9px;
    border: 1px solid rgba(86, 202, 255, 0.76);
    border-radius: 2px;
    background: rgba(9, 107, 180, 0.58);
    color: #ffffff;
    font-size: 13px;
    font-weight: 700;
    line-height: 22px;
    cursor: pointer;

    &:hover {
      border-color: rgba(135, 230, 255, 0.95);
      background: rgba(18, 139, 217, 0.75);
    }
  }
}

.weather-block {
  &__heading,
  .today-warning__heading {
    display: flex;
    align-items: center;
    height: 22px;
    gap: 6px;
    font-size: 17px;
    font-weight: 700;
    color: #d8efff;
  }

  &__arrow {
    width: 0;
    height: 0;
    border-top: 5px solid transparent;
    border-bottom: 5px solid transparent;
    border-left: 7px solid #8beeff;
    filter: drop-shadow(0 0 4px rgba(17, 236, 230, 0.8));
  }
}

.weather-days {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  align-items: start;
  margin-top: 13px;
}

.weather-day {
  min-width: 0;
  text-align: center;

  &__date {
    height: 20px;
    font-size: 15px;
    font-weight: 700;
    line-height: 20px;
    color: rgba(220, 240, 255, 0.82);
  }

  &__icon {
    display: block;
    width: 30px;
    height: 30px;
    margin: 7px auto 3px;
    object-fit: contain;
  }

  &__text,
  &__temp {
    height: 19px;
    font-size: 14px;
    font-weight: 700;
    line-height: 19px;
    color: #d9eeff;
  }
}

.wind-table {
  margin: 17px 0 26px;
  border-top: 1px solid rgba(37, 116, 203, 0.45);
  border-left: 1px solid rgba(37, 116, 203, 0.35);

  &__row {
    display: grid;
    grid-template-columns: 68px repeat(5, 1fr);
    height: 36px;
    background: rgba(0, 74, 157, 0.24);
    border-bottom: 1px solid rgba(37, 116, 203, 0.45);

    &--level {
      height: 44px;
    }
  }

  &__label,
  &__cell {
    display: flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
    border-right: 1px solid rgba(37, 116, 203, 0.45);
    font-size: 14px;
    font-weight: 700;
    color: rgba(219, 239, 255, 0.74);
  }

  &__cell {
    flex-direction: column;
    line-height: 18px;

    &--count {
      color: #29ffd7;
      font-size: 16px;
      line-height: 36px;
    }
  }
}

.rain-table {
  margin: 17px 0 31px;
  border-top: 1px solid rgba(37, 116, 203, 0.45);
  border-left: 1px solid rgba(37, 116, 203, 0.35);

  &__row {
    display: grid;
    grid-template-columns: 68px repeat(6, 1fr);
    height: 36px;
    background: rgba(0, 74, 157, 0.24);
    border-bottom: 1px solid rgba(37, 116, 203, 0.45);

    &--level {
      height: 44px;
    }
  }

  &__label,
  &__cell {
    display: flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
    border-right: 1px solid rgba(37, 116, 203, 0.45);
    font-size: 14px;
    font-weight: 700;
    color: rgba(219, 239, 255, 0.74);
  }

  &__cell {
    flex-direction: column;
    line-height: 18px;

    &--count {
      color: #29cfff;
      font-size: 16px;
      line-height: 36px;
    }
  }
}

.today-warning {
  &__heading {
    display: flex;
    align-items: center;
    gap: 6px;
    height: 22px;
    font-size: 17px;
    font-weight: 700;
    color: #d8efff;
  }

  &__meta {
    display: grid;
    grid-template-columns: 222px 129px 1fr;
    align-items: center;
    height: 34px;
    margin-top: 11px;
    font-size: 13px;
    font-weight: 700;
    color: rgba(220, 240, 255, 0.7);
  }

  &__level {
    display: flex;
    align-items: center;

    img {
      width: 26px;
      height: 22px;
      margin: 0 6px 0 2px;
      object-fit: contain;
    }
  }

  &__badge {
    display: inline-flex;
    align-items: center;
    height: 21px;
    padding: 0 8px;
    color: #ffffff;
    background: rgba(174, 33, 43, 0.88);
    border: 1px solid rgba(255, 167, 169, 0.45);
  }

  &__content {
    box-sizing: border-box;
    min-height: 78px;
    padding: 12px 13px;
    border: 1px solid rgba(44, 127, 205, 0.65);
    background: rgba(0, 76, 160, 0.24);
    color: #ffffff;
    font-size: 14px;
    font-weight: 700;
    line-height: 25px;
    text-align: justify;
  }
}

// ===================== 化龙桥监控视频弹窗样式 =====================
.hlq-video-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 9999;
  width: 480px;
  box-sizing: border-box;
  border: 1px solid rgba(75, 181, 255, 0.72);
  background: rgba(4, 35, 82, 0.94);
  box-shadow: 0 0 20px rgba(19, 145, 230, 0.5), inset 0 0 16px rgba(37, 150, 233, 0.18);
  border-radius: 6px;
  overflow: hidden;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 36px;
    padding: 0 12px;
    border-bottom: 1px solid rgba(75, 181, 255, 0.3);
    background: rgba(9, 60, 120, 0.6);
  }

  &__title {
    font-size: 14px;
    font-weight: 700;
    color: #ffffff;
    text-shadow: 0 1px 3px rgba(2, 20, 50, 0.75);
  }

  &__close {
    width: 20px;
    height: 20px;
    box-sizing: border-box;
    border: 1px solid rgba(75, 181, 255, 0.5);
    border-radius: 50%;
    background: rgba(4, 35, 82, 0.6);
    color: #b8dfff;
    font-size: 14px;
    font-weight: 400;
    line-height: 14px;
    text-align: center;
    cursor: pointer;
    transition: color 0.15s ease, border-color 0.15s ease, background 0.15s ease;

    &:hover {
      color: #ffffff;
      border-color: rgba(135, 230, 255, 0.95);
      background: rgba(18, 139, 217, 0.75);
    }
  }

  &__body {
    width: 100%;
    height: 270px;
    background: #000;
    overflow: hidden;
  }

  &__video {
    width: 100%;
    height: 100%;
    object-fit: contain;
    display: block;
  }

  &__enlarge {
    position: absolute;
    bottom: 8px;
    right: 8px;
    padding: 4px 10px;
    border: 1px solid rgba(86, 202, 255, 0.76);
    border-radius: 3px;
    background: rgba(9, 107, 180, 0.58);
    color: #ffffff;
    font-size: 12px;
    font-weight: 700;
    cursor: pointer;
    transition: border-color 0.15s ease, background 0.15s ease;

    &:hover {
      border-color: rgba(135, 230, 255, 0.95);
      background: rgba(18, 139, 217, 0.75);
    }
  }

  // 全屏放大状态
  &--fullscreen {
    width: 80vw;
    height: 80vh;

    .hlq-video-dialog__body {
      height: calc(80vh - 36px);
    }
  }
}
</style>
