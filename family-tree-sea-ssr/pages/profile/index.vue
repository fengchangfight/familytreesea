<template>
  <div class="profile-page">
    <div class="profile-content">
      <div class="profile-block">
        <b-tabs>
          <b-tab title="修改个人信息" active>
            <div style="display:flex;">
              <div class="basic-form-holder">
                <el-form  size="mini" ref="form" label-width="95px">
                <el-form-item label="昵称">
                <el-input v-model="nickname" @change="nickNameValidate" v-verify="nickname" ></el-input>
                <label v-if="nicknameError" class="nickname-invalid" >*{{nicknameErrorMessage}}*</label>
                </el-form-item>

                <el-form-item label="手机">
                <el-input @change="phoneValidate" v-verify="phone" v-model="phone"></el-input>
                <label v-if="phoneError" class="phone-invalid" >*{{phoneErrorMessage}}*</label>
                </el-form-item>

                <el-form-item label="邮箱">
                <el-input v-model="email"  v-verify="email" @change="emailValidate"></el-input>
                <label v-if="emailError" class="email-invalid" >*{{emailErrorMessage}}*</label>
                </el-form-item>

                <el-button type="primary" style="margin-left: 40px" size="mini" @click="updateProfile">立即更新</el-button>

                </el-form>

              </div>
            
            </div>


          </b-tab>
          <b-tab title="修改密码" >
            <div class="basic-form-holder">
              <el-form  size="mini" ref="pass-form" label-width="88px">

                <el-form-item label="原密码">
                <el-input v-model="old_password" type="password"></el-input>
                </el-form-item>

                <el-form-item label="新密码">
                <el-input v-model="new_password" type="password"  v-verify="new_password" @change="newpasswordValidate"></el-input>
                <label v-if="newpasswordError" class="password-invalid">*{{newpasswordErrorMessage}}*</label>
                </el-form-item>

                <el-form-item label="重复新密码">
                <el-input v-model="re_new_password" type="password" @change="checkSame"></el-input>
                <label v-if="renewpasswordError" class="password-invalid" >*两次输入密码不一致*</label>
                </el-form-item>

                <el-button type="primary" style="margin-left: 40px" size="mini" @click="updatePassword">更新密码</el-button>
              </el-form>
            </div>
          </b-tab>

        </b-tabs>
     </div>

    </div>

  </div>
</template>

<script>
import {AXIOS} from '~/common/http-commons'
import staticbase from '~/mixins/staticbase'

