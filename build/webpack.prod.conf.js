/*
 * @Author: kaix
 * @Date: 2023-05-24 10:27:22
 * @LastEditTime: 2025-07-04 15:10:29
 * @LastEditors: kaix
 * @Description: 组件库打包生产
 */
'use strict'
const path = require('path')
const copyWebpackPlugin = require('copy-webpack-plugin')
const TerserPlugin = require('terser-webpack-plugin')
const { merge } = require('webpack-merge')
const WebpackBaseConfig = require('./webpack.base.conf')
const WebpackRulesConfig = require('./webpack.rules.conf')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin
const { parseComponent } = require('./resolve-file.js')

// ☆☆☆☆☆☆☆☆☆☆☆ 组件编码 ☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
const compName ='Bz‌Pipeline'
// ☆☆☆☆☆☆☆☆☆☆☆ 改这里 ☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆

/*
 * entry: 组件打包入口文件  类似 'src/package/Decorates/Borders/Border03/export.ts'
 * version: 每个组件的版本号 在 'src/package/Decorates/Borders/Border03/index.vue' 中定义
 */
const [entry, version] = parseComponent(compName)

module.exports = merge(WebpackBaseConfig, {
  mode: 'production',
  entry: {
    [compName]: {
      import: path.resolve(__dirname, `../${entry}`),
      library: {
        name: `${compName}@${version}`,
        type: 'amd'
      }
    }
    // vendor: ["axios", "vue-router", "vuex"],
  },
  output: {
    path: path.resolve(__dirname, '../dist'),
    // 打包后的资源访问基础路径
    publicPath: `../component/${compName}/${version}/`,
    filename: `[name]@${version}.js`,
    libraryTarget: 'amd', // 设置 libraryTarget 为 'amd'
    umdNamedDefine: true,
    globalObject: "typeof self !== 'undefined' ? self : this"
  },
  // 排除打包库
  externals: {
    vue: 'vue',
    lodash: '_',
    'naive-ui': 'naiveUi',
    'echarts': 'echarts'
  },
  resolve: {
    extensions: ['.ts', '.js'] // 确保能够解析导入时省略后缀的 .ts 文件
  },
  module: WebpackRulesConfig.module,
  plugins: [
    new MiniCssExtractPlugin({
      // filename: "./css/[name].[contenthash].css",
      filename: '[name]@' + version + '.css',
      chunkFilename: '[id]@' + version + '.css'
    }),
    ...(process.env.ANALYZE === 'true' ? [new BundleAnalyzerPlugin()] : [])
    // 打包完成自动关闭
    //   {
    //     apply: (compiler) => {
    //       compiler.hooks.done.tap('DonePlugin', (stats) => {
    //         console.log('Compile is done !')
    //         setTimeout(() => {
    //           process.exit(0)
    //         })
    //       });
    //     }
    //  }
  ],
  optimization: {
    // 分包策略
    // splitChunks: {
    //   cacheGroups: {
    //     vendor: {
    //       name: 'vendor',
    //       test: /[\\/]node_modules[\\/]/,
    //       chunks: 'all',
    //       priority: 10,
    //     },
    //   },
    // },
    minimize: true,
    minimizer: [
      new TerserPlugin({
        parallel: true,
        extractComments: false,
        terserOptions: {
          compress: {
            drop_console: false,
            drop_debugger: true
          }
        }
      })
    ]
  }
})
