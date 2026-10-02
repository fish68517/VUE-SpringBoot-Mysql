'use strict';

const crypto = require('crypto');
const http = require('http');
const https = require('https');
const db = uniCloud.database();
const usersCollection = db.collection('zsh-ledger-users');
const sessionsCollection = db.collection('zsh-ledger-sessions');
const stateCollection = db.collection('zsh-ledger-state');

const SESSION_HOURS = 12;
const MAX_LOGIN_FAILURES = 5;
const LOCK_MINUTES = 15;
const MAX_PDF_BYTES = 1024 * 1024;
const STATIC_SITE_URL = 'https://static-mp-f4c892a2-7540-459d-9a6f-ccae78f87037.next.bspapp.com';

const FIXED_USERS = Object.freeze({
  admin: { id: 'admin', username: 'admin', name: '总管理员', role: 'admin', defaultPassword: 'Zsh@2026Admin' },
  operator1: { id: 'operator1', username: 'user01', name: '业务员1', role: 'operator', defaultPassword: 'Zsh@2026User01' },
  operator2: { id: 'operator2', username: 'user02', name: '业务员2', role: 'operator', defaultPassword: 'Zsh@2026User02' },
});

class HttpError extends Error {
  constructor(status, message) { super(message); this.status = status; }
}

function originFor(event) {
  const headers = event.headers || {};
  return headers.origin || headers.Origin || '';
}

function isAllowedOrigin(origin) {
  return !origin || origin === 'null' || origin === STATIC_SITE_URL || /^https?:\/\/(localhost|127\.0\.0\.1)(:\d+)?$/.test(origin);
}

function httpResponse(event, statusCode, payload) {
  return {
    mpserverlessComposedResponse: true,
    isBase64Encoded: false,
    statusCode,
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
      'Cache-Control': 'no-store',
    },
    body: JSON.stringify(payload),
  };
}

function attachmentResponse(event, buffer, fileName) {
  const origin = originFor(event);
  return {
    mpserverlessComposedResponse: true,
    isBase64Encoded: true,
    statusCode: 200,
    headers: {
      'Content-Type': 'application/pdf',
      'Content-Disposition': `inline; filename*=UTF-8''${encodeURIComponent(fileName || 'document.pdf')}`,
      'Cache-Control': 'private, no-store',
      ...(origin ? { 'Access-Control-Allow-Origin': origin, Vary: 'Origin' } : {}),
    },
    body: buffer.toString('base64'),
  };
}

function fetchCloudFile(url, redirectsLeft = 3) {
  return new Promise((resolve, reject) => {
    let parsed;
    try { parsed = new URL(url); }
    catch { reject(new HttpError(500, '云端 PDF 地址无效')); return; }
    if (!['http:', 'https:'].includes(parsed.protocol)) { reject(new HttpError(500, '云端 PDF 协议无效')); return; }
    const client = parsed.protocol === 'https:' ? https : http;
    let finished = false;
    const fail = error => { if (!finished) { finished = true; reject(error); } };
    const request = client.get(parsed, { headers: { 'User-Agent': 'zsh-ledger-api/2.2.8' } }, response => {
      if ([301,302,303,307,308].includes(response.statusCode) && response.headers.location) {
        response.resume();
        if (redirectsLeft <= 0) return fail(new HttpError(502, '云端 PDF 重定向次数过多'));
        finished = true;
        fetchCloudFile(new URL(response.headers.location, parsed).toString(), redirectsLeft - 1).then(resolve, reject);
        return;
      }
      if (response.statusCode < 200 || response.statusCode >= 300) {
        response.resume();
        return fail(new HttpError(502, `云存储返回 HTTP ${response.statusCode}`));
      }
      const declaredSize = Number(response.headers['content-length'] || 0);
      if (declaredSize > MAX_PDF_BYTES) {
        response.resume();
        return fail(new HttpError(413, '云端 PDF 超过 1MB，无法在线预览'));
      }
      const chunks = [];
      let total = 0;
      response.on('data', chunk => {
        if (finished) return;
        total += chunk.length;
        if (total > MAX_PDF_BYTES) {
          response.destroy();
          fail(new HttpError(413, '云端 PDF 超过 1MB，无法在线预览'));
          return;
        }
        chunks.push(chunk);
      });
      response.on('end', () => {
        if (finished) return;
        const buffer = Buffer.concat(chunks);
        if (buffer.subarray(0, 5).toString() !== '%PDF-') return fail(new HttpError(422, '云端附件不是有效的 PDF 文件'));
        finished = true;
        resolve(buffer);
      });
      response.on('error', fail);
    });
    request.setTimeout(20000, () => request.destroy(new HttpError(504, '读取云端 PDF 超时')));
    request.on('error', fail);
  });
}

