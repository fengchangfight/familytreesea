<template>
  <div class="login" v-if="staticbaseknown">
    <div class="main-image" v-lazy-load-bg="static_base+'/imgs/back-home.jpg'">
      <div class="center-content" >
         <div class="login-form" >
             <div class="logo"></div>
             <div class="login-title" v-if="!showEnterButton">登录</div>
             <div v-if="!showEnterButton" @keyup.enter="login" class="username"><el-input v-model="account" placeholder="手机号/邮箱"></el-input></div>
             <div v-if="!showEnterButton" id="password-container" @keyup.enter="login" class="password"><el-input  v-model="password" type="password" placeholder="密码"></el-input></div>
             <div v-if="!showEnterButton" class="submit">
               <a class="si-go-online green-button" @click='login'>本地登录</a>
             </div>
             <div v-if="showEnterButton" class="submit">
               <a class="si-go-online green-button" @click='enterPanel'>进入面板</a>
             </div>
             <div class="submit">
               <a class="si-login blue-button" href="http://www.familytreesea.com/">在线主站</a>
             </div>
         </div>
      </div>
    </div>

  </div>
</template>

<script>
import {AXIOS} from '~/common/http-commons'
import Qs from 'qs'
import staticbase from '~/mixins/staticbase'
export default {
    name: 'Login',
    mixins: [staticbase],
    data() {
      return {
        showEnterButton: false,
        staticbaseknown: false,
        static_base: '.',
        rest_base: '.',
        dialogVisible: false,
        account: '',
        password: '',
        selectedfamilytree: '',
        whoami: ''
      }
    },
    methods: {
      goPage(val){
        this.$router.push(val);
      },
      enterPanel(){
        this.$router.push('/working')
      },
      login() {
        var data = {'username': this.account, 'password':this.password, 'timeout':1000};
        AXIOS.post('/login', Qs.stringify(data), {headers:{'Content-Type':'application/x-www-form-urlencoded'}}).then(response => {
            location.reload();
            //this.$router.push('working')
            //this.$router.push('/working');
        }).catch(e => {
          this.$notify.error({
            title: '错误',
            message: '用户名或密码错误'
          });
          this.$router.push('/login')
          console.log(e)
        })
      },
      checklogin () {
            AXIOS.get('/api/v1/user/whoami').then(response => {
                  // JSON responses are automatically parsed.

                  this.whoami = response.data.data['nickname'].toString();
                  if(this.whoami!=null && this.whoami.length>0){
                //    this.$router.push('working')
                    this.showEnterButton = true;
                  }
                }).catch(e => {
                    var noLoginRequiredPath = this.no_auth_page;
                    if(noLoginRequiredPath.indexOf(this.$route.fullPath) != -1 || this.$route.fullPath.startsWith('/confirm') || this.$route.fullPath.startsWith('/public-tree-detail')){

                    }else{
                      this.$router.push('/login')
                    }
                  })
      },
    },
    created () {
      this.getStaticBase();
    },
    mounted () {
      this.checklogin();
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.login{
  flex-flow: column;
  width: 100%;
  height: 100%;
  display: flex;
  left: 0;
  position:absolute;
}

form{
  flex: 1 1 auto;
}






.main-image {
  width: 100%;
  min-height: 700px;
  height: 700px;
  background-color: #f1f4f5;
  background-size: 100% 100%;
}

.center-content {
  max-width: 1200px;
  height: 100%;
  margin: 0 auto;

  display: flex;
  flex-direction: column;
}
.login-form{
   margin: 0 auto;
   width: 400px;
   height: 390px;
   margin-top: 140px;
}
.logo{
  background-image: url('~/assets/logo-black.png');
  background-size: 100% 100%;
  width: 120px;
  height: 40px;
  margin: 0 auto;
  margin-top: 10px;
}
.login-title{
  margin: 0 auto;
  margin-top: 10px;
  font-size: 30px;
  width: 64px;
}

.password, .submit{
  width: 300px;
  height: 60px;
  margin: 0 auto;
}

.username{
  width: 300px;
  height: 60px;
  margin: 0 auto;
  margin-top: 10px
}



.center-intro {
  max-width: 1050px;
  height: 100%;
  margin: 0 auto;
  display: flex;
}

.introduction{
  min-height: 310px;
  height: 310px;
  width: 100%;
  background-color: #f1f4f5;
}

.space {
  width: 100%;
  background-color: #f1f4f5;
  min-height: 260px;
  height: 260px;

}

.intro-1 {
  width: 33%;
  height: 100%;
}
.intro-2 {
  width: 34%;
  height: 100%;
}
.intro-3 {
  width: 33%;
  height: 100%;
}



.inner {

  height: 90%;
  padding: 0 20px;
  -webkit-box-shadow: 3px 3px 5px 6px #ccc;  /* Safari 3-4, iOS 4.0.2 - 4.2, Android 2.3+ */
  -moz-box-shadow:    3px 3px 5px 6px #ccc;  /* Firefox 3.5 - 3.6 */
  box-shadow:         3px 3px 5px 6px #ccc;
}

h3{
  padding-top: 20px;
  font-size: 28px;
  color: #76bd1d;
  font-family: Montserrat,sans-serif;
  font-weight: 400;
  line-height: 1.2em;
}

h4{
  font-family: Montserrat,sans-serif;
  font-weight: 400;
  line-height: 1.5em;
  text-transform: uppercase;
}

p.feature{
  font-weight: 400;
  font-family: "Varela Round",sans-serif;
}

.seperate-line {
    overflow: hidden;
    text-align: center;
    width: 77%;
    margin: 0 auto;
    color: #1B1908;
    font-size: 10;
}
.seperate-line:before,
.seperate-line:after {
    background-color: #000;
    content: "";
    display: inline-block;
    height: 1px;
    position: relative;
    vertical-align: middle;
    width: 50%;
}
.seperate-line:before {
    right: 0.5em;
    margin-left: -50%;
}
.seperate-line:after {
    left: 0.5em;
    margin-right: -50%;
}

.login-item.weixin {
    margin-left: 40px;
    background-image: url(http://a.xnimg.cn/wap/pcLive/src/apps/common/img/wechat.png);
    background-size: 100%;
}

.login_corp {
  height: 60px;
}
.login_corp .login-item {
    float: left;
    width: 30px;
    height: 30px;
    overflow: hidden;
    margin: 15px 8px 0;
    display: block;
}

.Third-partyi-login{
  height:100%;
  width: 60%;
  margin: 0 auto;
}
.Third-partyi-login a {
    height: 40px;
    width: 40px;
    border-radius: 50%;
}
.login-item {
    cursor: pointer;
    text-decoration: none!important;
}
.login-item.qq {
    background-image: url(http://a.xnimg.cn/wap/pcLive/src/apps/common/img/qq.png);
}
.login-item.qq {
    margin-left: 60px;
    background-size: 100%;
}
.login-item.weibo {
    background-image: url(http://a.xnimg.cn/wap/pcLive/src/apps/common/img/weibo.png);
    margin-left: 55px;
    background-size: 100%;
}


#password-container{
  background-color: yellow;
  height: 40px;
}

.bottom{
  flex: 1;
  background-color: #F3F0EC;
}
.si-login,.si-login:visited{
    padding:5px 112px;
    border-radius: 10px;
    font-size: 19px;
    text-decoration: none;
    color: white !important;
    display: inline-block;
    margin: 0 auto;
    margin-top: 17px;
}

.si-go-online,.si-go-online:visited{
    padding:5px 112px;
    border-radius: 10px;
    font-size: 19px;
    text-decoration: none;
    color: white !important;
    display: inline-block;
    margin: 0 auto;
    margin-top: 17px;
}

.blue-button{
    background-color: rgb(44, 33, 199);
}
.blue-button:hover{
    cursor: pointer;
    background-color: rgba(39, 199, 33,0.5);
    transform: translateY(-2px);
}

.green-button{
    background-color: rgb(39, 199, 33);
}
.green-button:hover{
    cursor: pointer;
    background-color: rgba(44, 33, 199,0.5);
    transform: translateY(-2px);
}

</style>
