# HBuilderX 打包 Android 安装说明

> **最新安装包（2.0.2 / 202）：** `code/unpackage/release/apk/郑州智慧供水-2.0.2-地图加载修复.apk`。修复 App renderjs 误用占位节点而导致地图一直加载的问题，同时修正管网画布与图表的容器获取。Key 保持不变，继续使用 Web 端 JSAPI Key。签名与旧版一致，已校验 APK 资源与最终编译结果相同；未自动安装到手机。详细原因见[移动端高德地图修复说明](移动端高德地图修复说明.md)。校验记录：`code/unpackage/evidence/android-apk-202.json`。

> **2026-09-22 已实际打包交付：** 当前源码版本为 `2.0.1` / `201`。安装包为 `code/unpackage/release/apk/郑州智慧供水-2.0.1-移动地图.apk`（18,729,832 字节，约 17.9 MiB），包含移动端高德地图及最新图表修复。使用 HBuilderX 5.24 安心打包、原云端证书和原包名 `uni.app.UNI8E59F90`；签名校验通过，与 2026-09-21 旧安装包签名一致。支持 ARM64、Android 5.0 及以上；未进行手机安装实测。无需卸载旧版，可先尝试覆盖安装。本次未上传 uniCloud。下文的 `2.0.0` / `200` 为最初截图的填写示例，后续版本以源码为准。

本次已核对 APK 内的业务 JS、地图 renderjs、样式与最终编译资源逐字节一致，高德配置已包含，未携带需求文件中的 uniCloud SpaceId / ClientSecret。构建配置统一为 `code/vite.config.js`，其中本地业务插件在 SDK 编译前排除未使用的云空间配置，不修改 HBuilderX 的账号关联或云端资源。原 `JAVA_TOOL_OPTIONS` 问题通过仅清除启动进程环境解决，系统环境变量未修改。

校验记录：`code/unpackage/evidence/android-apk-201.json`；APK SHA-256：`bf02e8fbb40084073b76d4c422204a602220da872ef407e9a8a7c297cc54ce7d`。

HBuilderX 可以将本项目打包为 APK，在 Android 手机上安装运行。本项目为普通 uni-app Vue 3 工程，项目入口目录为 `code`。

**已经打开“App 打包”窗口时，直接看第 3 节，按表逐项填写即可。** 本说明按你提供的打包窗口截图和当前项目配置编写，目标是生成一个可以直接安装到自己 Android 手机上的 APK。

## 1. 准备项目

1. 安装 HBuilderX，并登录自己的 DCloud 账号。根据提示安装 uni-app 编译及 App 真机运行插件。
2. 选择“文件 → 导入 → 从本地目录导入”，选择 `E:\bishe27\springboot-vue-mysql-android\郑州-城市智慧供水\code`。不要选择外层文档目录，也不要把 H5 构建目录作为源码工程导入。
3. 在 `code` 目录打开终端。首次使用或依赖缺失时执行 `npm ci`，然后执行：

   ```powershell
   npm run prepare:demo
   npm run check
   npm run build:app
   ```

   `prepare:demo` 是现有资源准备命令，保留命令名称以兼容工程脚本；它复制 ECharts、巡检附件及视频到 `static`。每次更新这些源资源后都应重新执行。

4. `npm run build:app` 输出 `code/unpackage/dist/build/app` 下的 App 资源，**这一步不生成 APK**。真正的安装包需要下一节的签名打包。

## 2. 检查 manifest.json

在 HBuilderX 双击 `manifest.json`，进入可视化配置：

| 项目 | 当前配置或需要执行的操作 |
| --- | --- |
| 应用名称 | 郑州智慧供水 |
| DCloud AppID | 当前已填写 `__UNI__8E59F90`，请确认属于你当前登录账号或该账号有使用权限；不是 Android 包名 |
| 版本名称 / 版本号 | 当前为 `2.0.0` / `200`；后续更新安装包时递增版本号 |
| App 模块 | 已配置 `VideoPlayer`，保留视频播放模块 |
| Android 包名 | 保留你截图中的 `uni.app.UNI8E59F90`，本次直接使用这个值，无需另起包名 |
| 图标和启动图 | 在 App 图标、启动界面配置中按需要设置 |

