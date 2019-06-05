<template>
  <div class="family-tree-detail-page" v-loading="wholePageLoading" v-if="staticbaseknown">
    <div class="family-tree-detail-content">
      <div class="center-content">
        <div class="top-place-taker">
        <button v-if="!edit " @click="toggleEdit" class="ft-name-edit"></button>
        <button @click="back2list" title="返回家谱列表" id="back-to-ft-list"></button>
        </div>

        <div class="body-content">
           <h2 v-if="edit"><input v-model="name" maxlength="20" type="text" placeholder="家谱名"/></h2>
           <h2 v-if="!edit">{{name}}</h2>
        </div>
        <hr width="750">
        <div id="xu-title"></div>
        <div v-if="edit">
            <vue-editor style="margin-left:10px;margin-right:10px;" :editorToolbar="customToolbar" v-model="content"></vue-editor>
            <div class="button-container">
              <el-button style="float: right; margin-right:10px;" type="primary" size="mini" @click="cancel">取消</el-button>
              <el-button style="float: right; margin-right:10px;" type="primary" size="mini" @click="save">保存</el-button>
            </div>
        </div>
        <div v-if="!edit" class="xu-container"  v-lazy-load-bg="static_base+'/imgs/book.png'" >
        <div class="xu" v-html="content"></div>
        </div>


        <div class="bottom-place-taker"></div>
      </div>
    </div>

  </div>
</template>

<script>
import {AXIOS} from '~/common/http-commons'
import { VueEditor } from 'vue2-editor'
import staticbase from '~/mixins/staticbase'
import Qs from 'qs'
export default {
    name: 'FamilyTreeDetail',
    components: {
      VueEditor
    },
    mixins: [staticbase],
    data() {
      return {
        staticbaseknown: false,
        static_base: '.',
        rest_base: '.',
        edit: false,
        my_privilege: "viewer",
        wholePageLoading: false,
        name: '',
        content: '',
        customToolbar: [
          [{ 'header': [false, 1, 2, 3, 4, 5, 6] }],
            ['bold', 'italic', 'underline'],
            [{ 'list': 'ordered'}, { 'list': 'bullet' }]
        ]
      }
    },
    methods: {
      back2list(){
        this.$router.push('/familytree');
      },

      loadXu(){
        this.wholePageLoading = true;
        AXIOS.get('/api/v1/familytree/family-tree-info/'+this.$route.params.id).then(response =>{
          if(response.data.ok==true){
            this.name = response.data.data.name;
            this.content = response.data.data.description;
            this.wholePageLoading = false;
          }else{
            this.$notify.error({
              title:"错误",
              message:response.data.message
            });
            this.$router.push("/familytree")
          }
        }).catch(e => {
          console.log(e);
          this.$notify.error({
            title:"错误",
            message:'未知错误'
          });
          this.$router.push('/home');
        })
      },
      cancel() {
        this.edit = !this.edit;
        this.loadXu();
      },
      save(){
        if(this.name==null || this.name.length<1){
          swal ( "提示" ,  "家谱名称不能为空" ,  "info" );
          return;
        }
        AXIOS.put("/api/v1/familytree",{
          family_tree_id: this.$route.params.id,
          name: this.name,
          content: this.content
        }).then(response => {
          if(response.data.ok == true){
            this.$notify({
              title: '更新成功',
              type: 'success',
              message: '更新成功'
            });
            this.edit = !this.edit;
          }else{
            this.$notify.error({
              title: '更新失败',
              message: '更新失败'
            });
          }
        }).catch(e => {
          this.$notify.error({
            title: '更新失败',
            message: '更新失败'
          });
        })
      },
      toggleEdit() {
         this.edit = !this.edit;
      },
      goDemo () {

      }

    },
    created () {
      this.getStaticBase();
    },
    mounted() {
      this.loadXu();
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.family-tree-detail-page{
  flex-flow: column;
  width: 100%;
  display: flex;
  left: 0;
  position:absolute;
}

.family-tree-detail-content {
  width: 100%;
  min-height: 700px;
  background-color: #f1f4f5;
}

.center-content {
  max-width: 800px;
  height: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  background: white;
  position: relative;
}

.top-place-taker{
  width: 100%;
  height: 30px;
}

.body-content{
  width: 100%;
  min-height: 50px;
}
h2{
  text-align: center;
}
.xu{
  margin-left: 57px;
  margin-top: 30px;
  width: 85%;
  height: 450px;
  writing-mode: vertical-rl;
  overflow-x: scroll;

}
.xu-container{
  margin-top: 6px;
  background-size: 100% 100%;
  height: 500px;
  width: 100%;
}
.bottom-place-taker{

  background-color: white;
  width:100%;
  min-height: 300px;
}

.ft-name-edit:hover{
  cursor: pointer;
}
.ft-name-edit{
  position: absolute;
  top: 45px;
  left: 750px;
  background:url('~/assets/editbutton.png') no-repeat;
  background-size: 100% 100%;
  width:20px;
  height:20px;
  border:none;
}

#back-to-ft-list{
  position: absolute;
  top: 45px;
  left: 30px;
  background:url('~/assets/go-back-icon.png') no-repeat;
  background-size: 100% 100%;
  width:30px;
  height:30px;
  border:none;
}
#back-to-ft-list:hover{
  opacity: 0.8;
  cursor: pointer;
}

#xu-title{
  margin: 0 auto;
  width: 60px;
  height: 80px;
  background:url('~/assets/xu.png') no-repeat;
  background-size: 100% 100%;
}

.bottom{
  flex: 1;
  background-color: #F3F0EC;
}



</style>
