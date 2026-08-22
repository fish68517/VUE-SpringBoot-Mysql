'use client';

import { FormEvent, useEffect, useMemo, useState } from 'react';

type View = 'home' | 'stats1' | 'stats2' | 'portfolio';
type Task = { id: number; title: string; category: string; time: string; color: string; done: boolean };

const initialTasks: Task[] = [
  { id: 1, title: '整理本周项目进度', category: '工作', time: '09:30', color: 'violet', done: false },
  { id: 2, title: '完成界面交互检查', category: '设计', time: '11:00', color: 'orange', done: true },
  { id: 3, title: '回复重要邮件', category: '工作', time: '14:00', color: 'blue', done: false },
  { id: 4, title: '散步并整理明日计划', category: '生活', time: '18:30', color: 'green', done: false },
  { id: 5, title: '阅读 30 分钟', category: '成长', time: '20:00', color: 'pink', done: true },
];

const viewLabels: Record<View, string> = {
  home: '待办主页',
  stats1: '统计数据 1',
  stats2: '统计数据 2',
  portfolio: '作品集',
};

const barData = [18, 100, 122, 51, 0, 22, 21, 0, 20];
const weekData = [42, 68, 51, 86, 74, 112, 98];
const monthData = [72, 86, 68, 95, 81, 108, 92, 126, 101, 118, 134, 122];

function formatTimer(seconds: number) {
  const minutes = Math.floor(seconds / 60).toString().padStart(2, '0');
  const rest = (seconds % 60).toString().padStart(2, '0');
  return `${minutes}:${rest}`;
}