function parseBody(event) {
  const raw = event.isBase64Encoded ? Buffer.from(event.body || '', 'base64').toString('utf8') : event.body;
  if (typeof raw === 'string') return JSON.parse(raw || '{}');
  return raw && typeof raw === 'object' ? raw : {};
}

function passwordPolicy(password) {
  return typeof password === 'string' && password.length >= 8 && /[A-Za-z]/.test(password) && /\d/.test(password);
}

function hashPassword(password, salt = crypto.randomBytes(16).toString('hex')) {
  const hash = crypto.pbkdf2Sync(password, salt, 120000, 32, 'sha256').toString('hex');
  return { salt, hash };
}

function verifyPassword(password, user) {
  if (!user?.passwordHash || !user?.passwordSalt) return false;
  const calculated = hashPassword(String(password || ''), user.passwordSalt).hash;
  const left = Buffer.from(calculated, 'hex');
  const right = Buffer.from(user.passwordHash, 'hex');
  return left.length === right.length && crypto.timingSafeEqual(left, right);
}

function permissionsFor(user) {
  const admin = user.role === 'admin';
  return { canViewAll: admin, canExport: admin, canManageUsers: admin, canImport: true, canEditManualFields: admin, canModifyAlerts: admin, canRecordPayments: admin };
}

function safeUser(user) {
  return {
    id: user._id || user.id,
    username: user.username,
    name: user.name,
    role: user.role,
    mustChangePassword: Boolean(user.mustChangePassword),
    lockedUntil: Number(user.lockedUntil || 0),
    updatedAt: user.updatedAt || null,
    permissions: permissionsFor(user),
  };
}

async function ensureFixedUsers() {
  await Promise.all(Object.values(FIXED_USERS).map(async fixed => {
    const result = await usersCollection.doc(fixed.id).get();
    if (result.data?.[0]) return;
    const password = hashPassword(fixed.defaultPassword);
    await usersCollection.doc(fixed.id).set({
      username: fixed.username,
      name: fixed.name,
      role: fixed.role,
      passwordHash: password.hash,
      passwordSalt: password.salt,
      mustChangePassword: true,
      failedLoginCount: 0,
      lockedUntil: 0,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    });
  }));
}

async function findUserByUsername(username) {
  const normalized = String(username || '').trim().toLowerCase();
  const fixed = Object.values(FIXED_USERS).find(item => item.username.toLowerCase() === normalized);
  if (!fixed) return null;
  const result = await usersCollection.doc(fixed.id).get();
  return result.data?.[0] || null;
}

function bearerToken(event) {
  const headers = event.headers || {};
  const value = headers.authorization || headers.Authorization || '';
  return /^Bearer\s+(.+)$/i.exec(value)?.[1] || '';
}

function tokenHash(token) { return crypto.createHash('sha256').update(token).digest('hex'); }

async function requireActor(event) {
  const token = bearerToken(event);
  if (!token) throw new HttpError(401, '登录已失效，请重新登录');
  const sessionResult = await sessionsCollection.doc(tokenHash(token)).get();
  const session = sessionResult.data?.[0];
  if (!session || Number(session.expiresAt) <= Date.now()) {
    if (session) await sessionsCollection.doc(session._id).remove().catch(() => {});
    throw new HttpError(401, '登录已过期，请重新登录');
  }
  const userResult = await usersCollection.doc(session.userId).get();
  const user = userResult.data?.[0];
  if (!user || !FIXED_USERS[user._id]) throw new HttpError(401, '账号不可用');
  return user;
}

