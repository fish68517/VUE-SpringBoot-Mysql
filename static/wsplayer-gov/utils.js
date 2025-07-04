const userAgentKey = {
    Opera: "Opera",
    Chrome: "Chrome",
    Firefox: "Firefox",
    Edge: "Edge",
    IE: "IE",
    Safari: "Safari",
}

// 获取浏览器类型
function getBrowserType() {
    const {userAgent} = navigator;
    // 判断是否为Edge浏览器
    if(userAgent.includes("Edge")) {
        return userAgentKey.Edge;
    }
    // 判断是否为Firefox浏览器
    if(userAgent.includes("Firefox")) {
        return userAgentKey.Firefox;
    }
    // 判断是否为Chrome浏览器
    if(userAgent.includes("Chrome")) {
        return userAgentKey.Chrome;
    }
    // 判断是否为Safari浏览器
    if(userAgent.includes("Safari")) {
        return userAgentKey.Safari;
    }
    // 判断是否为IE浏览器
    if(userAgent.includes("compatible")
        && userAgent.includes("MSIE")
        && userAgent.includes("Opera")
    ) {
        return userAgentKey.IE;
    }
    // 判断是否为Opera浏览器
    if(userAgent.includes("Opera")) {
        return userAgentKey.Opera;
    }
    return "";
}

// 获取浏览器版本
function getBrowserVersion(browserType) {
    const {userAgent} = navigator;
    return userAgent.split(browserType)[1].split(".")[0].slice(1)
}

// 判断是否是chrome 104+的版本
function checkChromeV104Browser() {
    const browserType = getBrowserType();
    const browserVersion = getBrowserVersion(browserType);
    return browserType === "Chrome" && browserVersion >= 104;
}

export default checkChromeV104Browser
