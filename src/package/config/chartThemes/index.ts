import customed from './themes/customed.json'
import dark from './themes/dark.json'
import chalk from './themes/chalk.json'
import essos from './themes/essos.json'
import macarons from './themes/macarons.json'
import purplePassion from './themes/purple-passion.json'
import roma from './themes/roma.json'
import shine from './themes/shine.json'
import vintage from './themes/vintage.json'
import walden from './themes/walden.json'
import westeros from './themes/westeros.json'
import wonderland from './themes/wonderland.json'
import candy from './themes/candy.json'
import mystical from './themes/mystical.json'
import retro from './themes/retro.json'
import newColor from './themes/newColor.json'


export const chartColors = {
  dark,
  customed,
  macarons,
  walden,
  purplePassion,
  vintage,
  chalk,
  westeros,
  wonderland,
  essos,
  shine,
  roma,
  candy,
  mystical,
  retro,
  morandi: newColor.morandi,
  fog: newColor.fog,
  party: newColor.party,
  china: newColor.china,
  technology: newColor.technology,
  country: newColor.country
}

// 默认主题
export const defaultTheme = 'mystical'

// 主题色列表
export type ChartColorsNameType = keyof typeof chartColorsName
export const chartColorsName = {
  dark: '明亮',
  customed: '暗淡',
  macarons: '马卡龙',
  walden: '蓝绿',
  purplePassion: '深紫',
  vintage: '复古',
  chalk: '粉青',
  westeros: '灰粉',
  wonderland: '青草',
  essos: '橘红',
  shine: '深色',
  roma: '罗马红',
  candy: '糖果',
  mystical: '神秘',
  retro: '流行',
  morandi: '莫兰迪',
  fog: '雾霾色',
  party: '党建色',
  china: '中国风',
  technology: '科技色',
  country: '乡村色'
}

