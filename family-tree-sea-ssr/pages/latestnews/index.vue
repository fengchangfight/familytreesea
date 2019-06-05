<template>
  <div class="latestnews-page">
    <div class="latestnews-content">
      <div class="center-content">
        <div class="place-taker"></div>
        <timeline>
            <timeline-title>2018</timeline-title>
            <timeline-item color="#9dd8e0">4月28日新增小头像替换功能</timeline-item>
            <timeline-item color="#9dd8e0">4月24日公共谱评论功能上线</timeline-item>
            <timeline-item color="#9dd8e0">4月14日添加公共谱功能</timeline-item>
            <timeline-item color="#9dd8e0">4月6日支持邮箱注册</timeline-item>
            <timeline-item color="#9dd8e0">4月4日域名familytreesea.com正式启用</timeline-item>
            <timeline-item color="#9dd8e0">3月首发上线</timeline-item>
        </timeline>

      </div>
    </div>
  </div>
</template>

<script>
import {AXIOS} from '~/common/http-commons'
import { Timeline, TimelineItem, TimelineTitle } from 'vue-cute-timeline'
export default {
    name: 'LatestNews',
    components: {
      Timeline,
      TimelineItem,
      TimelineTitle
    },
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
.latestnews-page{
  flex-flow: column;
  width: 100%;
  display: flex;
  left: 0;
  position:absolute;
}

.latestnews-content {
  width: 100%;
  min-height: 700px;
  background-color: #f1f4f5;
}

.center-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
}

.place-taker{
  width: 100%;
  height: 50px;

}

.bottom{
  flex: 1;
  background-color: #F3F0EC;
}
</style>
