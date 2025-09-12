<template>
  <div>
    <link rel="stylesheet" href="https://g.alicdn.com/de/prismplayer/2.8.2/skins/default/aliplayer-min.css" >

    <script charset="utf-8" src="https://g.alicdn.com/de/prismplayer/2.8.2/aliplayer-min.js"/>
    <!-- 阿里云视频播放器组件 -->
    <script charset="utf-8" src="https://player.alicdn.com/aliplayer/presentation/js/aliplayercomponents.min.js"/>

    <!-- 播放器 -->
    <div id="J_prismPlayer" class="prism-player"/>
  </div>
</template>

<script>
import vodApi from '~/api/vod'

export default {
  asyncData({ params, error }) {
    return vodApi.getPlayAuthById(params.vid).then((response) => {
      // console.log(response),
          return {
          vid: params.vid,
          playAuth:  response.data.data.playAuth
          }
    })
  },
  // data() {
  //     return {
  //       playAuth: "",
  //       vid: "",
  //     };
  //   },
  //    created() {
  //     this.vid = this.$route.params.vid;
  //     this.getPlayAuth();
  //     console.log(this.playAuth);
  //   },
  //   methods: {
  //     getPlayAuth() {
  //       vodApi.getPlayAuthById(this.vid).then((resp) => {
  //         this.playAuth = resp.data.data.PlayAuth;
  //       });
  //     },
  //   },
    
  // 页面渲染之后执行
  mounted() {
    /* eslint-disable no-undef */
    new Aliplayer({
      id: 'J_prismPlayer',
      width:"1960px",
      height:"768px",
      vid: this.vid,
      playauth: this.playAuth,
      //encryptType: 1 // 当播放私有加密流时需要设置。

    }, function(player) {
      console.log('播放器创建好了。')
    })
  }
}
</script>