async function revokeUserSessions(userId) {
  const result = await sessionsCollection.where({ userId }).get();
  await Promise.all((result.data || []).map(item => sessionsCollection.doc(item._id).remove()));
}

function normalizeState(value) {
  return {
    projects: Array.isArray(value?.projects) ? value.projects : [],
    properties: Array.isArray(value?.properties) ? value.properties : [],
    payments: Array.isArray(value?.payments) ? value.payments : [],
    settings: value?.settings && typeof value.settings === 'object' ? value.settings : { reminderDays: 30 },
  };
}

// URL 化云函数的响应体上限为 2MB。历史版本或备份数据中可能残留
// PDF/base64 等大字段，因此列表读取时只传输台账所需的轻量数据。
const OMIT_TRANSFER_FIELD = /(?:^|_)(?:base64|dataurl|filecontent|attachmentdata|pdfdata|rawpdf|binary|buffer|bytes)(?:$|_)/i;
const MAX_TRANSFER_STRING_LENGTH = 64 * 1024;
const STATE_CHUNK_BYTES = 600 * 1024;
const STATE_CHUNK_ITEMS = 100;

function compactTransferValue(value, key = '', depth = 0) {
  if (OMIT_TRANSFER_FIELD.test(String(key))) return undefined;
  if (typeof value === 'string') {
    if (/^data:application\/pdf(?:;[^,]*)?,/i.test(value)) return undefined;
    return value.length > MAX_TRANSFER_STRING_LENGTH ? value.slice(0, MAX_TRANSFER_STRING_LENGTH) : value;
  }
  if (value === null || typeof value === 'number' || typeof value === 'boolean') return value;
  if (depth >= 5) return undefined;
  if (Array.isArray(value)) {
    return value.map(item => compactTransferValue(item, '', depth + 1)).filter(item => item !== undefined);
  }
  if (value && typeof value === 'object') {
    const result = {};
    for (const [childKey, childValue] of Object.entries(value)) {
      if (childKey === '__proto__' || childKey === 'constructor' || childKey === 'prototype') continue;
      const compacted = compactTransferValue(childValue, childKey, depth + 1);
      if (compacted !== undefined) result[childKey] = compacted;
    }
    return result;
  }
  return undefined;
}

function stateChunk(items, rawOffset) {
  const source = Array.isArray(items) ? items : [];
  const offset = Math.max(0, Math.min(source.length, Number.parseInt(rawOffset, 10) || 0));
  const result = [];
  let size = 0;
  let index = offset;
  while (index < source.length && result.length < STATE_CHUNK_ITEMS) {
    const item = compactTransferValue(source[index]);
    const itemSize = Buffer.byteLength(JSON.stringify(item ?? null), 'utf8');
    if (result.length && size + itemSize > STATE_CHUNK_BYTES) break;
    result.push(item);
    size += itemSize;
    index += 1;
  }
  return { items: result, nextOffset: index < source.length ? index : null, total: source.length };
}

async function readDoc(id) {
  const result = await stateCollection.doc(id).get();
  return result.data?.[0] || null;
}

