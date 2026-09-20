<template><view class="map-canvas" :prop="payload" :change:prop="map.update" /></template>
<script lang="ts">
import { defineComponent } from 'vue'
export default defineComponent({
  props: { config: { type: Object, required: true }, active: { type: Boolean, default: true } },
  emits: ['select'],
  computed: {
    payload() {
      return { ...this.config, active: this.active }
    },
  },
  methods: {
    onSelect(event: any) {
      this.$emit('select', event)
    },
  },
})
</script>
<script module="map" lang="renderjs">
export default {
 mounted(){this.update(this.prop)},
 methods:{
  update(value){if(!value)return;this.data=value;if(!value.active){this.release();return}if(!this.canvas)this.init();if(value.reset!==this.lastReset){this.zoom=1;this.offset=[0,0];this.lastReset=value.reset}if(value.zoom&&value.zoom!==this.lastZoom){this.zoom=value.zoom;this.lastZoom=value.zoom}this.draw()},
  init(){this.zoom=1;this.offset=[0,0];this.canvas=document.createElement('canvas');this.canvas.style.cssText='width:100%;height:100%;display:block;touch-action:none';this.$el.appendChild(this.canvas);this.ctx=this.canvas.getContext('2d');this.resize=()=>this.draw();window.addEventListener('resize',this.resize);if(window.ResizeObserver){this.observer=new ResizeObserver(this.resize);this.observer.observe(this.$el)};if(window.MutationObserver){this.removalObserver=new MutationObserver(()=>{if(!this.$el.isConnected)this.release()});this.removalObserver.observe(document.body,{childList:true,subtree:true})}
   this.down=e=>{if(this.data.selectionMode==='polygon'){this.$ownerInstance.callMethod('onSelect',{type:'polygonPoint',point:this.local(e)});return}this.drag={x:e.clientX,y:e.clientY,ox:this.offset[0],oy:this.offset[1],moved:false};this.canvas.setPointerCapture(e.pointerId)};
   this.move=e=>{if(!this.drag)return;if(this.data.selectionMode==='box'){this.boxEnd=this.local(e);this.draw();return}const dx=e.clientX-this.drag.x,dy=e.clientY-this.drag.y;if(Math.abs(dx)+Math.abs(dy)>5)this.drag.moved=true;this.offset=[this.drag.ox+dx,this.drag.oy+dy];this.draw()};
   this.up=e=>{if(this.drag&&this.data.selectionMode==='box'){const a=this.local({clientX:this.drag.x,clientY:this.drag.y}),b=this.local(e);if(Math.hypot(a[0]-b[0],a[1]-b[1])>3)this.$ownerInstance.callMethod('onSelect',{type:'box',polygon:[a,[b[0],a[1]],b,[a[0],b[1]]]});this.boxEnd=null}else if(this.drag&&!this.drag.moved)this.hit(e);this.drag=null};
   this.wheel=e=>{e.preventDefault();this.zoom=Math.max(.6,Math.min(3,this.zoom*(e.deltaY<0?1.12:.89)));this.draw()};
   this.canvas.addEventListener('pointerdown',this.down);this.canvas.addEventListener('pointermove',this.move);this.canvas.addEventListener('pointerup',this.up);this.canvas.addEventListener('pointercancel',this.up);this.canvas.addEventListener('wheel',this.wheel,{passive:false})
  },
  local(e){const r=this.canvas.getBoundingClientRect(),s=this.scale*this.zoom;return[(e.clientX-r.left-this.w/2-this.offset[0])/s+500,(e.clientY-r.top-this.h/2-this.offset[1])/s+300]},
  point(p){const s=this.scale*this.zoom;return [this.w/2+(p[0]-500)*s+this.offset[0],this.h/2+(p[1]-300)*s+this.offset[1]]},
  draw(){if(!this.canvas||!this.data)return;const rect=this.$el.getBoundingClientRect();this.w=rect.width;this.h=rect.height;if(!this.w||!this.h)return;const ratio=Math.min(window.devicePixelRatio||1,2);this.canvas.width=this.w*ratio;this.canvas.height=this.h*ratio;const c=this.ctx;c.setTransform(ratio,0,0,ratio,0,0);this.scale=Math.min(this.w/1000,this.h/600)*.94;const d=this.data,dark=d.theme!=='light';c.fillStyle=dark?'#0b263b':'#ecf3f8';c.fillRect(0,0,this.w,this.h);
   c.strokeStyle=dark?'rgba(86,135,170,.08)':'rgba(128,155,175,.10)';c.lineWidth=1;for(let x=0;x<this.w;x+=36){c.beginPath();c.moveTo(x,0);c.lineTo(x,this.h);c.stroke()}for(let y=0;y<this.h;y+=36){c.beginPath();c.moveTo(0,y);c.lineTo(this.w,y);c.stroke()}
   (d.regions||[]).forEach(r=>{c.beginPath();r.boundary.forEach((p,i)=>{const q=this.point(p);i?c.lineTo(...q):c.moveTo(...q)});c.closePath();c.fillStyle=dark?'rgba(20,67,95,.15)':'rgba(255,255,255,.38)';c.fill();c.strokeStyle=dark?'#245675':'#b4cedc';c.setLineDash([4,5]);c.stroke();c.setLineDash([]);const q=this.point(r.label);c.fillStyle=dark?'rgba(139,182,211,.35)':'#9fb4c5';c.font='17px sans-serif';c.textAlign='center';c.fillText(r.name,...q)});
   if(d.showPipes!==false)(d.pipes||[]).forEach(p=>{c.beginPath();p.path.forEach((v,i)=>{const q=this.point(v);i?c.lineTo(...q):c.moveTo(...q)});c.strokeStyle=d.selected===p.id?'#ffc44c':dark?'#3489ad':'#499ac5';c.lineWidth=d.selected===p.id?4:1.5;c.stroke()});
   (d.facilities||[]).forEach(f=>{const p=this.point(f.position),alert=(d.alarmIds||[]).includes(f.id),selected=f.id===d.selected||(d.selectedIds||[]).includes(f.id);c.beginPath();c.arc(p[0],p[1],selected?8:alert?5:3.5,0,Math.PI*2);c.fillStyle=alert?'#ffb632':selected?'#ffffff':dark?'#53d9eb':'#1685ef';if(alert||selected){c.shadowColor=c.fillStyle;c.shadowBlur=selected?18:10}c.fill();c.shadowBlur=0;if(selected){c.beginPath();c.arc(p[0],p[1],15,0,Math.PI*2);c.strokeStyle='#ffc44c';c.lineWidth=1;c.stroke();c.font='12px sans-serif';c.textAlign='center';const name=f.name;c.fillStyle=dark?'#092137':'#fff';c.fillRect(p[0]-c.measureText(name).width/2-10,p[1]-43,c.measureText(name).width+20,24);c.fillStyle=dark?'#e4f5ff':'#243247';c.fillText(name,p[0],p[1]-27)}});
   const poly=d.polygon||[];if(poly.length){c.beginPath();poly.forEach((p,i)=>{const q=this.point(p);i?c.lineTo(...q):c.moveTo(...q)});if(d.selectionMode!=='polygon')c.closePath();c.fillStyle='rgba(59,180,220,.18)';c.fill();c.strokeStyle='#ffcc55';c.lineWidth=2;c.stroke()}
   if(this.boxEnd&&this.drag){const a=this.point(this.local({clientX:this.drag.x,clientY:this.drag.y})),b=this.point(this.boxEnd);c.strokeStyle='#ffcc55';c.strokeRect(a[0],a[1],b[0]-a[0],b[1]-a[1])}
   if(d.track&&d.track.length){c.beginPath();d.track.forEach((p,i)=>{const q=this.point(p);i?c.lineTo(...q):c.moveTo(...q)});c.strokeStyle='#5dffaa';c.lineWidth=4;c.stroke();const q=this.point(d.track[d.track.length-1]);c.beginPath();c.arc(q[0],q[1],9,0,Math.PI*2);c.fillStyle='#5dffaa';c.fill()}
   ;(d.people||[]).forEach(p=>{const q=this.point(p.position);c.fillStyle='#cfa6ff';c.fillRect(q[0]-5,q[1]-5,10,10);c.font='11px sans-serif';c.fillText(p.name,q[0]+7,q[1]-8)});
   c.font='10px sans-serif';c.textAlign='left';c.fillStyle=dark?'#708ea6':'#6d8293';c.fillText('郑州供水示意图 · 非真实地理位置',16,this.h-16)
  },
  hit(e){const r=this.canvas.getBoundingClientRect(),x=e.clientX-r.left,y=e.clientY-r.top;let nearest=null,dist=16;(this.data.facilities||[]).forEach(f=>{const q=this.point(f.position),n=Math.hypot(q[0]-x,q[1]-y);if(n<dist){dist=n;nearest={type:'facility',id:f.id}}});if(!nearest&&this.data.showPipes!==false)(this.data.pipes||[]).forEach(p=>{for(let i=1;i<p.path.length;i++){const a=this.point(p.path[i-1]),b=this.point(p.path[i]),dx=b[0]-a[0],dy=b[1]-a[1],t=Math.max(0,Math.min(1,((x-a[0])*dx+(y-a[1])*dy)/(dx*dx+dy*dy||1))),distance=Math.hypot(x-a[0]-t*dx,y-a[1]-t*dy);if(distance<6&&distance<dist){dist=distance;nearest={type:'pipe',id:p.id}}}});(this.data.people||[]).forEach(p=>{const q=this.point(p.position);if(Math.hypot(q[0]-x,q[1]-y)<12)nearest={type:'person',id:p.id}});if(nearest)this.$ownerInstance.callMethod('onSelect',nearest)},
  release(){if(this.removalObserver)this.removalObserver.disconnect();if(this.observer)this.observer.disconnect();if(this.resize)window.removeEventListener('resize',this.resize);if(this.canvas){this.canvas.removeEventListener('pointerdown',this.down);this.canvas.removeEventListener('pointermove',this.move);this.canvas.removeEventListener('pointerup',this.up);this.canvas.removeEventListener('pointercancel',this.up);this.canvas.removeEventListener('wheel',this.wheel);this.canvas.remove();this.canvas=null}}
 }
}
</script>
<style>
.map-canvas {
  width: 100%;
  height: 100%;
  min-height: 320px;
  overflow: hidden;
  border-radius: 4px;
}
</style>