// 主题色列表
export const chartColorsshow = {
  dark: 'linear-gradient(to right, #4992ff 0%, #7cffb2 100%)',
  customed: 'linear-gradient(to right, #5470c6 0%, #91cc75 100%)',
  macarons: 'linear-gradient(to right, #2ec7c9 0%, #b6a2de 100%)',
  walden: 'linear-gradient(to right, #3fb1e3 0%, #6be6c1 100%)',
  purplePassion: 'linear-gradient(to right, #9b8bba 0%, #e098c7 100%)',
  vintage: 'linear-gradient(to right, #d87c7c 0%, #919e8b 100%)',
  chalk: 'linear-gradient(to right, #fc97af 0%, #87f7cf 100%)',
  westeros: 'linear-gradient(to right, #516b91 0%, #edafda 100%)',
  wonderland: 'linear-gradient(to right, #4ea397 0%, #22c3aa 100%)',
  essos: 'linear-gradient(to right, #893448 0%, #d95850 100%)',
  shine: 'linear-gradient(to right, #c12e34 0%, #0098d9 100%)',
  roma: 'linear-gradient(to right, #e01f54 0%, #5e4ea5 100%)',
  candy: 'linear-gradient(to right, #00F7FF 0%, #00A0FF 100%)',
  mystical: 'linear-gradient(to right, #DB4545 0%, #FFD400 100%)',
  retro: 'linear-gradient(to right, #4CECAA 0%, #00F7FF 100%)',
  morandi: 'linear-gradient(to right, #937372 0%, #e7e0d5 100%)',
  fog: 'linear-gradient(to right, #a3e3fd 0%, #91cff8 100%)',
  party: 'linear-gradient(to right, #de2910 0%, #ffde00 100%)',
  china: 'linear-gradient(to right, #B3B9AD 0%, #3E6F51 100%)',
  technology: 'linear-gradient(to right, #A682FF 0%, #705AFF 100%)',
  country: 'linear-gradient(to right, #EDF8E9 0%, #C8E9BF 100%)',
}
// 渐变主题色列表（主色1、主色2、阴影、渐变1、渐变2）
export const chartColorsSearch = {
  dark: ['#4992ff', '#7cffb2', 'rgba(68, 181, 226, 0.3)', 'rgba(73, 146, 255, 0.5)', 'rgba(124, 255, 178, 0.5)'],
  customed: ['#5470c6', '#91cc75', 'rgba(84, 112, 198, 0.5)', 'rgba(84, 112, 198, 0.5)', 'rgba(145, 204, 117, 0.5)'],
  macarons: ['#2ec7c9', '#b6a2de', 'rgba(182, 162, 222, 0.3)', 'rgba(46, 199, 201, 0.5)', 'rgba(182, 162, 222, 0.5)'],

  walden: ['#3fb1e3', '#6be6c1', 'rgba(68, 181, 226, 0.3)', 'rgba(63, 177, 227, 0.5)', 'rgba(107, 230, 193, 0.5)'],
  purplePassion: [
    '#9b8bba',
    '#e098c7',
    'rgba(182, 162, 222, 0.3)',
    'rgba(155, 139, 186, 0.5)',
    'rgba(237, 175, 218, 0.5)'
  ],
  vintage: ['#d87c7c', '#919e8b', 'rgba(182, 162, 222, 0.3)', 'rgba(216, 124, 124, 0.5)', 'rgba(145, 158, 139, 0.5)'],

  chalk: ['#fc97af', '#87f7cf', 'rgba(135, 247, 207, 0.3)', 'rgba(252, 151, 175, 0.5)', 'rgba(135, 247, 207, 0.5)'],
  westeros: ['#516b91', '#edafda', 'rgba(81, 107, 145, 0.3)', 'rgba(81, 107, 145, 0.5)', 'rgba(89, 196, 230, 0.5)'],
  wonderland: ['#4ea397', '#22c3aa', 'rgba(68, 181, 226, 0.3)', 'rgba(78, 163, 151, 0.5)', 'rgba(34, 195, 170, 0.5)'],

  essos: ['#893448', '#d95850', 'rgba(137, 52, 72, 0.3)', 'rgba(137, 52, 72, 0.5)', 'rgba(217, 88, 80, 0.5)'],
  shine: ['#c12e34', '#0098d9', 'rgba(137, 52, 72, 0.3)', 'rgba(193, 46, 52, 0.5)', 'rgba(230, 182, 0, 0.5)'],
  roma: ['#e01f54', '#5e4ea5', 'rgba(137, 52, 72, 0.3)', 'rgba(224, 31, 84, 0.5)', 'rgba(94, 78, 165, 0.5)'],
  candy: ['#00F7FF', '#00A0FF', 'rgba(137, 52, 72, 0.3)', 'rgba(0, 247, 255, 0.5)', 'rgba(0, 160, 255, 0.5)'],
  mystical: ['#DB4545', '#FFD400', 'rgba(137, 52, 72, 0.3)', 'rgba(219, 69, 69, 0.5)', 'rgba(255, 212, 0, 0.5)'],
  retro: ['#4CECAA', '#00F7FF', 'rgba(137, 52, 72, 0.3)', 'rgba(76, 236, 170, 0.5)', 'rgba(0, 247, 255, 0.5)'],
  morandi: ['#937372', '#e7e0d5', 'rgba(137, 52, 72, 0.3)', 'rgba(76, 236, 170, 0.5)', 'rgba(0, 247, 255, 0.5)'],
  fog: ['#a3e3fd', '#91cff8', 'rgba(137, 52, 72, 0.3)', 'rgba(76, 236, 170, 0.5)', 'rgba(0, 247, 255, 0.5)'],
  party: ['#de2910', '#ffde00', 'rgba(137, 52, 72, 0.3)', 'rgba(76, 236, 170, 0.5)', 'rgba(0, 247, 255, 0.5)'],
  china: ['#B3B9AD', '#3E6F51', 'rgba(137, 52, 72, 0.3)', 'rgba(76, 236, 170, 0.5)', 'rgba(0, 247, 255, 0.5)'],
  technology: ['#A682FF', '#705AFF', 'rgba(137, 52, 72, 0.3)', 'rgba(76, 236, 170, 0.5)', 'rgba(0, 247, 255, 0.5)'],
  country: ['#EDF8E9', '#C8E9BF', 'rgba(137, 52, 72, 0.3)', 'rgba(76, 236, 170, 0.5)', 'rgba(0, 247, 255, 0.5)'],
}