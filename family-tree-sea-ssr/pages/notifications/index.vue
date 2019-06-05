<template>
  <div class="notifications-page" >
    <div class="notifications-content">
        <div id='center-container'>
          <div class="place-taker">
          </div>
          <div v-for="v in comment_preview" class="card-item" v-bind:key="v.id">
            <hr width=780 />
            <div style="margin: 0 auto;  width:750px; ">
              <label style="color:#003300;">{{v.username}}</label>&nbsp;于{{v.pubDate}}&nbsp;在<a :href="v.url">“{{v.pageName}}”</a>评论了你:&nbsp;<a :href="v.url">{{v.content}}</a>
            </div>

          </div>
          <div id="pagi-container">
            <el-pagination style="margin: 0 auto" class="pagination" layout="prev, pager, next" :total="totalCount" :page-size="pageSize"
               v-on:current-change="changePage">
           </el-pagination>
         </div>
        </div>
    </div>

  </div>
</template>

<script>
import {AXIOS} from '~/common/http-commons'
import staticbase from '~/mixins/staticbase'
export default {
    name: 'Notifications',
    mixins: [staticbase],
    data() {
      return {
        totalCount: 0,
        currentPage: 1,
        pageSize: 0,
        staticbaseknown: false,
        static_base: '.',
        rest_base: '.',
        comment_preview:[]
      }
    },
    computed: {

    },
    methods: {
      changePage: function (currentPage) {
       this.currentPage = currentPage;
       this.loadCommentPreview();
      },
      loadCommentPreview(){
        var params = {};
        params.page = this.currentPage;
        AXIOS.get('/api/v1/comment/comment-notification-preview',{
            params: params
        }).then(response=>{
          if(response.data.ok==true){
            this.comment_preview = response.data.data.results;
            this.totalCount = response.data.data.total;
            this.currentPage = response.data.data.page;
            this.pageSize = Number(response.data.data.count);
            console.log(this.comment_preview);
          }
        }).catch(e=>{

        })
      }

    },
    created () {
      this.getStaticBase();
    },
    mounted() {
      this.loadCommentPreview();
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.notifications-page{
  flex-flow: column;
  width: 100%;
  display: flex;
  left: 0;
  position:absolute;
}
.bottom{
  flex: 1;
  background-color: #F3F0EC;
}

.notifications-content {
  width: 100%;
  min-height: 700px;
  background-color: #f1f4f5;
  flex: 1;
}

#center-container{
  width: 800px;
  margin: 0 auto;
  min-height: 700px;
  background-color: white;
}

.place-taker{
  width: 100%;
  height: 100px;
}
.card-item{
  width: 100%;
  height: 90px;
}
#pagi-container{
  max-width: 800px;
  margin: 0 auto;
  margin-top: 10px;
  height: 50px;
}
</style>
