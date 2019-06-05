<template>
  <div class="person" v-if="staticbaseknown">
    <div class="person-life">

      <div class="center-content">
        <div class="height-taker"></div>
        <div class="person-head">
            <div v-loading="profile_image_loading" class="profile-image-container">
              <a><img class="profile-image" :src="person.profile" onError="this.src='/imgs/noimage.jpg';"/></a>
              <button @click="toggleShow" class="profile-edit-note"></button>
            </div>
            <div class="brief" v-if="basic_mode=='view'">
              <div class="name">{{person.name}}<span class="sex-desc">({{person.sex}},{{person.born}}~{{person.death}})</span>
                  <button @click="editBasic()" id="edit-button-basic" class="button"></button>
                  <button style="float: right" title="返回家谱"  @click="goBackToFt()" class="go-back-button button"></button>
              </div>
              <div class="short-line"><b>字辈：</b>{{person.zibei}}</div>
              <div class="short-line"><b>排行：</b>{{person.rank}}</div>
              <div class="short-line"><b>联系电话：</b>{{person.phone}}</div>
              <div class="short-line"><b>地址：</b>{{person.address}}</div>
              <div class="short-line"><b>简介：</b>{{person.short_description}}</div>
            </div>
            <div class="brief-edit" v-if="basic_mode=='edit'">
              <table class="basic_table">
                <tr>
                  <td>姓名：</td>
                  <td><input type="text" maxlength="19" v-model="person.name"/></td>
                </tr>
                <tr>
                  <td>字辈：</td>
                  <td><input type="text" maxlength="15" v-model="person.zibei"/></td>
                </tr>
                <tr>
                  <td>排行：</td>
                  <td><input type="number" min="1" v-model="person.rank"/></td>
                </tr>
                <tr>
                  <td>联系电话：</td>
                  <td><input type="text" maxlength="15" v-model="person.phone"/></td>
                </tr>
                <tr>
                  <td>地址：</td>
                  <td><input type="text" maxlength="50" v-model="person.address"/></td>
                </tr>
                <tr>
                  <td>生年：</td>
                  <td><input type="text" maxlength="30" v-model="person.born"/></td>
                </tr>
                <tr>
                  <td>卒年：</td>
                  <td><input type="text" maxlength="30" v-model="person.death"/></td>
                </tr>
                <tr>
                  <td>简介：</td>
                  <td>​<textarea id="txtArea" rows="3" cols="50" maxlength="100" v-model="person.short_description"></textarea></td>
                </tr>
                <tr>

                    <td><el-button type="primary" size="mini" @click="cancelBasic">取消</el-button></td>
                    <td><el-button type="primary" size="mini" @click="saveBasic">保存</el-button></td>

                </tr>
              </table>
            </div>
            <div>
            <my-upload field="img"
                  @crop-success="cropSuccess"
                  @crop-upload-success="cropUploadSuccess"
                  @crop-upload-fail="cropUploadFail"
                  v-model="showProfileUpload"
          		:width="200"
          		:height="200"
          		:url="rest_base+'/api/v1/person/profile-upload'"
          		:params="upload_profile_params"
          		:headers="headers"
              :withCredentials="true"
          		img-format="jpg">
            </my-upload>
            </div>

        </div>
        <hr width="830">
        <div v-loading="profile_image_loading" class="partial-relation">
          <div class="sub-title"  v-lazy-load-bg="static_base+'/imgs/guanxititle.png'" ></div>

          <div id="column-width-adjust">
            列间距
            <el-slider
                  :min=50
                  :max=300
                  v-model="columnWidth"
                  :step="10"
                  @change="updateColumnWidth"
                  show-stops>
                </el-slider>
          </div>
          <svg  id="svg" overflow="scroll" >
          </svg>
        </div>
        <div class="life-title"></div>
        <button @click="editLife()" id="edit-life-button" class=" button"></button>
        <div v-loading="profile_image_loading" style="display: flex; flex-direction:column; margin-top:60px;">

          <div v-if="life_mode=='view'"  class="life-article" v-html="person.life_description">
          </div>
          <div v-if="life_mode=='edit'" v-loading="life_edit_loading" class="edit-life-section">
            <!-- <wysiwyg  v-model="person.life_description" /> -->
            <vue-editor useCustomImageHandler
        @imageAdded="handleImageAdded" v-model="person.life_description"></vue-editor>
          </div>
          <div class="button-container" v-if="life_mode=='edit'">
            <el-button  class="save-button" type="primary"  @click="saveLifeDesc()">保存</el-button>
            <el-button  class="save-button" type="primary" @click="cancelLifeDesc()">取消</el-button>
          </div>
        </div>

        <div class="height-taker-2"></div>



      </div>
    </div>


  </div>
