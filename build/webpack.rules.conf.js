/*
 * @Author: wangcong
 * @Date: 2023-07-14 15:22:39
 * @LastEditTime: 2024-03-13 08:56:20
 * @LastEditors: wangcong
 * @Description: 
 */
"use strict";
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const envMode = process.env.envMode !== "production";
const path = require("path");

module.exports = {
  module: {
    rules: [
      {
        test: /\.(t|j)s$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader",
          options: {
            cacheDirectory: true,
          },
        },
      },
      {
        test: /\.(png|jpg|svg|gif)$/,
        type: "asset/resource",
        generator: {
          filename: "assets/[hash:8].[name][ext]",
        },
      },
      {
        test: /\.(css|scss|sass)$/,
        use: [
          envMode
            ? "style-loader"
            : {
                loader: MiniCssExtractPlugin.loader,
                options: {
                  publicPath: "./",
                },
              },
          "css-loader",
          "sass-loader",
          {
            loader: "style-resources-loader",
            options: {
              patterns: [
                path.resolve(__dirname, "../src/styles/globals/*.scss"),
              ],
            },
          },
        ],
      },
      {
        test: /\.vue$/,
        use: ["vue-loader"],
      },
    ],
  },
};
