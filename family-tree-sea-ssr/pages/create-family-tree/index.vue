<template>
  <div class="create-family-tree">
      <div class="content">
         <div class="container">
           <el-form ref="form" :model="form" label-width="80px">
             <el-form-item label="家谱名称">
               <el-input :maxlength="20" :minlength="1" v-model="form.name"></el-input>
             </el-form-item>

             <el-form-item label="家谱序" size="large">

               <vue-editor :editorToolbar="customToolbar" id="createDesc" v-model="form.desc"></vue-editor>
             </el-form-item>
             <el-form-item>
               <el-button type="primary" :disabled="balance<=0" @click="onSubmit">创建</el-button>
             </el-form-item>
             <el-form-item>
              <label style="color:green;">总创建家谱额度:{{max}},当前剩余可创建家谱数:{{balance}}</label>
             </el-form-item>
             <el-form-item v-if="balance<=0">
              <label style="color:red;">*您允许创建的家谱数量已达上限*</label>
             </el-form-item>
           </el-form>
         </div>
      </div>

  </div>
</template>

<script>
import Qs from 'qs'
import {AXIOS} from '~/common/http-commons'
import state from '~/common/state';
export default {
    name: 'CreateFamilyTree',
    data() {
      return {
        state,
        balance: 0,
        max: 0,
        customToolbar: [
          [{ 'header': [false, 1, 2, 3, 4, 5, 6]}],
          ['bold', 'italic', 'underline'],
          [{ 'list': 'ordered'}, { 'list': 'bullet' }]
        ],
        form: {
          name: '',
          desc: ''
        }
      }
    },
    methods: {
       checkFtBalance () {
         AXIOS.get('/api/v1/user/remaining-ft-balance').then(response => {
             if(response.data.ok==true){
               this.balance = Number(response.data.data['balance']);
               this.max =  Number(response.data.data['max']);
             }
          }).catch(e => {
             this.$router.push('login')
          })
       },
       onSubmit (){
         if(this.form.name==null || this.form.name.length<1){
           swal ( "提示" ,  "家谱名不能为空" ,  "info" );
           return;
         }
         AXIOS.post('/api/v1/familytree', Qs.stringify({
           name: this.form.name,
           description: this.form.desc
         }),{headers:{'Content-Type':'application/x-www-form-urlencoded'}}).then(response => {
         if(response.data.ok==true){
           this.$notify({
            title: '成功',
            message: '创建成功',
            type: 'success'
           });
           var familyId = response.data.data;
           this.state.currentFt = familyId;
           this.$router.push('/working');
         }else{
           this.$notify.error({
             title: '错误',
             message: response.data.message
           });
         }
       }).catch(e => {
           console.log(e.response)
           this.$notify.error({
             title: '错误',
             message: "提交失败"
           });
         })
       }

    },
    created () {

    },
    updated () {
      //this.checkFtBalance();
    },
    mounted () {
      this.checkFtBalance();
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.create-family-tree{
  flex-flow: column;
  width: 100%;
  display: flex;
  position: absolute;

  left: 0;
}
.content {
  flex: 1 1 auto;
  width: 100%;
  height: 100%;
  min-height: 700px;
  position: relative;
}
.container {
  height: 100%;
  width: 80%;
  margin-left: auto;
  margin-right: auto;
  margin-top: 50px;
}
#createDesc{
  height: 600px;
}

.bottom{
  flex: 1;
  background-color: #F3F0EC;
}

</style>