</template>

<script>
import * as d3 from 'd3';
//import 'babel-polyfill';
import {AXIOS} from '~/common/http-commons';
import state from '~/common/state';
import '~/css/d3-context-menu.css'
import treeAlign from '~/mixins/treeAlign'
import staticbase from '~/mixins/staticbase'

export default {
    name: 'Person',
    data() {
      return {
        top_margin: 150,
        left_margin: 160,
        staticbaseknown: false,
        static_base: '.',
        rest_base: '.',
        rowHeight: 200,
        columnWidth: 90,
        current_access_right:'viewer',
        state,
        profile_image_loading: false,
        life_edit_loading: false,
        showProfileUpload: false,
        upload_profile_params: {
				  personId: '',
				  name: 'avatar'
			  },
			  headers: {
				  //smail: '*_~'
			  },
			  imgDataUrl: '',
        basic_mode: 'view',
        life_mode: 'view',
        familytreedata: {},
        manicon: '',
        womanicon: '',
        person:{
          id: '',
          name: '',
          sex: '',
          born: '',
          death: '',
          short_description: '',
          life_description: '',
          address: '',
          phone: '',
          profile: '',
          family_tree_id: '',
          zibei: '',
          rank: 0
        }
      }
    },
    mixins: [treeAlign, staticbase],
    methods: {
      updateRowHeight(){
        this.loadPartialFamilyTree(false);
      },
      updateColumnWidth(){
        this.loadPartialFamilyTree(false);
      },

      loadDefaultIcons (){
        AXIOS.get('/api/v1/user/default-human-icon').then(response => {
            this.manicon = response.data['man_icon']
            this.womanicon = response.data['woman_icon']
            this.panel_loading = false;
            if(this.selected_family_tree!=null && this.selected_family_tree!=''){
              this.changeSelect();
            }
            this.loadPersonDetail();
         }).catch(e => {
            this.$router.push('/login')
         })
      },
      clearSVG () {
        d3.select("#svg").selectAll("*").remove();
      },
      adjustPanelSize () {
        var svg = d3.select("#svg")
        svg.attr("width", this.familytreedata['column-count']*this.columnWidth+this.left_margin*2)

        svg.attr("height",  this.columnWidth*this.familytreedata['svgHeightIndex']+this.rowHeight)
      },
      loadPanelTree (){
          var storeThis = this;
          var svg = d3.select("#svg"),
          width = +svg.attr("width"),
          height = +svg.attr("height");
          var color = d3.scaleOrdinal(d3.schemeCategory20);
          var x = d3.scaleLog().rangeRound([0, width]);
          var y = d3.scaleLog().rangeRound([0, height]);
          var simulation = d3.forceSimulation().force("link", d3.forceLink()
          .distance(function(d){;return d.higher==0?90:160;})
          .id(function(d) { return d.id; }))
          .force("charge", function(d, i) { return -20000; })
          //.force("y", d3.forceY(function(d) { return y(height/2+d.level*height); }).strength(0.2))
          //.force("x",d3.forceX(width/2).strength(0.01))
          //.force("center", d3.forceCenter(width/3, height/3));
          var heightPosMap = this.familytreedata['heightPosMap'];

          var link = svg.selectAll(".links").data(this.familytreedata.links).enter().append("g").attr("class", "links")
          .append("line").attr("stroke", "#7E08F1").attr("opacity", 0.1)
          .attr("stroke-width", function(d) { return 3 });

          var node = svg.selectAll(".nodes").data(this.familytreedata.nodes).enter().append("g").attr("class", "nodes");

          node.append("image")
                   .attr("xlink:href", function(d){return d.sex==1?storeThis.manicon:storeThis.womanicon;})
                   .attr("x", -8)
                   .attr("y", -8)
                   .attr("width", 50)
                   .attr("height", 50);

        node.append("text").attr("dx", -2).attr("dy", "-0.5em").text(function(d) { return d.name });

        node.append("title").text(function(d) { return d.name; });

        simulation.nodes(this.familytreedata.nodes).on("tick", ticked);

        simulation.force("link").links(this.familytreedata.links);

        d3.contextMenu = function (menu, openCallback) {
          // create the div element that will hold the context menu
        d3.select('body').selectAll('.d3-context-menu').data([1])
         .enter()
         .append('div')
         .attr('class', 'd3-context-menu');
         // close menu
         d3.select('body').on('click.d3-context-menu', function() {
           //d3.select('.d3-context-menu').style('display', 'none');
           d3.select('body').select('.d3-context-menu').style('display', 'none');
           //d3.select('body').select('.d3-context-menu').classed('hidden', 'true');
         });

        // this gets executed when a contextmenu event occurs
        return function(data, index) {
          var elm = this;

          d3.select('body').selectAll('.d3-context-menu').html('');
          var list = d3.select('body').selectAll('.d3-context-menu').append('ul');
          list.selectAll('li').data(menu).enter()
           .append('li')
           .html(function(d) {
             return (typeof d.title === 'string') ? d.title : d.title(data);
           }).on('click', function(d, i) {
             d.action(elm, data, index);
             //d3.select('.d3-context-menu').style('display', 'none');
             d3.select('body').select('.d3-context-menu').style('display', 'none');
             //d3.select('body').select('.d3-context-menu').classed('hidden', 'true');
          });

        // the openCallback allows an action to fire before the menu is displayed
        // an example usage would be closing a tooltip
          if (openCallback) {
            if (openCallback(data, index) === false) {
              return;
            }
          }

          d3.select('.d3-context-menu')
           .style('left', (d3.event.pageX - 2) + 'px')
           .style('top', (d3.event.pageY - 2) + 'px')
           .style('display', 'block');

          d3.event.preventDefault();
          d3.event.stopPropagation();
        };
        };

        var menu = [
        {
        title: '人物生平',
        action: function(elm, d, i) {
          console.log(elm)
          console.log(d)
          console.log(i)
          storeThis.$router.push('/person/'+d.id);
          location.reload();
        }
        }
        ]

        node.on('contextmenu', d3.contextMenu(menu));


        function ticked() {
            link.attr("x1", function(d) { return d.source.x+15; })
             .attr("y1", function(d) { return d.source.y+15; })
             .attr("x2", function(d) { return d.target.x+15; })
             .attr("y2", function(d) { return d.target.y+15; });

            node
            .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
            .attr("cx", function(d) { return d.x = (d.column-1)*storeThis.columnWidth+storeThis.left_margin; })
            .attr("cy", function(d) { return d.y = storeThis.top_margin + storeThis.columnWidth*heightPosMap[d.level]; });
        }


      },
      loadPartialFamilyTree (fetchRemote) {
        var storeThis = this;
        if(this.person.family_tree_id.length<1)
        {
           return;
        }else{
          if(fetchRemote!=true){
            storeThis.clearSVG();
            storeThis.adjustPanelSize();
            this.loadPanelTree();
            return;
          }
          AXIOS.get('/api/v1/familytree/family-tree/'+this.person.family_tree_id+"/"+this.person.id).then(response => {

              if(response.data.ok==true){
                var raw_family_tree_data = response.data.data;
                storeThis.familytreedata = this.treeAlign(raw_family_tree_data);
                storeThis.clearSVG()
                storeThis.adjustPanelSize()
                this.loadPanelTree()
              }else{
                storeThis.$notify.error({
                  title: "错误",
                  message: response.data.message
                });
                storeThis.$router.push("/working");
              }
           }).catch(e => {
              console.log(e.response);
              storeThis.$notify.error({
                title: "错误",
                message: "未知错误"
              });
           })
        }
      },
      goBackToFt(){
         if(this.current_access_right=='public'){
           this.$router.push('/public-tree-detail/'+this.person.family_tree_id);
         }else{
           this.state.currentFt = this.person.family_tree_id;
           this.$router.push('/working');
         }
      },
      resizeIframe() {
        var tt = document.getElementsByTagName("iframe")
        if(tt.length>0){
          for(var i=0; i<tt.length; i++){
            var ifr = tt[i];
            if(!ifr.hasAttribute("width")){
              ifr.setAttribute("width","90%");
            }
            if(!ifr.hasAttribute("height")){
              ifr.setAttribute("height","400");
            }
          }
        }
      },
      handleImageAdded: function(file, Editor, cursorLocation, resetUploader) {
        if(file.size>2097152){
          this.$notify({
            title: '图片太大',
            type: 'warning',
            message: '图片太大'
          })
          return;
        }
        const formData = new FormData();
        formData.append("personId", this.upload_profile_params.personId);
        formData.append("img", file);
        this.life_edit_loading = true;
        AXIOS.post('/api/v1/person/content-image', formData).then(response => {
          if(response.data.ok==true){
            this.$notify({
              title: '成功',
              type: 'success',
              message: '上传图片成功'
            })
            this.life_edit_loading = false;
            let url = response.data.data // Get url from response
            Editor.insertEmbed(cursorLocation, 'image', url);
            resetUploader();
          }else{
            this.$notify.error({
              title:"错误",
              message: response.data.message
            });
            this.life_edit_loading = false;
          }
        }).catch(e => {
          console.log(e.response)
          this.$notify.error({
            title: '上传失败',
            message: '未知错误'
          })
          this.life_edit_loading = false
        })
      },
      updateLifeDescription(updateVal) {
         this.person.life_description = updateVal;
      },
      toggleShow() {
        console.log(this.showProfileUpload);
				this.showProfileUpload = !this.showProfileUpload;
			},
      cropSuccess(imgDataUrl, field){
				console.log('-------- crop success --------');
				this.imgDataUrl = imgDataUrl;
			},
      cropUploadSuccess(jsonData, field){
				console.log('-------- upload success --------');
				console.log(jsonData);
				console.log('field: ' + field);
        this.$notify({
          title: '上传成功',
          type: 'success',
          message: '上传成功'
        });
        this.loadPersonDetail();
        location.reload();
			},
      cropUploadFail(status, field){
				console.log('-------- upload fail --------');
				console.log(status);
				console.log('field: ' + field);
        this.$notify.error({
          title: '错误',
          message: '上传失败'
        });
			},
      editBasic () {
        this.basic_mode = 'edit';
      },
      editLife () {
        this.life_mode = 'edit';
      },
      cancelBasic () {
        this.basic_mode = 'view';
        this.loadPersonDetail();
      },
      saveBasic () {
        if(this.person.name==null || this.person.name.length<1){
          swal ( "提示" ,  "姓名不能为空" ,  "info" );
          return;
        }
        AXIOS.post('/api/v1/person/basic-description/'+this.$route.params.id, {
          name: this.person.name,
          phone: this.person.phone,
          address: this.person.address,
          born: this.person.born,
          death: this.person.death,
          short_description: this.person.short_description,
          zibei: this.person.zibei,
          rank: this.person.rank
        }).then(response => {
          if(response.data.ok==true){
            this.$notify({
              title: '成功',
              type: 'success',
              message: response.data.message
            });
            this.basic_mode = 'view';
            this.loadPersonDetail();
          }else{
            this.$notify.error({
              title: '错误',
              message: response.data.message
            });
          }

        }).catch(e => {
          console.log(e)
          this.$notify.error({
            title: '保存失败',
            message: '提交失败'
          });
        })
      },
      cancelLifeDesc() {
        this.life_mode = 'view';
        this.loadPersonDetail();
      },
      saveLifeDesc(){
        // do save, on success change to view mode
        AXIOS.post('/api/v1/person/life-description/'+this.$route.params.id, {
          life_description: this.person.life_description
        }).then(response => {
          if(response.data.ok==true){
            this.$notify({
              title: '成功',
              type: 'success',
              message: response.data.message
            });
            this.life_mode = 'view';
            this.loadPersonDetail();
          }else{
            this.$notify.error({
              title: '错误',
              message: response.data.message
            });
          }
        }).catch(e => {
          console.log(e)
          this.$notify.error({
            title: '错误',
            message: '未知错误'
          })
        })

      },
      loadPersonDetail () {
        this.profile_image_loading=true;
        var storeThis = this;
        AXIOS.get('/api/v1/person/'+this.$route.params.id).then(response => {
            //console.log(response.data)
            if(response.data.ok==true){
              this.upload_profile_params.personId = response.data.data.id;
              this.person.name = response.data.data.name;
              this.person.sex = response.data.data.sex==1?'男':'女';
              this.person.born = response.data.data.born;
              this.person.death = response.data.data.death;
              this.person.short_description = response.data.data.shortDesc;
              this.person.life_description = response.data.data.lifeDescription;
              this.person.address = response.data.data.address;
              this.person.phone = response.data.data.phone;
              this.person.profile = response.data.data.profileImgPath;
              this.profile_image_loading = false;
              this.person.family_tree_id = response.data.data.familyTreeId;
              this.person.id = response.data.data.id;
              this.person.zibei = response.data.data.zibei;
              this.person.rank = response.data.data.rank;
              storeThis.loadPartialFamilyTree(true)
            }else{
              storeThis.$notify.error({
                title: '错误',
                message: response.data.message
              });
              storeThis.$router.push("/home")
            }

         }).catch(e => {
            console.log(e);
            this.$notify.error({
              title: '错误',
              message: '未知错误'
            });
            this.$router.push('/login');
         })
      }
    },
    created () {
      this.getStaticBase();
    },
    updated () {
        this.resizeIframe();
    },
    mounted () {
      if(this.$route.params.id!=null){
        this.loadDefaultIcons();

      }
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.person{
  display: flex;
  flex-direction: column;
}
.person-life{
  display: flex;
  min-height: 700px;
  background-color: #f1f4f5;
}
.center-content{
  max-width: 850px;
  width: 850px;
  background-color: white;
  margin: 0 auto;
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
}
.person-head{
  width: 100%;
  height: 280px;
  min-height: 280px;
  font-size: 37.44px;
  margin-bottom: 5px;
  display: flex;
}
.name{
  margin-top: 0;
  margin-left: 18px;
}
.profile-image-container{
  position: relative;
  height: 100%;
  width: 220px;
  box-shadow: 0 8px 6px -6px black;
}
.profile-image{
   height:100%;

   width: 100%;
}
.sex-desc{
  font-size: 15px;
}
.brief{
  height: 100%;
  flex: 1;
}
.brief-edit{
  height: 100%;
  flex: 1;
  font-size: 13px;
}
.short-line{
  margin-left: 18px;
  font-size: 14px;
  margin-top: 5px;
}
.life-article{
  font-size: 20px;
  width: 95%;
  flex: 1;
  margin: 0 auto;
  margin-top: 10px;
  min-height: 700px;
}

.life-article >>> img {
    max-width: 800px;
}

.go-back-button{
  background:url('~/assets/go-back-icon.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:50px;
  height:50px;
  margin: 0 auto;
}
.go-back-button:hover{
  cursor: pointer;

}
.profile-edit-note{
  position: absolute;
  background:url('~/assets/editbutton.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:20px;
  height:20px;
  margin: 0 auto;
  display: none;
  top: 220px;
  left: 185px;
}
.profile-image-container:hover .profile-edit-note{
  display: block;
  cursor: pointer;
}
.edit-life-button:hover{
  cursor: pointer;
}
.edit-life-section{
  width: 95%;
  flex: 1;
  margin: 0 auto;
  background-position: red;
}
.button-container{
  width: 95%;
  margin: 0 auto;
  display:inline-block;
}
.save-button{
  float: right;
  margin-right: 5px;
}
.edit-basic-container{
  display: flex;
  margin-top: 5px;
}
.basic_table{
  margin-left: 14px;
  margin-top: 3px;
}
.height-taker{
  width: 100%;
  background-color: white;
  height: 5px;
}
.height-taker-2{
  width: 100%;
  background-color: white;
  height: 15px;
}
iframe{
  min-width: 90%;
}
.partial-relation{
  width: 100%;
  min-height: 600px;
  max-height: 600px;
  overflow: scroll;
  background-image:url("~/assets/partial_canvas.jpg");
  flex: 1;
  border: 5px solid #111;
  -moz-box-shadow: 35px 35px 7px #999999;
  -moz-border-bottom-right-radius: 15px;
  -moz-border-radius:25px;
  padding: 15px 25px;

}
.sub-title{
  position: absolute;
  background-size: 100% 100%;
  top:330px;
  left: 10px;
  width: 350px;
  height: 60px;
}
#row-width-adjust{
  position: absolute;
  top:340px;
  left: 400px;
  width: 150px;
  height: 60px;
}
#column-width-adjust{
  position: absolute;
  top:330px;
  left: 600px;
  width: 150px;
  height: 60px;
}
.life-title{
  position: absolute;
  background-image:url("~/assets/lifetitle.png");
  background-size: 100% 100%;
  top:940px;
  left: 10px;
  width: 150px;
  height: 40px;
}
#edit-life-button{
  background:url('~/assets/editbutton.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:20px;
  height:20px;
  margin: 0 auto;
  position: absolute;
  top: 948px;
  left: 208px;
}
#edit-button-basic{
  background:url('~/assets/editbutton.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:20px;
  height:20px;
  margin: 0 auto;
}
#edit-button-basic:hover{
  cursor: pointer;
}
#edit-life-button:hover{
  cursor: pointer;
}

.links line {
  stroke: #999;
  /* stroke-opacity: 0.6; */
}

.nodes circle {
  stroke: #fff;
  stroke-width: 1.5px;
}

.nodes test {
  pointer-events: none;
  font: 10px sans-serif;
}

path.link {
  fill: none;
  stroke: #000;
  stroke-width: 4px;
  cursor: default;

  stroke:black;
  fill:none;
}

svg:not(.active):not(.ctrl) path.link {
  cursor: pointer;
}

path.link.selected {
  stroke-dasharray: 10,2;
}

path.link.dragline {
  pointer-events: none;
}

path.link.dragline.hidden {
  stroke-width: 0;
}

path.link.hidden {
  stroke-width: 0;
}

.bottom{
  flex: 1;
  background-color: #F3F0EC;
}
</style>
