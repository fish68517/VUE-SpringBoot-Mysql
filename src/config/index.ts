const BASE = require("./config.base");

const ENV_MODE = process.env.envMode, EXCLUDE = ["./config.base.ts", "./index.ts"];

// 排除EXCLUDE获取当前文件下所有ts配置
const modulesFiles = require.context('./', true, /\.ts$/);
const modules = modulesFiles.keys().filter(name => !EXCLUDE.includes(name))
  .reduce((modules, modulePath) => {
    const moduleName = modulePath.replace(/^\.\/(.*)\.\w+$/, '$1');
    const value = modulesFiles(modulePath);
    modules[moduleName] = value;
    return modules;
  }, {});

export default Object.assign(BASE, modules[`config.${ENV_MODE}`]);
