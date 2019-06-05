<template>
  <div v-loading="wholePageLoading" class="family-tree-page" v-if="staticbaseknown">
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
    <div class="family-tree-content">
      <div class="center-content" v-lazy-load-bg="static_base+'/imgs/bookback.jpg'" >
         <div class="place-taker">
            <button @click="addFamilyTree" title="新增家谱" class="add-family-tree"></button>
         </div>
         <div v-if="myfamilytrees.length<1" id="empty-placeholder"  v-lazy-load-bg="static_base+'/imgs/zanwujiapu.png'" ></div>
         <div class="card-container"  v-bind:style="{height: (parseInt(myfamilytrees.length/4)+1)*292+100+'px'}">

           <div class="card-item" v-for="(item, index) in myfamilytrees" v-bind:key="item.id" :title="item.name" v-bind:style="{ top: parseInt(index/4)*292 + 'px', left: (index%4)*170 + 'px' }">

             <div class="book-cover"  v-lazy-load-bg="static_base+'/imgs/'+(item.isPublic==true?'oldbook_public.jpg':'oldbook.jpg')"  v-bind:class="{ viewer: item.accessRight=='viewer', admin: item.accessRight=='admin' }" @click="enterPanel(item.id)"></div>
             <p style="margin-left:10px">{{item.name.length>8?item.name.substring(0,6)+'...':item.name}}</p>
             <button v-if="item.accessRight=='creator'" @click="deleteFamilyTree(item.id)" class="delete-ft-note" title="删除"></button>
             <button @click="enterDetail(item.id)" class="enter-ft-note" title="进入家谱序"></button>
           </div>
         </div>
      </div>
    </div>

  </div>
</template>

<script>
import {AXIOS} from '~/common/http-commons'
import state from '~/common/state';
import staticbase from '~/mixins/staticbase'
export default {
    name: 'FamilyTree',
    data() {
      return {
        blockingLicenseWindow: false,
        licenseCode:'',
        staticbaseknown: false,
        static_base: '.',
        rest_base: '.',
        state,
        myfamilytrees: [],
        wholePageLoading: false
      }
    },
    mixins: [staticbase],
    methods: {
      addFamilyTree(){
        this.$router.push('/create-family-tree')
      },
      enterDetail(id){
        this.$router.push('/familytreedetail/'+id);
      },
      enterPanel(val){
        this.state.currentFt = val;
        this.$router.push('/working');
      },
      deleteFamilyTree(val){
        this.$confirm('删除不可恢复，确认删除？')
        .then(_ => {
          this.wholePageLoading = true;
          AXIOS.delete('/api/v1/familytree/'+val).then(response => {
            this.$notify({
            title: '成功',
            message: '家谱删除成功',
            type: 'success'
          });
          this.wholePageLoading = false;
          this.getMyFamilyTrees();
          }).catch(e=>{
            this.$notify.error({
                title: '错误',
                message: '删除发生了错误'
            });
            this.wholePageLoading = false;
          });
          done();
        }).catch(_ => {});
      },
      getMyFamilyTrees (){
        AXIOS.get('/api/v1/familytree/my-family-trees').then(response => {
            var arr = [];
            for(var i in response.data.data){
              arr = arr.concat(response.data.data[i].groupData);
            }
            this.myfamilytrees = arr;
         }).catch(e => {
            this.$router.push('/login')
         })
      }
    },
    created () {
      this.getStaticBase();
    },
    mounted() {
      this.getMyFamilyTrees();
      this.checkLicense();
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.family-tree-page{
  flex-flow: column;
  width: 100%;
  display: flex;
  left: 0;
  position:absolute;
}

.family-tree-content {
  width: 100%;
  min-height: 700px;
  background-color: #f1f4f5;
}

.center-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  background-color: white;
  background-size: 100% 140%;
  min-height: 700px;

}
.place-taker{
  width: 100%;
  height: 100px;
  position: relative;
}

.card-item{
  position: absolute;
  width: 152px;
  height:270px;
  margin-left: 20px;
  background-color: #F1F5F6;
  box-shadow: 10px 10px 5px #888888;
  flex: 1;
}
.card-container{
  position: relative;
  display: flex;
  flex: 1;
  overflow-y: scroll;
  max-height: 800px;
  margin-bottom: 30px;
}

.card-item:hover{
  cursor: crosshair;
}

.card-item:hover .delete-ft-note{
  display: block;
  cursor: pointer;
}
.card-item:hover .privilige-setting{
  display: block;
  cursor: pointer;
}
.card-item:hover .enter-ft-note{
  display: block;
  cursor: pointer;
}

.delete-ft-note{
  position: absolute;
  background:url('~/assets/delete20.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:20px;
  height:20px;
  margin: 0 auto;
  display: none;
  top: 245px;
  left: 125px;
}
.enter-ft-note{
  position: absolute;
  background:url('~/assets/tree.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:20px;
  height:20px;
  margin: 0 auto;
  display: none;
  top: 245px;
  left: 70px;
}
.privilige-setting{
  position: absolute;
  background-size: 100% 100%;
  border:none;
  width:20px;
  height:20px;
  margin: 0 auto;
  display: none;
  top: 245px;
  left: 15px;
}

#empty-placeholder{
  width: 400px;
  height: 88px;
  margin-left: 200px;
  background-size: 100% 100%;
}

.book-cover{
  opacity: 1;
  width: 152px;
  height: 220px;
  background-size: 100% 100%;
}
.admin{
  opacity: 0.8;
}
.viewer{
  opacity: 0.5;
}

.bottom{
  flex: 1;
  background-color: #F3F0EC;
}

.add-family-tree{
  position: absolute;
  top: 30px;
  left: 10px;
  background:url('~/assets/addft.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:40px;
  height:40px;
}
.add-family-tree:hover{
  cursor: pointer;
  opacity: 0.8;
}

</style>
