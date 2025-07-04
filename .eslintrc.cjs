/*
 * @Author: kaix
 * @Date: 2023-03-12 20:07:17
 * @LastEditTime: 2023-03-16 21:31:32
 * @LastEditors: kaix
 * @Description: 
 */
module.exports = {
  root: true,
  parser: 'vue-eslint-parser',
  globals: {
    postMessage: true
  },
  parserOptions: {
    parser: '@typescript-eslint/parser',
    sourceType: 'module',
    ecmaFeatures: {
      jsx: true,
      tsx: true
    }
  },
  env: {
    node: true,
    // The Follow config only works with eslint-plugin-vue v8.0.0+
    'vue/setup-compiler-macros': true
  },
  extends: ['plugin:vue/vue3-essential', 'eslint:recommended'],
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-unused-vars': 'off',
    'vue/no-unused-vars': 'off',
    'vue/multi-word-component-names': 'off',
    'vue/valid-template-root': 'off',
    'vue/no-mutating-props': 'off'
  }
}
