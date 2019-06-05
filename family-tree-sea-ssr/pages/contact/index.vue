<template>
  <div class="contact-page" >
    <div class="contact-content">
      <div class="center-content" >
        <table class="contact-table">
          <tr>
             <td>站长昵称</td>
             <td>蟹蟹</td>
          </tr>
          <tr>
             <td>本站邮箱</td>
             <td><a href="mailto:family_tree_sea@163.com">family_tree_sea@163.com</a></td>
          </tr>
        </table>

      </div>
    </div>
  </div>
</template>

<script>
import {AXIOS} from '~/common/http-commons'
//import staticbase from '../mixins/staticbase'
export default {
    name: 'Contact',
    data() {
      return {
        staticbaseknown: false,
        static_base: '.',
        rest_base: '.'
      }
    },
    methods: {

    },
    created () {
      //this.getStaticBase();
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.contact-page{
  flex-flow: column;
  width: 100%;
  height: 100vh;
  display: flex;
  left: 0;
  position:absolute;
}

.contact-content {
  width: 100%;
  min-height: 700px;
  height: 700px;
  background-color: #f1f4f5;
}

.center-content {
  max-width: 1200px;
  width: 1200px;
  height: 100%;
  margin: 0 auto;
  background-size: 100% 100%;
  display: flex;
  flex-direction: column;
}

#phone-holder{
  margin-top: 330px;
  margin-left: 600px;
  width: 300px;
  height: 50px;
  font-size: 30px;
}

#email-holder{
  margin-top: 55px;
  margin-left: 600px;
  width: 300px;
  height: 50px;
  font-size: 30px;
  font-family:Arial,'Times New Roman','Microsoft YaHei',SimHei;
  font:bold 30px/0.75em Arial,'Times New Roman','Microsoft YaHei',SimHei;
}

.field-name{
  font-size: 20px;
  font-family: bold;
}

.bottom{
  flex: 1;
  background-color: #F3F0EC;
}

.contact-table{
  margin: 0 auto;
  width: 300px;
  margin-top:200px;
}

table
{
  border-collapse:collapse;
}

table, td, th
{
  border:1px solid black;
}
</style>
