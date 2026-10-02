import * as pdfjsLib from './vendor/pdf.min.mjs';
import { CLOUD_CONFIG } from './cloud-config.js';
import { createCloudApi } from './cloud-api.js';
import { moneyCents, paymentAllocations, allocatedCents, isPaymentMatched, associatePayment, removePayment, detachProjectPayments, validInspectionDate } from './ledger-data.js';

import pdfWorkerUrl from './vendor/pdf.worker.min.mjs?url';
import JSZip from 'jszip';

// H5 DOM is mounted before creating the ledger runtime. Each mount owns its listeners/state.
export function mountLedger(root) {
pdfjsLib.GlobalWorkerOptions.workerSrc = pdfWorkerUrl;

const STORAGE_KEY = 'zsh-elevator-ledger-v1';
const DB_NAME = 'zsh-elevator-ledger-files';
const DB_STORE = 'attachments';
const TODAY = new Date();
const cloudApi = createCloudApi(CLOUD_CONFIG);
const USER_LABELS = Object.freeze({ admin: '总管理员', operator1: '业务员1', operator2: '业务员2' });

const LEDGER_DEFINITIONS = Object.freeze({
  detection: { title: '检测台账', sheetTitle: '中晟辉电梯检测台账', nature: '自行检测' },
  inspection: { title: '检验台账', sheetTitle: '中晟辉电梯检验台账', nature: '定期检验', provincialPayment: true },
  speedGovernor: { title: '限速器台账', sheetTitle: '中晟辉限速器校验台账', nature: '限速器校验', aliases: ['限速器'] },
  loadTest: { title: '125%额定载重试验', sheetTitle: '中晟辉125%额定载重试验台账', nature: '125%额定载重试验' },
});

const pageTitles = {
  dashboard: '工作概览',
  ledger: '检测台账',
  reminders: '到期提醒',
  alerts: '预警台账',
  payments: '到款记录',
  data: '系统管理',
};

const demoProjects = [
  {
    id: 'demo-jinke', date: '2026-09-17', unit: '吉林金科陶瓷有限公司', projectName: '/',
    address: '吉林省长春市二道区东环城路与四通路交汇', maintainer: '吉林省恒拓机电安装工程有限公司',
    contact: '/', phone: '18946700826', nature: '自行检测', dueDate: '2027-09-01', equipment: '乘客电梯',
    controlMode: '集选', floors: '2/2', quantity: 1, unitPrice: 535.8, total: 535.8,
    receivable: 535.8, actual: 150, receivedAmount: 0, owner: '冯一', inspector: '', notes: 'PDF 受理单导入示例',
    attachmentName: '恒拓检测1台-吉林金科陶瓷有限公司-9月17日.pdf', attachmentUrl: `${import.meta.env.BASE_URL}static/sample/恒拓检测1台-吉林金科陶瓷有限公司-9月17日.pdf`,
    source: 'PDF', createdBy: 'operator1', createdAt: '2026-09-17T10:49:00.000Z'
  },
  {
    id: 'demo-hongxi', date: '2026-09-18', unit: '吉林省鸿禧沛泽养老护理服务有限公司', projectName: '电梯定期检验',
    address: '吉林省长春市', maintainer: '吉林省西继迅达电梯工程有限公司', contact: '业务联系人', phone: '',
    nature: '定期检验', dueDate: '2027-09-01', equipment: '乘客电梯', controlMode: '集选', floors: '5/5',
    quantity: 2, unitPrice: 535.8, total: 1071.6, receivable: 1071.6, actual: 600, receivedAmount: 1071.6,
    owner: '冯一', inspector: '', notes: '银行短信已自动确款', source: '手动', createdBy: 'operator2', createdAt: '2026-09-18T09:15:00.000Z'
  },
  {
    id: 'demo-quanyu', date: '2026-09-18', unit: '长春市权宇物业服务有限公司', projectName: '物业电梯年度检测',
    address: '吉林省长春市宽城区兰家镇姜家村', maintainer: '吉林省泰永机电设备安装有限公司', contact: '刘月', phone: '15144023300',
    nature: '定期检验', dueDate: '2027-02-02', equipment: '曳引驱动乘客电梯', controlMode: '/', floors: '/',
    quantity: 5, unitPrice: 535.8, total: 2679, receivable: 2679, actual: 0, receivedAmount: 0,
    owner: '待分配', inspector: '', notes: '手动录入示例', source: '手动', createdBy: 'operator1', createdAt: '2026-09-18T11:20:00.000Z'
  }
];

const demoPayments = [
  {
    id: 'payment-demo-1', accountSuffix: '9868', date: '2026-09-18', amount: 1071.6,
    payer: '吉林省鸿禧沛泽养老护理服务有限公司', bank: '中国银行', projectId: 'demo-hongxi',
    status: '已确款', createdBy: 'operator2', raw: '您公司账户9868,于09月18日收入人民币1071.60,对方为吉林省鸿禧沛泽养老护理服务有限公司【中国银行】',
    createdAt: '2026-09-18T09:20:00.000Z'
  }
];

let state = emptyState();
let activeView = 'ledger';
let importingFiles = false;
let activeLedgerKind = 'detection';
let currentPdfObjectUrl = null;
let currentUserInfo = null;
let currentUserId = '';
let cloudSyncTimer = null;
let cloudRevisions = {};
let passwordChangeRequired = false;
let managedUsers = [];
const selectedPropertyIds = new Set();
const PROPERTY_EDIT_FIELDS = [
  ['unit', '使用单位'], ['equipmentType', '设备品种'], ['deviceCode', '设备代码'],
  ['internalNo', '单位内编号'], ['dueDate', '下次检验日期', 'date'], ['registrationNo', '登记证编号'],
  ['inspectionAgency', '检测机构'], ['address', '单位地址'], ['maintainer', '维保单位'],
  ['emergencyPhone', '应急电话', 'tel'], ['contact', '联系人'], ['phone', '联系电话', 'tel'],
  ['phone1', '联系电话1', 'tel'], ['source', '数据来源'],
];

function emptyState() { return { projects: [], properties: [], payments: [], settings: { reminderDays: 30 } }; }

function projectContactInfo(project = {}) {
  return String(project.contactInfo || [project.contact, project.phone].filter(Boolean).join(' ')).trim();
}

function normalizeProjectRecord(project = {}) {
  return {
    ...project,
    supervision: String(project.supervision || project.supervise || ''),
    contact: String(project.contact || ''),
    phone: String(project.phone || ''),
    contactInfo: projectContactInfo(project),
    inspector: String(project.inspector || ''),
    zshActualPrice: numberValue(project.zshActualPrice),
    receivedAmount: numberValue(project.receivedAmount),
    paymentDate: String(project.paymentDate || ''),
    paymentCounterparty: String(project.paymentCounterparty || ''),
    paymentMethod: String(project.paymentMethod || ''),
    invoice: String(project.invoice || ''),
    report: String(project.report || ''),
    refundAmount: numberValue(project.refundAmount),
    provincialPayment: numberValue(project.provincialPayment),
    followUpStatus: String(project.followUpStatus || '待跟进'),
    notes: String(project.notes || ''),
  };
}

function normalizePropertyRecord(property = {}) {
  return {
    ...property,
    phone: String(property.phone || ''),
    phone1: String(property.phone1 || ''),
    notes: String(property.notes || ''),
    followUpStatus: String(property.followUpStatus || '待跟进'),
  };
}

function normalizeAppState(value = {}) {
  return {
    projects: Array.isArray(value.projects) ? value.projects.map(normalizeProjectRecord) : [],
    properties: Array.isArray(value.properties) ? value.properties.map(normalizePropertyRecord) : [],
    payments: Array.isArray(value.payments) ? value.payments : [],
    settings: { reminderDays: 30, ...(value.settings || {}) },
  };
}

function cacheKey() { return `${STORAGE_KEY}:${currentUserId || 'signed-out'}`; }

function loadState() {
  if (!currentUserId) return emptyState();
  try {
    const saved = JSON.parse(localStorage.getItem(cacheKey()));
    if (saved && Array.isArray(saved.projects)) {
      const projects = saved.projects.map(project => ({ ...project, createdBy: project.createdBy || (project.id === 'demo-hongxi' ? 'operator2' : project.id?.startsWith('demo-') ? 'operator1' : 'admin') }));
      const payments = (saved.payments || []).map(payment => ({ ...payment, createdBy: payment.createdBy || (payment.id?.startsWith('payment-demo-') ? 'operator2' : 'admin') }));
      return normalizeAppState({ projects, properties: saved.properties || [], payments, settings: saved.settings || { reminderDays: 30 } });
    }
  } catch (error) {
    console.warn('读取本地数据失败', error);
  }
  return emptyState();
}

function saveState() {
  if (!currentUserId) return;
  localStorage.setItem(cacheKey(), JSON.stringify(state));
  scheduleCloudSave();
}

function currentUser() { return currentUserInfo || { id: '', name: '未登录', role: 'operator', permissions: {} }; }
function userName(id) { return managedUsers.find(user => user.id === id)?.name || USER_LABELS[id] || '总管理员'; }
function isAdmin() { return currentUser().role === 'admin'; }
function canExport() { return Boolean(currentUser().permissions?.canExport); }
function canEditManualFields() { return isAdmin(); }
function canModifyAlerts() { return isAdmin(); }
function canRecordPayments() { return isAdmin() && currentUser().permissions?.canRecordPayments !== false; }
function canImportPropertyExcel() { return Boolean(currentUserId); }
function projectCreatedBy(project) { return project.createdBy || 'admin'; }
function canAccessProject(project) { return isAdmin() || projectCreatedBy(project) === currentUserId; }
function visibleProjects() { return state.projects.filter(canAccessProject); }
function visiblePayments() {
  if (isAdmin()) return state.payments;
  const projectIds = new Set(visibleProjects().map(project => project.id));
  return state.payments.filter(payment => payment.createdBy === currentUserId || paymentAllocations(payment).some(item => projectIds.has(item.projectId)));
}

function setCloudStatus(title, text, connected = false) {
  document.querySelector('#storageStatusTitle').textContent = title;
  document.querySelector('#storageStatusText').textContent = text;
  document.querySelector('.local-dot').classList.toggle('cloud-connected', connected);
}

function scheduleCloudSave() {
  if (!cloudApi.isConfigured || !currentUserId) return;
  clearTimeout(cloudSyncTimer);
  setCloudStatus('等待云同步', '本机已保存，正在排队上传');
  cloudSyncTimer = setTimeout(async () => {
    try {
      const saved = await cloudApi.save(state, cloudRevisions);
      cloudRevisions = saved.revisions || cloudRevisions;
      setCloudStatus('云端已同步', `${currentUser().name} · 刚刚保存`, true);
    } catch (error) {
      setCloudStatus('云同步失败', error.message);
      showToast(`云同步失败：${error.message}；请勿关闭网页，检查网络后重试`, true);
    }
  }, CLOUD_CONFIG.syncDelayMs || 700);
}

async function hydrateFromCloud() {
  if (!cloudApi.isConfigured || !currentUserId) return;
  setCloudStatus('连接云端', '正在读取云数据库…');
  try {
    const cloudState = await cloudApi.load();
    if (cloudState?.projects && Array.isArray(cloudState.projects)) {
      cloudRevisions = cloudState._revisions || {};
      state = normalizeAppState({ projects: cloudState.projects, properties: cloudState.properties || [], payments: cloudState.payments || [], settings: cloudState.settings || { reminderDays: 30 } });
      localStorage.setItem(cacheKey(), JSON.stringify(state));
      renderAll();
    }
    setCloudStatus('云端已连接', `${currentUser().name} · 数据已加载`, true);
  } catch (error) {
    state = loadState();
    renderAll();
    setCloudStatus('云端连接失败', '显示本机缓存，请检查网络');
    showToast(`读取云数据失败：${error.message}`, true);
  }
}

function setAuthenticatedUser(user) {
  currentUserInfo = user;
  currentUserId = user.id;
  passwordChangeRequired = Boolean(user.mustChangePassword);
  state = loadState();
  document.querySelector('#loginScreen').hidden = true;
  document.querySelector('#appShell').hidden = false;
  renderAll();
}

function showLogin(message = '账号由管理员预设；首次登录后必须修改密码。', isError = false) {
  selectedPropertyIds.clear();
  clearTimeout(cloudSyncTimer);
  currentUserInfo = null;
  currentUserId = '';
  state = emptyState();
  managedUsers = [];
  cloudRevisions = {};
  document.querySelector('#appShell').hidden = true;
  document.querySelector('#loginScreen').hidden = false;
  const target = document.querySelector('#loginMessage');
  target.textContent = message;
  target.classList.toggle('error', isError);
}

async function initializeAuth() {
  const warning = document.querySelector('#configWarning');
  warning.hidden = cloudApi.isConfigured;
  document.querySelector('#loginForm button').disabled = !cloudApi.isConfigured;
  if (!cloudApi.isConfigured) return showLogin('部署完成并填写云函数 URL 后即可登录。', true);
  if (!cloudApi.hasToken()) return showLogin();
  try {
    const data = await cloudApi.me();
    setAuthenticatedUser(data.user);
    if (passwordChangeRequired) {
      openPasswordModal(true);
      return;
    }
    await hydrateFromCloud();
    if (isAdmin()) await loadManagedUsers();
  } catch (error) {
    showLogin(error.message || '登录已失效，请重新登录', true);
  }
}

async function loadManagedUsers() {
  if (!isAdmin()) return;
  try {
    managedUsers = (await cloudApi.listUsers()).users || [];
    renderAccountList();
  } catch (error) { showToast(`读取账号信息失败：${error.message}`, true); }
}

function openPasswordModal(force = false) {
  passwordChangeRequired = force || passwordChangeRequired;
  const form = document.querySelector('#passwordForm');
  form.reset();
  document.querySelector('#passwordTitle').textContent = passwordChangeRequired ? '首次登录，请修改密码' : '修改登录密码';
  document.querySelector('#passwordHelp').textContent = passwordChangeRequired ? '为保护账号安全，修改初始密码后才能继续使用。新密码至少 8 位，必须同时包含字母和数字。' : '新密码至少 8 位，必须同时包含字母和数字。保存后需要重新登录。';
  document.querySelector('#passwordClose').hidden = passwordChangeRequired;
  document.querySelector('#passwordCancel').hidden = passwordChangeRequired;
  openModal('passwordModal');
}

async function signOut(message = '已安全退出') {
  try { await cloudApi.logout(); } catch (error) { console.warn('云端退出失败', error); }
  closeModal('passwordModal', true);
  showLogin(message);
}

function openFilesDb() {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, 1);
    request.onupgradeneeded = () => {
      const db = request.result;
      if (!db.objectStoreNames.contains(DB_STORE)) db.createObjectStore(DB_STORE, { keyPath: 'id' });
    };
    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
  });
}

