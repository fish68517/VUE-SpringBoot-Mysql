/*
 * @Author: kaix
 * @Date: 2021-11-22 16:19:35
 * @LastEditTime: 2023-07-21 10:33:42
 * @LastEditors: kaix
 * @Description: 
 */
"use strict";
const path = require("path");
const webpack = require("webpack");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const { VueLoaderPlugin } = require("vue-loader/dist/index");
const WebpackBar = require("webpackbar");
const config = require("../src/config/config.base.ts");

const envMode = process.env.envMode;

require("dotenv").config({ path: `.env.${envMode}` });

const prefixRE = /^VUE_APP_/;
let env = {};

//定义envMode || BASE_URL 开头的环境变量
for (const key in process.env) {
  if (key === "envMode" || key === "BASE_URL" || prefixRE.test(key)) {
    env[key] = JSON.stringify(process.env[key]);
  }
}

module.exports = {
  plugins: [
    new CleanWebpackPlugin(),
    new webpack.DefinePlugin({
      "process.env": {
        ...env,
      },
    }),
    new VueLoaderPlugin(),
    new WebpackBar()
  ],
  resolve: {
    extensions: [".ts", ".js", ".vue", ".json"],
    mainFields: ["jsnext:main", "module", "browser", "main"],
    alias: {
      "@": path.resolve(__dirname, "../src"),
      assets: path.resolve(__dirname, "../src/assets/"),
      images: path.resolve(__dirname, "../src/assets/images"),
      utils: path.resolve(__dirname, "../src/utils"),
      api: path.resolve(__dirname, "../src/api"),
    },
  },
};
