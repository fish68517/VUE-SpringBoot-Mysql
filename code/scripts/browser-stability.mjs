import {chromium} from 'playwright'
import fs from 'node:fs'
import assert from 'node:assert/strict'
import crypto from 'node:crypto'
const duration=Number(process.env.STABILITY_MINUTES||30)*60000,base='http://127.0.0.1:4173',out='unpackage/evidence/phase2'
fs.mkdirSync(out,{recursive:true})
const browser=await chromium.launch({channel:'chrome',headless:true}),context=await browser.newContext({viewport:{width:1440,height:1000}}),page=await context.newPage(),cdp=await context.newCDPSession(page)
await cdp.send('Performance.enable')
const errors=[],samples=[];page.on('pageerror',e=>errors.push(e.message));page.on('response',r=>{if(r.status()>=400)errors.push('HTTP '+r.status()+' '+r.url())})
const initialHash=crypto.createHash('sha256').update(fs.readFileSync('unpackage/dist/build/h5/index.html')).digest('hex')
let coldDashboardMs=0,cachedDashboardMs=0,started=Date.now(),passed=false
async function nav(path){await page.goto(base+'/#/pages/'+path);await page.locator('.page-title,.screen-title').first().waitFor()}
async function cycle(){
 await nav('inspection/replay?id=TASK-001');await page.getByText('播放轨迹',{exact:true}).click();await page.waitForTimeout(1100)
 await nav('video/index');await page.locator('video').first().evaluate(async v=>{v.muted=true;await v.play()});await page.waitForTimeout(500)
 await nav('map/index');await page.locator('.map-canvas canvas').waitFor()
 const t=Date.now();await nav('dashboard/index');await page.locator('.chart-view canvas').first().waitFor();cachedDashboardMs=Math.max(cachedDashboardMs,Date.now()-t)
 assert.equal(await page.locator('video').count(),0)
 await cdp.send('HeapProfiler.collectGarbage')
 const result=await cdp.send('Performance.getMetrics'),m=Object.fromEntries(result.metrics.map(v=>[v.name,v.value]))
 samples.push({elapsedSeconds:Math.round((Date.now()-started)/1000),heapBytes:m.JSHeapUsedSize,nodes:m.Nodes,listeners:m.JSEventListeners,documents:m.Documents,canvases:await page.locator('canvas').count()})
 fs.writeFileSync(out+'/stability-progress.json',JSON.stringify({durationMinutes:duration/60000,elapsedSeconds:Math.round((Date.now()-started)/1000),samples,errors},null,2))
}
try{
 await page.goto(base+'/#/pages/login/index');await page.getByText('管理员',{exact:true}).click();const t=Date.now();await page.locator('.login-submit').click();await page.locator('.chart-view canvas').first().waitFor();coldDashboardMs=Date.now()-t
 // Warm all tested routes before establishing the retained-resource baseline.
 await cycle();await cycle();started=Date.now()
 while(Date.now()-started<duration){await cycle();await page.waitForTimeout(Math.min(55000,Math.max(0,duration-(Date.now()-started))))}
 await cycle()
 const a=samples[2],b=samples.at(-1)
 assert.deepEqual(errors,[])
 assert.ok(b.heapBytes-a.heapBytes<40*1024*1024,'Retained heap grew by 40 MB')
 assert.ok(b.nodes-a.nodes<1000,'DOM nodes kept accumulating')
 assert.ok(b.listeners-a.listeners<150,'Listeners kept accumulating')
 assert.ok(samples.slice(2).every(s=>s.canvases===2),'Dashboard canvas count changed')
 assert.equal(crypto.createHash('sha256').update(fs.readFileSync('unpackage/dist/build/h5/index.html')).digest('hex'),initialHash,'Build changed during stability run')
 passed=true
}catch(e){errors.push(e.message.split('\n')[0]);process.exitCode=1}
finally{const report={passed,durationMinutes:duration/60000,actualSeconds:Math.round((Date.now()-started)/1000),indexSha256:initialHash,coldDashboardMs,cachedDashboardMs,samples,errors,scope:'Local Chrome H5; not Android device performance; forced GC used to compare retained resources'};fs.writeFileSync(out+'/stability.json',JSON.stringify(report,null,2));console.log(JSON.stringify({...report,samples:samples.length}));await browser.close()}