async function putAttachment(file) {
  const db = await openFilesDb();
  const id = `file-${crypto.randomUUID()}`;
  await new Promise((resolve, reject) => {
    const tx = db.transaction(DB_STORE, 'readwrite');
    tx.objectStore(DB_STORE).put({ id, name: file.name, type: file.type, blob: file, savedAt: new Date().toISOString() });
    tx.oncomplete = resolve;
    tx.onerror = () => reject(tx.error);
  });
  db.close();
  return id;
}

async function getAttachment(id) {
  const db = await openFilesDb();
  const result = await new Promise((resolve, reject) => {
    const tx = db.transaction(DB_STORE, 'readonly');
    const request = tx.objectStore(DB_STORE).get(id);
    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
  });
  db.close();
  return result;
}

async function deleteAttachment(id) {
  if (!id) return;
  const db = await openFilesDb();
  await new Promise((resolve, reject) => {
    const tx = db.transaction(DB_STORE, 'readwrite');
    tx.objectStore(DB_STORE).delete(id);
    tx.oncomplete = resolve;
    tx.onerror = () => reject(tx.error);
  });
  db.close();
}

async function clearAttachments() {
  const db = await openFilesDb();
  await new Promise((resolve, reject) => {
    const tx = db.transaction(DB_STORE, 'readwrite');
    tx.objectStore(DB_STORE).clear();
    tx.oncomplete = resolve;
    tx.onerror = () => reject(tx.error);
  });
  db.close();
}

const yuan = value => `¥${Number(value || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
const safe = value => String(value ?? '').replace(/[&<>'"]/g, char => ({ '&':'&amp;', '<':'&lt;', '>':'&gt;', "'":'&#39;', '"':'&quot;' }[char]));
const numberValue = value => {
  if (typeof value === 'number') return Number.isFinite(value) ? value : 0;
  const parsed = Number(String(value ?? '').replace(/[,，￥¥元\s]/g, ''));
  return Number.isFinite(parsed) ? parsed : 0;
};

function formatDate(value, monthOnly = false) {
  if (!value) return '—';
  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) return String(value);
  return monthOnly ? `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}` : `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}

function formatChineseMonth(value) {
  const text = String(value || '').trim();
  if (!text) return '';
  const matched = text.match(/^(\d{4})[-/.年](\d{1,2})/);
  if (matched) return `${matched[1]}年${matched[2].padStart(2, '0')}月`;
  const date = new Date(text);
  if (Number.isNaN(date.getTime())) return text;
  return `${date.getFullYear()}年${String(date.getMonth() + 1).padStart(2, '0')}月`;
}

function daysUntil(value) {
  if (!value) return Infinity;
  const date = new Date(`${value}T00:00:00`);
  const today = new Date(TODAY.getFullYear(), TODAY.getMonth(), TODAY.getDate());
  return Math.ceil((date - today) / 86400000);
}

function paymentStatus(project) {
  // 四类台账统一以实收金额作为确款标准，不改动实际到款数字。
  const actual = numberValue(project.actual);
  const received = numberValue(project.receivedAmount);
  if (actual > 0 && received >= actual - 0.005) return '已确款';
  if (received > 0) return '部分到账';
  return '待收款';
}

function statusTag(status) {
  const color = ['已确款','已完成'].includes(status) ? 'green' : ['部分到账','跟进中'].includes(status) ? 'amber' : ['已逾期'].includes(status) ? 'red' : 'blue';
  return `<span class="status-tag ${color}">${safe(status)}</span>`;
}