async function readState(actor) {
  const shared = await readDoc('shared-properties');
  const ownerIds = actor.role === 'admin' ? Object.keys(FIXED_USERS) : [actor._id];
  const ownerDocs = await Promise.all(ownerIds.map(id => readDoc(`user-${id}`)));
  const projects = ownerDocs.flatMap(item => Array.isArray(item?.projects) ? item.projects : []);
  let payments = ownerDocs.flatMap(item => Array.isArray(item?.payments) ? item.payments : []);
  if (actor.role !== 'admin') {
    const projectIds = new Set(projects.map(item => item.id).filter(Boolean));
    const allOwnerDocs = await Promise.all(Object.keys(FIXED_USERS).map(id => readDoc(`user-${id}`)));
    payments = allOwnerDocs
      .flatMap(item => Array.isArray(item?.payments) ? item.payments : [])
      .filter(item => item.createdBy === actor._id || paymentParts(item).some(part => projectIds.has(part.projectId)))
      .map(item => {
        // 一笔付款跨业务员项目时，仅返回当前账号可见的关联明细。
        const allocations = paymentParts(item).filter(part => projectIds.has(part.projectId));
        return { ...item, projectId: projectIds.has(item.projectId) ? item.projectId : '', allocations };
      });
  }
  const revisions = { shared: Number(shared?.revision || 0) };
  ownerIds.forEach((id, index) => { revisions[id] = Number(ownerDocs[index]?.revision || 0); });
  return {
    projects,
    payments,
    properties: Array.isArray(shared?.properties) ? shared.properties : [],
    settings: shared?.settings || { reminderDays: 30 },
    _revisions: revisions,
  };
}

function paymentParts(payment) {
  return Array.isArray(payment.allocations) ? payment.allocations : payment.projectId ? [{ projectId: payment.projectId, amount: Number(payment.amount) }] : [];
}

function cleanRecord(record) {
  const result = {};
  for (const [key, value] of Object.entries(record || {})) {
    if (key === '__proto__' || key === 'constructor' || key === 'prototype') continue;
    if (typeof value !== 'function' && value !== undefined) result[key] = value;
  }
  return result;
}

async function saveState(actor, incomingValue, expectedRevisions = {}) {
  const incoming = normalizeState(incomingValue);
  const now = Date.now();
  const ownerIds = actor.role === 'admin' ? Object.keys(FIXED_USERS) : [actor._id];
  const sharedCurrent = await readDoc('shared-properties');
  const ownerCurrent = await Promise.all(ownerIds.map(id => readDoc(`user-${id}`)));
  const actual = { shared: Number(sharedCurrent?.revision || 0) };
  ownerIds.forEach((id, index) => { actual[id] = Number(ownerCurrent[index]?.revision || 0); });
  const sharedBefore = { properties: Array.isArray(sharedCurrent?.properties) ? sharedCurrent.properties : [], settings: sharedCurrent?.settings || { reminderDays: 30 } };
  const sharedNext = actor.role === 'admin'
    ? { properties: incoming.properties.map(cleanRecord), settings: cleanRecord(incoming.settings) }
    : sharedBefore;
  const ownerNext = {};
  const ownerBefore = {};
  for (const ownerId of ownerIds) {
    const current = ownerCurrent[ownerIds.indexOf(ownerId)];
    ownerBefore[ownerId] = { projects: Array.isArray(current?.projects) ? current.projects : [], payments: Array.isArray(current?.payments) ? current.payments : [] };
    const belongsToOwner = item => actor.role !== 'admin' || (FIXED_USERS[item.createdBy] ? item.createdBy : 'admin') === ownerId;
    const currentProjectsById = new Map(ownerBefore[ownerId].projects.map(item => [item.id, item]));
    const protectPaymentFields = item => {
      const cleaned = cleanRecord(item);
      if (actor.role === 'admin') return cleaned;
      const previous = currentProjectsById.get(item.id) || {};
      return {
        ...cleaned,
        receivedAmount: Number(previous.receivedAmount || 0),
        paymentDate: String(previous.paymentDate || ''),
        paymentCounterparty: String(previous.paymentCounterparty || ''),
        paymentMethod: String(previous.paymentMethod || ''),
      };
    };
    ownerNext[ownerId] = {
      projects: incoming.projects.filter(belongsToOwner).map(item => ({ ...protectPaymentFields(item), createdBy: ownerId })),
      payments: actor.role === 'admin'
        ? incoming.payments.filter(belongsToOwner).map(item => ({ ...cleanRecord(item), createdBy: ownerId }))
        : ownerBefore[ownerId].payments,
    };
  }
  const changedKeys = [];
  if (JSON.stringify(sharedNext) !== JSON.stringify(sharedBefore)) changedKeys.push('shared');
  for (const ownerId of ownerIds) if (JSON.stringify(ownerNext[ownerId]) !== JSON.stringify(ownerBefore[ownerId])) changedKeys.push(ownerId);
  if (changedKeys.some(key => Number(expectedRevisions[key] || 0) !== actual[key])) {
    throw new HttpError(409, '检测到其他账号刚刚更新了数据。请刷新页面获取最新内容后再操作');
  }
  const nextRevisions = { ...actual };
  if (changedKeys.includes('shared')) {
    nextRevisions.shared = actual.shared + 1;
    await stateCollection.doc('shared-properties').set({
      ...sharedNext, updatedAt: now, updatedBy: actor._id, revision: nextRevisions.shared,
    });
  }
  await Promise.all(ownerIds.filter(ownerId => changedKeys.includes(ownerId)).map(ownerId => {
      nextRevisions[ownerId] = actual[ownerId] + 1;
      return stateCollection.doc(`user-${ownerId}`).set({
        ...ownerNext[ownerId], updatedAt: now, updatedBy: actor._id, revision: nextRevisions[ownerId],
      });
    }));
  return { saved: true, updatedAt: now, revisions: nextRevisions };
}

