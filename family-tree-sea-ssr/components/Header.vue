<template>
  <div class="header">
    <b-navbar toggleable="md" type="dark" variant="dark">
      <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>
      <!-- <b-navbar-brand href="#">家谱海</b-navbar-brand> -->
      <b-navbar-brand href="#">
        <img src="../assets/logo_local.png" v-on:click="goPage('/')" class="d-inline-block align-top" alt="LOGO">
     </b-navbar-brand>
      <b-collapse is-nav id="nav_collapse">
        <b-navbar-nav>
          <b-nav-item ></b-nav-item>
          <b-nav-item ></b-nav-item>
          <b-nav-item v-on:click="goPage('/working')">主面板</b-nav-item>
          <b-nav-item ></b-nav-item>
          <b-nav-item ></b-nav-item>
          <b-nav-item v-if="whoami!=null && whoami.length>0" v-on:click="goPage('/familytree')">我的家谱</b-nav-item>
          <b-nav-item v-if="whoami!=null && whoami.length>0"  ></b-nav-item>
          <b-nav-item v-if="whoami!=null && whoami.length>0"  ></b-nav-item>
          <b-nav-item v-if="whoami!=null && whoami.length>0" v-on:click="goPage('/snapshot-summary')">我的快照</b-nav-item>
          <b-nav-item v-if="whoami!=null && whoami.length>0"  ></b-nav-item>
          <b-nav-item  ></b-nav-item>
          <b-nav-item v-on:click="goPage('/about')">关于我们</b-nav-item>
          <b-nav-item ></b-nav-item>
          <b-nav-item ></b-nav-item>
          <b-nav-item v-on:click="goPage('/contact')">联系我们</b-nav-item>
          <b-nav-item ></b-nav-item>
          <b-nav-item ></b-nav-item>
        </b-navbar-nav>
        <!-- Right aligned nav items -->
        <b-navbar-nav class="ml-auto">
          <b-nav-item v-if="whoami.length>0">
            <div v-lazy-load-bg="profile_image" style="width:25px;height:25px" v-on:click="goPage('/profile')" ></div>
          </b-nav-item>

          <b-nav-item-dropdown right>
            <!-- Using button-content slot -->
            <template  slot="button-content">
              <em v-if="whoami!=null && whoami.length>0">{{whoami}}</em>
              <em v-if="whoami==null || whoami.length<1">用户</em>
            </template>

            <b-dropdown-item v-if="whoami.length>0" v-on:click="goPage('/profile')" >个人信息</b-dropdown-item>
            <b-dropdown-item v-if="whoami.length>0" v-on:click="logout" >登出</b-dropdown-item>

            <b-dropdown-item v-if="whoami==null || whoami.length<1" v-on:click="goPage('/login')">登录</b-dropdown-item>
          </b-nav-item-dropdown>


        </b-navbar-nav>
      </b-collapse>
    </b-navbar>

  </div>
</template>

<script>
import {AXIOS} from '../common/http-commons'

export default {
  name: 'Header',
  data() {
      return {
        profile_image: '',
        activeIndex: '1',
        activeIndex2: '1',
        whoami: '',
        notification_count: 0,
        admin_request_count: 0,
        inbox_count: 0,
        no_auth_page: ['/','/public-resource', '/demo','/public-zone', '/about','/contact','/feedback','/latestnews','/register',
        '/register-success','/regulation','/forget-password',"/reset-password-success"]
      };
  },
  components: {
  },
  methods: {
    goDemo () {
      this.$router.push('/demo')
    },
    handleSelect(key, keyPath) {
        console.log(key, keyPath);
    },
    goPage (val) {
        this.$router.push(val)
    },
    getProfileImg(){
      AXIOS.get('/api/v1/user/profile-image').then(response => {
        this.profile_image = response.data.toString();
      }).catch(e=>{
        console.log(e);
      });
    },
    whoamifoo () {
          AXIOS.get('/api/v1/user/whoami').then(response => {
                // JSON responses are automatically parsed.

                this.whoami = response.data.data['nickname'].toString();
              }).catch(e => {
                  var noLoginRequiredPath = this.no_auth_page;
                  if(noLoginRequiredPath.indexOf(this.$route.fullPath) != -1 || this.$route.fullPath.startsWith('/confirm') || this.$route.fullPath.startsWith('/public-tree-detail')){

                  }else{
                    this.$router.push('/login')
                  }
                })
    },
    logout () {
        AXIOS.post('/logout', {}, {headers:{'Content-Type':'application/x-www-form-urlencoded'}})
          .then(response => {

            //window.location.href = '/#/login';
            location.reload();
            //this.$router.push('/login')

          })
          .catch(e => {
            //window.location.href = '/#/login';
            this.$router.push('/login')
            location.reload();
          })
      }
  },
  mounted () {
    this.whoamifoo();
  },
  created(){
    this.getProfileImg();
    this.whoamifoo()
  },
  updated() {
    this.whoamifoo()
  }
}
</script>

<style scoped>

.header{
  flex: 0 1 auto;
}



</style>
