// 金额以分计算；兼容旧版一笔到款只有 projectId 的数据。
export function moneyCents(value) {
  const amount = Number(value);
  const cents = Math.round((amount + Number.EPSILON) * 100);
  if (!Number.isFinite(amount) || !Number.isSafeInteger(cents)) throw new Error('金额无效或超出范围');
  return cents;
}

export function paymentAllocations(payment) {
  if (Array.isArray(payment.allocations)) return payment.allocations;
  return payment.projectId ? [{ projectId: payment.projectId, amount: Number(payment.amount) }] : [];
}

export function allocatedCents(payment) {
  return paymentAllocations(payment).reduce((sum, item) => sum + moneyCents(item.amount), 0);
}

export function isPaymentMatched(payment) {
  return paymentAllocations(payment).length > 0 && allocatedCents(payment) === moneyCents(payment.amount);
}

function setAllocations(payment, allocations) {
  payment.allocations = allocations;
  // 仅完整单项目关联保留旧字段，多项目的权威数据在 allocations 中。
  payment.projectId = allocations.length === 1 && isPaymentMatched(payment) ? allocations[0].projectId : '';
  payment.status = isPaymentMatched(payment) ? '已确款' : allocations.length ? '部分关联' : '待确认';
}

function validateAllocations(payment, rows, projects) {
  if (!rows.length) throw new Error('请至少添加一个关联项目');
  const seen = new Set();
  const allocations = rows.map(row => {
    if (!projects.some(project => project.id === row.projectId)) throw new Error('请选择有效的对应项目');
    if (seen.has(row.projectId)) throw new Error('同一个项目不能重复选择，请合并填写金额');
    seen.add(row.projectId);
    if (!/^\d+(?:\.\d{1,2})?$/.test(String(row.amount).trim()) || moneyCents(row.amount) <= 0) {
      throw new Error('每个项目的关联金额必须大于 0，最多保留两位小数');
    }
    return { projectId: row.projectId, amount: moneyCents(row.amount) / 100 };
  });
  if (allocations.reduce((sum, item) => sum + moneyCents(item.amount), 0) !== moneyCents(payment.amount)) {
    throw new Error('各项目关联金额合计必须等于本笔到账金额');
  }
  return allocations;
}

function updateProjectAmounts(state, payment, before, after) {
  const delta = new Map();
  for (const item of before) delta.set(item.projectId, (delta.get(item.projectId) || 0) - moneyCents(item.amount));
  for (const item of after) delta.set(item.projectId, (delta.get(item.projectId) || 0) + moneyCents(item.amount));
  for (const [projectId, change] of delta) {
    if (!change) continue; // 重复确认不能重复入账，也不覆盖手工字段。
    const project = state.projects.find(item => item.id === projectId);
    if (!project) continue;
    // 只调整本次流水的差额，保留其他流水及既有手工到款。
    project.receivedAmount = Math.max(0, moneyCents(project.receivedAmount || 0) + change) / 100;
    if (change > 0) {
      project.paymentDate = payment.date;
      project.paymentCounterparty = payment.payer;
      project.paymentMethod = payment.bank ? `${payment.bank}转账` : '银行转账';
    } else if (project.paymentDate === payment.date && project.paymentCounterparty === payment.payer) {
      const remaining = state.payments.filter(item => paymentAllocations(item).some(part => part.projectId === projectId))
        .sort((a, b) => String(b.date).localeCompare(String(a.date)))[0];
      if (remaining) {
        project.paymentDate = remaining.date;
        project.paymentCounterparty = remaining.payer;
        project.paymentMethod = remaining.bank ? `${remaining.bank}转账` : '银行转账';
      } else if (!project.receivedAmount) {
        project.paymentDate = ''; project.paymentCounterparty = ''; project.paymentMethod = '';
      }
    }
  }
}

export function associatePayment(state, paymentId, rows) {
  const payment = state.payments.find(item => item.id === paymentId);
  if (!payment) throw new Error('未找到这条到账记录');
  const next = validateAllocations(payment, rows, state.projects);
  const before = paymentAllocations(payment);
  setAllocations(payment, next);
  updateProjectAmounts(state, payment, before, next);
}

export function removePayment(state, paymentId) {
  const payment = state.payments.find(item => item.id === paymentId);
  if (!payment) throw new Error('未找到这条到账记录');
  const before = paymentAllocations(payment);
  state.payments = state.payments.filter(item => item.id !== paymentId);
  updateProjectAmounts(state, payment, before, []);
}

export function detachProjectPayments(state, projectId) {
  for (const payment of state.payments) {
    const before = paymentAllocations(payment);
    if (before.some(item => item.projectId === projectId)) {
      setAllocations(payment, before.filter(item => item.projectId !== projectId));
    }
  }
}

export function validInspectionDate(value) {
  if (!value) return true; // 允许清空，回到“日期待补”。
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value) || value.startsWith('0000')) return false;
  const date = new Date(`${value}T00:00:00Z`);
  return !Number.isNaN(date.getTime()) && date.toISOString().slice(0, 10) === value;
}
