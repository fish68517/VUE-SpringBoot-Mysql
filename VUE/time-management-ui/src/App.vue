<template>
  <div id="app">
    <UserList />
  </div>
</template>

<script>
import UserList from './views/UserList.vue';

export default {
  name: 'App',
  components: {
    UserList
  }
};
</script>