async function findAccessibleProject(actor, projectId) {
  const state = await readState(actor);
  return state.projects.find(item => item.id === projectId) || null;
}

function propertyImportKey(item) {
  const deviceCode = String(item?.deviceCode || '').trim();
  if (deviceCode) return `code:${deviceCode}`;
  return `unit:${String(item?.unit || '').trim()}|${String(item?.internalNo || '').trim()}`;
}

async function importProperties(actor, records, expectedRevision = 0) {
  if (!Array.isArray(records) || !records.length) throw new HttpError(400, '没有可导入的物业设备数据');
  if (records.length > 10000) throw new HttpError(413, '单次导入物业设备不能超过 10000 条');
  const sharedCurrent = await readDoc('shared-properties');
  const actualRevision = Number(sharedCurrent?.revision || 0);
  if (Number(expectedRevision || 0) !== actualRevision) throw new HttpError(409, '预警台账刚刚被其他账号更新，请刷新后重新导入');
  const properties = Array.isArray(sharedCurrent?.properties) ? sharedCurrent.properties.map(cleanRecord) : [];
  let added = 0;
  for (const raw of records) {
    const property = cleanRecord(raw);
    property.unit = String(property.unit || '').trim();
    property.deviceCode = String(property.deviceCode || '').trim();
    property.internalNo = String(property.internalNo || '').trim();
    if (!property.unit || (!property.deviceCode && !property.internalNo)) continue;
    const key = propertyImportKey(property);
    const index = properties.findIndex(item => propertyImportKey(item) === key);
    if (index >= 0) {
      const previous = properties[index];
      properties[index] = {
        ...previous,
        ...property,
        id: previous.id,
        notes: property.notes || previous.notes || '',
        followUpStatus: previous.followUpStatus || '待跟进',
        importedBy: actor._id,
      };
    } else {
      properties.push({ ...property, importedBy: actor._id, followUpStatus: property.followUpStatus || '待跟进' });
      added++;
    }
  }
  const revision = actualRevision + 1;
  await stateCollection.doc('shared-properties').set({
    properties,
    settings: sharedCurrent?.settings || { reminderDays: 30 },
    updatedAt: Date.now(), updatedBy: actor._id, revision,
  });
  return { properties, added, revision };
}

