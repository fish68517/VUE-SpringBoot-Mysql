<template>
  <!-- 公共头 -->
  <header id="header">
      <section class="container">
        <h1 id="logo">
          <a href="#" title="IT在线学院">
            <img src="~/assets/img/title.png" width="150px" height="50px" alt="IT在线学院">
          </a>
        </h1>
        <div class="h-r-nsl">
          <ul class="nav">
            <router-link to="/" tag="li" active-class="current" exact>
              <a>首页</a>
            </router-link>
            <router-link to="/course" tag="li" active-class="current">
              <a>课程</a>
            </router-link>
            <router-link to="/teacher" tag="li" active-class="current">
              <a>名师</a>
            </router-link>
            <router-link to="/article" tag="li" active-class="current">
              <a>文章</a>
            </router-link>
            <router-link to="/qa" tag="li" active-class="current">
              <a>问答</a>
            </router-link>
          </ul>
          <!-- / nav -->
          <!-- / nav -->
          <ul class="h-r-login">
    <li v-if="!loginInfo.id" id="no-login">
        <a href="/login" title="登录">
            <em class="icon18 login-icon">&nbsp;</em>
            <span class="vam ml5">登录</span>
        </a>
        <a href="/register" title="注册">
            <span class="vam ml5">注册</span>
        </a>
    </li>
    <li v-if="loginInfo.id" id="is-login-one" class="mr10">
        <a id="headerMsgCountId" href="#" title="消息">
            <em class="icon18 news-icon">&nbsp;</em>
        </a>
        <q class="red-point" style="display: none">&nbsp;</q>
    </li>
    <li v-if="loginInfo.id" id="is-login-two" class="h-r-user">
        <a href="/ucenter" title>
            <img
                 :src="loginInfo.avatar"
                 width="30"
                 height="30"
                 class="vam picImg"
                 alt
                 />
            <span id="userName" class="vam disIb">{{
                loginInfo.nickname
                }}</span>
        </a>
        <a
           href="javascript:void(0);"
           title="退出"
           @click="logout()"
           class="ml5"
           >退 出</a
            >
    </li>
    <!-- /未登录显示第1 li；登录后显示第2，3 li -->
</ul>
          <aside class="h-r-search">
            <form action="#" method="post">
              <label class="h-r-s-box">
                <input type="text" placeholder="输入你想学的课程" name="queryCourse.courseName" value>
                <button type="submit" class="s-btn">
                  <em class="icon18">&nbsp;</em>
                </button>
              </label>
            </form>
          </aside>
        </div>
        <aside class="mw-nav-btn">
          <div class="mw-nav-icon"></div>
        </aside>
        <div class="clear"></div>
      </section>
    </header>
  <!-- /公共头 -->
</template>



<script>

import banner from '@/api/banner'
import index from '@/api/index'
import cookie from 'js-cookie'

export default {
  data() {
    return { 
      swiperOption: {  
        pagination: {
          el: ".swiper-pagination"
        },
        navigation: {
          nextEl: ".swiper-button-next", 
          prevEl: ".swiper-button-prev"
        }
      },
      token: '',
      loginInfo: {
        id: '',
        age: '',
        avatar: '',
        mobile: '',
        nickname: '',
        sex: ''
      },
      bannerList:[],
      teacherList: [],
      courseList: []
    }
  },
  created() {
    //获取两条banner数据
    this.getBannerList();
    //获取热门课程和讲师
    this.getHotCourseAndTeacher();
    this.showInfo();
  },
  methods: {
    getBannerList(){
      banner.getListBanner().then(resp=>{
        this.bannerList = resp.data.data.list
      })
    },
     //查询热门课程和讲师
    getHotCourseAndTeacher() {
      index.getIndexData().then((resp) => {
        this.courseList = resp.data.data.courseList;
        this.teacherList = resp.data.data.teacherList;
      })
    },
    logout() {
      //清空cookie值
      cookie.set("guli_token", "", {
        domain: "localhost",
      });
      cookie.set("guli_ucenter", "", {
        domain: "localhost",
      })
      //跳转页面到登录
      this.$router.push({ path: "/login" });
    },
     //创建方法从cookie中获取信息
    showInfo() {
      //从cookie中获取信息
      var userStr = cookie.get("guli_ucenter");
      //转字符串转换成json对象(js对象)
      if (userStr) {
        this.loginInfo = JSON.parse(userStr);
      }
    }  
  }
};
</script>