function uniqueId(prefix = 'id') { return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`; }

function showToast(message, isError = false) {
  const item = document.createElement('div');
  item.className = `toast${isError ? ' error' : ''}`;
  item.textContent = message;
  document.querySelector('#toastStack').append(item);
  setTimeout(() => item.remove(), 4200);
}

function openModal(id) {
  const modal = document.getElementById(id);
  modal.hidden = false;
  document.body.style.overflow = 'hidden';
}

function closeModal(id, force = false) {
  if (id === 'passwordModal' && passwordChangeRequired && !force) return;
  const modal = document.getElementById(id);
  modal.hidden = true;
  if (id === 'pdfModal') {
    if (currentPdfObjectUrl) URL.revokeObjectURL(currentPdfObjectUrl);
    currentPdfObjectUrl = null;
    const frame = document.querySelector('#pdfFrame');
    frame.src = 'about:blank';
    frame.hidden = true;
    const pages = document.querySelector('#pdfPages');
    pages.hidden = true;
    pages.innerHTML = '';
    const loading = document.querySelector('#pdfLoading');
    loading.hidden = true;
    loading.classList.remove('error');
    for (const id of ['pdfOpenExternal','pdfDownload']) {
      const link = document.getElementById(id);
      link.hidden = true;
      link.removeAttribute('href');
    }
  }
  if (![...document.querySelectorAll('.modal-backdrop')].some(item => !item.hidden)) document.body.style.overflow = '';
}

function switchView(view, ledgerKind = '') {
  if (view === 'ledger' && LEDGER_DEFINITIONS[ledgerKind]) activeLedgerKind = ledgerKind;
  activeView = view;
  document.querySelector('#ledgerDropHint').hidden = view !== 'ledger' || activeLedgerKind !== 'detection';
  root.classList.remove('is-file-dragging');
  document.querySelectorAll('.nav-item').forEach(item => {
    const sameView = item.dataset.view === view;
    const sameLedger = view !== 'ledger' || item.dataset.ledgerKind === activeLedgerKind;
    item.classList.toggle('active', sameView && sameLedger);
  });
  document.querySelectorAll('.view').forEach(item => item.classList.toggle('active', item.id === `view-${view}`));
  document.querySelector('#pageTitle').textContent = view === 'ledger' ? LEDGER_DEFINITIONS[activeLedgerKind].title : pageTitles[view];
  renderAll();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function renderAll() {
  renderUserContext();
  renderDashboard();
  renderLedger();
  renderReminders();
  renderProperties();
  renderPayments();
}

function renderUserContext() {
  document.querySelector('#currentUserName').textContent = currentUser().name;
  document.querySelector('#currentUserRole').textContent = isAdmin() ? '总管理员 · 可查看和管理全部数据' : '业务账号 · 到款记录及预警只读';
  document.querySelectorAll('.admin-only').forEach(item => { item.hidden = !isAdmin(); });
  document.querySelectorAll('.export-only').forEach(item => { item.hidden = !canExport(); });
  document.querySelector('#propertyReminderMonths').disabled = !canModifyAlerts();
  renderAccountList();
}

function renderAccountList() {
  const target = document.querySelector('#accountList');
  if (!target || !isAdmin()) return;
  target.innerHTML = managedUsers.length ? managedUsers.map(user => `<div class="account-row"><div class="account-main"><strong>${safe(user.name)}</strong><span>登录账号：${safe(user.username)}</span></div><div class="account-meta"><span class="security-chip">${user.role === 'admin' ? '全部项目 / 可导出' : '本人项目 / 禁止导出'}</span>${user.mustChangePassword ? '<span class="security-chip warn">待修改初始密码</span>' : '<span class="security-chip">密码已修改</span>'}</div><button class="button button-quiet" data-reset-password="${safe(user.id)}">重置密码</button></div>`).join('') : '<div class="empty-state">正在读取账号信息…</div>';
}

function renderDashboard() {
  const projects = visibleProjects();
  const properties = state.properties;
  const totalReceivable = projects.reduce((sum, item) => sum + numberValue(item.receivable), 0);
  const totalReceived = projects.reduce((sum, item) => sum + numberValue(item.receivedAmount), 0);
  const pending = Math.max(0, totalReceivable - totalReceived);
  const due90 = [...projects.map(p => p.dueDate), ...properties.map(p => p.dueDate)].filter(date => { const d = daysUntil(date); return d >= 0 && d <= 90; }).length;
  const overdue = [...projects.map(p => p.dueDate), ...properties.map(p => p.dueDate)].filter(date => daysUntil(date) < 0).length;
  const metrics = [
    ['项目台账', projects.length, '个项目', ''],
    ['物业设备', properties.length, '台设备', ''],
    ['90 天内到期', due90, '项', 'warn'],
    ['已逾期', overdue, '项', 'accent'],
    ['待收金额', yuan(pending), '', pending === 0 ? 'good' : 'warn'],
  ];
  document.querySelector('#metricsGrid').innerHTML = metrics.map(([label, value, unit, cls]) => `<article class="metric-card ${cls}" style="--metric-tint:${cls === 'accent' ? '#fff0ef' : cls === 'warn' ? '#fff4dc' : '#e7f3f4'}"><p>${label}</p><strong>${safe(value)}<small>${unit}</small></strong></article>`).join('');

  const reminders = getReminderItems().slice(0, 4);
  document.querySelector('#attentionList').innerHTML = reminders.length ? reminders.map(item => {
    const due = new Date(`${item.dueDate}T00:00:00`);
    const days = daysUntil(item.dueDate);
    const label = days < 0 ? '已逾期' : days <= 30 ? `${days} 天内` : formatDate(item.dueDate, true);
    return `<div class="attention-item"><div class="attention-date"><strong>${String(due.getDate()).padStart(2,'0')}</strong><small>${due.getMonth()+1}月</small></div><div class="attention-copy"><strong>${safe(item.unit)}</strong><span>${safe(item.type)} · ${safe(item.meta || '待跟进')}</span></div>${statusTag(days < 0 ? '已逾期' : label)}</div>`;
  }).join('') : '<div class="empty-state">当前没有临近到期项目</div>';

  const monthText = `${TODAY.getFullYear()}年${TODAY.getMonth() + 1}月`;
  document.querySelector('#currentMonthChip').textContent = monthText;
  document.querySelector('#cashAmount').textContent = yuan(totalReceivable);
  document.querySelector('#receivedAmount').textContent = yuan(totalReceived);
  document.querySelector('#pendingAmount').textContent = yuan(pending);
  document.querySelector('#cashProgress').style.width = `${totalReceivable ? Math.min(100, totalReceived / totalReceivable * 100) : 0}%`;

  document.querySelector('#recentTable').innerHTML = [...projects].sort((a,b) => String(b.createdAt).localeCompare(String(a.createdAt))).slice(0,6).map(project => `<tr><td><span class="table-main">${safe(project.unit)}</span><span class="table-sub">${safe(project.projectName || project.address || '')}</span></td><td>${safe(project.nature || '—')}</td><td>${formatDate(project.date, true)}</td><td>${formatDate(project.dueDate, true)}</td><td class="money">${yuan(project.receivable)}</td><td>${statusTag(paymentStatus(project))}</td><td><button class="row-action" data-detail="${safe(project.id)}">查看</button></td></tr>`).join('');
}

function activeLedgerDefinition() { return LEDGER_DEFINITIONS[activeLedgerKind] || LEDGER_DEFINITIONS.detection; }

function projectMatchesLedger(project, definition = activeLedgerDefinition()) {
  return [definition.nature, ...(definition.aliases || [])].includes(project.nature);
}

function renderLedger() {
  const definition = activeLedgerDefinition();
  const query = document.querySelector('#ledgerSearch').value.trim().toLowerCase();
  const month = document.querySelector('#ledgerMonthFilter').value;
  const payment = document.querySelector('#paymentFilter').value;
  const followUp = document.querySelector('#followUpFilter').value;
  const ledgerProjects = visibleProjects().filter(project => projectMatchesLedger(project, definition));
  const monthRows = ledgerProjects.filter(project => !month || String(project.date || '').slice(0, 7) === month);
  const rows = monthRows.filter(project => {
    const text = [project.unit, project.projectName, project.contact, project.maintainer, project.owner, project.address, project.inspector, project.paymentCounterparty].join(' ').toLowerCase();
    return (!query || text.includes(query)) && (!payment || paymentStatus(project) === payment) && (!followUp || project.followUpStatus === followUp);
  }).sort((a,b) => String(b.date).localeCompare(String(a.date)));

  const total = key => monthRows.reduce((sum, item) => sum + numberValue(item[key]), 0);
  const monthLabel = month ? `${Number(month.slice(5, 7))}月` : '全部月份';
  const summary = [
    [`${monthLabel}${definition.title}项目数`, monthRows.length, '个项目'],
    [`${monthLabel}总台数`, total('quantity'), '台'],
    [`${monthLabel}应收金额`, yuan(total('receivable')), ''],
    [`${monthLabel}实收金额`, yuan(total('actual')), ''],
    [`${monthLabel}中晟辉到款金额`, yuan(total('receivedAmount')), ''],
    [`${monthLabel}中晟辉实收金额`, yuan(total('zshActualPrice')), ''],
    [`${monthLabel}中晟辉返款金额`, yuan(total('refundAmount')), ''],
  ];
  if (definition.provincialPayment && isAdmin()) summary.push([`${monthLabel}省市打款`, yuan(total('provincialPayment')), '']);
  const summaryTarget = document.querySelector('#ledgerSummary');
  summaryTarget.dataset.count = String(summary.length);
  summaryTarget.innerHTML = summary.map(([label, value, unit]) => `<article class="ledger-summary-card"><span>${safe(label)}</span><strong>${safe(value)}</strong>${unit ? `<small>${safe(unit)}</small>` : ''}</article>`).join('');
  document.querySelector('#ledgerTableTitle').textContent = definition.sheetTitle;
  document.querySelector('#ledgerSectionKicker').textContent = `${monthLabel}汇总与明细`;

  const manualHeaders = ['检验员','中晟辉实收价格','中晟辉到款金额','到款日期','到款对应名','打款方式','发票','报告','中晟辉返款金额'];
  if (definition.provincialPayment && isAdmin()) manualHeaders.push('省市打款');
  manualHeaders.push('备注');
  const pdfHeaders = ['日期','使用单位','监检','项目名称','项目地址','维保单位','联系人及电话','检测性质','下次检验检测日期','设备名称','控制方式','层/站','数量','应收金额','实收金额','项目经办人'];
  document.querySelector('#ledgerTableHead').innerHTML = `<tr class="group-row"><th class="base-group sticky-seq" rowspan="2">序号</th><th class="base-group sticky-file" rowspan="2">文件位置</th><th class="pdf-group" colspan="${pdfHeaders.length}">PDF 自动提取字段</th><th class="manual-group" colspan="${manualHeaders.length}">手工补录字段</th><th class="action-group" rowspan="2">操作</th></tr><tr class="field-row">${pdfHeaders.map(label => `<th class="pdf-col">${label}</th>`).join('')}${manualHeaders.map(label => `<th class="manual-col">${label}</th>`).join('')}</tr>`;

  document.querySelector('#ledgerCount').textContent = `${rows.length} 条`;
  document.querySelector('#ledgerEmpty').hidden = rows.length > 0;
  document.querySelector('#ledgerTable').innerHTML = rows.map((project, index) => {
    const pdfCells = [
      formatDate(project.date), project.unit, project.supervision, project.projectName, project.address, project.maintainer,
      projectContactInfo(project), project.nature, formatDate(project.dueDate), project.equipment, project.controlMode,
      project.floors, project.quantity, yuan(project.receivable), yuan(project.actual), project.owner,
    ];
    const manualCells = [
      project.inspector, yuan(project.zshActualPrice), `<div class="status-stack"><strong>${yuan(project.receivedAmount)}</strong>${statusTag(paymentStatus(project))}</div>`,
      formatDate(project.paymentDate), project.paymentCounterparty, project.paymentMethod, project.invoice, project.report, yuan(project.refundAmount),
    ];
    if (definition.provincialPayment && isAdmin()) manualCells.push(yuan(project.provincialPayment));
    manualCells.push(`<div class="status-stack">${statusTag(project.followUpStatus || '待跟进')}<span>${safe(project.notes || '—')}</span></div>`);
    const pdfHtml = pdfCells.map(value => `<td class="pdf-col" title="${safe(value || '—')}">${safe(value || '—')}</td>`).join('');
    const manualHtml = manualCells.map((value, cellIndex) => `<td class="manual-col" title="${cellIndex === manualCells.length - 1 ? safe(project.notes || '') : safe(String(value || '').replace(/<[^>]+>/g, ''))}">${cellIndex === 2 || cellIndex === manualCells.length - 1 ? value : safe(value || '—')}</td>`).join('');
    const fileButton = project.attachmentName ? `<button class="row-action" data-view-pdf="${safe(project.id)}">查看 PDF</button>` : '<span class="table-sub">无附件</span>';
    const editButton = canEditManualFields() ? `<button class="row-action" data-edit-project="${safe(project.id)}">补录</button>` : '';
    return `<tr><td class="base-col sticky-seq">${index + 1}</td><td class="base-col sticky-file">${fileButton}</td>${pdfHtml}${manualHtml}<td class="action-col"><div class="row-actions"><button class="row-action" data-detail="${safe(project.id)}">详情</button>${editButton}</div></td></tr>`;
  }).join('');
}

function getReminderItems() {
  const projectItems = visibleProjects().filter(p => p.dueDate).map(p => ({ id: p.id, source: 'project', unit: p.unit, dueDate: p.dueDate, type: p.nature || '项目检测', meta: `${p.quantity || 0} 台 · ${p.owner || '未分配'}` }));
  const propertyItems = state.properties.filter(p => p.dueDate).map(p => ({ id: p.id, source: 'property', unit: p.unit, dueDate: p.dueDate, type: p.equipmentType || '物业设备', meta: p.internalNo || p.deviceCode || '设备' }));
  return [...projectItems, ...propertyItems].sort((a,b) => String(a.dueDate).localeCompare(String(b.dueDate)));
}

function renderReminders() {
  const all = getReminderItems();
  const buckets = [
    ['已逾期', all.filter(i => daysUntil(i.dueDate) < 0).length],
    ['30 天内', all.filter(i => daysUntil(i.dueDate) >= 0 && daysUntil(i.dueDate) <= 30).length],
    ['90 天内', all.filter(i => daysUntil(i.dueDate) >= 0 && daysUntil(i.dueDate) <= 90).length],
    ['半年内', all.filter(i => daysUntil(i.dueDate) >= 0 && daysUntil(i.dueDate) <= 180).length],
  ];
  document.querySelector('#reminderSummary').innerHTML = buckets.map(([label,count]) => `<article class="summary-card"><span>${label}</span><strong>${count}</strong></article>`).join('');
  const range = document.querySelector('#reminderRange').value;
  const rows = all.filter(item => {
    const days = daysUntil(item.dueDate);
    if (range === 'all') return true;
    if (range === 'overdue') return days < 0;
    return days >= 0 && days <= Number(range);
  });
  document.querySelector('#reminderList').innerHTML = rows.length ? rows.map(item => {
    const days = daysUntil(item.dueDate);
    const status = days < 0 ? `逾期 ${Math.abs(days)} 天` : days === 0 ? '今天到期' : `${days} 天后`;
    return `<div class="reminder-row ${days < 0 ? 'overdue' : days <= 30 ? 'soon' : ''}"><div class="date-block"><strong>${formatDate(item.dueDate)}</strong><span>${status}</span></div><div class="unit-block"><strong>${safe(item.unit)}</strong><span>${safe(item.source === 'project' ? '项目台账' : '物业设备')}</span></div><div class="meta-block"><strong>${safe(item.type)}</strong><span>${safe(item.meta)}</span></div>${statusTag(days < 0 ? '已逾期' : days <= 30 ? '需跟进' : '待跟进')}<button class="row-action" data-${item.source === 'project' ? 'detail' : 'property-detail'}="${safe(item.id)}">查看</button></div>`;
  }).join('') : '<div class="empty-state">此时间范围内没有到期项目</div>';
}

function propertyWarningStatus(item) {
  if (!item.dueDate) return { key: 'missing', label: '日期待补', tone: 'amber' };
  const days = daysUntil(item.dueDate);
  if (days < 0) return { key: 'overdue', label: `已逾期 ${Math.abs(days)} 天`, tone: 'red' };
  if (days <= Number(state.settings.reminderDays || 30)) return { key: 'soon', label: `${days} 天后到期`, tone: 'amber' };
  return { key: 'normal', label: '正常', tone: 'green' };
}

function renderProperties() {
  const select = document.querySelector('#propertyUnitFilter');
  const current = select.value;
  const units = [...new Set(state.properties.map(item => item.unit).filter(Boolean))].sort();
  select.innerHTML = '<option value="">全部单位</option>' + units.map(unit => `<option ${unit === current ? 'selected' : ''}>${safe(unit)}</option>`).join('');
  const maintainerSelect = document.querySelector('#propertyMaintainerFilter');
  const currentMaintainer = maintainerSelect.value;
  const maintainers = [...new Set(state.properties.map(item => item.maintainer).filter(Boolean))].sort();
  maintainerSelect.innerHTML = '<option value="">全部维保单位</option>' + maintainers.map(name => `<option ${name === currentMaintainer ? 'selected' : ''}>${safe(name)}</option>`).join('');
  const reminderMonths = document.querySelector('#propertyReminderMonths');
  reminderMonths.value = String(Math.max(1, Math.min(6, Math.round(Number(state.settings.reminderDays || 30) / 30))));
  const query = document.querySelector('#propertySearch').value.trim().toLowerCase();
  const unit = select.value;
  const statusFilter = document.querySelector('#propertyStatusFilter').value;
  const rows = state.properties.filter(item => {
    const text = [item.unit, item.address, item.deviceCode, item.internalNo, item.registrationNo, item.maintainer, item.contact].join(' ').toLowerCase();
    return (!query || text.includes(query)) && (!unit || item.unit === unit) && (!maintainerSelect.value || item.maintainer === maintainerSelect.value) && (!statusFilter || propertyWarningStatus(item).key === statusFilter);
  }).sort((a,b) => String(a.dueDate).localeCompare(String(b.dueDate)));
  const rowIds = new Set(rows.map(item => item.id));
  for (const id of selectedPropertyIds) if (!rowIds.has(id) || !canModifyAlerts()) selectedPropertyIds.delete(id);

  const all = state.properties;
  const thisMonth = new Date().toISOString().slice(0, 7);
  const summary = [
    ['总台数', all.length, '台'],
    ['已逾期', all.filter(item => propertyWarningStatus(item).key === 'overdue').length, '台'],
    ['本月到期', all.filter(item => String(item.dueDate || '').startsWith(thisMonth)).length, '台'],
    [`提前 ${Math.round(Number(state.settings.reminderDays || 30) / 30)} 个月预警`, all.filter(item => propertyWarningStatus(item).key === 'soon').length, '台'],
    ['使用单位', new Set(all.map(item => item.unit).filter(Boolean)).size, '家'],
    ['维保单位', new Set(all.map(item => item.maintainer).filter(Boolean)).size, '家'],
    ['日期待补', all.filter(item => !item.dueDate).length, '台'],
  ];
  document.querySelector('#alertSummary').innerHTML = summary.map(([label, value, unitLabel]) => `<article class="ledger-summary-card"><span>${safe(label)}</span><strong>${safe(value)}</strong><small>${safe(unitLabel)}</small></article>`).join('');
  document.querySelector('#propertyCount').textContent = `${rows.length} 台`;
  document.querySelector('#propertyEmpty').hidden = rows.length > 0;
  document.querySelector('#propertyTable').innerHTML = rows.map(item => {
    const warning = propertyWarningStatus(item);
    const modifyButtons = canModifyAlerts() ? `<button class="row-action" data-edit-property="${safe(item.id)}">编辑</button><button class="row-action row-action-danger" data-delete-property="${safe(item.id)}">删除</button>` : '';
    const dueMonth = item.dueDate ? safe(item.dueDate.slice(0, 7)) : '日期待补';
    const dateCell = canModifyAlerts() ? `<label class="property-check"><input type="checkbox" data-select-property="${safe(item.id)}" aria-label="选择 ${safe(item.unit)} ${safe(item.internalNo || item.deviceCode || item.id)}" ${selectedPropertyIds.has(item.id) ? 'checked' : ''} /></label><button class="row-action property-date-edit" data-edit-property="${safe(item.id)}" title="下次检验日期：${safe(item.dueDate || '未填写')}，点击修改">${dueMonth}</button>` : dueMonth;
    return `<tr><td><div class="property-date-cell">${dateCell}</div></td><td>${safe(item.unit || '—')}</td><td title="${safe(item.address || '')}">${safe(item.address || '—')}</td><td>${safe(item.equipmentType || '—')}</td><td>${safe(item.internalNo || '—')}</td><td>${safe(item.deviceCode || '—')}</td><td>${safe(item.inspectionAgency || '—')}</td><td>${safe(item.registrationNo || '—')}</td><td>${safe(item.maintainer || '—')}</td><td>${safe(item.emergencyPhone || '—')}</td><td>${safe(item.contact || '—')}</td><td>${safe(item.phone || '—')}</td><td>${safe(item.phone1 || '—')}</td><td title="${safe(item.notes || '')}">${safe(item.notes || '—')}</td><td><span class="status-tag ${warning.tone}">${safe(warning.label)}</span></td><td>${statusTag(item.followUpStatus || '待跟进')}</td><td><div class="row-actions"><button class="row-action" data-property-detail="${safe(item.id)}">查看</button>${modifyButtons}</div></td></tr>`;
  }).join('');
  updatePropertySelection();
}

function updatePropertySelection() {
  const boxes = [...document.querySelectorAll('[data-select-property]')];
  const selectAll = document.querySelector('#propertySelectAll');
  selectAll.checked = boxes.length > 0 && boxes.every(box => box.checked);
  selectAll.indeterminate = boxes.some(box => box.checked) && !selectAll.checked;
  selectAll.disabled = !canModifyAlerts() || !boxes.length;
  document.querySelector('#deleteSelectedProperties').disabled = !canModifyAlerts() || !selectedPropertyIds.size;
  document.querySelector('#propertySelectionCount').textContent = `已选 ${selectedPropertyIds.size} 台（仅当前筛选结果）`;
}

function paymentAllocationRow(part = {}) {
  return `<div class="allocation-row"><label>关联项目<select data-allocation-project aria-label="关联项目"><option value="">选择对应项目</option>${visibleProjects().map(project => `<option value="${safe(project.id)}" ${part.projectId === project.id ? 'selected' : ''}>${safe(project.unit)} · ${safe(project.nature || project.projectName || '项目')} · ${safe(project.date || '')} · ${yuan(project.receivable)} · ${safe(project.id.slice(-6))}</option>`).join('')}</select></label><label>关联金额（元）<input data-allocation-amount aria-label="关联金额（元）" type="number" min="0.01" step="0.01" value="${safe(part.amount ?? '')}" placeholder="手动填写金额" /></label><button class="text-button danger-text" type="button" data-remove-allocation>移除</button></div>`;
}

function updateAllocationTotal(card) {
  const payment = state.payments.find(item => item.id === card.dataset.paymentMatch);
  if (!payment) return;
  let total = 0;
  for (const input of card.querySelectorAll('[data-allocation-amount]')) {
    try { total += moneyCents(input.value || 0); } catch { /* 提交时显示具体错误 */ }
  }
  const remaining = moneyCents(payment.amount) - total;
  const target = card.querySelector('[data-allocation-total]');
  target.textContent = `已填写 ${yuan(total / 100)} / 到账 ${yuan(payment.amount)}；${remaining < 0 ? '超出' : '剩余'} ${yuan(Math.abs(remaining) / 100)}`;
  target.classList.toggle('danger-text', remaining !== 0);
}

function renderPayments() {
  const payments = [...visiblePayments()].sort((a,b) => String(b.date).localeCompare(String(a.date)));
  document.querySelector('#paymentList').innerHTML = payments.length ? payments.map(payment => {
    const parts = paymentAllocations(payment);
    const details = parts.map(part => { const project = state.projects.find(item => item.id === part.projectId); return project ? `${project.unit}（${project.nature || project.projectName || '项目'} · ${project.date || ''}）：${yuan(part.amount)}` : ''; }).filter(Boolean).join('；');
    return `<div class="payment-entry"><time>${formatDate(payment.date)}</time><div><strong class="payer">${safe(payment.payer)}</strong><span class="bank">${safe(payment.bank || '银行未识别')} · 尾号 ${safe(payment.accountSuffix || '—')} · ${safe(payment.status)}</span>${details ? `<span class="payment-links">${safe(details)}</span>` : ''}</div><div class="payment-amount-actions"><strong class="amount">${yuan(payment.amount)}<small>${parts.length ? `已关联 ${parts.length} 个项目` : '待人工确认'}</small></strong>${canRecordPayments() ? `<button class="row-action row-action-danger" data-delete-payment="${safe(payment.id)}">删除</button>` : ''}</div></div>`;
  }).join('') : '<div class="empty-state">尚未录入银行到账信息</div>';
  const unmatched = payments.filter(item => !isPaymentMatched(item));
  document.querySelector('#unmatchedCount').textContent = unmatched.length;
  document.querySelector('#unmatchedList').innerHTML = unmatched.length ? unmatched.map(payment => {
    const parts = paymentAllocations(payment);
    const rows = [...parts, { amount: (moneyCents(payment.amount) - allocatedCents(payment)) / 100 }];
    return `<div class="unmatched-item" data-payment-match="${safe(payment.id)}"><strong>${safe(payment.payer)} · ${yuan(payment.amount)}</strong><span>${formatDate(payment.date)} · ${safe(payment.bank || '银行未识别')}</span><div data-allocation-rows>${rows.map(paymentAllocationRow).join('')}</div><button class="text-button" type="button" data-add-allocation>＋ 添加关联项目</button><p class="allocation-total" data-allocation-total aria-live="polite"></p><button class="button button-secondary" data-manual-match="${safe(payment.id)}">确认关联</button></div>`;
  }).join('') : '<div class="empty-state">所有到账记录都已匹配</div>';
  document.querySelectorAll('[data-payment-match]').forEach(updateAllocationTotal);
}

function excelSerialToIso(value) {
  const num = Number(value);
  if (!Number.isFinite(num) || num < 1) return '';
  const utc = new Date(Date.UTC(1899, 11, 30) + Math.round(num) * 86400000);
  return utc.toISOString().slice(0, 10);
}

function parseFlexibleDate(value, fallbackYear = TODAY.getFullYear()) {
  if (value == null || value === '') return '';
  if (typeof value === 'number') {
    if (value > 20000) return excelSerialToIso(value);
    const text = String(value);
    const parts = text.split('.');
    if (parts.length === 2) return `${fallbackYear}-${parts[0].padStart(2,'0')}-${parts[1].padStart(2,'0')}`;
  }
  const text = String(value).trim();
  if (/^\d{4}-\d{1,2}-\d{1,2}/.test(text)) {
    const [y,m,d] = text.match(/^\d{4}-\d{1,2}-\d{1,2}/)[0].split('-');
    return `${y}-${m.padStart(2,'0')}-${d.padStart(2,'0')}`;
  }
  let match = text.match(/(\d{4})[年/.\-](\d{1,2})(?:[月/.\-](\d{1,2}))?/);
  if (match) return `${match[1]}-${match[2].padStart(2,'0')}-${String(match[3] || 1).padStart(2,'0')}`;
  match = text.match(/^(\d{1,2})[月/.\-](\d{1,2})/);
  if (match) return `${fallbackYear}-${match[1].padStart(2,'0')}-${match[2].padStart(2,'0')}`;
  return '';
}

function normalizeHeader(value) {
  return String(value ?? '').replace(/[\s/\\()（）]/g, '').trim();
}

function columnIndex(cellRef) {
  const letters = String(cellRef).match(/[A-Z]+/i)?.[0] || 'A';
  let index = 0;
  for (const char of letters.toUpperCase()) index = index * 26 + char.charCodeAt(0) - 64;
  return index - 1;
}

async function parseXlsx(buffer) {
  if (!JSZip) throw new Error('Excel 解析组件未加载');
  const zip = await JSZip.loadAsync(buffer);
  const parser = new DOMParser();
  const workbookXml = parser.parseFromString(await zip.file('xl/workbook.xml').async('text'), 'application/xml');
  const relsXml = parser.parseFromString(await zip.file('xl/_rels/workbook.xml.rels').async('text'), 'application/xml');
  const relationships = new Map([...relsXml.getElementsByTagNameNS('*','Relationship')].map(node => [node.getAttribute('Id'), node.getAttribute('Target')]));
  const sharedStrings = [];
  if (zip.file('xl/sharedStrings.xml')) {
    const sharedXml = parser.parseFromString(await zip.file('xl/sharedStrings.xml').async('text'), 'application/xml');
    for (const item of sharedXml.getElementsByTagNameNS('*','si')) sharedStrings.push([...item.getElementsByTagNameNS('*','t')].map(t => t.textContent).join(''));
  }
  const sheets = [];
  for (const sheet of workbookXml.getElementsByTagNameNS('*','sheet')) {
    const relId = [...sheet.attributes].find(attr => attr.localName === 'id')?.value;
    let target = relationships.get(relId);
    if (!target) continue;
    target = target.replace(/^\//,'');
    if (!target.startsWith('xl/')) target = `xl/${target.replace(/^\.\//,'')}`;
    const file = zip.file(target);
    if (!file) continue;
    const xml = parser.parseFromString(await file.async('text'), 'application/xml');
    const rows = [];
    for (const rowNode of xml.getElementsByTagNameNS('*','row')) {
      const row = [];
      for (const cell of rowNode.getElementsByTagNameNS('*','c')) {
        const index = columnIndex(cell.getAttribute('r'));
        const type = cell.getAttribute('t');
        let value = '';
        if (type === 'inlineStr') value = [...cell.getElementsByTagNameNS('*','t')].map(t => t.textContent).join('');
        else {
          const raw = cell.getElementsByTagNameNS('*','v')[0]?.textContent ?? '';
          if (type === 's') value = sharedStrings[Number(raw)] ?? '';
          else if (type === 'b') value = raw === '1';
          else if (type === 'str') value = raw;
          else value = raw === '' ? '' : Number(raw);
        }
        row[index] = value;
      }
      rows.push(row);
    }
    sheets.push({ name: sheet.getAttribute('name'), rows });
  }
  return sheets;
}

function findHeaderRow(rows) {
  const known = new Set(['序号','使用单位','使用单位名称','设备代码','检测性质','应收金额','下检日期','下次检验检测日期','到期年月','文件位置','设备品种']);
  for (let index = 0; index < Math.min(rows.length, 25); index++) {
    const score = rows[index].filter(value => known.has(normalizeHeader(value))).length;
    if (score >= 2) return index;
  }
  return -1;
}

function objectFromRow(headers, row) {
  const result = {};
  headers.forEach((header, index) => { if (header) result[normalizeHeader(header)] = row[index]; });
  return result;
}

function propertyFromExcel(row, fileName) {
  const unit = row['使用单位'] || row['使用单位名称'];
  const deviceCode = row['设备代码'];
  if (!unit || (!deviceCode && !row['单位内编号'] && !row['单内位编号'])) return null;
  return {
    id: uniqueId('device'), unit: String(unit), region: String(row['所在区域'] || row['区域'] || ''), address: String(row['单位地址'] || row['项目地址'] || ''),
    category: String(row['设备类别'] || ''), equipmentType: String(row['设备品种'] || row['设备名称'] || ''), internalNo: String(row['单位内编号'] || row['单内位编号'] || ''),
    deviceCode: String(deviceCode || ''), registrationAuthority: String(row['登记机关'] || ''), inspectionAgency: String(row['检测机构'] || ''),
    registrationNo: String(row['登记证编号'] || ''), dueDate: parseFlexibleDate(row['下检日期'] || row['到期年月'] || row['下次检验检测日期']),
    maintainer: String(row['维保单位'] || ''), emergencyPhone: String(row['应急电话'] || ''), contact: String(row['联系人'] || ''),
    phone: String(row['联系电话'] || ''), phone1: String(row['联系电话1'] || ''), notes: String(row['备注'] || ''), followUpStatus: '待跟进', source: fileName, createdAt: new Date().toISOString()
  };
}

async function importPropertyExcelFiles(files, quiet = false) {
  if (!canImportPropertyExcel()) throw new Error('请先登录后再导入物业 Excel');
  const previousProperties = state.properties;
  state.properties = [...state.properties];
  const importedProperties = [];
  let propertyCount = 0;
  for (const file of files) {
    const sheets = await parseXlsx(await file.arrayBuffer());
    for (const sheet of sheets) {
      const headerIndex = findHeaderRow(sheet.rows);
      if (headerIndex < 0) continue;
      const headers = sheet.rows[headerIndex];
      for (const values of sheet.rows.slice(headerIndex + 1)) {
        const row = objectFromRow(headers, values);
        const property = propertyFromExcel(row, file.name);
        if (!property) continue;
        importedProperties.push(property);
        const key = property.deviceCode || `${property.unit}|${property.internalNo}`;
        const existing = state.properties.findIndex(item => (item.deviceCode || `${item.unit}|${item.internalNo}`) === key);
        if (existing >= 0) {
          const previous = state.properties[existing];
          state.properties[existing] = {
            ...previous,
            ...property,
            id: previous.id,
            notes: property.notes || previous.notes || '',
            followUpStatus: previous.followUpStatus || '待跟进',
          };
        }
        else { state.properties.push(property); propertyCount++; }
      }
    }
  }
  try {
    if (cloudApi.isConfigured && importedProperties.length) {
      const imported = await cloudApi.importProperties(importedProperties, cloudRevisions.shared || 0);
      state.properties = (imported.properties || []).map(normalizePropertyRecord);
      cloudRevisions.shared = Number(imported.revision || cloudRevisions.shared || 0);
    }
  } catch (error) {
    state.properties = previousProperties;
    throw error;
  }
  localStorage.setItem(cacheKey(), JSON.stringify(state));
  renderAll();
  if (!quiet) showToast(propertyCount ? `导入完成：新增 ${propertyCount} 台物业设备` : '未识别到物业设备，请检查 Excel 是否包含使用单位、设备代码或单位内编号', !propertyCount);
  return { propertyCount };
}

function groupPdfItems(items) {
  const lines = [];
  for (const item of items.filter(item => item.str?.trim())) {
    const y = Math.round(item.transform[5]);
    let line = lines.find(candidate => Math.abs(candidate.y - y) <= 2);
    if (!line) { line = { y, items: [] }; lines.push(line); }
    line.items.push({ x: item.transform[4], width: Number(item.width || 0), text: item.str.trim() });
  }
  return lines.sort((a,b) => b.y - a.y).map(line => {
    const sortedItems = [...line.items].sort((a,b) => a.x - b.x);
    return { y: line.y, items: sortedItems, tokens: sortedItems.map(item => item.text), text: sortedItems.map(item => item.text).join(' ') };
  });
}

function extractBetween(text, start, ends) {
  const startIndex = text.indexOf(start);
  if (startIndex < 0) return '';
  const rest = text.slice(startIndex + start.length);
  const positions = ends.map(end => rest.indexOf(end)).filter(index => index >= 0);
  return rest.slice(0, positions.length ? Math.min(...positions) : undefined).replace(/^[:：|\s]+|[|\s]+$/g,'').trim();
}

function extractSameLineValue(lines, label) {
  for (const line of lines) {
    const tokenIndex = line.tokens.findIndex(token => String(token).replace(/\s/g, '').includes(label));
    if (tokenIndex < 0) continue;
    const labelToken = String(line.tokens[tokenIndex]).replace(/\s/g, '');
    const inlineSuffix = labelToken.slice(labelToken.indexOf(label) + label.length);
    const value = [inlineSuffix, ...line.tokens.slice(tokenIndex + 1)].join('').trim();
    if (value) return value;
  }
  return '';
}

function extractVisualCellValue(lines, label, rightLabel = '', nextRowLabel = '') {
  const compact = value => String(value || '').replace(/[\s:：]/g, '');
  const positioned = lines.flatMap(line => (line.items || []).map(item => ({ ...item, y: line.y })));
  const labelHit = positioned.find(item => compact(item.text).includes(label));
  if (!labelHit) return '';

  const labelText = compact(labelHit.text);
  const inlineSuffix = labelText.slice(labelText.indexOf(label) + label.length);
  const nextHit = nextRowLabel
    ? positioned.filter(item => item.y < labelHit.y && compact(item.text).includes(nextRowLabel)).sort((a,b) => b.y - a.y)[0]
    : null;
  const rowGap = nextHit ? Math.max(20, labelHit.y - nextHit.y) : 34;
  const verticalRadius = Math.min(23, Math.max(10, rowGap / 2 - 1));
  const rightHit = rightLabel
    ? positioned.filter(item => item.x > labelHit.x && Math.abs(item.y - labelHit.y) <= verticalRadius && compact(item.text).includes(rightLabel)).sort((a,b) => a.x - b.x)[0]
    : null;
  const estimatedLabelWidth = Math.max(Number(labelHit.width || 0), label.length * 8);
  const minX = labelHit.x + estimatedLabelWidth + 2;
  const maxX = rightHit ? rightHit.x - 2 : Number.POSITIVE_INFINITY;
  const values = positioned
    .filter(item => item !== labelHit && item.x >= minX && item.x < maxX && Math.abs(item.y - labelHit.y) <= verticalRadius)
    .sort((a,b) => Math.abs(b.y - a.y) > 2 ? b.y - a.y : a.x - b.x)
    .map(item => item.text.trim())
    .filter(Boolean);
  return [inlineSuffix, ...values].join('').replace(/^\|+|\|+$/g, '').trim();
}

const EQUIPMENT_NAME_PATTERN = /曳引驱动乘客电梯|曳引驱动载货电梯|乘客电梯|载货电梯|杂物电梯|自动扶梯|自动人行道|人行道|限速器校验|限速器|125[％%]额定载重试验/;

function findEquipmentDataLine(lines) {
  for (const candidate of lines) {
    const compactTokens = candidate.tokens.map(token => String(token).replace(/\s/g, ''));
    const compactText = compactTokens.join('');
    const equipmentToken = compactTokens.find(token => EQUIPMENT_NAME_PATTERN.test(token));
    const equipmentMatch = compactText.match(EQUIPMENT_NAME_PATTERN)?.[0] || '';
    if ((!equipmentToken && !equipmentMatch) || candidate.tokens.some(token => /设备名称/.test(token))) continue;

    // 部分 WPS PDF 会把同一表格行的设备名称与数量画在相邻基线上。
    // 合并相近基线后再读取最后 3 个数字，分别对应数量、单价、金额。
    const nearbyItems = lines
      .filter(line => Math.abs(line.y - candidate.y) <= 4)
      .flatMap(line => line.items || [])
      .sort((a, b) => a.x - b.x);
    const tokens = nearbyItems.map(item => item.text);
    const numbers = tokens
      .filter(token => /^\d[\d,]*(?:\.\d+)?$/.test(token))
      .map(numberValue);
    if (numbers.length < 3) continue;

    return {
      equipment: (equipmentToken || equipmentMatch).replace('％', '%'),
      tokens,
      numbers,
    };
  }
  return null;
}

function findPdfSummaryNumbers(lines, label) {
  const summaryLine = lines.find(line => String(line.text || '').replace(/\s/g, '').includes(label));
  if (!summaryLine) return [];
  // PDF 可能把同一表格行的文字与数字放在相邻基线上；按横坐标合并后，
  // “应收合计”行末尾两个数字依次为数量总数、金额总数。
  return lines
    .filter(line => Math.abs(line.y - summaryLine.y) <= 4)
    .flatMap(line => line.items || [])
    .sort((a, b) => a.x - b.x)
    .map(item => String(item.text || '').trim())
    .filter(value => /^\d[\d,]*(?:\.\d+)?$/.test(value))
    .map(value => Number(value.replace(/,/g, '')))
    .filter(Number.isFinite);
}

async function parsePdf(file) {
  const data = new Uint8Array(await file.arrayBuffer());
  const pdf = await pdfjsLib.getDocument({ data }).promise;
  const allLines = [];
  for (let pageNo = 1; pageNo <= pdf.numPages; pageNo++) {
    const page = await pdf.getPage(pageNo);
    const content = await page.getTextContent();
    allLines.push(...groupPdfItems(content.items));
  }
  const text = allLines.map(line => line.text).join('\n');
  const flat = text.replace(/[\s|]/g,'');
  const unit = extractVisualCellValue(allLines, '使用单位名称', '监检', '项目名称') || extractBetween(flat, '使用单位名称', ['监检','项目名称']) || file.name.replace(/\.pdf$/i,'');
  const supervisionBlock = extractVisualCellValue(allLines, '监检', '', '项目地址') || extractBetween(flat, '监检', ['项目名称','项目地址']);
  const supervisionYear = supervisionBlock.match(/\d{4}/)?.[0] || '';
  const supervision = supervisionYear || (/^\//.test(supervisionBlock) ? '/' : supervisionBlock);
  const projectName = extractVisualCellValue(allLines, '项目名称', '项目地址', '维保单位名称') || extractBetween(flat, '项目名称', ['项目地址','维保单位名称']) || '/';
  const address = extractVisualCellValue(allLines, '项目地址', '', '联系人及电话') || extractBetween(flat, '项目地址', ['维保单位名称','联系人及电话','检测性质']);
  const maintainer = extractVisualCellValue(allLines, '维保单位名称', '联系人及电话', '检测性质') || extractBetween(flat, '维保单位名称', ['联系人及电话','检测性质']);
  const contactAndPhone = extractVisualCellValue(allLines, '联系人及电话', '', '检测性质') || extractSameLineValue(allLines, '联系人及电话') || extractBetween(flat, '联系人及电话', ['检测性质','下次检验','设备名称']);
  const phone = (contactAndPhone.match(/1\d{10}|\d{7,}/) || [])[0] || '';
  const contact = contactAndPhone.replace(phone, '').replace(/^[:：/]+|[:：/]+$/g, '') || '';
  const dueMatch = flat.match(/下次检验\/?检测日期(\d{4})年(\d{1,2})月(?:(\d{1,2})日)?/);
  const dueDate = dueMatch ? `${dueMatch[1]}-${dueMatch[2].padStart(2,'0')}-${String(dueMatch[3] || 1).padStart(2,'0')}` : '';
  const dateMatches = [...flat.matchAll(/(20\d{2})年(\d{1,2})月(\d{1,2})日/g)];
  const lastDate = dateMatches.at(-1);
  const date = lastDate ? `${lastDate[1]}-${lastDate[2].padStart(2,'0')}-${lastDate[3].padStart(2,'0')}` : new Date().toISOString().slice(0,10);
  const owner = (flat.match(/项目经办人[:：]?(.+?)联系电话/) || [])[1] || '';
  let nature = '自行检测';
  if (/[☑√■]定期检验/.test(flat) || /定期检验\d*台/i.test(file.name)) nature = '定期检验';
  else if (/[☑√■]限速器校验/.test(flat) || /限速器/.test(file.name)) nature = '限速器校验';
  else if (/[☑√■]125%额定载重试验/.test(flat) || /125%/.test(file.name)) nature = '125%额定载重试验';

  let equipment = '', controlMode = '', floors = '', quantity = 1, unitPrice = 0, total = 0;
  const equipmentLine = findEquipmentDataLine(allLines);
  if (equipmentLine) {
    equipment = equipmentLine.equipment;
    controlMode = equipmentLine.tokens.find(token => /集选|并联|按钮|群控/.test(token)) || '';
    floors = equipmentLine.tokens.find(token => /^\d+\s*\/\s*\d+$/.test(token)) || '';
    [quantity, unitPrice, total] = equipmentLine.numbers.slice(-3);
  }
  const receivableNumbers = findPdfSummaryNumbers(allLines, '应收合计');
  const actualLine = allLines.find(line => /实收合计/.test(line.text));
  const lastNumber = line => line ? numberValue((line.text.match(/\d+(?:\.\d+)?/g) || []).at(-1)) : 0;
  // 多个设备明细行时，台账数量取“应收合计”行数量列中的总数，
  // 不再取第一条设备明细的数量。
  if (receivableNumbers.length >= 2 && receivableNumbers.at(-2) > 0) quantity = receivableNumbers.at(-2);
  const receivable = receivableNumbers.at(-1) || total;
  const actual = lastNumber(actualLine);
  return normalizeProjectRecord({
    date, unit, supervision, projectName, address, maintainer, contact, phone, contactInfo: contactAndPhone, nature, dueDate, equipment, controlMode, floors,
    quantity, unitPrice, total: total || receivable, receivable, actual, receivedAmount: 0, owner, inspector: '',
    zshActualPrice: 0, paymentDate: '', paymentCounterparty: '', paymentMethod: '', invoice: '', report: '', refundAmount: 0,
    provincialPayment: 0, followUpStatus: '待跟进', notes: '', source: `PDF · ${file.name}`, createdBy: currentUserId,
    createdAt: new Date().toISOString(),
  });
}

async function pdfFingerprint(file) {
  if (!globalThis.crypto?.subtle) return '';
  const digest = await globalThis.crypto.subtle.digest('SHA-256', await file.arrayBuffer());
  return [...new Uint8Array(digest)].map(value => value.toString(16).padStart(2, '0')).join('');
}

async function importPdfFiles(files, targetNature = activeLedgerDefinition().nature) {
  let count = 0;
  let lastRelevantMonth = '';
  for (const file of files) {
    if (file.size > (CLOUD_CONFIG.maxPdfBytes || 1024 * 1024)) throw new Error(`${file.name} 超过 1MB，请压缩后重新导入`);
    const fingerprint = await pdfFingerprint(file);
    const project = await parsePdf(file);
    project.nature = targetNature;
    project.attachmentName = file.name;
    project.attachmentFingerprint = fingerprint;
    lastRelevantMonth = String(project.date || '').slice(0, 7);
    project.id = uniqueId('project');
    project.attachmentId = await putAttachment(file);
    const uploaded = await cloudApi.uploadAttachment(file);
    project.attachmentFileId = uploaded.fileID;
    state.projects.push(project);
    count++;
  }
  if (count) saveState();
  if (lastRelevantMonth) document.querySelector('#ledgerMonthFilter').value = lastRelevantMonth;
  renderAll();
  if (count) showToast(`已从 ${count} 份 PDF 生成项目台账，已切换到 ${lastRelevantMonth || '对应'} 月份`);
  else showToast('未选择可导入的 PDF 文件', true);
  return { count, month: lastRelevantMonth };
}

function normalizeCompany(name) {
  return String(name || '').replace(/[\s（()）]/g,'').replace(/(有限责任公司|有限公司|公司|吉林省|长春市)/g,'').toLowerCase();
}

function companySimilarity(a, b) {
  const left = normalizeCompany(a), right = normalizeCompany(b);
  if (!left || !right) return 0;
  if (left.includes(right) || right.includes(left)) return 1;
  const chars = new Set(left);
  const common = [...new Set(right)].filter(char => chars.has(char)).length;
  return common / Math.max(new Set(left).size, new Set(right).size);
}

function parseBankMessage(raw) {
  const amount = numberValue((raw.match(/收入人民币\s*([\d,]+(?:\.\d{1,2})?)/) || [])[1]);
  const payer = (raw.match(/对方为(.+?)(?:【|\[|$)/) || [])[1]?.trim() || '';
  const bank = (raw.match(/[【\[]([^】\]]+)[】\]]/) || [])[1] || '';
  const accountSuffix = (raw.match(/账户\s*(\d+)/) || [])[1] || '';
  const dateMatch = raw.match(/于\s*(\d{1,2})月(\d{1,2})日/);
  let year = TODAY.getFullYear();
  if (dateMatch && Number(dateMatch[1]) > TODAY.getMonth() + 2) year--;
  const date = dateMatch ? `${year}-${dateMatch[1].padStart(2,'0')}-${dateMatch[2].padStart(2,'0')}` : new Date().toISOString().slice(0,10);
  return { amount, payer, bank, accountSuffix, date, raw };
}

function findPaymentMatch(payment) {
  return visibleProjects()
    .map(project => {
      const diff = Math.abs(numberValue(project.receivable) - payment.amount);
      const nameScore = companySimilarity(project.unit, payment.payer);
      const score = (diff < .01 ? 0.62 : diff <= Math.max(10, payment.amount * .03) ? .26 : 0) + nameScore * .38;
      return { project, score };
    })
    .filter(item => item.score >= .72)
    .sort((a,b) => b.score - a.score)[0]?.project || null;
}

function importPayment(raw) {
  if (!canRecordPayments()) throw new Error('只有总管理员可以录入银行到款');
  const parsed = parseBankMessage(raw);
  if (!parsed.amount || !parsed.payer) throw new Error('未识别到完整的到账金额或付款单位，请检查文字');
  const duplicate = visiblePayments().find(item => item.date === parsed.date && Math.abs(item.amount - parsed.amount) < .01 && normalizeCompany(item.payer) === normalizeCompany(parsed.payer));
  if (duplicate) throw new Error('这条到账信息已经录入过了');
  const match = findPaymentMatch(parsed);
  const payment = { id: uniqueId('payment'), ...parsed, projectId: '', allocations: [], status: '待确认', createdBy: currentUserId, createdAt: new Date().toISOString() };
  state.payments.push(payment);
  if (match) {
    associatePayment(state, payment.id, [{ projectId: match.id, amount: parsed.amount }]);
  }
  saveState();
  renderAll();
  return match;
}

function downloadBlob(content, fileName, type) {
  const blob = content instanceof Blob ? content : new Blob([content], { type });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url; link.download = fileName; link.click();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}

function csvCell(value) { return `"${String(value ?? '').replace(/"/g,'""')}"`; }
function exportCsv(rows, columns, fileName) {
  const csv = '\ufeff' + [columns.map(([label]) => csvCell(label)).join(','), ...rows.map((row, rowIndex) => columns.map(([,key]) => csvCell(typeof key === 'function' ? key(row, rowIndex) : row[key])).join(','))].join('\r\n');
  downloadBlob(csv, fileName, 'text/csv;charset=utf-8');
}

async function showProjectDetail(id) {
  const project = state.projects.find(item => item.id === id);
  if (!project || !canAccessProject(project)) return showToast('无权查看这条项目台账', true);
  document.querySelector('#detailTitle').textContent = project.unit;
  const fields = [
    ['业务日期', formatDate(project.date)], ['使用单位', project.unit || '—'], ['监检', project.supervision || '—'],
    ['项目名称', project.projectName || '—'], ['项目地址', project.address || '—'], ['维保单位', project.maintainer || '—'],
    ['联系人及电话', projectContactInfo(project) || '—'], ['检测性质', project.nature || '—'],
    ['下次检验检测日期', formatDate(project.dueDate)], ['设备名称', project.equipment || '—'], ['控制方式', project.controlMode || '—'],
    ['层/站', project.floors || '—'], ['数量', `${project.quantity || 0} 台`],
    ['应收金额', yuan(project.receivable)], ['实收金额', yuan(project.actual)],
    ['项目经办人', project.owner || '—'], ['检验员', project.inspector || '—'], ['中晟辉实收价格', yuan(project.zshActualPrice)],
    ['中晟辉到款金额', yuan(project.receivedAmount)], ['到款日期', formatDate(project.paymentDate)], ['到款对应名', project.paymentCounterparty || '—'],
    ['打款方式', project.paymentMethod || '—'], ['发票', project.invoice || '—'], ['报告', project.report || '—'],
    ['中晟辉返款金额', yuan(project.refundAmount)], ...(isAdmin() ? [['省市打款', project.nature === '定期检验' ? yuan(project.provincialPayment) : '不适用']] : []),
    ['跟进状态', project.followUpStatus || '待跟进'], ['创建人', userName(projectCreatedBy(project))], ['数据来源', project.source || '—'],
    ['PDF 附件', project.attachmentName || '未上传'], ['备注', project.notes || '—'],
  ];
  const editButton = canEditManualFields() ? `<button class="button button-secondary" data-edit-project="${safe(project.id)}">补录手工信息</button>` : '';
  document.querySelector('#detailContent').innerHTML = `<div class="detail-grid">${fields.map(([label,value]) => `<div class="detail-cell"><span>${safe(label)}</span><strong>${safe(value)}</strong></div>`).join('')}</div><div class="detail-actions">${project.attachmentName ? `<button class="button button-primary" data-view-pdf="${safe(project.id)}">查看 PDF 原件</button>` : ''}${editButton}<button class="button button-secondary" data-print-detail="${safe(project.id)}">打印详情</button><button class="button button-quiet" data-delete-project="${safe(project.id)}">删除台账</button></div>`;
  openModal('detailModal');
}

function populateManualChoices(form, project) {
  const presets = { inspector: [], invoice: ['专票', '普票', '不开'], report: ['未出', '已出'] };
  for (const [field, defaults] of Object.entries(presets)) {
    const current = String(project[field] || '').trim();
    // Saved cloud records provide reusable options across all four ledgers/devices.
    const options = [...new Set([...defaults, ...visibleProjects().map(item => String(item[field] || '').trim()), current].filter(Boolean))];
    const select = form.querySelector(`[data-manual-choice="${field}"]`);
    // Index values keep custom text (including HTML and reserved words) as plain data.
    select.innerHTML = '<option value="">未填写</option>' + options.map((text, index) => `<option value="${index}">${safe(text)}</option>`).join('')
      + `<option value="custom">${field === 'inspector' ? '＋ 新增人员' : '其他 / 手动填写'}</option>`;
    select.value = current ? String(options.indexOf(current)) : '';
    form.elements[field].hidden = true;
  }
}

function openProjectEditor(id) {
  if (!canEditManualFields()) return showToast('只有总管理员可以补录手工字段', true);
  const project = state.projects.find(item => item.id === id);
  if (!project || !canAccessProject(project)) return showToast('无权修改这条项目台账', true);
  const form = document.querySelector('#ledgerEditForm');
  form.reset();
  const values = {
    projectId: project.id, inspector: project.inspector, zshActualPrice: project.zshActualPrice,
    receivedAmount: project.receivedAmount, paymentDate: project.paymentDate, paymentCounterparty: project.paymentCounterparty,
    paymentMethod: project.paymentMethod, invoice: project.invoice, report: project.report, refundAmount: project.refundAmount,
    provincialPayment: project.provincialPayment, followUpStatus: project.followUpStatus || '待跟进', notes: project.notes,
  };
  Object.entries(values).forEach(([key, value]) => { if (form.elements[key]) form.elements[key].value = value ?? ''; });
  populateManualChoices(form, project);
  document.querySelector('#ledgerEditProjectName').textContent = `${project.unit} · ${project.nature}`;
  document.querySelector('#provincialPaymentField').hidden = project.nature !== '定期检验';
  closeModal('detailModal');
  openModal('ledgerEditModal');
}

async function renderPdfPages(blob) {
  const loading = document.querySelector('#pdfLoading');
  const pages = document.querySelector('#pdfPages');
  const data = new Uint8Array(await blob.arrayBuffer());
  const pdf = await pdfjsLib.getDocument({ data }).promise;
  pages.innerHTML = '';
  pages.hidden = false;
  const availableWidth = Math.max(320, pages.clientWidth - 30);
  const pixelRatio = Math.min(Number(window.devicePixelRatio || 1), 2);
  for (let pageNo = 1; pageNo <= pdf.numPages; pageNo++) {
    loading.textContent = `正在渲染 PDF：第 ${pageNo} / ${pdf.numPages} 页`;
    const page = await pdf.getPage(pageNo);
    const baseViewport = page.getViewport({ scale: 1 });
    const scale = Math.min(2.2, availableWidth / baseViewport.width);
    const viewport = page.getViewport({ scale });
    const canvas = document.createElement('canvas');
    const wrapper = document.createElement('article');
    wrapper.className = 'pdf-page';
    canvas.width = Math.floor(viewport.width * pixelRatio);
    canvas.height = Math.floor(viewport.height * pixelRatio);
    canvas.style.width = `${Math.floor(viewport.width)}px`;
    canvas.style.height = `${Math.floor(viewport.height)}px`;
    wrapper.append(canvas);
    pages.append(wrapper);
    await page.render({ canvasContext: canvas.getContext('2d', { alpha: false }), viewport, transform: pixelRatio === 1 ? null : [pixelRatio, 0, 0, pixelRatio, 0, 0] }).promise;
  }
}

async function fetchPdfBlob(url) {
  const response = await fetch(url, { cache: 'no-store' });
  if (!response.ok) throw new Error(`PDF 返回 HTTP ${response.status}`);
  const bytes = new Uint8Array(await response.arrayBuffer());
  const header = new TextDecoder('latin1').decode(bytes.subarray(0, Math.min(bytes.length, 1024)));
  if (!header.includes('%PDF-')) throw new Error('返回内容不是有效的 PDF 文件');
  return new Blob([bytes], { type: 'application/pdf' });
}

async function showPdf(id) {
  const project = state.projects.find(item => item.id === id);
  if (!project || !canAccessProject(project)) return showToast('无权查看这个项目附件', true);
  if (currentPdfObjectUrl) URL.revokeObjectURL(currentPdfObjectUrl);
  currentPdfObjectUrl = null;
  let previewBlob = null;
  let cloudError = null;
  const frame = document.querySelector('#pdfFrame');
  const pages = document.querySelector('#pdfPages');
  const loading = document.querySelector('#pdfLoading');
  const openLink = document.querySelector('#pdfOpenExternal');
  const downloadLink = document.querySelector('#pdfDownload');
  document.querySelector('#pdfTitle').textContent = project.attachmentName || 'PDF 受理单';
  openLink.hidden = true;
  downloadLink.hidden = true;
  frame.src = 'about:blank';
  frame.hidden = true;
  pages.hidden = true;
  pages.innerHTML = '';
  loading.classList.remove('error');
  loading.innerHTML = '正在读取 PDF…<small>首次从云端读取可能需要几秒钟</small>';
  loading.hidden = false;
  openModal('pdfModal');
  if (project.attachmentId) {
    try {
      const stored = await getAttachment(project.attachmentId);
      if (stored?.blob) {
        previewBlob = stored.blob.type === 'application/pdf' ? stored.blob : new Blob([stored.blob], { type: 'application/pdf' });
      }
    } catch (error) { console.warn('读取本机 PDF 缓存失败，将改用云端附件', error); }
  }
  if (!previewBlob && project.attachmentFileId) {
    try {
      const blob = await cloudApi.getAttachmentBlob(project.id);
      previewBlob = blob.type === 'application/pdf' ? blob : new Blob([blob], { type: 'application/pdf' });
    } catch (error) {
      cloudError = error;
      // 兼容尚未部署 getAttachmentContent 的旧云函数。临时地址只用于
      // fetch 读取二进制，不交给浏览器直接打开，因此不会自动触发下载。
      try {
        const temporary = await cloudApi.getAttachmentUrl(project.id);
        previewBlob = await fetchPdfBlob(temporary.url);
      } catch (fallbackError) {
        const unsupportedAction = /(?:不支持|unsupported).{0,20}action|getAttachmentContent/i.test(error?.message || '');
        cloudError = unsupportedAction
          ? new Error('线上云函数版本过旧，且云存储临时地址无法跨域读取。请重新上传部署 zsh-ledger-api 云函数后再试')
          : fallbackError;
      }
    }
  }
  if (!previewBlob && project.attachmentUrl) {
    try {
      // Older records use sample/...; preserve them after moving assets into static/.
      const url = project.attachmentUrl.startsWith('sample/')
        ? `${import.meta.env.BASE_URL}static/${project.attachmentUrl}` : project.attachmentUrl;
      previewBlob = await fetchPdfBlob(url);
    }
    catch (error) { cloudError = cloudError || error; }
  }
  if (!previewBlob) {
    const message = project.attachmentName && !project.attachmentFileId
      ? '这条旧记录只保存了上传电脑的本地 PDF，没有云端附件。请原上传者使用新版重新导入该 PDF。'
      : `云端 PDF 读取失败：${cloudError?.message || '未找到附件'}`;
    loading.textContent = message;
    loading.classList.add('error');
    loading.hidden = false;
    frame.hidden = true;
    showToast(message, true);
    return;
  }
  currentPdfObjectUrl = URL.createObjectURL(previewBlob);
  openLink.href = currentPdfObjectUrl;
  openLink.hidden = false;
  downloadLink.href = currentPdfObjectUrl;
  downloadLink.download = project.attachmentName || '台账附件.pdf';
  downloadLink.hidden = false;
  closeModal('detailModal');
  try {
    await renderPdfPages(previewBlob);
    loading.hidden = true;
    pages.hidden = false;
  } catch (error) {
    loading.textContent = `PDF 页面渲染失败：${error.message}。可使用“在新窗口预览”或“下载 PDF”。`;
    loading.classList.add('error');
    loading.hidden = false;
    pages.hidden = true;
  }
}

function showPropertyDetail(id) {
  if (canModifyAlerts()) return openPropertyEditor(id);
  const item = state.properties.find(row => row.id === id);
  if (!item) return;
  document.querySelector('#detailTitle').textContent = item.unit;
  const fields = [
    ['设备品种', item.equipmentType || '—'], ['设备代码', item.deviceCode || '—'], ['单位内编号', item.internalNo || '—'],
    ['下检日期', formatDate(item.dueDate)], ['登记证编号', item.registrationNo || '—'], ['检测机构', item.inspectionAgency || '—'],
    ['单位地址', item.address || '—'], ['维保单位', item.maintainer || '—'], ['应急电话', item.emergencyPhone || '—'],
    ['联系人', item.contact || '—'], ['联系电话', item.phone || '—'], ['联系电话1', item.phone1 || '—'],
    ['跟进状态', item.followUpStatus || '待跟进'], ['备注', item.notes || '—'], ['数据来源', item.source || '—'],
  ];
  const actions = canModifyAlerts() ? `<div class="detail-actions"><button class="button button-secondary" data-edit-property="${safe(item.id)}">补录跟进信息</button><button class="button button-danger" data-delete-property="${safe(item.id)}">删除这台设备</button></div>` : '';
  document.querySelector('#detailContent').innerHTML = `<div class="detail-grid">${fields.map(([label,value]) => `<div class="detail-cell"><span>${safe(label)}</span><strong>${safe(value)}</strong></div>`).join('')}</div>${actions}`;
  openModal('detailModal');
}

function openPropertyEditor(id) {
  if (!canModifyAlerts()) return showToast('预警台账对业务员为只读', true);
  const item = state.properties.find(row => row.id === id);
  if (!item) return showToast('未找到这条物业设备记录', true);
  const form = document.querySelector('#propertyEditForm');
  document.querySelector('#propertyEditFields').innerHTML = PROPERTY_EDIT_FIELDS.map(([name, label, type = 'text']) => `<label><span>${label}</span><input name="${name}" type="${type}" ${name === 'unit' ? 'required' : ''} /></label>`).join('');
  form.reset();
  form.elements.propertyId.value = item.id;
  document.querySelector('#propertyEditDelete').dataset.deleteProperty = item.id;
  for (const [name] of PROPERTY_EDIT_FIELDS) form.elements[name].value = item[name] ?? '';
  form.elements.followUpStatus.value = item.followUpStatus || '待跟进';
  form.elements.notes.value = item.notes || '';
  document.querySelector('#propertyEditTitle').textContent = `${item.unit} · ${item.internalNo || item.deviceCode || '设备'}`;
  closeModal('detailModal');
  openModal('propertyEditModal');
}

async function exportFullBackup() {
  if (!canExport()) throw new Error('当前账号没有导出权限');
  const projects = visibleProjects();
  const payments = visiblePayments();
  const attachmentIds = new Set(projects.map(project => project.attachmentId).filter(Boolean));
  const db = await openFilesDb();
  const attachments = await new Promise((resolve, reject) => {
    const tx = db.transaction(DB_STORE, 'readonly');
    const request = tx.objectStore(DB_STORE).getAll();
    request.onsuccess = () => resolve(request.result || []);
    request.onerror = () => reject(request.error);
  });
  db.close();
  const encoded = [];
  for (const item of attachments) {
    if (!attachmentIds.has(item.id)) continue;
    const dataUrl = await new Promise(resolve => { const reader = new FileReader(); reader.onload = () => resolve(reader.result); reader.readAsDataURL(item.blob); });
    encoded.push({ id: item.id, name: item.name, type: item.type, dataUrl, savedAt: item.savedAt });
  }
  const included = new Set(encoded.map(item => item.id));
  for (const project of projects.filter(item => item.attachmentFileId && item.attachmentId && !included.has(item.attachmentId))) {
    const temp = await cloudApi.getAttachmentUrl(project.id);
    const response = await fetch(temp.url);
    if (!response.ok) throw new Error(`读取“${project.attachmentName}”云端附件失败`);
    const blob = await response.blob();
    const dataUrl = await new Promise(resolve => { const reader = new FileReader(); reader.onload = () => resolve(reader.result); reader.readAsDataURL(blob); });
    encoded.push({ id: project.attachmentId, name: project.attachmentName, type: 'application/pdf', dataUrl, savedAt: new Date().toISOString() });
  }
  const scopedState = { ...state, projects, payments };
  downloadBlob(JSON.stringify({ version: 2, exportedAt: new Date().toISOString(), exportedBy: currentUserId, state: scopedState, attachments: encoded }, null, 2), `电梯台账备份_${currentUser().name}_${new Date().toISOString().slice(0,10)}.json`, 'application/json');
  showToast('完整备份已导出，请妥善保存');
}

async function restoreBackup(file) {
  if (!isAdmin()) throw new Error('只有管理员可以恢复完整备份');
  const backup = JSON.parse(await file.text());
  if (!backup?.state?.projects || !Array.isArray(backup.state.projects)) throw new Error('备份文件格式不正确');
  state = normalizeAppState(backup.state);
  for (const item of backup.attachments || []) {
    const response = await fetch(item.dataUrl);
    const blob = await response.blob();
    const db = await openFilesDb();
    await new Promise((resolve, reject) => {
      const tx = db.transaction(DB_STORE, 'readwrite');
      tx.objectStore(DB_STORE).put({ id: item.id, name: item.name, type: item.type, blob, savedAt: item.savedAt });
      tx.oncomplete = resolve; tx.onerror = () => reject(tx.error);
    });
    db.close();
    if (blob.size <= (CLOUD_CONFIG.maxPdfBytes || 1024 * 1024)) {
      const file = new File([blob], item.name || 'document.pdf', { type: item.type || 'application/pdf' });
      const uploaded = await cloudApi.uploadAttachment(file);
      state.projects.filter(project => project.attachmentId === item.id).forEach(project => { project.attachmentFileId = uploaded.fileID; });
    }
  }
  saveState(); renderAll(); showToast('备份已恢复');
}

async function clearAllData() {
  if (!isAdmin()) throw new Error('只有管理员可以删除全部数据');
  if (!confirm('确定一键删除全部云端数据吗？\n\n3 个账号的项目台账、物业设备、到账记录和 PDF 附件都会被删除，且无法恢复。')) return;
  for (const project of state.projects.filter(item => item.attachmentFileId)) await cloudApi.deleteAttachment(project.id);
  await clearAttachments();
  state = emptyState();
  localStorage.setItem(cacheKey(), JSON.stringify(state));
  clearTimeout(cloudSyncTimer);
  const saved = await cloudApi.save(state, cloudRevisions);
  cloudRevisions = saved.revisions || cloudRevisions;
  if (currentPdfObjectUrl) URL.revokeObjectURL(currentPdfObjectUrl);
  currentPdfObjectUrl = null;
  document.querySelectorAll('.modal-backdrop').forEach(item => { item.hidden = true; });
  renderAll();
  showToast('全部云端数据已删除');
}

function registerWebMcpTools() {
  const context = document.modelContext;
  if (!context?.registerTool) return;
  const lifecycle = new AbortController();
  const tools = [
    {
      name: 'read_ledger_summary', title: '读取台账汇总', description: '读取当前登录账号有权访问的项目数、设备数、应收、到账和近期到期数量。',
      inputSchema: { type: 'object', properties: {}, additionalProperties: false }, annotations: { readOnlyHint: true, untrustedContentHint: false },
      execute: async () => { const projects = visibleProjects(); return { user: currentUser().name, projects: projects.length, properties: state.properties.length, receivable: projects.reduce((s,p) => s + numberValue(p.receivable),0), received: projects.reduce((s,p) => s + numberValue(p.receivedAmount),0), dueWithin90Days: getReminderItems().filter(i => { const d=daysUntil(i.dueDate); return d>=0&&d<=90; }).length }; }
    },
    {
      name: 'record_bank_payment', title: '录入银行到账', description: '解析一条银行短信，自动匹配项目并更新到账状态。',
      inputSchema: { type: 'object', properties: { message: { type: 'string', minLength: 8 } }, required: ['message'], additionalProperties: false }, annotations: { readOnlyHint: false, untrustedContentHint: true },
      execute: async input => { if (!canRecordPayments()) throw new Error('只有总管理员可以录入银行到款'); if (!input || typeof input.message !== 'string') throw new Error('message 必须是银行短信文字'); const match = importPayment(input.message); return { status: match ? 'matched' : 'needs_confirmation', projectId: match?.id || null, projectName: match?.unit || null }; }
    }
  ];
  for (const tool of tools) Promise.resolve(context.registerTool(tool, { signal: lifecycle.signal })).catch(() => {});
  return () => lifecycle.abort();
}

// File picker and drag/drop share validation, permissions and one import queue.
async function importSelectedFiles(files, kind = '') {
  if (!files.length) return;
  if (!currentUserId || passwordChangeRequired) return showToast('请先完成登录和密码修改后再导入', true);
  if (importingFiles) return showToast('正在导入，请等待当前任务完成', true);
  const targetLedger = activeLedgerDefinition(); // Capture destination before asynchronous parsing.
  const accepted = files.filter(file => kind === 'excel' ? /\.xlsx$/i.test(file.name)
    : kind === 'pdf' ? /\.pdf$/i.test(file.name) : /\.(xlsx|pdf)$/i.test(file.name));
  if (accepted.length !== files.length) showToast('已跳过不支持的文件，仅支持 PDF 和 .xlsx（旧版 .xls 请先另存为 .xlsx）', true);
  if (!accepted.length) return;
  importingFiles = true;
  const hint = document.querySelector('#ledgerDropHint');
  hint.setAttribute('aria-busy', 'true');
  hint.textContent = '正在导入文件，请稍候…';
  try {
    // Process each file separately so a failed file does not discard later files.
    for (const file of accepted) {
      try {
        if (/\.xlsx$/i.test(file.name)) await importPropertyExcelFiles([file]);
        else {
          showToast(`正在导入到${targetLedger.title}：${file.name}`);
          await importPdfFiles([file], targetLedger.nature);
        }
      } catch (error) { showToast(`${file.name} 导入失败：${error.message}`, true); }
    }
  } finally {
    importingFiles = false;
    hint.setAttribute('aria-busy', 'false');
    hint.textContent = '拖入 PDF / 物业 Excel（.xlsx）即可导入，支持多个文件；也可使用上方导入按钮';
  }
}

function bindEvents() {
  document.querySelector('#ledgerMonthFilter').value = new Date().toISOString().slice(0, 7);
  document.querySelector('#loginForm').addEventListener('submit', async event => {
    event.preventDefault();
    // currentTarget is only guaranteed while the event is being dispatched.
    // Keep a stable reference before the first await so the form can be reset
    // safely after the asynchronous login and cloud hydration complete.
    const form = event.currentTarget;
    const button = form.querySelector('button');
    const values = Object.fromEntries(new FormData(form));
    button.disabled = true;
    document.querySelector('#loginMessage').classList.remove('error');
    document.querySelector('#loginMessage').textContent = '正在安全登录…';
    try {
      const data = await cloudApi.login(values.username, values.password);
      setAuthenticatedUser(data.user);
      if (passwordChangeRequired) {
        openPasswordModal(true);
        form.reset();
        return;
      }
      await hydrateFromCloud();
      if (isAdmin()) await loadManagedUsers();
      form.reset();
    } catch (error) {
      showLogin(error.message, true);
    } finally { button.disabled = !cloudApi.isConfigured; }
  });
  document.querySelector('#openPasswordChange').addEventListener('click', () => openPasswordModal(false));
  document.querySelector('#logoutButton').addEventListener('click', () => signOut());
  document.querySelector('#passwordForm').addEventListener('submit', async event => {
    event.preventDefault();
    const values = Object.fromEntries(new FormData(event.currentTarget));
    if (values.newPassword !== values.confirmPassword) return showToast('两次输入的新密码不一致', true);
    const button = event.currentTarget.querySelector('[type="submit"]');
    button.disabled = true;
    try {
      await cloudApi.changePassword(values.currentPassword, values.newPassword);
      passwordChangeRequired = false;
      closeModal('passwordModal', true);
      await signOut('密码修改成功，请使用新密码重新登录');
    } catch (error) { showToast(error.message, true); }
    finally { button.disabled = false; }
  });
  document.querySelector('#resetPasswordForm').addEventListener('submit', async event => {
    event.preventDefault();
    if (!isAdmin()) return showToast('只有总管理员可以重置密码', true);
    const values = Object.fromEntries(new FormData(event.currentTarget));
    if (values.newPassword !== values.confirmPassword) return showToast('两次输入的临时密码不一致', true);
    const button = event.currentTarget.querySelector('[type="submit"]');
    button.disabled = true;
    try {
      await cloudApi.resetPassword(values.targetUserId, values.newPassword);
      closeModal('resetPasswordModal');
      if (values.targetUserId === currentUserId) {
        await signOut('管理员密码已重置，请使用临时密码重新登录并再次修改');
        return;
      }
      await loadManagedUsers();
      showToast('密码已重置；该账号需要使用临时密码登录并再次修改');
    } catch (error) { showToast(error.message, true); }
    finally { button.disabled = false; }
  });
  document.querySelectorAll('.nav-item').forEach(button => button.addEventListener('click', () => switchView(button.dataset.view, button.dataset.ledgerKind)));
  root.addEventListener('click', async event => {
    const go = event.target.closest('[data-go]'); if (go) return switchView(go.dataset.go);
    const closer = event.target.closest('[data-close]'); if (closer) return closeModal(closer.dataset.close);
    const detail = event.target.closest('[data-detail]'); if (detail) return showProjectDetail(detail.dataset.detail);
    const property = event.target.closest('[data-property-detail]'); if (property) return showPropertyDetail(property.dataset.propertyDetail);
    const editProject = event.target.closest('[data-edit-project]'); if (editProject) return openProjectEditor(editProject.dataset.editProject);
    const editProperty = event.target.closest('[data-edit-property]'); if (editProperty) return openPropertyEditor(editProperty.dataset.editProperty);
    const pdf = event.target.closest('[data-view-pdf]'); if (pdf) return showPdf(pdf.dataset.viewPdf);
    const print = event.target.closest('[data-print-detail]'); if (print) return window.print();
    const reset = event.target.closest('[data-reset-password]');
    if (reset) {
      if (!isAdmin()) return showToast('只有总管理员可以重置密码', true);
      const user = managedUsers.find(item => item.id === reset.dataset.resetPassword);
      if (!user) return;
      const form = document.querySelector('#resetPasswordForm');
      form.reset();
      form.elements.targetUserId.value = user.id;
      form.elements.targetLabel.value = `${user.name}（${user.username}）`;
      openModal('resetPasswordModal');
      return;
    }
    const remove = event.target.closest('[data-delete-project]');
    if (remove) {
      const project = state.projects.find(item => item.id === remove.dataset.deleteProject);
      if (!project || !canAccessProject(project)) return showToast('无权删除这条项目台账', true);
      if (!confirm(`确定删除“${project.unit}”这条台账吗？`)) return;
      if (project.attachmentFileId) await cloudApi.deleteAttachment(project.id);
      await deleteAttachment(project.attachmentId);
      state.projects = state.projects.filter(item => item.id !== project.id);
      detachProjectPayments(state, project.id);
      saveState(); closeModal('detailModal'); renderAll(); showToast('台账已删除'); return;
    }
    const removeProperty = event.target.closest('[data-delete-property]');
    if (removeProperty) {
      if (!canModifyAlerts()) return showToast('预警台账对业务员为只读', true);
      const item = state.properties.find(property => property.id === removeProperty.dataset.deleteProperty);
      if (!item || !confirm(`确定删除“${item.unit}”的这台设备吗？`)) return;
      state.properties = state.properties.filter(property => property.id !== item.id);
      saveState(); closeModal('detailModal'); closeModal('propertyEditModal'); renderAll(); showToast('物业设备已删除'); return;
    }
    const deletePayment = event.target.closest('[data-delete-payment]');
    if (deletePayment) {
      if (!canRecordPayments()) return showToast('只有总管理员可以删除到账记录', true);
      const payment = state.payments.find(item => item.id === deletePayment.dataset.deletePayment);
      if (!payment) return;
      if (!confirm(`确定删除“${payment.payer}”的 ${yuan(payment.amount)} 到账记录吗？\n已关联项目将扣回本笔分配的到款金额，未关联金额不影响台账。`)) return;
      removePayment(state, payment.id);
      saveState(); renderAll(); showToast('到账记录已删除，关联到款金额已更新'); return;
    }
    const addAllocation = event.target.closest('[data-add-allocation]');
    if (addAllocation) {
      if (!canRecordPayments()) return;
      const card = addAllocation.closest('[data-payment-match]');
      card.querySelector('[data-allocation-rows]').insertAdjacentHTML('beforeend', paymentAllocationRow());
      updateAllocationTotal(card); return;
    }
    const removeAllocation = event.target.closest('[data-remove-allocation]');
    if (removeAllocation) {
      if (!canRecordPayments()) return;
      const card = removeAllocation.closest('[data-payment-match]');
      if (card.querySelectorAll('.allocation-row').length === 1) return showToast('至少保留一个关联项目', true);
      removeAllocation.closest('.allocation-row').remove();
      updateAllocationTotal(card); return;
    }
    const manualMatch = event.target.closest('[data-manual-match]');
    if (manualMatch) {
      if (!canRecordPayments()) return showToast('只有总管理员可以确认到账关联', true);
      const payment = state.payments.find(item => item.id === manualMatch.dataset.manualMatch);
      if (!payment || !visiblePayments().includes(payment)) return showToast('未找到有权访问的到账记录', true);
      const rows = [...manualMatch.closest('[data-payment-match]').querySelectorAll('.allocation-row')].map(row => ({ projectId: row.querySelector('[data-allocation-project]').value, amount: row.querySelector('[data-allocation-amount]').value }));
      try { associatePayment(state, payment.id, rows); }
      catch (error) { return showToast(error.message, true); }
      saveState(); renderAll(); showToast('到账记录已按填写金额关联台账'); return;
    }
    const toggle = event.target.closest('[data-column-group]');
    if (toggle) {
      const group = toggle.dataset.columnGroup;
      const wrapper = document.querySelector('#ledgerTableWrap');
      const hidden = wrapper.classList.toggle(`hide-${group}-columns`);
      toggle.classList.toggle('active', !hidden);
    }
  });

  document.querySelector('#ledgerEditForm').addEventListener('change', event => {
    const select = event.target.closest('[data-manual-choice]');
    if (!select) return;
    const input = event.currentTarget.elements[select.dataset.manualChoice];
    input.hidden = select.value !== 'custom';
    if (select.value === 'custom') input.focus();
    else input.value = select.value === '' ? '' : select.selectedOptions[0].textContent;
  });

  for (const id of ['excelInput', 'excelInput2', 'pdfInput', 'pdfInput2']) {
    document.getElementById(id).addEventListener('change', async event => {
      const input = event.target;
      const files = [...input.files];
      const kind = id.startsWith('excel') ? 'excel' : 'pdf';
      try { await importSelectedFiles(files, kind); }
      finally { input.value = ''; }
    });
  }
  const hasFiles = event => [...(event.dataTransfer?.types || [])].includes('Files');
  const acceptsDrop = () => Boolean(currentUserId) && !passwordChangeRequired
    && activeView === 'ledger' && activeLedgerKind === 'detection' && !importingFiles
    && ![...root.querySelectorAll('.modal-backdrop')].some(modal => !modal.hidden);
  let dragDepth = 0;
  root.addEventListener('dragenter', event => {
    if (!hasFiles(event)) return;
    event.preventDefault();
    dragDepth++;
    root.classList.toggle('is-file-dragging', acceptsDrop());
  });
  root.addEventListener('dragover', event => {
    if (!hasFiles(event)) return;
    event.preventDefault(); // Prevent the browser from opening the dropped PDF/Excel.
    event.dataTransfer.dropEffect = acceptsDrop() ? 'copy' : 'none';
  });
  root.addEventListener('dragleave', event => {
    if (!hasFiles(event)) return;
    dragDepth = Math.max(0, dragDepth - 1);
    if (!dragDepth) root.classList.remove('is-file-dragging');
  });
  root.addEventListener('drop', async event => {
    if (!hasFiles(event)) return;
    event.preventDefault();
    dragDepth = 0;
    root.classList.remove('is-file-dragging');
    if (!acceptsDrop()) {
      showToast(importingFiles ? '正在导入，请等待完成后再拖入' : '请登录并关闭弹窗，在检测台账页面拖入文件', true);
      return;
    }
    const files = [...event.dataTransfer.files];
    if (!files.length) return showToast('请拖入 PDF 或 .xlsx 文件，不支持文件夹', true);
    await importSelectedFiles(files);
  });

  ['ledgerSearch','ledgerMonthFilter','paymentFilter','followUpFilter'].forEach(id => document.getElementById(id).addEventListener('input', renderLedger));
  ['propertySearch','propertyUnitFilter','propertyMaintainerFilter','propertyStatusFilter'].forEach(id => document.getElementById(id).addEventListener('input', () => { selectedPropertyIds.clear(); renderProperties(); }));
  document.querySelector('#unmatchedList').addEventListener('input', event => {
    const card = event.target.closest('[data-payment-match]');
    if (card) updateAllocationTotal(card);
  });
  document.querySelector('#propertyTable').addEventListener('change', event => {
    const box = event.target.closest('[data-select-property]');
    if (!box || !canModifyAlerts()) return;
    if (box.checked) selectedPropertyIds.add(box.dataset.selectProperty); else selectedPropertyIds.delete(box.dataset.selectProperty);
    updatePropertySelection();
  });
  document.querySelector('#propertySelectAll').addEventListener('change', event => {
    if (!canModifyAlerts()) return;
    document.querySelectorAll('[data-select-property]').forEach(box => {
      box.checked = event.target.checked;
      if (box.checked) selectedPropertyIds.add(box.dataset.selectProperty); else selectedPropertyIds.delete(box.dataset.selectProperty);
    });
    updatePropertySelection();
  });
  document.querySelector('#deleteSelectedProperties').addEventListener('click', () => {
    if (!canModifyAlerts()) return showToast('预警台账对业务员为只读', true);
    const ids = new Set([...document.querySelectorAll('[data-select-property]:checked')].map(box => box.dataset.selectProperty));
    if (!ids.size || !confirm(`确定删除当前选中的 ${ids.size} 台物业设备吗？此操作不会删除项目台账或到账记录。`)) return;
    state.properties = state.properties.filter(item => !ids.has(item.id));
    selectedPropertyIds.clear();
    saveState(); renderAll(); showToast(`已删除 ${ids.size} 台物业设备`);
  });
  document.querySelector('#propertyReminderMonths').addEventListener('change', event => {
    if (!canModifyAlerts()) return showToast('预警台账对业务员为只读', true);
    state.settings.reminderDays = Number(event.target.value) * 30;
    saveState();
    renderProperties();
    showToast(`预警范围已设为提前 ${event.target.value} 个月`);
  });
  document.querySelector('#reminderRange').addEventListener('change', renderReminders);

  const openPaymentModal = () => canRecordPayments() ? openModal('paymentModal') : showToast('业务员只能查看到款记录', true);
  document.querySelector('#openPaymentImport').addEventListener('click', openPaymentModal);
  document.querySelector('#openPaymentImport2').addEventListener('click', openPaymentModal);
  document.querySelector('#bankMessage').addEventListener('input', event => {
    const parsed = parseBankMessage(event.target.value);
    document.querySelector('#parsePreview').textContent = parsed.amount && parsed.payer ? `识别结果：${formatDate(parsed.date)} · ${parsed.payer} · ${yuan(parsed.amount)} · ${parsed.bank || '银行未识别'}` : '输入后会识别到账日期、金额、付款单位和银行。';
  });
  document.querySelector('#confirmPaymentImport').addEventListener('click', () => {
    if (!canRecordPayments()) return showToast('业务员只能查看到款记录', true);
    try { const match = importPayment(document.querySelector('#bankMessage').value); closeModal('paymentModal'); switchView('payments'); showToast(match ? `已自动匹配：${match.unit}` : '到账已录入，但未找到可靠匹配，请人工确认'); }
    catch (error) { showToast(error.message, true); }
  });

  document.querySelector('#openManualProject').addEventListener('click', () => {
    if (!canEditManualFields()) return showToast('只有总管理员可以手工新增台账', true);
    const form = document.querySelector('#projectForm');
    form.reset();
    form.elements.date.value = new Date().toISOString().slice(0,10);
    form.elements.nature.value = activeLedgerDefinition().nature;
    form.elements.equipment.value = '乘客电梯';
    form.elements.quantity.value = 1;
    openModal('projectModal');
  });
  document.querySelector('#projectForm').addEventListener('submit', event => {
    event.preventDefault();
    if (!canEditManualFields()) return showToast('只有总管理员可以手工新增台账', true);
    const values = Object.fromEntries(new FormData(event.currentTarget));
    const quantity = numberValue(values.quantity);
    const unitPrice = numberValue(values.unitPrice);
    const total = numberValue(values.total) || quantity * unitPrice;
    state.projects.push(normalizeProjectRecord({
      id: uniqueId('project'), date: values.date, unit: values.unit.trim(), supervision: values.supervision.trim(),
      projectName: values.projectName.trim(), nature: values.nature, dueDate: values.dueDate, address: values.address.trim(),
      maintainer: values.maintainer.trim(), contact: values.contact.trim(), phone: values.phone.trim(), equipment: values.equipment.trim(),
      controlMode: values.controlMode.trim(), floors: values.floors.trim(), quantity, unitPrice, total,
      receivable: numberValue(values.receivable) || total, actual: numberValue(values.actual), receivedAmount: 0,
      owner: values.owner.trim(), source: '手动录入', createdBy: currentUserId, createdAt: new Date().toISOString(),
    }));
    saveState(); closeModal('projectModal'); renderAll(); showToast('项目台账已新增');
  });

  document.querySelector('#ledgerEditForm').addEventListener('submit', event => {
    event.preventDefault();
    if (!canEditManualFields()) return showToast('只有总管理员可以补录手工字段', true);
    const values = Object.fromEntries(new FormData(event.currentTarget));
    const project = state.projects.find(item => item.id === values.projectId);
    if (!project || !canAccessProject(project)) return showToast('无权修改这条项目台账', true);
    Object.assign(project, {
      inspector: values.inspector.trim(), zshActualPrice: numberValue(values.zshActualPrice), receivedAmount: numberValue(values.receivedAmount),
      paymentDate: values.paymentDate, paymentCounterparty: values.paymentCounterparty.trim(), paymentMethod: values.paymentMethod.trim(),
      invoice: values.invoice.trim(), report: values.report.trim(), refundAmount: numberValue(values.refundAmount),
      provincialPayment: numberValue(values.provincialPayment), followUpStatus: values.followUpStatus, notes: values.notes.trim(),
      updatedAt: new Date().toISOString(),
    });
    saveState(); closeModal('ledgerEditModal'); renderAll(); showToast('手工业务信息已保存');
  });

  document.querySelector('#propertyEditForm').addEventListener('submit', event => {
    event.preventDefault();
    if (!canModifyAlerts()) return showToast('预警台账对业务员为只读', true);
    const values = Object.fromEntries(new FormData(event.currentTarget));
    const item = state.properties.find(property => property.id === values.propertyId);
    if (!item) return showToast('未找到这条物业设备记录', true);
    if (!validInspectionDate(values.dueDate)) return showToast('请填写有效的下次检验日期', true);
    if (!values.unit.trim()) return showToast('使用单位不能为空', true);
    for (const [name] of PROPERTY_EDIT_FIELDS) item[name] = String(values[name] ?? '').trim();
    item.followUpStatus = values.followUpStatus;
    item.notes = values.notes.trim();
    item.updatedAt = new Date().toISOString();
    saveState(); closeModal('propertyEditModal'); renderAll(); showToast('设备信息已保存');
  });

  document.querySelector('#exportLedger').addEventListener('click', () => {
    if (!canExport()) return showToast('当前账号没有导出权限', true);
    const definition = activeLedgerDefinition();
    const columns = [
      ['序号', (_row, index) => index + 1], ['文件位置', 'attachmentName'], ['日期', 'date'], ['使用单位', 'unit'], ['监检', 'supervision'],
      ['项目名称', 'projectName'], ['项目地址', 'address'], ['维保单位', 'maintainer'], ['联系人及电话', project => projectContactInfo(project)],
      ['检测性质', 'nature'], ['下次检验检测日期', 'dueDate'], ['设备名称', 'equipment'], ['控制方式', 'controlMode'], ['层/站', 'floors'],
      ['数量', 'quantity'], ['应收金额', 'receivable'], ['实收金额', 'actual'],
      ['项目经办人', 'owner'], ['检验员', 'inspector'], ['中晟辉实收价格', 'zshActualPrice'], ['中晟辉到款金额', 'receivedAmount'],
      ['到款日期', 'paymentDate'], ['到款对应名', 'paymentCounterparty'], ['打款方式', 'paymentMethod'], ['发票', 'invoice'], ['报告', 'report'],
      ['中晟辉返款金额', 'refundAmount'],
    ];
    if (definition.provincialPayment && isAdmin()) columns.push(['省市打款', 'provincialPayment']);
    columns.push(['备注', 'notes']);
    const rows = visibleProjects().filter(project => projectMatchesLedger(project, definition));
    exportCsv(rows, columns, `${definition.title}_${currentUser().name}_${new Date().toISOString().slice(0,10)}.csv`);
  });
  document.querySelector('#exportProperties').addEventListener('click', () => {
    if (!canExport()) return showToast('当前账号没有导出权限', true);
    exportCsv(state.properties, [['到期年月', item => formatChineseMonth(item.dueDate)],['使用单位','unit'],['单位地址','address'],['设备品种','equipmentType'],['单位内编号','internalNo'],['设备代码','deviceCode'],['检测机构','inspectionAgency'],['登记证编号','registrationNo'],['维保单位','maintainer'],['应急电话','emergencyPhone'],['联系人','contact'],['联系电话','phone'],['联系电话1','phone1'],['备注','notes'],['跟进状态','followUpStatus']], `电梯检测汇总预警_${new Date().toISOString().slice(0,10)}.csv`);
  });
  document.querySelector('#exportBackup').addEventListener('click', () => exportFullBackup().catch(error => showToast(error.message,true)));
  document.querySelector('#backupInput').addEventListener('change', async event => { try { if (event.target.files[0]) await restoreBackup(event.target.files[0]); } catch (error) { showToast(`恢复失败：${error.message}`, true); } event.target.value=''; });
  document.querySelector('#clearAllData').addEventListener('click', () => clearAllData().catch(error => showToast(`删除失败：${error.message}`, true)));

  document.querySelectorAll('.modal-backdrop').forEach(backdrop => backdrop.addEventListener('click', event => { if (event.target === backdrop) closeModal(backdrop.id); }));
  root.addEventListener('keydown', event => { if (event.key === 'Escape') { const open = [...document.querySelectorAll('.modal-backdrop')].reverse().find(item => !item.hidden); if (open) closeModal(open.id); } });
}

async function seedPropertySample() {
  if (cloudApi.isConfigured) return;
  if (state.properties.length || localStorage.getItem(`${STORAGE_KEY}-sample-attempted`)) return;
  localStorage.setItem(`${STORAGE_KEY}-sample-attempted`, '1');
  try {
    const response = await fetch(`${import.meta.env.BASE_URL}static/sample/权宇物业示例.xlsx`);
    if (!response.ok) return;
    const blob = await response.blob();
    const file = new File([blob], '权宇物业示例.xlsx', { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    const result = await importPropertyExcelFiles([file], true);
    if (result.propertyCount) showToast(`已载入示例物业数据：${result.propertyCount} 台设备`);
  } catch (error) { console.warn('示例数据载入失败', error); }
}

bindEvents();
const unregisterTools = registerWebMcpTools();
const onAuthExpired = () => showLogin('登录已过期，请重新登录', true);
window.addEventListener('zsh-auth-expired', onAuthExpired);
initializeAuth();
return () => {
  window.removeEventListener('zsh-auth-expired', onAuthExpired);
  unregisterTools?.();
  clearTimeout(cloudSyncTimer);
  if (currentPdfObjectUrl) URL.revokeObjectURL(currentPdfObjectUrl);
};
}
