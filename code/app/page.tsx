'use client';

import { FormEvent, useEffect, useMemo, useState } from 'react';

type View = 'home' | 'stats1' | 'stats2' | 'portfolio';
type Task = {
  id: number;
  title: string;
  category: string;
  time: string;
  color: string;
  done: boolean;
  totalSeconds: number;
  sessions: number;
};
type PortfolioCollection = { id: number; title: string; category: string; color: string; updated: string; projectCount: number };

const initialTasks: Task[] = [
  { id: 1, title: '整理本周项目进度', category: '工作', time: '09:30', color: 'violet', done: false, totalSeconds: 0, sessions: 0 },
  { id: 2, title: '完成界面交互检查', category: '设计', time: '11:00', color: 'orange', done: true, totalSeconds: 0, sessions: 0 },
  { id: 3, title: '回复重要邮件', category: '工作', time: '14:00', color: 'blue', done: false, totalSeconds: 0, sessions: 0 },
  { id: 4, title: '散步并整理明日计划', category: '生活', time: '18:30', color: 'green', done: false, totalSeconds: 0, sessions: 0 },
  { id: 5, title: '阅读 30 分钟', category: '成长', time: '20:00', color: 'pink', done: true, totalSeconds: 0, sessions: 0 },
];

const viewLabels: Record<View, string> = {
  home: '待办主页',
  stats1: '统计数据 1',
  stats2: '统计数据 2',
  portfolio: '待办集',
};

const barData = [18, 100, 122, 51, 0, 22, 21, 0, 20];
const weekData = [42, 68, 51, 86, 74, 112, 98];
const monthData = [72, 86, 68, 95, 81, 108, 92, 126, 101, 118, 134, 122];

function formatTimer(seconds: number) {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60).toString().padStart(2, '0');
  const rest = (seconds % 60).toString().padStart(2, '0');
  return hours ? `${hours.toString().padStart(2, '0')}:${minutes}:${rest}` : `${minutes}:${rest}`;
}

function formatDuration(seconds: number) {
  if (seconds < 60) return `${seconds} 秒`;
  if (seconds < 3600) return `${Math.floor(seconds / 60)} 分 ${seconds % 60} 秒`;
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  return minutes ? `${hours} 小时 ${minutes} 分` : `${hours} 小时`;
}