export default {
    name: 'Profile',

    verify: {
      phone: ["mobile"],
      email: ["email"],
      nickname: {
         maxLength:8,
         minLength:1,
         message: "昵称1~8个字符"
      },
      new_password: {
         minLength:6,
         message: "密码不得小于6位"
      }
    },
    mixins: [staticbase],
    data() {
      return {
        staticbaseknown: false,
        profile_image: '',
        rest_base: '.',
        showProfileUpload: false,
        old_password: '',
        new_password: '',
        re_new_password: '',
        newpasswordError: false,
        renewpasswordError: false,
        newpasswordErrorMessage: '',
        phoneError: false,
        emailError: false,
        nicknameError: false,
        phoneErrorMessage: '',
        emailErrorMessage: '',
        nicknameErrorMessage: '',
        nickname: '',
        phone: '',
        email: '',
        userid: ''
      }
    },
    methods: {
      cropSuccess(imgDataUrl, field){
				this.imgDataUrl = imgDataUrl;
			},
      cropUploadSuccess(jsonData, field){
        this.$notify({
          title: '上传成功',
          type: 'success',
          message: '上传成功'
        });
        this.loadUserDetail();
        location.reload();
			},
      cropUploadFail(status, field){
        this.$notify.error({
          title: '错误',
          message: '上传失败'
        });
			},
      toggleShow(){
        this.showProfileUpload = !this.showProfileUpload;
      },
      checkSame(){
        if(this.re_new_password!=this.new_password){
          this.renewpasswordError = true;
        }else{
          this.renewpasswordError = false;
        }
      },
      updatePassword() {
        var valid = this.newpasswordValidate();
        if(!valid){
          return;
        }
        if(this.re_new_password!=this.new_password){
          this.renewpasswordError = true;
          return;
        }
        // do the real stuff to update password
        AXIOS.post('/api/v1/user/password',{
          old_password: this.old_password,
          new_password: this.new_password
        }).then(response => {
          if(response.data.ok==true){
            this.$notify({
              title: '成功',
              type: 'success',
              message: '密码更新成功'
            });
          }else{
            this.$notify.error({
              title: '更新失败',
              message: response.data.message
            });
          }

        }).catch(e => {
          this.$notify.error({
            title: '失败',
            message: '密码更新失败'
          });
        })
      },
      newpasswordValidate(){
        var valid = this.$verify.check();
        if(this.$verify.$errors.new_password.length>0){
          this.newpasswordError = true;
          this.newpasswordErrorMessage = this.$verify.$errors.new_password[0];
        }else{
          this.newpasswordError = false;
          this.newpasswordErrorMessage = '';
        }
        return !this.newpasswordError;
      },
      nickNameValidate() {
        this.$verify.check();
        if(this.$verify.$errors.nickname.length>0){
          this.nicknameError = true;
          this.nicknameErrorMessage = this.$verify.$errors.nickname[0];
        }else{
          this.nicknameError = false;
          this.nicknameErrorMessage = '';
        }
        return this.nicknameError;
      },
      emailValidate() {
        this.$verify.check();
        if(this.$verify.$errors.email.length>0){
          this.emailError = true;
          this.emailErrorMessage = this.$verify.$errors.email[0];
        }else{
          this.emailError = false;
          this.emailErrorMessage = '';
        }
        return this.emailError;
      },
      phoneValidate () {
        this.$verify.check();
        if(this.$verify.$errors.phone.length>0){
          this.phoneError = true;
          this.phoneErrorMessage = this.$verify.$errors.phone[0];
        }else{
          this.phoneError = false;
          this.phoneErrorMessage = '';
        }
        return this.phoneError;
      },
      loadUserDetail () {
        AXIOS.get("/api/v1/user/user-info").then(response => {
          this.nickname = response.data.nickName;
          this.phone = response.data.phone;
          this.email = response.data.email;
          this.userid = response.data.id;
          this.profile_image = response.data.profileImage;
        }).catch(e => {
          console.log(e.response);
        })
      },
      empty(st){
        if(st!=null && st.length>0){
          return false;
        }else{
          return true;
        }

      },
      updateProfile() {
        this.$verify.check();
        if( (!this.empty(this.nickname) && this.nickNameValidate()) || (!this.empty(this.phone) && this.phoneValidate()) || (!this.empty(this.email) && this.emailValidate())){
          return;
        }else{
          AXIOS.post("/api/v1/user/user-info",{
            nick_name:  this.nickname,
            phone: this.phone,
            email: this.email
          }).then(response =>{
            this.$notify({
              title: '更新成功',
              type: 'success',
              message: '更新成功'
            });
            this.loadUserDetail();
            location.reload();
          }).catch(e => {
            this.$notify.error({
              title: '更新失败',
              message: '更新失败'
            });
          })
        }
      }

    },
    created () {
      this.getStaticBase();
    },
    mounted (){
      this.loadUserDetail();
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.profile-page{
  flex-flow: column;
  width: 100%;
  height: 100vh;
  display: flex;
  left: 0;
  position:absolute;
}

.profile-content {
  width: 100%;
  min-height: 700px;
  height: 700px;
  background-color: #f1f4f5;
  display: flex;


}
.profile-block{
  background-color: white;
  width: 700px;
  height: 500px;
  margin: 0 auto;
  margin-top: 100px;
  border: 1px solid #000;
}

.basic-form-holder{
  margin-left: 50px;
  margin-top: 50px;
  width: 50%;
  height: 300px;
}

.phone-invalid, .email-invalid, .nickname-invalid, .password-invalid{
  color: red;
  font: 12px/18px STHeiti,'Microsoft YaHei',arial,\5b8b\4f53;
}

.bottom{
  flex: 1;
  background-color: #F3F0EC;
}
#profile{
  width: 160px;
  height: 200px;
  margin-top: 10px;
  margin-left: 125px;
  position: relative;
}

.profile-edit-note{
  position: absolute;
  background:url('~/assets/brush.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:20px;
  height:20px;
  margin: 0 auto;
  display: none;
  top: 170px;
  left: 130px;
}

#profile:hover .profile-edit-note{
  display: block;
  cursor: pointer;
}

</style>
