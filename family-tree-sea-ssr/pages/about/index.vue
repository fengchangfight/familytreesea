<template>
  <div class="about-page">

    <div class="about-content">
      <div id="introduction">
        <h3>本站概况</h3>
        <hr width="690px"/>
        <div id="article">
             <p style="text-indent: 2em;">家谱海为热爱历史文化的技术青年所创，建站的初衷非常简单，就是想为大家提供一个方便的在线可视化数字家谱制作工具，让传统的修谱活动变得简单有趣，让信息能够更方便地低成本地更新和维护。</p>
             <p style="text-indent: 2em;">本站既可以作为传统家谱的数字化工具，也可以作为历史爱好者的人物关系建模工具，兼具丰富性和灵活性。我们希望能够用一种人人都能参与进来的方式，帮助每个人了解自己家庭的历史，让我们更清晰地了解有哪些人，他们在过去都做过什么，跟我有什么关系。</p>
             <p style="text-indent: 2em;">本站工具在使用方式上尽力保持极简的风格，让只要会电脑基本操作的人都能零门槛使用，但如果有任何使用方式上的问题，仍然可以参考 <router-link to="/demo">演示</router-link>或者<router-link to="/contact">联系本站</router-link></p>
             <p style="text-indent: 2em;">尽管本站尽力保护用户隐私，仍有用户担心数据保存问题，解决方案之一是用户自己部署
    <span style="background-color: rgb(255, 0, 0);"><strong>家谱海私有服务器版</strong></span>，可<router-link to="/contact">联系本站</router-link>获得指南。</p>
             <p style="text-align: right;">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 最后更新：2019年3月</p>
        </div>

      </div>
    </div>

  </div>
</template>

<script>
import {AXIOS} from '~/common/http-commons'
export default {
    name: 'About',
    data() {
      return {

      }
    },
    methods: {

    },
    created () {

    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.about-page{
  flex-flow: column;
  width: 100%;
  height: 100vh;
  display: flex;
  left: 0;
  position:absolute;
}

.about-content {
  width: 100%;
  min-height: 700px;
  height: 700px;
  background-color: #f1f4f5;
}

#introduction{
  width: 700px;
  height: 500px;
  margin: 0 auto;
  margin-top: 100px;

}

h3{
  font-size: 20px;
  font-weight: normal;
  color: #0e6aad;
}

.bottom{
  flex: 1;
  background-color: #F3F0EC;
}
</style>
