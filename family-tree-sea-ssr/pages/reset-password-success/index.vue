<template>
  <div class="reset-password-success-page">
    <div class="reset-password-success-content">
       <p>重置密码成功，正在跳转...</p>
    </div>


  </div>
</template>

<script>
import {AXIOS} from '~/common/http-commons'
export default {
    name: 'ResetPasswordSuccess',
    data() {
      return {

      }
    },
    methods: {
    },
    created () {

    },
    mounted () {
      var count = 3;
      var storeThis = this;
      var refreshIntervalId = setInterval(function() {
          count = count - 1;
          if(count==0){
            clearInterval(refreshIntervalId);
            storeThis.$router.push("/login");
          }
      }, 1000);
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.reset-password-success-page{
  flex-flow: column;
  width: 100%;
  height: 100vh;
  display: flex;
  left: 0;
  position:absolute;
}

.reset-password-success-content {
  width: 100%;
  min-height: 700px;
  height: 700px;
  background-color: #f1f4f5;
}

.bottom{
  flex: 1;
  background-color: #F3F0EC;
}
</style>
