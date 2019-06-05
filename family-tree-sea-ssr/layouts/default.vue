<template>
  <div id='ftapp'>
    <div id="whole-header"><app-header></app-header></div>

    <div class='whole-body'><nuxt/></div>

  </div>
</template>

<script>
import Header from '~/components/Header.vue'
export default {
  components: {
    'app-header': Header
  }
}
</script>


<style>
html {
  font-family: "Source Sans Pro", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  font-size: 16px;
  word-spacing: 1px;
  -ms-text-size-adjust: 100%;
  -webkit-text-size-adjust: 100%;
  -moz-osx-font-smoothing: grayscale;
  -webkit-font-smoothing: antialiased;
  box-sizing: border-box;
}



*, *:before, *:after {
  box-sizing: border-box;
  margin: 0;
}

.button--green {
  display: inline-block;
  border-radius: 4px;
  border: 1px solid #3b8070;
  color: #3b8070;
  text-decoration: none;
  padding: 10px 30px;
}

.button--green:hover {
  color: #fff;
  background-color: #3b8070;
}

.button--grey {
  display: inline-block;
  border-radius: 4px;
  border: 1px solid #35495e;
  color: #35495e;
  text-decoration: none;
  padding: 10px 30px;
  margin-left: 15px;
}

.button--grey:hover {
  color: #fff;
  background-color: #35495e;
}


#ftapp {
   height: 100vh;
   display: flex;
   flex-direction: column;
}

#whole-header{
  min-height: 55px;
  height: 55px;
}

.whole-body{
  width: 100%;
  flex: 1;

  position: relative;
}
</style>
