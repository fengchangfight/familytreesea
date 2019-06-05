<template>
  <div v-loading="wholePageLoading" class="snapshot-summary-page" v-if="staticbaseknown">
    <el-dialog
      title="序列号过期或不合法"
      :visible.sync="blockingLicenseWindow"
      width="50%"
      :close-on-press-escape=false
      :close-on-click-modal=false
      :show-close=false
      model=true>
      <el-input
      type="textarea" :row="2" placeholder="请输入序列号" v-model="licenseCode">
      </el-input>
      <span slot="footer" class="dialog-footer">
        <el-button type="success" @click="getOnlineLicense()">获取序列号</el-button>
        <el-button type="primary" @click="updateLicense()">更新</el-button>
      </span>
    </el-dialog>
    <div class="snapshot-summary-content">
      <div class="center-content"  v-lazy-load-bg="static_base+'/imgs/snapback.jpg'" >
        <div class="place-taker"></div>
        <div v-if="mysnapshots.length<1" id="snap-empty-placeholder"></div>
        <div v-if="mysnapshots.length>0" id="snap-title"></div>
        <div class="snapshot-item-holder">

          <ul>
              <li  v-for="item in mysnapshots" v-bind:key="item.id" >
                <div class="icon-holder"></div>
                <h3>{{item.name}}</h3>
                <p>为&quot;{{item.originFtName}}&quot;于{{item.createdTime}}创建</p>
                <div @click="deleteSnapshot(item.id)" class="del-icon"></div>
              </li>
            </ul>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import {AXIOS} from '~/common/http-commons'
import staticbase from '~/mixins/staticbase'
export default {
    name: 'SnapshotSummary',
    mixins: [staticbase],
    data() {
      return {
        blockingLicenseWindow: false,
        licenseCode:'',
        staticbaseknown: false,
        static_base: '.',
        rest_base: '.',
        wholePageLoading: false,
        mysnapshots: []
      }
    },
    methods: {
      loadSnapshot () {
        AXIOS.get('/api/v1/familytree/snapshot').then(response =>{
          if(response.data.ok==true){
            console.log(response.data.data);
            this.mysnapshots = response.data.data
          }else{
            this.$notify.error({
              title: '错误',
              message: '载入快照数据错误'
            });
            location.reload();
          }
        }).catch(e=>{
          this.$notify.error({
            title: '错误',
            message: '载入快照数据错误'
          });
          location.reload();
        })
      },
      deleteSnapshot(val){
        this.$confirm('删除不可恢复，确认删除快照？')
        .then(_ => {
          this.wholePageLoading = true;
          AXIOS.delete('/api/v1/familytree/snapshot/'+val).then(response => {
            if(response.data.ok==true){
              this.$notify({
                    title: '成功',
                    message: '删除快照成功',
                    type: 'success'
              });
              this.wholePageLoading = false;
              this.loadSnapshot();
            }else{
              this.$notify.error({
                    title: '错误',
                    message: '删除快照出错'
              });
              this.wholePageLoading = false;
            }
          }).catch(e => {
            this.wholePageLoading = false;
          })
          done();
        }).catch(_ => {

        });

      }


    },
    created () {
      this.getStaticBase();
    },
    mounted () {
      this.loadSnapshot();
      this.checkLicense();
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.snapshot-summary-page{
  flex-flow: column;
  width: 100%;
  display: flex;
  left: 0;
  position:absolute;
}

.snapshot-summary-content {
  width: 100%;
  min-height: 700px;

  background-color: #f1f4f5;
}

.center-content {
  max-width: 800px;
  min-height: 700px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  background-color: white;
  background-size: 100% 100%;
}

.snapshot-item-holder{
  width: 500px;
  margin: 0 auto;
}

ul {
  list-style-type: none;
  width: 500px;
}

h3 {
  font: bold 20px/1.5 Helvetica, Verdana, sans-serif;
}

li .icon-holder{
  float: left;
  margin: 0 15px 0 0;
}

li p {
  font: 200 12px/1.5 Georgia, Times New Roman, serif;
}

li {
  padding: 10px;
  overflow: auto;
  position: relative;
}

li:hover {
  background: #eee;
  cursor: pointer;
}

.icon-holder{
  width: 50px;
  height: 50px;
  background-image: url('~/assets/backupstore.png');
  background-size: 100% 100%;
  border: none;
}

.del-icon{
  position: absolute;
  background:url('~/assets/delete20.png') no-repeat;
  background-size: 100% 100%;
  width:20px;
  height:20px;
  top:25px;
  left: 400px;
}
#snap-empty-placeholder{
  width: 400px;
  height: 108px;
  margin-left: 130px;
  background-image: url('~/assets/zanwukuaizhao.png');
  background-size: 100% 100%;
}
.place-taker{
  width: 100%;
  height: 100px;

}

#snap-title{
  width: 270px;
  height: 100px;
  background-image: url('~/assets/snapshottitle.png');
  background-size: 100% 100%;
  margin: 0 auto;
}

.bottom{
  flex: 1;
  background-color: #F3F0EC;
}
</style>
