/*
 * @Author: kaix
 * @Date: 2021-11-22 16:19:35
 * @LastEditTime: 2025-07-04 15:30:24
 * @LastEditors: kaix
 * @Description:
 */
'use strict'
const path = require('path')
const { merge } = require('webpack-merge')
const WebpackBaseConfig = require('./webpack.base.conf')
const WebpackRulesConfig = require('./webpack.rules.conf')
const FriendlyErrorsWebpackPlugin = require('friendly-errors-webpack-plugin')
const config = require('../src/config/config.base.ts')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const { getComponents } = require('./resolve-file')
const webpack = require('webpack')

const list = getComponents()
module.exports = merge(WebpackBaseConfig, {
  entry: {
    index: path.resolve(__dirname, '../src/main.ts'),
    'vendor-common': ['vue-echarts']
  },
  output: {
    path: path.resolve(__dirname, '../dist'),
    filename: '[name].bundle.js'
  },
  mode: 'development',
  devtool: 'eval-source-map',
  target: 'web',
  module: WebpackRulesConfig.module,
  optimization: {
    runtimeChunk: 'single'
  },
  devServer: {
    static: {
      directory: path.join(__dirname, '../static'),
      watch: true
    },
    open: true,
    hot: true,
    port: 8085,
    historyApiFallback: true,
    client: {
      // logging: 'warn',
      overlay: false
    },
    proxy: config.proxy
  },
  plugins: [
    new HtmlWebpackPlugin({
      title: config.title,
      template: path.resolve(__dirname, '../static/index.html'),
      filename: 'index.html',
      minify: {
        html5: true,
        collapseWhitespace: true,
        preserveLineBreaks: false,
        minifyCSS: true,
        minifyJS: true,
        removeComments: false
      }
    }),
    new FriendlyErrorsWebpackPlugin(),
    // 将node环境读取的值传给main.js
    new webpack.DefinePlugin({
      COMPONENT_LIST: JSON.stringify(list)
    })
  ]
})
