const TOKEN_KEY = 'zsh-ledger-session-v2';

export function createCloudApi(config) {
  const isConfigured = Boolean(
    config?.enabled &&
    /^https:\/\//i.test(config.apiBaseUrl || '') &&
    !String(config.apiBaseUrl).includes('YOUR_UNICLOUD')
  );
  let token = localStorage.getItem(TOKEN_KEY) || '';

  async function request(action, payload = {}, auth = true) {
    if (!isConfigured) throw new Error('uniCloud 接口尚未配置，请先完成部署说明中的第 4 步');
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), config.requestTimeoutMs || 15000);
    try {
      const headers = { 'Content-Type': 'application/json' };
      if (auth && token) headers.Authorization = `Bearer ${token}`;
      const response = await fetch(config.apiBaseUrl, {
        method: 'POST', headers, body: JSON.stringify({ action, ...payload }), signal: controller.signal,
      });
      const rawResult = await response.json().catch(() => ({ code: response.status, message: `云接口返回 HTTP ${response.status}` }));
      let result = rawResult;
      let effectiveStatus = response.status;

      // 兼容曾经未启用阿里云“集成响应”的云函数返回外壳，避免网页把 body 字符串误判为业务结果。
      if (rawResult && typeof rawResult === 'object' && typeof rawResult.body === 'string' && Number.isFinite(Number(rawResult.statusCode))) {
        effectiveStatus = Number(rawResult.statusCode);
        try { result = JSON.parse(rawResult.body); }
        catch { result = { code: effectiveStatus, message: '云接口返回内容格式错误，请重新部署云函数' }; }
      }

      const resultCode = result?.code ?? result?.errCode ?? result?.errorCode ?? result?.Code;
      const resultMessage = result?.message
        || result?.errMsg
        || result?.errorMessage
        || result?.Message
        || result?.error?.message;
      const numericCode = Number(resultCode);

      if (effectiveStatus === 401 || numericCode === 401) {
        token = '';
        localStorage.removeItem(TOKEN_KEY);
        window.dispatchEvent(new CustomEvent('zsh-auth-expired'));
      }
      const statusOk = effectiveStatus >= 200 && effectiveStatus < 300;
      const codeOk = resultCode === 0 || resultCode === '0';
      if (!statusOk || (resultCode !== undefined && !codeOk)) {
        const detail = resultCode !== undefined ? `，错误码：${resultCode}` : '';
        throw new Error(resultMessage || `云接口调用失败（HTTP ${effectiveStatus}${detail}）`);
      }

      // 标准响应是 { code: 0, data }。同时兼容平台代理直接返回 data，
      // 避免网关在成功请求中剥掉业务外壳后被误判为失败。
      if (codeOk) return result?.data;
      if (result && typeof result === 'object' && Object.prototype.hasOwnProperty.call(result, 'data')) return result.data;
      if (result && typeof result === 'object' && !resultMessage) return result;
      throw new Error(resultMessage || `云接口返回格式异常（HTTP ${effectiveStatus}）`);
    } catch (error) {
      if (error.name === 'AbortError') throw new Error('连接云端超时，请检查网络后重试');
      throw error;
    } finally { clearTimeout(timer); }
  }

  async function requestPdfBlob(projectId) {
    if (!isConfigured) throw new Error('uniCloud 接口尚未配置');
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), Math.max(config.requestTimeoutMs || 15000, 30000));
    try {
      const headers = { 'Content-Type': 'application/json' };
      if (token) headers.Authorization = `Bearer ${token}`;
      const response = await fetch(config.apiBaseUrl, {
        method: 'POST', headers, body: JSON.stringify({ action: 'getAttachmentContent', projectId }), signal: controller.signal,
      });
      // uniCloud 的 URL 化网关在部分配置下会把 PDF 的 Content-Type 改成
      // application/octet-stream。先按字节识别 PDF，避免误把有效文件当成错误
      // JSON，继而回退到会触发浏览器下载的云存储临时地址。
      const responseBytes = new Uint8Array(await response.arrayBuffer());
      const pdfHeaderLimit = Math.min(responseBytes.length, 1024);
      let isPdf = false;
      for (let index = 0; index <= pdfHeaderLimit - 5; index++) {
        if (responseBytes[index] === 0x25 && responseBytes[index + 1] === 0x50 && responseBytes[index + 2] === 0x44 && responseBytes[index + 3] === 0x46 && responseBytes[index + 4] === 0x2d) {
          isPdf = true;
          break;
        }
      }
      if (response.ok && isPdf) return new Blob([responseBytes], { type: 'application/pdf' });

      const rawText = new TextDecoder().decode(responseBytes);
      let result;
      try { result = JSON.parse(rawText); }
      catch { result = { code: response.status, message: `云端 PDF 返回 HTTP ${response.status}` }; }

      // 兼容未开启阿里云“集成响应”时返回的响应外壳。
      if (result && typeof result === 'object' && typeof result.body === 'string' && Number.isFinite(Number(result.statusCode))) {
        if (result.isBase64Encoded && Number(result.statusCode) >= 200 && Number(result.statusCode) < 300) {
          const binary = atob(result.body);
          const bytes = new Uint8Array(binary.length);
          for (let index = 0; index < binary.length; index++) bytes[index] = binary.charCodeAt(index);
          return new Blob([bytes], { type: 'application/pdf' });
        }
        try { result = JSON.parse(result.body); }
        catch { result = { code: Number(result.statusCode), message: '云端 PDF 返回内容格式错误' }; }
      }

      if (response.status === 401 || result?.code === 401) {
        token = '';
        localStorage.removeItem(TOKEN_KEY);
        window.dispatchEvent(new CustomEvent('zsh-auth-expired'));
      }
      throw new Error(result?.message || '读取云端 PDF 失败');
    } catch (error) {
      if (error.name === 'AbortError') throw new Error('读取云端 PDF 超时，请检查网络后重试');
      throw error;
    } finally { clearTimeout(timer); }
  }

  async function loadState() {
    let metadata;
    try {
      metadata = await request('loadMeta');
    } catch (error) {
      // 兼容尚未部署分批读取接口的旧云函数。
      if (/(?:不支持|unsupported).{0,20}action/i.test(error?.message || '')) return request('load');
      throw error;
    }

    async function loadSection(section) {
      const items = [];
      let offset = 0;
      for (let page = 0; page < 10000; page += 1) {
        const chunk = await request('loadChunk', { section, offset });
        if (!chunk || !Array.isArray(chunk.items)) throw new Error(`云端${section}分批数据格式异常`);
        items.push(...chunk.items);
        if (chunk.nextOffset === null || chunk.nextOffset === undefined) return items;
        const nextOffset = Number(chunk.nextOffset);
        if (!Number.isFinite(nextOffset) || nextOffset <= offset) throw new Error(`云端${section}分页位置异常`);
        offset = nextOffset;
      }
      throw new Error(`云端${section}数据页数异常`);
    }

    const [projects, properties, payments] = await Promise.all([
      loadSection('projects'),
      loadSection('properties'),
      loadSection('payments'),
    ]);
    return {
      projects,
      properties,
      payments,
      settings: metadata?.settings || { reminderDays: 30 },
      _revisions: metadata?._revisions || {},
    };
  }

  async function login(username, password) {
    const data = await request('login', { username, password }, false);
    token = data.token;
    localStorage.setItem(TOKEN_KEY, token);
    return data;
  }

  async function logout() {
    try { if (token) await request('logout'); } finally {
      token = '';
      localStorage.removeItem(TOKEN_KEY);
    }
  }

  function fileToBase64(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(String(reader.result).split(',')[1] || '');
      reader.onerror = () => reject(reader.error || new Error('读取 PDF 失败'));
      reader.readAsDataURL(file);
    });
  }

  return Object.freeze({
    isConfigured,
    hasToken: () => Boolean(token),
    login,
    logout,
    me: () => request('me'),
    load: loadState,
    save: (state, revisions = {}) => request('save', { state, revisions }),
    importProperties: (properties, expectedRevision = 0) => request('importProperties', { properties, expectedRevision }),
    changePassword: (currentPassword, newPassword) => request('changePassword', { currentPassword, newPassword }),
    listUsers: () => request('listUsers'),
    resetPassword: (targetUserId, newPassword) => request('resetPassword', { targetUserId, newPassword }),
    uploadAttachment: async file => request('uploadAttachment', {
      fileName: file.name, mimeType: file.type || 'application/pdf', base64: await fileToBase64(file),
    }),
    getAttachmentUrl: projectId => request('getAttachmentUrl', { projectId }),
    getAttachmentBlob: projectId => requestPdfBlob(projectId),
    deleteAttachment: projectId => request('deleteAttachment', { projectId }),
  });
}