当前 H5 的 `router.base` 为 `/web/`，它是网页部署路径，不是 Android 包名，也不用为打 APK 改回根路径。网页预览执行 `npm run preview` 后访问 `http://127.0.0.1:4173/web/`；若旧预览服务仍在运行，需重启以加载新的预览脚本。

## 3. 对照你的截图逐项填写

你截图中的应用名称、包名、“使用云端证书”、“打正式包”和“快速安心打包”已经选对，可以保留。灰色的证书输入框不用填，也不用准备任何密码。

### 3.1 窗口上半部分

| 截图中的项目 | 本项目怎么填或怎么选 | 说明 |
| --- | --- | --- |
| 应用名称 | 保持 `郑州智慧供水` | 从 `manifest.json` 读取，已正确 |
| 应用版本号 | 保持 `200` | 对应版本名称 `2.0.0`，本次无需修改 |
| Android（apk包） | **勾选** | 本次需要的手机安装包 |
| iOS（ipa包） | **不勾选** | 本次只做 Android |
| Android 包名 | **`uni.app.UNI8E59F90`** | 保留截图现有值，大小写照抄，不加空格 |
| 使用自有证书 | **不选** | 本次无需自行生成或选择证书文件 |
| 使用云端证书 | **选中** | 你已经选中，保持即可 |
| 使用公共测试证书 | **不选** | 官方已下线此方式，即使旧窗口显示也不要选 |
| 证书文件 | **留空** | 使用云端证书时输入框灰色属于正常情况 |
| 证书库密码 | **留空** | 不填 DCloud 登录密码，也不填系统登录密码 |
| 证书别名 | **留空** | 不填应用名称或 AppID |
| 证书私钥密码 | **留空** | 不需要自己设置 |

可直接复制的 Android 包名：

```text
uni.app.UNI8E59F90
```

`__UNI__8E59F90` 是项目里的 **DCloud AppID**，`uni.app.UNI8E59F90` 是截图中的 **Android 包名**。两者不是同一个字段，不要把带下划线的 AppID 粘贴进 Android 包名输入框。包名也不是网址，不填 `http://`、服务器地址或 `/web/`。

