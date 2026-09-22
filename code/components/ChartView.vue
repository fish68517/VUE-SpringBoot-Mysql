<template><view class="chart-view" :prop="payload" :change:prop="chart.update" /></template>
<script lang="ts">
import { defineComponent } from 'vue'
export default defineComponent({
  props: {
    labels: { type: Array, default: () => [] },
    values: { type: Array, default: () => [] },
    name: { type: String, default: '' },
    dark: Boolean,
    active: { type: Boolean, default: true },
  },
  computed: {
    payload() {
      return {
        labels: this.labels,
        values: this.values,
        name: this.name,
        dark: this.dark,
        active: this.active,
      }
    },
  },
})
</script>
<script module="chart" lang="renderjs">
import { renderHost } from '../platform/render-host.js'
import { init, use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
use([LineChart, GridComponent, TooltipComponent, CanvasRenderer])
export default {
  mounted(){this.alive=true;if(this.current)this.update(this.current)},
  methods:{
    update(value,oldValue,owner,instance){if(!value)return;this.host=renderHost(this,owner,instance);this.current=value;if(!value.active){this.release();return}this.alive=true;this.draw();if(!this.instance)requestAnimationFrame(()=>this.draw())},
    draw(){
      if(!this.current||!this.alive||!this.host||!this.host.isConnected)return;
      if(!this.instance){
        this.instance=init(this.host);
        this.resize=()=>this.instance&&this.instance.resize();
        window.addEventListener('resize',this.resize);
        if(window.ResizeObserver){this.observer=new ResizeObserver(this.resize);this.observer.observe(this.host)}
        if(window.MutationObserver){this.removalObserver=new MutationObserver(()=>{if(!this.host.isConnected)this.release()});this.removalObserver.observe(document.body,{childList:true,subtree:true})}
      }
      const v=this.current,c=v.dark?'#51d7ef':'#1685ef';
      this.instance.setOption({
        animationDuration:300,
        grid:{left:48,right:20,top:25,bottom:32},
        tooltip:{trigger:'axis',confine:true},
        xAxis:{type:'category',boundaryGap:false,data:v.labels,axisLabel:{color:v.dark?'#94b6cf':'#718096',fontSize:10},axisLine:{lineStyle:{color:v.dark?'#205a7b':'#d5dee8'}}},
        yAxis:{type:'value',name:v.name,nameTextStyle:{color:'#8ba5bb'},axisLabel:{color:v.dark?'#94b6cf':'#718096',fontSize:10},splitLine:{lineStyle:{color:v.dark?'rgba(80,160,200,.12)':'#edf2f7'}}},
        series:[{
          name:v.name,type:'line',data:v.values,smooth:true,symbol:'none',
          lineStyle:{color:c,width:2},itemStyle:{color:c},
          areaStyle:{color:{type:'linear',x:0,y:0,x2:0,y2:1,colorStops:[
            {offset:0,color:v.dark?'rgba(81,215,239,.25)':'rgba(22,133,239,.20)'},
            {offset:1,color:'rgba(22,133,239,0)'}
          ]}}
        }]
      },true)
    },
    release(){this.alive=false;if(this.observer)this.observer.disconnect();if(this.removalObserver)this.removalObserver.disconnect();if(this.resize)window.removeEventListener('resize',this.resize);if(this.instance){this.instance.dispose();this.instance=null}}
  }
}
</script>
<style>
.chart-view {
  height: 220px;
  width: 100%;
  min-width: 0;
}
</style>
