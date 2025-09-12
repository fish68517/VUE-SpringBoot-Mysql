<template>
  <div class="community-container">
    <!-- 头部标题 -->
    <header class="comm-title">
      <h2 class="tac">
        <span class="c-333">交流社区</span>
      </h2>
    </header>

    <!-- 发帖区域 -->
    <div class="post-box bg-white">
      <el-input
        v-model="newPost.content"
        type="textarea"
        :rows="3"
        placeholder="分享你的想法..."
        class="mb-10"
      ></el-input>
      <div class="tar">
        <el-button type="primary" @click="submitPost">发布</el-button>
      </div>
    </div>

    <!-- 帖子列表 -->
    <article class="post-list mt-20">
      <div v-for="post in postList" :key="post.id" class="post-item bg-white">
        <div class="post-header">
          <img :src="post.avatar" class="avatar" alt="用户头像" />
          <div class="user-info">
            <h3 class="user-name">{{ post.nickname }}</h3>
            <p class="post-time">{{ post.createTime }}</p>
          </div>
        </div>
        <div class="post-content">
          <p>{{ post.content }}</p>
        </div>
        <div class="post-footer">
          <span class="action-btn" @click="likePost(post.id)">
            <i class="iconfont icon-like"></i> {{ post.likeCount }}
          </span>
          <span class="action-btn" @click="showComments(post)">
            <i class="iconfont icon-comment"></i> {{ post.commentCount }}
          </span>
        </div>

        <!-- 评论区域 -->
        <div v-if="post.showComments" class="comment-section">
          <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
            <img :src="comment.avatar" class="comment-avatar" alt="评论者头像" />
            <div class="comment-content">
              <strong>{{ comment.nickname }}：</strong>
              <span>{{ comment.content }}</span>
              <p class="comment-time">{{ comment.createTime }}</p>
            </div>
          </div>
          <div class="new-comment mt-10">
            <el-input
              v-model="post.newComment"
              placeholder="写下你的评论..."
              size="small"
            ></el-input>
            <el-button
              type="text"
              size="small"
              class="mt-5"
              @click="submitComment(post)"
            >
              发布
            </el-button>
          </div>
        </div>
      </div>
    </article>
  </div>
</template>

<script>
export default {
  data() {
    return {
      newPost: {
        content: ""
      },
      postList: [
        {
          id: 1,
          nickname: "技术达人",
          avatar: require("@/assets/img/avatar-boy.gif"),
          content: "今天分享一个Vue性能优化的小技巧，使用v-once指令可以显著减少不必要的DOM更新！",
          createTime: "2小时前",
          likeCount: 24,
          commentCount: 8,
          showComments: false,
          comments: [
            {
              id: 101,
              nickname: "前端小白",
              avatar: require("@/assets/img/avatar-boy.gif"),
              content: "感谢分享，学到了！",
              createTime: "1小时前"
            }
          ],
          newComment: ""
        },
        {
          id: 2,
          nickname: "架构师老王",
          avatar: require("@/assets/img/avatar-boy.gif"),
          content: "微服务架构在大型项目中的应用实践，有疑问的可以留言讨论",
          createTime: "5小时前",
          likeCount: 42,
          commentCount: 15,
          showComments: false,
          comments: [],
          newComment: ""
        }
      ]
    }
  },
  methods: {
    submitPost() {
      if (!this.newPost.content.trim()) {
        this.$message.warning("请输入内容")
        return
      }

      // 实际项目中调用API提交帖子
      // communityApi.createPost(this.newPost).then(response => {
      //   // 处理响应
      // })

      // 模拟添加新帖子
      this.postList.unshift({
        id: Date.now(),
        nickname: "当前用户",
        avatar: require("@/assets/img/avatar-boy.gif"),
        content: this.newPost.content,
        createTime: "刚刚",
        likeCount: 0,
        commentCount: 0,
        showComments: false,
        comments: [],
        newComment: ""
      })

      this.newPost.content = ""
      this.$message.success("发布成功")
    },

    likePost(postId) {
      const post = this.postList.find(p => p.id === postId)
      if (post) {
        post.likeCount++
      }
    },

    showComments(post) {
      post.showComments = !post.showComments
    },

    submitComment(post) {
      if (!post.newComment.trim()) {
        this.$message.warning("请输入评论内容")
        return
      }

      // 实际项目中调用API提交评论
      // communityApi.createComment(post.id, { content: post.newComment }).then(response => {
      //   // 处理响应
      // })

      // 模拟添加新评论
      post.comments.push({
        id: Date.now(),
        nickname: "当前用户",
        avatar: require("@/assets/img/avatar-boy.gif"),
        content: post.newComment,
        createTime: "刚刚"
      })

      post.commentCount++
      post.newComment = ""
    }
  }
}
</script>

<style scoped>
.community-container {
  padding: 20px;
}

.post-box {
  padding: 15px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.post-item {
  padding: 15px;
  margin-bottom: 15px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.user-name {
  font-size: 16px;
  margin: 0;
}

.post-time {
  font-size: 12px;
  color: #999;
  margin: 0;
}

.post-content {
  margin-bottom: 10px;
  font-size: 14px;
  line-height: 1.5;
}

.post-footer {
  display: flex;
  border-top: 1px solid #eee;
  padding-top: 10px;
}

.action-btn {
  display: flex;
  align-items: center;
  margin-right: 20px;
  color: #666;
  cursor: pointer;
}

.action-btn i {
  margin-right: 5px;
}

.comment-section {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #eee;
}

.comment-item {
  display: flex;
  margin-bottom: 10px;
}

.comment-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 10px;
}

.comment-content {
  flex: 1;
  font-size: 13px;
}

.comment-time {
  font-size: 12px;
  color: #999;
  margin-top: 3px;
}

.new-comment {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.mt-5 {
  margin-top: 5px;
}

.mt-10 {
  margin-top: 10px;
}

.mt-20 {
  margin-top: 20px;
}

.mb-10 {
  margin-bottom: 10px;
}

.tar {
  text-align: right;
}
</style>