选择云端证书后，DCloud 会按应用 AppID 生成和管理对应的证书，证书信息自动处理，所以你不需要手工填写四个灰色框。需要查看或备份时，可在 DCloud 开发者中心找到该应用的 Android 云端证书。该用法及公共测试证书的停用状态见[官方证书说明](https://ask.dcloud.net.cn/article/id-35985__page-8)。

### 3.2 渠道包、防重签和窗口下半部分

| 截图中的项目 | 本次选择 | 说明 |
| --- | --- | --- |
| 渠道包 → 无 | **勾选“无”** | 直接安装到手机，不制作应用商店渠道包 |
| GooglePlay（AAB） | **不勾选** | 这次需要 APK，不需要 Google Play 的 AAB |
| 应用宝、360、华为、小米、OPPO、VIVO | **全部不勾选** | 本次没有上架应用商店的需求 |
| 防重签 → appid | **勾选** | 本次采用 AppID 校验 |
| 防重签 → 包名 | **先不勾选** | 本次不额外启用这一校验项 |
| 防重签 → 证书 | **先不勾选** | 本次不额外绑定证书指纹校验 |
| 打正式包 | **选中** | 生成可以独立安装、从桌面打开的应用；不表示已经上架商店 |
| 打自定义调试基座 | **不选** | 本次目标是安装使用，不制作调试工具 |
| 生成 iOS 符号表（dsym）文件 | **不勾选** | 本次没有 iOS 包 |
| 生成 SourceMap | **不勾选** | 本次不需要上传源码映射用于错误分析 |
| 原生混淆 → 对配置的 .js/.nvue 文件进行原生混淆 | **不勾选** | 先按项目当前配置打包，无需新增混淆配置 |
| 底部 → 传统打包 | **不选** | 使用右边的快速安心打包 |
| 底部 → 快速安心打包 | **选中** | 你截图中已选中，保持即可 |

“防重签”的以上选法是本次安装使用的配置建议，不是生成 APK 的必填要求。官方说明 AppID 校验是默认校验项，包名和证书是可增加的校验项，见[防重签说明](https://uniapp.dcloud.net.cn/tutorial/app-android-antiresigne.html)。

截图底部只露出了“广告联盟”标题，没有显示完整选项。若向下滚动看到广告、开屏广告或广告 SDK 等开关，本项目本次均不启用，不填写广告位 ID；不要为了打包额外申请广告服务。其他未显示的设置先保持项目原有配置。

“快速安心打包”仍需要联网和 DCloud 账号，不是完全离线打包；其打包方式无需上传应用代码和证书。具体机制见[官方云打包说明](https://uniapp.dcloud.net.cn/dev/app/cloud-build.html)。

### 3.3 填完后怎么操作

1. 确认 Android 已勾选，包名为 `uni.app.UNI8E59F90`，选择了云端证书。
2. 确认渠道包只勾“无”，选择“打正式包”和“快速安心打包”。
3. 点击右下角 **“打包(P)”**。上传、提交和云端打包由你自行操作。
4. 查看 HBuilderX 下方控制台，等待出现打包成功和 APK 下载地址；仍在排队或编译时继续等待，不要重复提交。
5. 按控制台提供的地址下载 `.apk` 文件，再按第 4 节传到手机安装。

若提示未登录，先登录 DCloud 账号再提交。若提示没有 `__UNI__8E59F90` 的权限，请登录创建该应用的账号或让应用所有者授予权限；不要反复改包名来解决账号权限问题。当前本地只能确认这个 AppID 已写入配置，不能确认你的云端账号权限。

若窗口要求你手工填写证书文件或密码，先检查是否误选了“使用自有证书”，切回“使用云端证书”。如果控制台明确提示证书尚未创建或需账号认证，再按该提示到 DCloud 开发者中心处理对应应用；不要把网站登录密码填入签名证书密码框。

后续重新打包更新时，继续使用相同 AppID、包名及云端证书，并递增 `manifest.json` 中的版本号，例如下次由 `200` 改为 `201`。这有助于保持覆盖安装的一致性。

App 云打包与 uniCloud 云函数、数据库或网页托管上传是不同操作。本次仅更新操作文档，没有替你提交云打包或上传 uniCloud。

## 4. 安装到 Android 手机

1. 将生成的 `.apk` 文件复制或发送到手机，在文件管理器中打开。
2. 若系统提示，允许当前文件管理器或下载应用“安装未知应用”，然后安装。
3. 从桌面打开“郑州智慧供水”，登录后检查工作台、地图、设备、巡检、视频及导出功能。
4. 若提示与已有应用冲突，先核对包名和签名证书。不要直接卸载旧应用来处理：卸载会清除本机业务记录，应先在系统设置导出快照备份。

打包后的静态页面和资源随 APK 安装，不需要电脑一直运行 H5 预览服务。当前业务仍使用项目已有的本地数据与设备存储，改页面文案及打包本身不会新增云端同步、真实设备控制或短信发送能力。

## 5. 先用 USB 检查真机运行

需要在打包前调试时：开启手机开发者选项和 USB 调试，用可传数据的 USB 线连接电脑，在手机上允许调试授权。在 HBuilderX 选择“运行 → 运行到手机或模拟器 → Android App 基座”，选择设备运行。运行基座用于验证界面与原生能力；分发给其他手机仍按第 3 节生成 APK。

如果菜单不可用，确认选中了 `code` 工程、项目被识别为 uni-app、相关插件已安装；找不到手机时检查 USB 调试授权、数据线及驱动。

本次只验证本地类型、数据、路由、单元测试及资源编译。签名 APK 生成和 Android 真机安装结果需在你执行后确认。

## 6. 本机“Apk tool decompile package failed”报错处理

### 6.1 本次已确认的情况

你本次日志包含：

```text
[Error] Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
[Error] Apk tool decompile package failed
[Error] 制作结果：Failed. Reason:
```

已检查当前电脑：HBuilderX 安装在 `D:\soft\HBuilderX.5.24.2026081301\HBuilderX`，系统级环境变量确实设置了 `JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8`。

使用该 HBuilderX 自带的 Java、Apktool 和本项目缓存的 `__UNI__8E59F90_cm.apk`，进行了不联网、不签名的本地解包对比：

| 环境 | Apktool 退出码 | 错误输出 stderr | AndroidManifest.xml |
| --- | --- | --- | --- |
| 保留 JAVA_TOOL_OPTIONS | 0，成功 | 输出 `Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8` | 解包成功 |
| 仅清除当前进程的 JAVA_TOOL_OPTIONS | 0，成功 | 空 | 解包成功 |

因此，当前缓存 APK 可以解包；日志表现高度符合安心打包将 Java 启动提示作为错误处理的情况。这是基于本机对比的判断，尚未完成 HBuilderX 整条打包流程复验。没有证据表明需要修改包名、证书或页面代码。对比结果保存在 `code/unpackage/evidence/apk-diagnosis/result.json`。

### 6.2 按这四步重新打开并打包

1. **先保存 HBuilderX 中打开的文件，然后完全退出 HBuilderX。** 仅关闭“App 打包”窗口不够，因为主程序仍保留旧环境变量。
2. 在资源管理器打开本项目的 `code` 文件夹，双击 **`启动HBuilderX安心打包.cmd`**。如果窗口已退出但仍有同一安装目录下的无窗口后台进程，新版脚本会检查并结束这些残留，再启动 HBuilderX；如果仍有编辑器窗口，或进程路径不一致，则停止操作并提示你手动退出，不结束该编辑器。
3. 在重新打开的 HBuilderX 中选中 `code` 项目，再打开“发行 → App-Android/iOS-云打包”。继续使用第 3 节的设置：包名 `uni.app.UNI8E59F90`、云端证书、渠道“无”、打正式包、快速安心打包。
4. **由你点击“打包”重新尝试**。查看控制台，这次应不再出现 `Picked up JAVA_TOOL_OPTIONS`；最终以打包成功提示和生成的 APK 为准。

启动文件的完整位置：

```text
E:\bishe27\springboot-vue-mysql-android\郑州-城市智慧供水\code\启动HBuilderX安心打包.cmd
```

该入口调用 `code/scripts/start-hbuilder-safe.ps1`，只对它启动的 HBuilderX 及子进程移除 `JAVA_TOOL_OPTIONS`，不修改 Windows 的用户或系统环境变量。它不包含账号或证书密码，不会自动打包、签名或上传。以后需要安心打包时，也从这个文件启动；直接从原快捷方式启动仍可能继承该系统变量。如果未来移动或升级 HBuilderX 安装目录，需同步更新 PowerShell 脚本中的 `hbuilderExe` 路径。

若旧启动文件出现乱码和 `is not recognized as an internal or external command`，请使用项目中已更新的文件。新版 `.cmd` 只含 ASCII 命令，中文提示由带 UTF-8 BOM 的 `.ps1` 输出，避免 CMD 对中文批处理的解析问题。两个文件需一起保留，不要只复制 `.cmd` 到其他目录。

### 6.3 如果还有报错

- 仍出现 `Picked up JAVA_TOOL_OPTIONS`：通常是旧 HBuilderX 没有完全退出，或重新从原快捷方式启动了。先按 6.2 重新操作。
- 该行消失但仍报失败：保留本次完整日志，尤其是新的 `[Error]` 和 `Exception` 内容；本地解包通过不能代表签名、对齐及后续所有步骤都通过。
- 如需绕过本地安心打包步骤，可由你将窗口底部改成“传统打包”后重试。传统打包会上传应用代码和签名材料，方式不同于快速安心打包；是否采用由你决定。官方说明见[云打包模式](https://uniapp.dcloud.net.cn/dev/app/cloud-build.html)。

无需为了这条 Java 提示删除项目、清空业务数据、重新申请 AppID 或更换证书。

## 官方参考

- [App 云打包说明](https://uniapp.dcloud.net.cn/dev/app/cloud-build.html)
- [uni CLI 与 HBuilderX CLI 的打包区别](https://uniapp.dcloud.net.cn/worktile/CLI.html)
- [Android 云打包证书说明](https://ask.dcloud.net.cn/article/35985)
- [App 真机运行](https://uniapp.dcloud.io/tutorial/run/run-app.html)