export default function Home() {
  const [active, setActive] = useState<View>('home');
  const [statsExpanded, setStatsExpanded] = useState(false);
  const [tasks, setTasks] = useState<Task[]>(initialTasks);
  const [storageReady, setStorageReady] = useState(false);
  const [showCompleted, setShowCompleted] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [toast, setToast] = useState('');
  const [seconds, setSeconds] = useState(25 * 60);
  const [running, setRunning] = useState(false);

  useEffect(() => {
    const saved = window.localStorage.getItem('schedule-tasks');
    if (saved) {
      try { setTasks(JSON.parse(saved) as Task[]); } catch { /* 保留初始数据 */ }
    }
    setStorageReady(true);
  }, []);

  useEffect(() => {
    if (!storageReady) return;
    window.localStorage.setItem('schedule-tasks', JSON.stringify(tasks));
  }, [storageReady, tasks]);

  useEffect(() => {
    if (!running || seconds <= 0) return;
    const timer = window.setInterval(() => setSeconds((current) => current - 1), 1000);
    return () => window.clearInterval(timer);
  }, [running, seconds]);

  useEffect(() => {
    if (seconds !== 0) return;
    setRunning(false);
    setToast('本次专注已完成，做得很好！');
    setSeconds(25 * 60);
  }, [seconds]);

  useEffect(() => {
    if (!toast) return;
    const timer = window.setTimeout(() => setToast(''), 2600);
    return () => window.clearTimeout(timer);
  }, [toast]);

  const completedCount = tasks.filter((task) => task.done).length;
  const visibleTasks = useMemo(
    () => showCompleted ? tasks : tasks.filter((task) => !task.done).slice(0, 4),
    [showCompleted, tasks],
  );

  function toggleTask(id: number) {
    setTasks((current) => current.map((task) => task.id === id ? { ...task, done: !task.done } : task));
  }

  function addTask(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const title = String(data.get('title') || '').trim();
    if (!title) return;
    const category = String(data.get('category') || '工作');
    const palette: Record<string, string> = { 工作: 'violet', 设计: 'orange', 生活: 'green', 成长: 'pink' };
    setTasks((current) => [...current, {
      id: Date.now(), title, category, time: String(data.get('time') || '09:00'), color: palette[category] || 'blue', done: false,
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
        </nav>
        <div className="sidebar-quote"><span>“</span><p>每一次专注，<br />都在靠近理想的生活。</p></div>
        <div className="profile"><span className="avatar">林</span><div><strong>林小满</strong><small>保持专注，稳步前进</small></div><button aria-label="更多账户设置">•••</button></div>
      </aside>

      <section className="workspace">
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
            seconds={seconds}
            running={running}
            onToggle={toggleTask}
            onShowAll={() => setShowCompleted((current) => !current)}
            onNew={() => setDialogOpen(true)}
            onTimer={() => setRunning((current) => !current)}
          />
        )}
        {active === 'stats1' && <StatsOverview onFocus={() => switchView('home')} />}
        {active === 'stats2' && <StatsTrend />}
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

function HomeView({ tasks, total, completed, showCompleted, seconds, running, onToggle, onShowAll, onNew, onTimer }: {
  tasks: Task[]; total: number; completed: number; showCompleted: boolean; seconds: number; running: boolean;
  onToggle: (id: number) => void; onShowAll: () => void; onNew: () => void; onTimer: () => void;
}) {
  const progress = total ? Math.round((completed / total) * 100) : 0;
  return <>
    <PageHeading eyebrow="TODAY OVERVIEW" title="待办主页" description="规划今天，专注每一件重要的小事。" action={<button className="primary-button" onClick={onNew}><span>＋</span> 新建待办</button>} />
    <div className="metric-grid">
      <Metric icon="▣" tone="purple" label="今日待办" value={String(total)} note={`还有 ${total - completed} 项待完成`} />
      <Metric icon="◷" tone="amber" label="专注时长" value="3.2" unit="小时" note="比昨日多 38 分钟" />
      <Metric icon="✓" tone="cyan" label="已完成" value={String(completed)} note={`完成率 ${progress}%`} />
      <Metric icon="↗" tone="green" label="连续达成" value="12" unit="天" note="本月最佳记录" />
    </div>
    <div className="dashboard-grid">
      <section className="panel task-panel">
        <div className="panel-title"><div><h3>{showCompleted ? '全部待办' : '接下来要做'}</h3><p>轻点圆圈即可更新完成状态</p></div><button onClick={onShowAll}>{showCompleted ? '收起列表' : '查看全部'} <span>→</span></button></div>
        <div className="task-list">
          {tasks.length ? tasks.map((task) => <label className={task.done ? 'task-row completed' : 'task-row'} key={task.id}><input type="checkbox" checked={task.done} onChange={() => onToggle(task.id)} /><span className="checkmark" /><span className={`task-color ${task.color}`} /><span className="task-main"><strong>{task.title}</strong><small>{task.category}</small></span><time>{task.time}</time><span className="row-more">•••</span></label>) : <div className="empty-list"><span>✓</span><p>当前待办都完成了</p></div>}
        </div>
        <button className="quick-add" onClick={onNew}>＋ 快速添加待办</button>
      </section>
      <aside className="panel focus-card"><span className="eyebrow">FOCUS MOMENT</span><h3>为重要的事情<br />留出一段安静时间</h3><div className={running ? 'timer-ring running' : 'timer-ring'} style={{ '--timer-progress': `${seconds / 1500 * 100}%` } as React.CSSProperties}><strong>{formatTimer(seconds)}</strong><span>{running ? '保持专注' : '准备开始'}</span></div><button className="focus-button" onClick={onTimer}>{running ? '暂停一下' : '开始专注'} <span>{running ? 'Ⅱ' : '▶'}</span></button><p className="focus-hint">本周已完成 6 次专注</p></aside>
    </div>
    <section className="panel weekly-panel"><div className="panel-title"><div><h3>本周节奏</h3><p>每天向前一点点，积累清晰可见</p></div><span className="trend-positive">↗ 较上周 +18%</span></div><WeeklyBars data={weekData} /></section>
  </>;
}

function Metric({ icon, tone, label, value, unit, note }: { icon: string; tone: string; label: string; value: string; unit?: string; note: string }) {
  return <article className="metric-card"><span className={`metric-icon ${tone}`}>{icon}</span><div><p>{label}</p><strong>{value}{unit && <em>{unit}</em>}</strong><small>{note}</small></div></article>;
}

function StatsOverview({ onFocus }: { onFocus: () => void }) {
  return <>
    <PageHeading eyebrow="FOCUS OVERVIEW" title="统计数据 1" description="把每一次投入，沉淀成看得见的成长。" action={<button className="outline-button">2026 年 8 月⌄</button>} />
    <section className="panel cumulative-card">
      <div className="stats-card-title"><div><span className="stats-badge">◎</span><div><span className="eyebrow">ALL-TIME FOCUS</span><h3>累计专注</h3></div></div><button aria-label="分享统计">↗</button></div>
      <div className="cumulative-grid"><div><span>专注次数</span><strong>41</strong><small>次</small></div><div><span>累计时长</span><strong>34</strong><small>小时</small><strong>32</strong><small>分钟</small></div><div><span>日均时长</span><strong>1</strong><small>小时</small><strong>32</strong><small>分钟</small></div></div>
    </section>
    <div className="stats-layout">
      <section className="panel chart-panel wide"><div className="panel-title"><div><h3>本月专注时段分布</h3><p>2026年08月 · 按开始时间统计</p></div><div className="chart-nav"><button aria-label="上个月">‹</button><button aria-label="下个月">›</button></div></div><TimeBars data={barData} /></section>
      <section className="panel today-focus"><div className="panel-title"><div><h3>今日专注</h3><p>2026-08-22</p></div><span className="soft-badge">今日</span></div><div className="today-numbers"><div><strong>3</strong><span>次</span><small>专注次数</small></div><div><strong>72</strong><span>分钟</span><small>累计时长</small></div></div><button className="primary-button" onClick={onFocus}>开始一次专注</button></section>
    </div>
    <section className="panel monthly-line"><div className="panel-title"><div><h3>月度专注统计</h3><p>最近 12 天专注分钟数</p></div><span className="trend-positive">本月共 486 分钟</span></div><LineChart data={monthData} /></section>
  </>;
}

function StatsTrend() {
  return <>
    <PageHeading eyebrow="FOCUS INSIGHTS" title="统计数据 2" description="理解你的节奏，找到更高效的专注方式。" action={<button className="outline-button">最近 30 天⌄</button>} />
    <div className="insight-metrics">
      <article className="panel insight-main"><span>本月目标完成度</span><div className="goal-content"><div className="goal-ring"><strong>78<small>%</small></strong></div><div><h3>距离目标还差 132 分钟</h3><p>已经超过上月同期 16%，继续保持当前节奏。</p><div className="mini-legend"><span><i className="purple-dot" />已完成 468 分钟</span><span><i />目标 600 分钟</span></div></div></div></article>
      <article className="panel mini-insight"><span className="metric-icon amber">♨</span><p>最长连续专注</p><strong>12<small> 天</small></strong><span className="trend-positive">个人新纪录</span></article>
      <article className="panel mini-insight"><span className="metric-icon green">⌁</span><p>最高效时段</p><strong>17<small> 点</small></strong><span className="trend-positive">平均完成率 92%</span></article>
    </div>
    <div className="stats-layout equal">
      <section className="panel chart-panel"><div className="panel-title"><div><h3>近 7 日专注趋势</h3><p>按日统计专注分钟数</p></div><span className="soft-badge">周</span></div><WeeklyBars data={weekData} tall /></section>
      <section className="panel chart-panel"><div className="panel-title"><div><h3>任务分类占比</h3><p>完成时间主要花在哪里</p></div><button className="plain-more">•••</button></div><div className="donut-wrap"><div className="donut"><div><strong>486</strong><span>分钟</span></div></div><div className="legend-list"><span><i className="legend-violet" />工作 <b>42%</b></span><span><i className="legend-orange" />设计 <b>28%</b></span><span><i className="legend-green" />成长 <b>18%</b></span><span><i className="legend-blue" />生活 <b>12%</b></span></div></div></section>
    </div>
    <section className="panel achievements"><div><span className="achievement-icon">✦</span><div><span className="eyebrow">LATEST ACHIEVEMENT</span><h3>专注探索者</h3><p>累计完成 40 次专注，新的里程碑已经点亮。</p></div></div><button>查看全部成就 →</button></section>
  </>;
}

function Portfolio() {
  return <>
    <PageHeading eyebrow="PORTFOLIO" title="作品集" description="这里将集中展示值得记录的成果与成长轨迹。" />
    <section className="panel portfolio-placeholder"><div className="portfolio-mark"><span>◇</span><i /></div><span className="eyebrow">A PLACE FOR YOUR WORK</span><h3>为好作品，留一块安静的展示空间</h3><p>页面结构已经预留。后续获得具体内容与视觉参考后，可以在这里继续完善项目卡片、分类筛选与详情展示。</p><div className="placeholder-grid"><span /><span /><span /></div></section>
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