async function handleAction(event, body) {
  await ensureFixedUsers();
  if (body.action === 'login') {
    const user = await findUserByUsername(body.username);
    if (!user) throw new HttpError(401, '账号或密码错误');
    if (Number(user.lockedUntil || 0) > Date.now()) {
      const minutes = Math.ceil((Number(user.lockedUntil) - Date.now()) / 60000);
      throw new HttpError(423, `密码错误次数过多，请 ${minutes} 分钟后再试`);
    }
    if (!verifyPassword(body.password, user)) {
      const failed = Number(user.failedLoginCount || 0) + 1;
      const lockedUntil = failed >= MAX_LOGIN_FAILURES ? Date.now() + LOCK_MINUTES * 60000 : 0;
      await usersCollection.doc(user._id).update({ failedLoginCount: failed >= MAX_LOGIN_FAILURES ? 0 : failed, lockedUntil, updatedAt: Date.now() });
      throw new HttpError(401, failed >= MAX_LOGIN_FAILURES ? `密码错误次数过多，账号已锁定 ${LOCK_MINUTES} 分钟` : `账号或密码错误，还可尝试 ${MAX_LOGIN_FAILURES - failed} 次`);
    }
    await usersCollection.doc(user._id).update({ failedLoginCount: 0, lockedUntil: 0, lastLoginAt: Date.now(), updatedAt: Date.now() });
    const previousSessions = await sessionsCollection.where({ userId: user._id }).get();
    await Promise.all((previousSessions.data || []).filter(item => Number(item.expiresAt) <= Date.now()).map(item => sessionsCollection.doc(item._id).remove()));
    const token = crypto.randomBytes(32).toString('hex');
    const expiresAt = Date.now() + SESSION_HOURS * 60 * 60 * 1000;
    await sessionsCollection.doc(tokenHash(token)).set({ userId: user._id, createdAt: Date.now(), expiresAt });
    return { token, expiresAt, user: safeUser(user) };
  }

  const actor = await requireActor(event);
  if (actor.mustChangePassword && !['me', 'logout', 'changePassword'].includes(body.action)) {
    throw new HttpError(403, '首次登录后必须先修改初始密码');
  }
  if (body.action === 'me') return { user: safeUser(actor) };
  if (body.action === 'logout') {
    const token = bearerToken(event);
    if (token) await sessionsCollection.doc(tokenHash(token)).remove().catch(() => {});
    return { loggedOut: true };
  }
  if (body.action === 'loadMeta') {
    const state = await readState(actor);
    return {
      settings: compactTransferValue(state.settings) || { reminderDays: 30 },
      _revisions: state._revisions,
      counts: {
        projects: state.projects.length,
        properties: state.properties.length,
        payments: state.payments.length,
      },
    };
  }
  if (body.action === 'loadChunk') {
    if (!['projects', 'properties', 'payments'].includes(body.section)) throw new HttpError(400, '分批读取的数据类型无效');
    const state = await readState(actor);
    return stateChunk(state[body.section], body.offset);
  }
  if (body.action === 'load') return readState(actor);
  if (body.action === 'save') return saveState(actor, body.state, body.revisions || {});
  if (body.action === 'importProperties') return importProperties(actor, body.properties, body.expectedRevision);

  if (body.action === 'changePassword') {
    if (!verifyPassword(body.currentPassword, actor)) throw new HttpError(400, '当前密码不正确');
    if (!passwordPolicy(body.newPassword)) throw new HttpError(400, '新密码至少 8 位，且必须同时包含字母和数字');
    if (verifyPassword(body.newPassword, actor)) throw new HttpError(400, '新密码不能与当前密码相同');
    const password = hashPassword(body.newPassword);
    await usersCollection.doc(actor._id).update({ passwordHash: password.hash, passwordSalt: password.salt, mustChangePassword: false, updatedAt: Date.now() });
    await revokeUserSessions(actor._id);
    return { changed: true, requiresRelogin: true };
  }

  if (body.action === 'listUsers') {
    if (actor.role !== 'admin') throw new HttpError(403, '只有总管理员可以管理账号');
    const users = await Promise.all(Object.keys(FIXED_USERS).map(async id => (await usersCollection.doc(id).get()).data?.[0]));
    return { users: users.filter(Boolean).map(safeUser) };
  }

  if (body.action === 'resetPassword') {
    if (actor.role !== 'admin') throw new HttpError(403, '只有总管理员可以重置密码');
    if (!FIXED_USERS[body.targetUserId]) throw new HttpError(400, '只能管理系统预设的 3 个账号');
    if (!passwordPolicy(body.newPassword)) throw new HttpError(400, '新密码至少 8 位，且必须同时包含字母和数字');
    const password = hashPassword(body.newPassword);
    await usersCollection.doc(body.targetUserId).update({ passwordHash: password.hash, passwordSalt: password.salt, mustChangePassword: true, failedLoginCount: 0, lockedUntil: 0, updatedAt: Date.now() });
    await revokeUserSessions(body.targetUserId);
    return { reset: true };
  }

  if (body.action === 'uploadAttachment') {
    if (!/^application\/pdf(?:$|;)/i.test(body.mimeType || '') && !/\.pdf$/i.test(body.fileName || '')) throw new HttpError(400, '只允许上传 PDF 文件');
    const fileContent = Buffer.from(String(body.base64 || ''), 'base64');
    if (!fileContent.length) throw new HttpError(400, 'PDF 文件为空');
    if (fileContent.length > MAX_PDF_BYTES) throw new HttpError(413, 'PDF 超过 1MB；请压缩后重新导入');
    const safeName = String(body.fileName || 'document.pdf').replace(/[^\w\u4e00-\u9fa5.-]+/g, '_').slice(-100);
    const cloudPath = `ledger-pdf/${actor._id}/${Date.now()}-${crypto.randomBytes(6).toString('hex')}-${safeName}`;
    const uploaded = await uniCloud.uploadFile({ cloudPath, fileContent });
    return { fileID: uploaded.fileID, fileName: safeName };
  }

  if (body.action === 'getAttachmentUrl') {
    const project = await findAccessibleProject(actor, body.projectId);
    if (!project) throw new HttpError(404, '未找到有权访问的项目');
    if (!project.attachmentFileId) throw new HttpError(404, '该项目没有云端 PDF');
    const result = await uniCloud.getTempFileURL({ fileList: [project.attachmentFileId] });
    const item = result.fileList?.[0] || {};
    if (item.code && item.code !== 0) throw new HttpError(500, item.message || '生成 PDF 临时地址失败');
    return { url: item.tempFileURL || item.download_url || item.fileID };
  }

  if (body.action === 'getAttachmentContent') {
    const project = await findAccessibleProject(actor, body.projectId);
    if (!project) throw new HttpError(404, '未找到有权访问的项目');
    if (!project.attachmentFileId) throw new HttpError(404, '该项目没有云端 PDF');
    const result = await uniCloud.getTempFileURL({ fileList: [project.attachmentFileId] });
    const item = result.fileList?.[0] || {};
    if (item.code && item.code !== 0) throw new HttpError(502, item.message || '生成 PDF 临时地址失败');
    const url = item.tempFileURL || item.download_url || item.fileID;
    if (!url) throw new HttpError(502, '云存储没有返回 PDF 地址');
    const buffer = await fetchCloudFile(url);
    return { attachmentBuffer: buffer, attachmentName: project.attachmentName || 'document.pdf' };
  }

  if (body.action === 'deleteAttachment') {
    const project = await findAccessibleProject(actor, body.projectId);
    if (!project) throw new HttpError(404, '未找到有权访问的项目');
    if (project.attachmentFileId) await uniCloud.deleteFile({ fileList: [project.attachmentFileId] });
    return { deleted: true };
  }

  throw new HttpError(400, '不支持的 action');
}

exports.main = async event => {
  const origin = originFor(event);
  if (!isAllowedOrigin(origin)) return httpResponse(event, 403, { code: 403, message: '当前网页来源不在允许名单中' });
  if (event.httpMethod === 'OPTIONS') return httpResponse(event, 200, {});
  try {
    const body = parseBody(event);
    const data = await handleAction(event, body);
    if (data?.attachmentBuffer) return attachmentResponse(event, data.attachmentBuffer, data.attachmentName);
    return httpResponse(event, 200, { code: 0, data });
  } catch (error) {
    const status = error instanceof HttpError ? error.status : 500;
    if (status === 500) console.error(error);
    return httpResponse(event, status, { code: status, message: status === 500 ? '服务器内部错误' : error.message });
  }
};
