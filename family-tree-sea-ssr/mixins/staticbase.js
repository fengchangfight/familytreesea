import {AXIOS} from '~/common/http-commons'
export default{
  data (){
    return {
      staticbaseknown: false
    }

  },
  methods: {
    getStaticBase(){
      AXIOS.get('/resource').then(response =>{
        console.log("------");
        console.log(response.data);
        this.static_base = response.data.static;
        this.rest_base = response.data.rest;
        this.staticbaseknown = true;
      }).catch(e=>{
        this.static_base = '.';
        this.rest_base = '.';
        this.staticbaseknown = true;
      })
    },
    checkLicense(){
      AXIOS.get('/api/v1/user/license-check').then(response => {
        if(response.data.ok==true){
          // do nothing
          this.blockingLicenseWindow = false;
        }else{
          // pop up license window
          this.blockingLicenseWindow = true;
        }
       }).catch(e => {
          this.$router.push('login')
       })
    },
    getOnlineLicense(){
      window.location.href = 'http://www.familytreesea.com/license';
    },
    updateLicense(){
      AXIOS.post('/api/v1/user/license-update',{
        license_code: this.licenseCode
      }).then(response=>{
        if(response.data.ok==true){
          this.$notify({
            title: '成功',
            type: 'success',
            message: response.data.message
          });
          this.checkLicense();
        }else{
          this.$notify.error({
            title: '错误',
            message: response.data.message
          });
        }
      }).catch(e=>{
        this.$notify.error({
          title: '错误',
          message: '未知错误'
        });
      })
    },
  }
}