export default function Home() {
  const [active, setActive] = useState<View>('home');
  const [statsExpanded, setStatsExpanded] = useState(false);
  const [tasks, setTasks] = useState<Task[]>(initialTasks);
  const [storageReady, setStorageReady] = useState(false);
  const [showCompleted, setShowCompleted] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [toast, setToast] = useState('');
  const [activeTaskId, setActiveTaskId] = useState<number | null>(null);
  const [sessionSeconds, setSessionSeconds] = useState(0);
  const [running, setRunning] = useState(false);

  useEffect(() => {
    let cancelled = false;
    window.queueMicrotask(() => {
      if (cancelled) return;
      const saved = window.localStorage.getItem('schedule-tasks');
      if (saved) {
        try {
          const restored = JSON.parse(saved) as Partial<Task>[];
          setTasks(restored.map((task) => ({
            ...task,
            totalSeconds: Number(task.totalSeconds) || 0,
            sessions: Number(task.sessions) || 0,
          })) as Task[]);
        } catch { /* 保留初始数据 */ }
      }
      setStorageReady(true);
    });
    return () => { cancelled = true; };
  }, []);

  useEffect(() => {
    if (!storageReady) return;
    window.localStorage.setItem('schedule-tasks', JSON.stringify(tasks));
  }, [storageReady, tasks]);

  useEffect(() => {
    if (!running || activeTaskId === null) return;
    const timer = window.setInterval(() => {
      setSessionSeconds((current) => current + 1);
      setTasks((current) => current.map((task) => task.id === activeTaskId
        ? { ...task, totalSeconds: task.totalSeconds + 1 }
        : task));
    }, 1000);
    return () => window.clearInterval(timer);
  }, [activeTaskId, running]);

  useEffect(() => {
    if (!toast) return;
    const timer = window.setTimeout(() => setToast(''), 2600);
    return () => window.clearTimeout(timer);
  }, [toast]);

  const completedCount = tasks.filter((task) => task.done).length;
  const totalFocusSeconds = tasks.reduce((sum, task) => sum + task.totalSeconds, 0);
  const totalSessions = tasks.reduce((sum, task) => sum + task.sessions, 0);
  const activeTask = tasks.find((task) => task.id === activeTaskId) ?? null;
  const visibleTasks = useMemo(
    () => showCompleted ? tasks : tasks.filter((task) => !task.done).slice(0, 4),
    [showCompleted, tasks],
  );

  function toggleTask(id: number) {
    setTasks((current) => current.map((task) => task.id === id ? { ...task, done: !task.done } : task));
  }

  function toggleTaskTimer(id: number) {
    const target = tasks.find((task) => task.id === id);
    if (!target) return;
    if (activeTaskId === id) {
      setRunning((current) => !current);
      setToast(running ? `“${target.title}”计时已暂停` : `继续记录“${target.title}”`);
      return;
    }
    setTasks((current) => current.map((task) => task.id === id
      ? { ...task, sessions: task.sessions + 1 }
      : task));
    setActiveTaskId(id);
    setSessionSeconds(0);
    setRunning(true);
    setToast(`开始记录“${target.title}”`);
  }

  function stopTaskTimer() {
    if (!activeTask) return;
    setRunning(false);
    setActiveTaskId(null);
    setSessionSeconds(0);
    setToast(`“${activeTask.title}”本次已累计 ${formatDuration(sessionSeconds)}`);
  }

  function addTask(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const title = String(data.get('title') || '').trim();
    if (!title) return;
    const category = String(data.get('category') || '工作');
    const palette: Record<string, string> = { 工作: 'violet', 设计: 'orange', 生活: 'green', 成长: 'pink' };
    setTasks((current) => [...current, {
      id: Date.now(), title, category, time: String(data.get('time') || '09:00'), color: palette[category] || 'blue', done: false, totalSeconds: 0, sessions: 0,
    }]);
    setDialogOpen(false);
    setToast('新待办已添加');
  }

  function switchView(view: View) {
    setActive(view);
    if (view === 'stats1' || view === 'stats2') setStatsExpanded(true);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  const viewTitle = viewLabels[active];
  const statsActive = active === 'stats1' || active === 'stats2';

  return (
    <main className="app-shell">
      <aside className="sidebar">
        <button className="brand" onClick={() => switchView('home')} aria-label="返回待办主页">
          <span className="brand-mark">✓</span><span>日程清单</span>
        </button>
        <nav className="nav-list" aria-label="主要导航">
          <button className={active === 'home' ? 'nav-item active' : 'nav-item'} onClick={() => switchView('home')}>
            <span className="nav-icon">⌂</span><span>待办主页</span>
          </button>
          <div className={statsExpanded ? 'nav-group expanded' : 'nav-group'}>
            <button
              className={statsActive ? 'nav-item stats-parent active' : 'nav-item stats-parent'}
              onClick={() => setStatsExpanded((current) => !current)}
              aria-expanded={statsExpanded}
              aria-controls="statistics-submenu"
            >
              <span className="nav-icon">◒</span><span>统计数据</span><span className="nav-chevron">⌄</span>
            </button>
            {statsExpanded && <div className="subnav-list" id="statistics-submenu">
              <button className={active === 'stats1' ? 'subnav-item active' : 'subnav-item'} onClick={() => switchView('stats1')}><span>01</span>统计数据 1</button>
              <button className={active === 'stats2' ? 'subnav-item active' : 'subnav-item'} onClick={() => switchView('stats2')}><span>02</span>统计数据 2</button>
            </div>}
          </div>
          <button className={active === 'portfolio' ? 'nav-item active' : 'nav-item'} onClick={() => switchView('portfolio')}>
            <span className="nav-icon">◇</span><span>作品集</span>
          </button>
          <button className="nav-item mobile-nav-only" onClick={() => setToast('锁机功能已预留')}>
            <span className="nav-icon">▣</span><span>锁机</span>
          </button>
          <button className="nav-item mobile-nav-only" onClick={() => setToast('个人设置功能已预留')}>
            <span className="nav-icon">●</span><span>我的</span>
          </button>
        </nav>
        <div className="sidebar-quote"><span>“</span><p>每一次专注，<br />都在靠近理想的生活。</p></div>
        <div className="profile"><span className="avatar">林</span><div><strong>林小满</strong><small>保持专注，稳步前进</small></div><button aria-label="更多账户设置">•••</button></div>
      </aside>

      <section className={active === 'portfolio' ? 'workspace portfolio-workspace' : 'workspace'}>
        <header className="topbar">
          <div><p>2026年8月22日 · 星期六</p><h1>下午好，林小满</h1></div>
          <div className="top-actions"><button aria-label="搜索">⌕</button><button className="notification" aria-label="通知">♢<span /></button><span className="header-avatar">林</span></div>
        </header>

        <div className="mobile-title"><strong>{viewTitle}</strong><span>日程清单</span></div>

        {active === 'home' && (
          <HomeView
            tasks={visibleTasks}
            total={tasks.length}
            completed={completedCount}
            showCompleted={showCompleted}
            activeTaskId={activeTaskId}
            activeTaskTitle={activeTask?.title ?? ''}
            sessionSeconds={sessionSeconds}
            totalFocusSeconds={totalFocusSeconds}
            running={running}
            onToggle={toggleTask}
            onTaskTimer={toggleTaskTimer}
            onShowAll={() => setShowCompleted((current) => !current)}
            onNew={() => setDialogOpen(true)}
            onTimer={() => activeTaskId !== null && toggleTaskTimer(activeTaskId)}
            onStopTimer={stopTaskTimer}
          />
        )}
        {active === 'stats1' && <StatsOverview totalSeconds={totalFocusSeconds} totalSessions={totalSessions} onFocus={() => switchView('home')} />}
        {active === 'stats2' && <StatsTrend tasks={tasks} />}
        {active === 'portfolio' && <Portfolio />}
      </section>

      {dialogOpen && (
        <div className="dialog-backdrop" role="presentation" onMouseDown={() => setDialogOpen(false)}>
          <section className="dialog" role="dialog" aria-modal="true" aria-labelledby="dialog-title" onMouseDown={(event) => event.stopPropagation()}>
            <div className="dialog-heading"><div><span className="eyebrow">NEW TASK</span><h2 id="dialog-title">添加一个新待办</h2></div><button onClick={() => setDialogOpen(false)} aria-label="关闭">×</button></div>
            <form onSubmit={addTask}>
              <label>待办内容<input name="title" autoFocus placeholder="例如：整理本周计划" maxLength={36} required /></label>
              <div className="form-row"><label>分类<select name="category"><option>工作</option><option>设计</option><option>生活</option><option>成长</option></select></label><label>计划时间<input name="time" type="time" defaultValue="09:00" /></label></div>
              <div className="dialog-actions"><button type="button" className="cancel-button" onClick={() => setDialogOpen(false)}>取消</button><button className="primary-button" type="submit">保存待办</button></div>
            </form>
          </section>
        </div>
      )}

      {toast && <div className="toast" role="status"><span>✓</span>{toast}</div>}
    </main>
  );
}

function PageHeading({ eyebrow, title, description, action }: { eyebrow: string; title: string; description: string; action?: React.ReactNode }) {
  return <div className="page-heading"><div><span className="eyebrow">{eyebrow}</span><h2>{title}</h2><p>{description}</p></div>{action}</div>;
}

function HomeView({ tasks, total, completed, showCompleted, activeTaskId, activeTaskTitle, sessionSeconds, totalFocusSeconds, running, onToggle, onTaskTimer, onShowAll, onNew, onTimer, onStopTimer }: {
  tasks: Task[];
  total: number;
  completed: number;
  showCompleted: boolean;
  activeTaskId: number | null;
  activeTaskTitle: string;
  sessionSeconds: number;
  totalFocusSeconds: number;
  running: boolean;
  onToggle: (id: number) => void;
  onTaskTimer: (id: number) => void;
  onShowAll: () => void;
  onNew: () => void;
  onTimer: () => void;
  onStopTimer: () => void;
}) {
  const progress = total ? Math.round((completed / total) * 100) : 0;
  const focusMetric = totalFocusSeconds < 60
    ? { value: String(totalFocusSeconds), unit: '秒' }
    : totalFocusSeconds < 3600
      ? { value: String(Math.floor(totalFocusSeconds / 60)), unit: '分钟' }
      : { value: (totalFocusSeconds / 3600).toFixed(1), unit: '小时' };
  return <>
    <PageHeading eyebrow="TODAY OVERVIEW" title="待办主页" description="规划今天，专注每一件重要的小事。" action={<button className="primary-button" onClick={onNew}><span>＋</span> 新建待办</button>} />
    <div className="metric-grid">
      <Metric icon="▣" tone="purple" label="今日待办" value={String(total)} note={`还有 ${total - completed} 项待完成`} />
      <Metric icon="◷" tone="amber" label="累计计时" value={focusMetric.value} unit={focusMetric.unit} note="按待办自动累计并保存" />
      <Metric icon="✓" tone="cyan" label="已完成" value={String(completed)} note={`完成率 ${progress}%`} />
      <Metric icon="↗" tone="green" label="连续达成" value="12" unit="天" note="本月最佳记录" />
    </div>
    <div className="dashboard-grid">
      <section className="panel task-panel">
        <div className="panel-title"><div><h3>{showCompleted ? '全部待办' : '接下来要做'}</h3><p>点击待办开始正计时，圆圈用于更新完成状态</p></div><button onClick={onShowAll}>{showCompleted ? '收起列表' : '查看全部'} <span>→</span></button></div>
        <div className="task-list">
          {tasks.length ? tasks.map((task) => {
            const isActive = activeTaskId === task.id;
            return <article className={`${task.done ? 'task-row completed' : 'task-row'}${isActive ? ' timing' : ''}`} key={task.id}>
              <label className="task-check" title={task.done ? '标记为未完成' : '标记为已完成'}><input type="checkbox" checked={task.done} onChange={() => onToggle(task.id)} /><span className="checkmark" /></label>
              <button className="task-timer-trigger" onClick={() => onTaskTimer(task.id)} aria-label={`${running && isActive ? '暂停' : '开始'} ${task.title} 计时`}>
                <span className={`task-color ${task.color}`} />
                <span className="task-main"><strong>{task.title}</strong><small>{task.category} · 累计 {formatDuration(task.totalSeconds)} · {task.sessions} 次</small></span>
              </button>
              <time>{task.time}</time>
              <button className={isActive ? 'row-timer active' : 'row-timer'} onClick={() => onTaskTimer(task.id)}>{isActive ? (running ? '暂停' : '继续') : '计时'}</button>
            </article>;
          }) : <div className="empty-list"><span>✓</span><p>当前待办都完成了</p></div>}
        </div>
        <button className="quick-add" onClick={onNew}>＋ 快速添加待办</button>
      </section>
      <aside className="panel focus-card"><span className="eyebrow">TASK TIMER</span><h3>{activeTaskTitle || '点击左侧待办'}<br />{activeTaskTitle ? '本次专注正累计到该项目' : '即可开始正计时'}</h3><div className={running ? 'timer-ring running' : 'timer-ring'} style={{ '--timer-progress': `${Math.min(sessionSeconds / 3600 * 100, 100)}%` } as React.CSSProperties}><strong>{formatTimer(sessionSeconds)}</strong><span>{running ? '正在累计' : activeTaskTitle ? '已暂停' : '等待选择'}</span></div><div className="focus-actions"><button className="focus-button" onClick={onTimer} disabled={!activeTaskTitle}>{running ? '暂停计时' : '继续计时'} <span>{running ? 'Ⅱ' : '▶'}</span></button>{activeTaskTitle && <button className="focus-stop" onClick={onStopTimer}>结束本次</button>}</div><p className="focus-hint">项目累计：{activeTaskId === null ? '选择待办后显示' : formatDuration(tasks.find((task) => task.id === activeTaskId)?.totalSeconds ?? 0)}</p></aside>
    </div>
    <section className="panel weekly-panel"><div className="panel-title"><div><h3>本周节奏</h3><p>每天向前一点点，积累清晰可见</p></div><span className="trend-positive">↗ 较上周 +18%</span></div><WeeklyBars data={weekData} /></section>
  </>;
}

function Metric({ icon, tone, label, value, unit, note }: { icon: string; tone: string; label: string; value: string; unit?: string; note: string }) {
  return <article className="metric-card"><span className={`metric-icon ${tone}`}>{icon}</span><div><p>{label}</p><strong>{value}{unit && <em>{unit}</em>}</strong><small>{note}</small></div></article>;
}

function StatsOverview({ totalSeconds, totalSessions, onFocus }: { totalSeconds: number; totalSessions: number; onFocus: () => void }) {
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const averageSeconds = totalSessions ? Math.floor(totalSeconds / totalSessions) : 0;
  return <>
    <PageHeading eyebrow="FOCUS OVERVIEW" title="统计数据 1" description="把每一次投入，沉淀成看得见的成长。" action={<button className="outline-button">2026 年 8 月⌄</button>} />
    <section className="panel cumulative-card">
      <div className="stats-card-title"><div><span className="stats-badge">◎</span><div><span className="eyebrow">ALL-TIME FOCUS</span><h3>累计专注</h3></div></div><button aria-label="分享统计">↗</button></div>
      <div className="cumulative-grid"><div><span>计时次数</span><strong>{totalSessions}</strong><small>次</small></div><div><span>累计时长</span><strong>{hours}</strong><small>小时</small><strong>{minutes}</strong><small>分钟</small></div><div><span>单次平均</span><strong>{Math.floor(averageSeconds / 60)}</strong><small>分钟</small><strong>{averageSeconds % 60}</strong><small>秒</small></div></div>
    </section>
    <div className="stats-layout">
      <section className="panel chart-panel wide"><div className="panel-title"><div><h3>本月专注时段分布</h3><p>2026年08月 · 按开始时间统计</p></div><div className="chart-nav"><button aria-label="上个月">‹</button><button aria-label="下个月">›</button></div></div><TimeBars data={barData} /></section>
      <section className="panel today-focus"><div className="panel-title"><div><h3>当前累计</h3><p>数据保存在本地浏览器</p></div><span className="soft-badge">实时</span></div><div className="today-numbers"><div><strong>{totalSessions}</strong><span>次</span><small>计时次数</small></div><div><strong>{Math.floor(totalSeconds / 60)}</strong><span>分钟</span><small>累计时长</small></div></div><button className="primary-button" onClick={onFocus}>选择待办开始计时</button></section>
    </div>
    <section className="panel monthly-line"><div className="panel-title"><div><h3>月度专注统计</h3><p>最近 12 天专注分钟数</p></div><span className="trend-positive">本月共 486 分钟</span></div><LineChart data={monthData} /></section>
  </>;
}

function StatsTrend({ tasks }: { tasks: Task[] }) {
  const chartTasks = tasks.filter((task) => task.totalSeconds > 0).sort((a, b) => b.totalSeconds - a.totalSeconds);
  const chartTotal = chartTasks.reduce((sum, task) => sum + task.totalSeconds, 0);
  const colors = ['#7160e6', '#efa76b', '#62c5a2', '#70b7e8', '#e780a7', '#9b8bf0', '#f1c45d', '#58b8c7'];
  let cursor = 0;
  const gradient = chartTasks.length
    ? `conic-gradient(${chartTasks.map((task, index) => {
      const start = cursor;
      cursor += task.totalSeconds / chartTotal * 100;
      return `${colors[index % colors.length]} ${start}% ${cursor}%`;
    }).join(', ')})`
    : 'conic-gradient(#ecebf3 0 100%)';
  return <>
    <PageHeading eyebrow="FOCUS INSIGHTS" title="统计数据 2" description="理解你的节奏，找到更高效的专注方式。" action={<button className="outline-button">最近 30 天⌄</button>} />
    <div className="insight-metrics">
      <article className="panel insight-main"><span>本月目标完成度</span><div className="goal-content"><div className="goal-ring"><strong>78<small>%</small></strong></div><div><h3>距离目标还差 132 分钟</h3><p>已经超过上月同期 16%，继续保持当前节奏。</p><div className="mini-legend"><span><i className="purple-dot" />已完成 468 分钟</span><span><i />目标 600 分钟</span></div></div></div></article>
      <article className="panel mini-insight"><span className="metric-icon amber">♨</span><p>最长连续专注</p><strong>12<small> 天</small></strong><span className="trend-positive">个人新纪录</span></article>
      <article className="panel mini-insight"><span className="metric-icon green">⌁</span><p>最高效时段</p><strong>17<small> 点</small></strong><span className="trend-positive">平均完成率 92%</span></article>
    </div>
    <div className="stats-layout equal">
      <section className="panel chart-panel"><div className="panel-title"><div><h3>近 7 日专注趋势</h3><p>按日统计专注分钟数</p></div><span className="soft-badge">周</span></div><WeeklyBars data={weekData} tall /></section>
      <section className="panel chart-panel project-time-panel"><div className="panel-title"><div><h3>项目累计时间占比</h3><p>每个待办的实际计时会实时汇总到这里</p></div><span className="soft-badge">实时</span></div><div className="donut-wrap"><div className="donut project-donut" style={{ background: gradient }}><div><strong>{formatTimer(chartTotal)}</strong><span>总累计</span></div></div><div className="legend-list project-legend">{chartTasks.length ? chartTasks.map((task, index) => <span key={task.id}><i style={{ background: colors[index % colors.length] }} /><em title={task.title}>{task.title}</em><b>{formatDuration(task.totalSeconds)}</b></span>) : <div className="chart-empty"><strong>暂无计时数据</strong><small>回到待办主页，点击任一待办开始计时。</small></div>}</div></div></section>
    </div>
    <section className="panel achievements"><div><span className="achievement-icon">✦</span><div><span className="eyebrow">LATEST ACHIEVEMENT</span><h3>专注探索者</h3><p>累计完成 40 次专注，新的里程碑已经点亮。</p></div></div><button>查看全部成就 →</button></section>
  </>;
}

function Portfolio() {
  const initialCollections: PortfolioCollection[] = [{ id: 1, title: '作业', category: '默认集合', color: 'teal', updated: '今天', projectCount: 0 }];
  const [collections, setCollections] = useState<PortfolioCollection[]>(initialCollections);
  const [storageReady, setStorageReady] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [studyMode, setStudyMode] = useState(false);
  const [historyOpen, setHistoryOpen] = useState(false);
  const [statsOpen, setStatsOpen] = useState(false);
  const [moreOpen, setMoreOpen] = useState(false);
  const [settingsId, setSettingsId] = useState<number | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [notice, setNotice] = useState('');

  useEffect(() => {
    let cancelled = false;
    window.queueMicrotask(() => {
      if (cancelled) return;
      const saved = window.localStorage.getItem('portfolio-collections');
      if (saved) {
        try { setCollections(JSON.parse(saved) as PortfolioCollection[]); } catch { /* 保留默认集合 */ }
      }
      setStorageReady(true);
    });
    return () => { cancelled = true; };
  }, []);

  useEffect(() => {
    if (storageReady) window.localStorage.setItem('portfolio-collections', JSON.stringify(collections));
  }, [collections, storageReady]);

  useEffect(() => {
    if (!notice) return;
    const timer = window.setTimeout(() => setNotice(''), 2400);
    return () => window.clearTimeout(timer);
  }, [notice]);

  function addCollection(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const title = String(data.get('title') || '').trim();
    if (!title) return;
    const category = String(data.get('category') || '自定义集合');
    const colors = ['teal', 'violet', 'orange', 'blue'];
    const item: PortfolioCollection = {
      id: Date.now(), title, category, color: colors[collections.length % colors.length], updated: '刚刚', projectCount: 0,
    };
    setCollections((current) => [...current, item]);
    setDialogOpen(false);
    setNotice('新的待办集已创建');
  }

  const selected = collections.find((item) => item.id === selectedId);

  return <>
    <section className="portfolio-screen">
      <div className="portfolio-header">
        <div className="portfolio-status"><span>5:36</span><span>◒  ···</span></div>
        <div className="portfolio-heading"><div><span className="portfolio-kicker">MY COLLECTIONS</span><h2>待办集</h2></div><div className="portfolio-system-icons"><span>◔</span><span>♧</span><span>▮</span><span>•••</span></div></div>
        <div className="portfolio-header-actions"><button className={studyMode ? 'study-mode active' : 'study-mode'} onClick={() => { setStudyMode((current) => !current); setNotice(studyMode ? '已关闭学霸模式' : '已开启学霸模式'); }}>{studyMode ? '学霸模式已开启' : '点击开启学霸模式'}</button><span className="permission-copy">必 开<br />权 限</span><button className="header-tool" onClick={() => { setHistoryOpen((current) => !current); setMoreOpen(false); }} aria-label="打开历史记录">◷</button><button className="header-tool" onClick={() => setDialogOpen(true)} aria-label="新建待办集">＋</button><button className="header-tool more" onClick={() => { setMoreOpen((current) => !current); setHistoryOpen(false); }} aria-label="更多操作">⋮</button></div>
        {historyOpen && <div className="portfolio-popover history-popover"><strong>最近访问</strong><span>今天 · 作业</span><span>昨天 · 工作安排</span><button onClick={() => setHistoryOpen(false)}>关闭</button></div>}
        {moreOpen && <div className="portfolio-popover more-popover"><button onClick={() => setNotice('排序功能已准备')}>调整排序</button><button onClick={() => setNotice('已同步本地数据')}>同步本地数据</button><button onClick={() => setMoreOpen(false)}>关闭菜单</button></div>}
      </div>

      <div className="portfolio-content">
        <div className="collection-list">
          {collections.map((collection) => <article className={selectedId === collection.id ? 'collection-row selected' : 'collection-row'} key={collection.id} onClick={() => { setSelectedId(collection.id); setStatsOpen(false); }}>
            <span className={`collection-accent ${collection.color}`} />
            <div className="collection-name"><strong>{collection.title}</strong><small>{collection.category} · {collection.updated}</small></div>
            <button className="collection-action arrow" onClick={(event) => { event.stopPropagation(); setSelectedId(collection.id); }}>›</button>
            <button className="collection-action" onClick={(event) => { event.stopPropagation(); setStatsOpen((current) => !current); setSelectedId(collection.id); }} aria-label={`${collection.title}统计`}>◔</button>
            <button className={settingsId === collection.id ? 'collection-action active' : 'collection-action'} onClick={(event) => { event.stopPropagation(); setSettingsId((current) => current === collection.id ? null : collection.id); }} aria-label={`${collection.title}设置`}>⚙</button>
            <button className="collection-action" onClick={(event) => { event.stopPropagation(); setDialogOpen(true); }} aria-label="新建待办集">＋</button>
            {settingsId === collection.id && <div className="collection-settings" onClick={(event) => event.stopPropagation()}><button onClick={() => setNotice('集合名称可在此处编辑')}>编辑集合</button><button onClick={() => setNotice('已保留集合内容')}>归档集合</button></div>}
          </article>)}
        </div>
        {statsOpen && selected && <section className="collection-stats panel"><div><span className="eyebrow">COLLECTION INSIGHTS</span><h3>{selected.title} · 数据概览</h3></div><button onClick={() => setStatsOpen(false)} aria-label="关闭统计">×</button><div className="collection-stat-grid"><div><strong>{selected.projectCount}</strong><span>项目数</span></div><div><strong>0</strong><span>已完成</span></div><div><strong>0%</strong><span>完成率</span></div></div><p>添加项目后，这里会显示当前待办集的完成趋势。</p></section>}
        {selected && !statsOpen && <section className="collection-detail panel"><div className="detail-heading"><div><button className="back-button" onClick={() => setSelectedId(null)}>‹ 返回待办集</button><span className="eyebrow">COLLECTION DETAIL</span><h3>{selected.title}</h3></div><button className="detail-add" onClick={() => setNotice('项目添加入口已准备')}>＋ 添加项目</button></div><div className="detail-empty"><span>◇</span><strong>这个待办集还没有项目</strong><p>点击“添加项目”，把需要持续跟进的事情放进这里。</p></div></section>}
        {!selected && !statsOpen && <div className="portfolio-empty"><div className="empty-orbit"><span>◇</span><i /><b /></div><h3>把相关事项放在一起</h3><p>点击上方的集合，查看其中的项目与完成进度。<br />也可以使用右侧的加号创建新的待办集。</p></div>}
      </div>
    </section>

    {dialogOpen && <div className="dialog-backdrop" role="presentation" onMouseDown={() => setDialogOpen(false)}><section className="dialog portfolio-dialog" role="dialog" aria-modal="true" aria-labelledby="portfolio-dialog-title" onMouseDown={(event) => event.stopPropagation()}><div className="dialog-heading"><div><span className="eyebrow">NEW COLLECTION</span><h2 id="portfolio-dialog-title">创建待办集</h2></div><button onClick={() => setDialogOpen(false)} aria-label="关闭">×</button></div><form onSubmit={addCollection}><label>待办集名称<input name="title" autoFocus placeholder="例如：作品整理、旅行计划" maxLength={24} required /></label><label className="portfolio-category">集合说明<select name="category"><option>自定义集合</option><option>工作安排</option><option>学习计划</option><option>生活记录</option></select></label><div className="dialog-actions"><button type="button" className="cancel-button" onClick={() => setDialogOpen(false)}>取消</button><button className="primary-button" type="submit">创建待办集</button></div></form></section></div>}
    {notice && <div className="toast portfolio-toast" role="status"><span>✓</span>{notice}</div>}
  </>;
}

function TimeBars({ data }: { data: number[] }) {
  const labels = ['15点', '16点', '17点', '18点', '19点', '20点', '21点', '22点', '23点'];
  return <div className="time-chart"><div className="y-axis"><span>120 分钟</span><span>90 分钟</span><span>60 分钟</span><span>30 分钟</span><span>0</span></div><div className="bar-field">{data.map((value, index) => <div className="time-column" key={labels[index]}><span className="bar-value">{value || ''}</span><i style={{ height: `${Math.max(value / 1.22, 2)}%` }} /><span>{labels[index]}</span></div>)}</div></div>;
}

function WeeklyBars({ data, tall = false }: { data: number[]; tall?: boolean }) {
  const labels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
  return <div className={tall ? 'weekly-bars tall' : 'weekly-bars'}>{data.map((value, index) => <div key={labels[index]}><span className="weekly-value">{value}</span><i style={{ height: `${value / 1.35}%` }} className={index === 5 ? 'peak' : ''} /><span>{labels[index]}</span></div>)}</div>;
}

function LineChart({ data }: { data: number[] }) {
  return <div className="line-chart"><div className="line-grid"><span /><span /><span /><span /></div><div className="line-bars">{data.map((value, index) => <div key={index}><i style={{ height: `${value / 1.45}%` }} /><span>{index < 9 ? `8-${index + 10}` : `8-${index + 10}`}</span></div>)}</div></div>;
}
