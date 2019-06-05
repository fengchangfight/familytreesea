<template>
  <div class="main-page" v-loading="wholePageLoading" v-if="staticbaseknown">
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

    <el-dialog
      title="快照备份"
      :visible.sync="snapshotDialogVisible"
      width="30%"
      :before-close="handleClose">
      <el-form ref="snap-form" label-width="80px">
        <el-form-item label="快照名">
          <el-input v-verify="snapshotName" v-model="snapshotName" @change="verifySnapshot"></el-input>
          <label v-if="snapshotError" class="snapshot-invalid" >*{{snapshotErrorMessage}}*</label>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="snapshotDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="executeSnapshot">执行</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="新建人物节点"
      :visible.sync="dialogVisible"
      width="30%"
      :before-close="handleClose">
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="人物姓名">
          <el-input v-model="form.name" maxlength="15"></el-input>
        </el-form-item>

        <el-form-item label="人物性别">
          <el-radio-group v-model="form.sex">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addPerson">创建</el-button>
      </span>
    </el-dialog>


    <el-dialog
      title="建立人物关系"
      :visible.sync="connectionDialogVisible"
      width="40%"
      :before-close="handleClose">
      <el-form ref="form" :model="form" label-width="170px">
        <el-form-item :label="relationTip">
          <el-select v-model="relationType" placeholder="关系">
            <el-option
             v-for="item in relationTypes"
            v-bind:key="item.id"
            :label="item.name"
            :value="item.id">
           </el-option>
          </el-select>
        </el-form-item>
      </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="connectionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="buildRelation">建立关系</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="添加关系人物"
      :visible.sync="addConnectionVisible"
      width="30%"
      :before-close="handleClose">
      <el-form ref="form" :model="form" label-width="85px">
        <el-form-item :label="contextName+'的'">
          <el-select v-model="relationType" placeholder="关系">
            <el-option
             v-for="item in relationTypes4Sex"
            :key="item.id"
            :label="item.name"
            :value="item.id">
           </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="人物姓名">
          <el-input minlength="1" maxlength="15" v-model="form.name"></el-input>
        </el-form-item>

      </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="addConnectionVisible = false">取消</el-button>
          <el-button type="primary" @click="addRelation">添加</el-button>
      </span>
    </el-dialog>


    <div id="content">
       <div class="sidebar"  v-lazy-load-bg="static_base+'/imgs/zhujianweisuo.jpg'" >
          <table class="side-op" style="margin-top:10px;">

             <tr style="height:40px;" >
               <td colspan=2  align="center" height="50px">
                 <el-button style="background-color:#FBC8C1" round v-on:click="createFamilyTree">创建家谱</el-button>
               </td>
             </tr>
             <tr style="height:40px;">
               <td colspan=2  align="center" height="50px">

                 <el-select v-model="selected_family_tree" filterable placeholder="我的家谱" @change="changeSelect">
                     <el-option-group
                       v-for="group in myfamilytrees"
                       :key="group.groupLabel"
                       :label="group.groupLabel">
                       <el-option
                         v-for="item in group.groupData"
                         :key="item.id"
                         :label="item.name"
                         :value="item.id">
                       </el-option>
                     </el-option-group>
                   </el-select>
               </td>
             </tr>
             <tr v-if="current_access_right=='creator'" style="height:60px;">
               <td colspan=2  align="center" height="50px">
                 <el-select v-model="snapshot_id" placeholder="快照恢复" @change="applySnapshot">
                   <el-option
                     v-for="item in mysnapshots"
                     :key="item.id"
                     :label="item.name"
                     :value="item.id">
                   </el-option>
                 </el-select>
               </td>
             </tr>
             <tr style="height:60px;">
               <td width="40%">男性头像</td>
               <td width="60%" align="center" height="50px">
                 <el-select v-model="man_select" placeholder="男性头像" @change="setManIcon()">
                   <el-option
                     v-for="item in manicons"
                     :key="item.id"
                     :label="item.name"
                     :value="item.id">
                     <span style="float: left"><img width="20" height="20" :src="static_base+'/imgs/'+item.id"/></span>
                     <span style="float: right; color: #8492a6; font-size: 13px">{{ item.name }}</span>
                   </el-option>
                 </el-select>
               </td>
             </tr>
             <tr style="height:60px;">
               <td width="40%">女性头像</td>
               <td width="60%" align="center" height="50px">
                 <el-select v-model="woman_select" placeholder="女性头像" @change="setWomanIcon()">
                   <el-option
                     v-for="item in womanicons"
                     :key="item.id"
                     :label="item.name"
                     :value="item.id">
                     <span style="float: left"><img width="20" height="20" :src="static_base+'/imgs/'+item.id"/></span>
                     <span style="float: right; color: #8492a6; font-size: 13px">{{ item.name }}</span>
                   </el-option>
                 </el-select>
               </td>
             </tr>

             <tr width="100%">
               <td  width="30%" >列间距</td>
               <td  width="70%">
                 <el-slider
                       :min=50
                       :max=300
                       v-model="columnWidth"
                       :step="10"
                       @change="updateColumnWidth"
                       show-stops>
                     </el-slider>
               </td>
             </tr>
             <tr width="100%">
               <td  width="30%" >层间距</td>
               <td  width="70%">
                 <el-slider
                       :min=1
                       :max=3
                       v-model="heightWidthRatio"
                       :step="0.1"
                       @change="updateHeightWidthRatio"
                       show-stops>
                     </el-slider>
               </td>
             </tr>


          </table>
       </div>
       <div id="board-panel"  v-lazy-load-bg="static_base+'/imgs/mu.jpg'"  v-loading="panel_loading" >
          <svg id="main-page-svg" width="960" height="700" style="background-color:rgba(192,192,192,0.3);" >
          </svg>

       </div>
       <button v-if="current_access_right=='admin' || current_access_right=='creator'" @click="createPerson" title="新建人物" class="add-person-plus"></button>
       <button v-if="current_access_right=='admin' || current_access_right=='creator'" @click="clearPeople" title="清空人物" id="clear-person-plus"></button>
       <button v-if="current_access_right=='creator'" @click="takeSnapshot" title="快照备份" class="take-snapshot-plus"></button>
       <button @click="exportImage" title="导出图片" class="export-img-plus"></button>
       <button @click="exportExcel" title="导出Excel表格" class="export-excel-plus"></button>
       <button @click="resetPosition" title="缩放平移复位" class="reset-pos"></button>
       <div id="suofangbi">缩放比:{{(svg_scale*100).toFixed(2)}}%</div>

    </div>
  </div>
</template>

<script>
import {AXIOS} from '~/common/http-commons'
import * as d3 from 'd3';
import FileSaver from 'file-saver'
import '~/css/d3-context-menu.css'
import state from '~/common/state'
import treeAlign from '~/mixins/treeAlign'
import staticbase from '~/mixins/staticbase'
import base from '~/mixins/base'
import "canvas-toBlob"
import swal from 'sweetalert'

export default {
  name: 'MainPage',
  components: {
  },
  verify: {
    snapshotName: {
       maxLength:18,
       minLength:1,
       message: "快照名1~18个字符"
    }
  },
  data () {
    return {
      svg_scale: 1,
      previousSearchName: '',
      searchName: '',
      searchIndex: 0,
      heightWidthRatio: 1,
      staticbaseknown: false,
      static_base: '.',
      rest_base: '.',
      current_access_right:'creator',
      snapshot_id: '',
      contextName: '',
      contextId: '',
      addConnectionVisible: false,
      wholePageLoading: false,
      snapshotErrorMessage: '',
      snapshotError: false,
      snapshotName: '',
      state,
      left_margin: 160,
      blockingLicenseWindow: false,
      top_margin: 150,
      panel_loading: true,
      manicon: '',
      womanicon: '',
      clickSwitch: false,
      rowDistance: 190,
      columnWidth: 90,
      whoami: 'null',
      fromsex: '',
      tosex: '',
      relationTypes: [],
      relationTypes4Sex: [],
      relationType: '',
      selected_family_tree: '',
      myfamilytrees: [],
      mysnapshots: [],
      dialogVisible: false,
      connectionDialogVisible: false,
      snapshotDialogVisible: false,
      relationTip: '',
      familytreedata: '',
      from_person_id: '',
      to_person_id: '',
      zoom_handler: {},
      nodes_connected: false,
      expect_level_gap: 0,
      licenseCode:'',
      form: {
        name: '',
        sex: '1',
        born: ''
      }
    }
  },
  mixins: [treeAlign,staticbase, base],
  methods: {
    setWomanIcon(){
      AXIOS.get('/api/v1/user/icon-by-file-name/'+this.woman_select+'/').then(response =>{
        if(response.data.ok==true){
          this.womanicon = response.data.data;
          this.loadFamilyTree(false);
        }
      }).catch(e=>{

      })
    },
    setManIcon(){
      AXIOS.get('/api/v1/user/icon-by-file-name/'+this.man_select+'/').then(response =>{
        if(response.data.ok==true){
          this.manicon = response.data.data;
          this.loadFamilyTree(false);
        }
      }).catch(e=>{

      })
    },

    removeFromPublic(){
      if(this.selected_family_tree==null || this.selected_family_tree.length<1){
        swal ( "提示" ,  "您尚未选中一个家谱" ,  "info" );
        return;
      }
      this.$confirm('确认撤离公共区？')
                  .then(_ => {
                    AXIOS.delete('/api/v1/familytree/public-tree/'+this.selected_family_tree).then(response=>{
                      if(response.data.ok==true){
                        this.$notify({
                          title: '成功',
                          type: 'success',
                          message: response.data.message
                        });
                      }else{
                        this.$notify.error({
                          title: '失败',
                          message: response.data.message
                        });
                      }
                    }).catch(e=>{
                      this.$notify.error({
                        title: '错误',
                        message: '未知错误'
                      });
                    })
                    done();
                  }).catch(_ => {

                  });

    },
    publishTree(){
      if(this.selected_family_tree==null || this.selected_family_tree.length<1){
        swal ( "提示" ,  "您尚未选中一个家谱" ,  "info" );
        return;
      }
      this.$confirm('发布到公共区会允许所有用户看到该家谱，确认发布？')
                  .then(_ => {
                    AXIOS.post('/api/v1/familytree/public-tree',{
                      family_tree_id: this.selected_family_tree
                    }).then(response=>{
                      if(response.data.ok==true){
                        this.$notify({
                          title: '成功',
                          type: 'success',
                          message: response.data.message
                        });
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
                    done();
                  }).catch(_ => {

                  });
    },
    updateHeightWidthRatio(){
      if(this.selected_family_tree==null || this.selected_family_tree.length<1){
        return;
      }
      this.loadFamilyTree(false);
    },
    updateColumnWidth(){
      if(this.selected_family_tree==null || this.selected_family_tree.length<1){
        return;
      }
      this.loadFamilyTree(false);
    },
    updateRowDistance(){
      if(this.selected_family_tree==null || this.selected_family_tree.length<1){
        return;
      }
      this.loadFamilyTree(false);
    },

    loadMySnapshots(){
      AXIOS.get('/api/v1/familytree/snapshot').then(response => {
        if(response.data.ok == true){
          this.mysnapshots = response.data.data;
        }
      }).catch(e => {
        this.$router.push("/login");
      })
    },
    applySnapshot(){
      if(this.selected_family_tree==null || this.selected_family_tree.length<1){
        swal ( "提示" ,  "您尚未选中一个家谱" ,  "info" );
        this.snapshot_id = '';
        return;
      }

      this.$confirm('从快照恢复会删除当前家谱内的所有数据，确认从快照恢复？')
                  .then(_ => {
                    this.wholePageLoading = true;
                    AXIOS.post('/api/v1/familytree/snapshot-restore',{
                      snapshot_id: this.snapshot_id,
                      family_tree_id: this.selected_family_tree
                    }).then(response => {
                      if(response.data.ok==true){
                        this.$notify({
                          title: '成功',
                          type: 'success',
                          message: '快照恢复成功'
                        });
                        this.snapshot_id = '';
                        this.wholePageLoading = false;
                        this.loadFamilyTree(true);
                      }else{
                        this.$notify.error({
                          title: '错误',
                          message: '快照恢复错误'
                        });
                        this.wholePageLoading = false;
                        this.snapshot_id = '';
                      }
                    }).catch(e => {
                      console.log(e.response);
                      this.$notify.error({
                        title: '错误',
                        message: '快照恢复错误'
                      });
                      this.wholePageLoading = false;
                      this.snapshot_id = '';
                    })
                    done();
                  }).catch(_ => {
                    this.snapshot_id = '';
                  });
    },
    addRelation() {
      if(this.relationType==null || this.relationType.length<1){
        swal ( "提示" ,  "您尚未选中一种关系" ,  "info" );
        return;
      }
      if(this.form.name==null || this.form.name.length<1){
        swal ( "提示" ,  "姓名不能为空" ,  "info" );
        return;
      }
      this.wholePageLoading = true;
      AXIOS.post('/api/v1/relation/node',{
        from_person_id: this.contextId,
        relation_type: this.relationType,
        family_tree_id: this.selected_family_tree,
        new_person_name: this.form.name
      }).then(response => {
        if(response.data.ok==true){
          this.$notify({
            title: '创建成功',
            type: 'success',
            message: response.data.message
          });
          this.wholePageLoading = false;
        }else{
          this.$notify.error({
            title: '错误',
            message: response.data.message
          })
          this.wholePageLoading = false;
        }
        this.addConnectionVisible = false;
        this.loadFamilyTree(true);
      }).catch(e => {
        this.$notify.error({
          title: '错误',
          message: '新建关系人物失败'
        });
        this.wholePageLoading = false;
      })
    },
    resetPosition(){
      var svg_cont = d3.select("#board-panel")
      var t = d3.zoomIdentity.translate(0, 0).scale(1)
      svg_cont.call(this.zoom_handler.transform, t)
    },
    exportExcel() {
      if(this.selected_family_tree==null || this.selected_family_tree.length<1){
        swal ( "提示" ,  "您尚未选中一个家谱" ,  "info" );
        return;
      }
      if(this.familytreedata.nodes==null || this.familytreedata.nodes.length<1){
        swal ( "提示" ,  "当前家谱没有人物节点" ,  "info" );
        return;
      }
      this.wholePageLoading = true;
      AXIOS.post("/api/v1/familytree/excel",{
        family_tree_id: this.selected_family_tree
      }).then(response => {
        this.wholePageLoading = false;
        var downloadForm = document.createElement("form");
        downloadForm.setAttribute("method", "post");
        downloadForm.setAttribute("action", this.rest_base+'/api/v1/familytree/download');
        var pathfield = document.createElement("input");
        pathfield.setAttribute("type", "hidden");
        pathfield.setAttribute("name", 'path');
        pathfield.setAttribute("value", response.data.data);
        downloadForm.appendChild(pathfield);
        document.body.appendChild(downloadForm);
        downloadForm.submit();
      }).catch(e => {
        this.wholePageLoading = false;
      })
    },
    exportImage () {

      if(this.selected_family_tree==null || this.selected_family_tree.length<1){
        swal ( "提示" ,  "您尚未选中一个家谱" ,  "info" );
        return;
      }
      if(this.familytreedata.nodes==null || this.familytreedata.nodes.length<1){
        swal ( "提示" ,  "当前家谱没有人物节点" ,  "info" );
        return;
      }

      this.resetPosition();
      this.wholePageLoading = true;
      var doctype = '<?xml version="1.0" standalone="no"?>'
  + '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">';

// serialize our SVG XML to a string.
      var source = (new XMLSerializer()).serializeToString(d3.select('#main-page-svg').node());
      var blob = new Blob([ doctype + source], { type: 'image/svg+xml;charset=utf-8' });
      var url = window.URL.createObjectURL(blob);
      console.log(url)
      var downloadImg=document.createElement("img");
      downloadImg.src=url;

      var storeThis = this;

      downloadImg.onload=function(){
         var canvas=document.createElement("canvas");
         canvas.width=d3.select('#main-page-svg').attr('width');
         canvas.height=d3.select('#main-page-svg').attr('height');
         var ctx=canvas.getContext('2d');
         //this.setAttributeNS(xlinkNS, 'href', canvas.toDataURL());
         ctx.font = "28px Arial";
         ctx.fillStyle = "rgba(180,180,180,0.9)";
         ctx.fillText('家谱海 familytreesea.com',0,30);
         ctx.drawImage(downloadImg,0,0);


         try{
           if(canvas.toBlobHD!=null){
             canvas.toBlobHD(function(blob) {
                    FileSaver.saveAs(
                          blob
                        , "family.png"//保存文件名称
                    );
                }, "image/png");
                storeThis.wholePageLoading = false;
           }else{
             swal ( "提示" ,  "该功能仅支持谷歌(Chrome)，火狐(Firefox)，猎豹和遨游等浏览器" ,  "info" );
           }
         }catch(err){
           swal ( "提示" ,  "该功能仅支持谷歌(Chrome)，火狐(Firefox)，猎豹和遨游等浏览器" ,  "info" );
         }
       }

    },
    loadRelationBySex () {
      if(this.fromsex.length<1 || this.tosex.length<1){
        return
      }
      AXIOS.get('/api/v1/relation/type/'+this.fromsex+'/'+this.tosex).then(response => {
          this.relationTypes = response.data
          //this.loadPanelTree()
       }).catch(e => {
          this.$router.push('/login')
       })
    },
    loadRelationBySingleSex (sex) {
      if(sex.length<1){
        return
      }
      AXIOS.get('/api/v1/relation/type/'+sex).then(response => {
          this.relationTypes4Sex = response.data
       }).catch(e => {
          this.$router.push('/login')
       })
    },

    loadPanelTree (){
      var storeThis = this;
      var lastClickNode = null;
      var svg_container = d3.select("#board-panel");
      var svg = d3.select("#main-page-svg"),
      width = +svg.attr("width"),
      height = +svg.attr("height");
      var color = d3.scaleOrdinal(d3.schemeCategory20);
      var x = d3.scaleLog().rangeRound([0, width]);
      var y = d3.scaleLog().rangeRound([0, height]);
      var simulation = d3.forceSimulation().force("link", d3.forceLink()
      .distance(function(d){;return d.higher==0?90:160;})
      .id(function(d) { return d.id; }))
      .force("charge", d3.forceManyBody())

      this.zoom_handler = d3.zoom()
          .on("zoom", zoom_actions);

      this.zoom_handler(svg_container);

      for (var i = 0, n = 250; i < n; ++i) {
         simulation.tick();
      }
      //.force("y", d3.forceY(function(d) { return y(height/2+d.level*height); }).strength(0.2))
      //.force("x",d3.forceX(width/2).strength(0.01))
      //.force("center", d3.forceCenter(width/3, height/3));

      var g = svg.append("g")
          .attr("class", "everything");

      var link = g.selectAll(".links").data(this.familytreedata.links).enter().append("g").attr("class", "links")
      .append("line").attr("stroke", "#7E08F1").attr("opacity",0.1)
      .attr("stroke-width", function(d) { return 3 });



      var node = g.selectAll(".nodes").data(this.familytreedata.nodes).enter().append("g").attr("class", "nodes");
      // .call(d3.drag()
      //    .on("start", dragstarted)
      //    .on("drag", dragged)
      //    .on("end", dragended));
      //.call(d3.drag().on("start", dragstarted).on("drag", dragged).on("end", dragended));
      var heightPosMap = this.familytreedata['heightPosMap'];

      var drag_line = g.append('svg:path')
           .attr('class', 'link dragline hidden')
           .attr('style','stroke:black;fill:none;')
           .attr('stroke-width','2')
           .attr('d', 'M0,0L0,0');

      node.append("image")
               .attr("xlink:href", function(d){return d.sex==1?storeThis.manicon:storeThis.womanicon;})
               .attr("x", -8)
               .attr("y", -8)
               .attr("width", 50)
               .attr("height", 50);



      node.append("text").attr("dx", -2).attr("dy", "-0.5em").text(function(d) { return d.name });

      node.append("title").text(function(d) { return d.name; });

      simulation.nodes(this.familytreedata.nodes).on("tick", ticked);

      svg.on('mousemove', function(){
        // update drag line
          if(storeThis.clickSwitch){
            var tmpx = d3.mouse(this)[0];
            var tmpy = d3.mouse(this)[1];
            if(tmpx>lastClickNode.x){
              // on the right side
              tmpx = tmpx -2;

            }else if(tmpx<lastClickNode.x){
              // on the left side
              tmpx = tmpx + 2;

            }else{
              // compare Y
              if(tmpy<lastClickNode.y){
                // drag downwards
                tmpy = tmpy+2;

              }else if(tmpy>lastClickNode.y){
                // drag upwards
                tmpy = tmpy-2;

              }else{
                // do nothing
              }
            }
            drag_line.attr('d', 'M' + lastClickNode.x + ',' + lastClickNode.y + 'L' + tmpx + ',' + tmpy);

          }

      });

      //cc.on("dblclick", function(d){ alert("node was double clicked"); console.log(d); });

      node.on("click", function(d){
        if(storeThis.current_access_right!='admin' && storeThis.current_access_right!='creator'){
          return;
        }
        if(!storeThis.clickSwitch){
          storeThis.clickSwitch = !storeThis.clickSwitch
          //first click
          // select node
          var click_node = d;
          lastClickNode = d;

          drag_line
           .classed('hidden', false)
           .attr('stroke-width','2')
           .attr('d', 'M' + click_node.x + ',' + click_node.y + 'L' + click_node.x + ',' + click_node.y);
        }else{
          function checkConnection(from_id, to_id){
            console.log("hell---");
            console.log(storeThis.familytreedata.links);
            var neighborMap = {}
            for(var i in storeThis.familytreedata.links){
              var link = storeThis.familytreedata.links[i];
              var source_id = link.source.id;
              var target_id = link.target.id;

              if(source_id in neighborMap){
                neighborMap[source_id].add(target_id);
              }else{
                neighborMap[source_id]=new Set([]);
                neighborMap[source_id].add(target_id);
              }

              if(target_id in neighborMap){
                neighborMap[target_id].add(source_id);
              }else{
                neighborMap[target_id]=new Set([]);
                neighborMap[target_id].add(source_id);
              }
            }

            function union(setA, setB) {
                var _union = new Set(setA);
                for (var elem of setB) {
                    _union.add(elem);
                }
                return _union;
            }
            var visitSet = new Set([]);
            var neighborSet=neighborMap[from_id];
            if(neighborSet==null){
              neighborSet = new Set([]);
            }
            var oldSize = neighborSet==null?0:neighborSet.size;
            var newSize = oldSize;
            var finalSet = new Set([]);
            finalSet.add(from_id);
            neighborSet.add(from_id);
            do{
              oldSize=newSize;
              for (var it = neighborSet.values(), val= null; val=it.next().value; ) {
                 if(visitSet.has(val)){
                   // do nothing
                 }else{
                   visitSet.add(val);
                   finalSet=union(neighborMap[val], finalSet);
                 }
              }
              neighborSet=finalSet;
              newSize=neighborSet==null?0:neighborSet.size;
            }while(newSize>oldSize)

            if(finalSet.has(to_id)){
              console.log("is connected")
              return true;
            }
            console.log("not connected")
            return false;
          }
          //second click
          storeThis.fromsex = lastClickNode.sex;
          storeThis.from_person_id = lastClickNode.id;
          var firstLevel = lastClickNode.level;
          var relationLabel = '';
          relationLabel+=lastClickNode.name;
          relationLabel+="是";

          lastClickNode = d;
          var secondLevel = lastClickNode.level;
          relationLabel+=lastClickNode.name;
          relationLabel+="的:";
          storeThis.tosex = lastClickNode.sex;
          storeThis.to_person_id = lastClickNode.id;

          storeThis.nodes_connected = checkConnection(storeThis.from_person_id, storeThis.to_person_id);
          storeThis.expect_level_gap = firstLevel-secondLevel;

          if(storeThis.to_person_id==storeThis.from_person_id){
            storeThis.clickSwitch = !storeThis.clickSwitch;
            drag_line
             .attr('stroke-width','0')
             .style('hidden', 'true')
             .classed('hidden', true);
            return;
          }

          storeThis.clickSwitch = !storeThis.clickSwitch;
          drag_line
           .attr('stroke-width','0')
           .style('hidden', 'true')
           .classed('hidden', true);

          storeThis.relationTip = relationLabel;
          storeThis.connectionDialogVisible = true;
          storeThis.relationType='';
          storeThis.loadRelationBySex();

        }

      });


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
              title: '删除人物',
              action: function(elm, d, i) {
                var personId = d.id;
                storeThis.$confirm('如果无快照不能恢复哦，确认删除吗？')
                  .then(_ => {
                    storeThis.wholePageLoading = true;
                    AXIOS.delete('/api/v1/person/'+personId).then(response => {
                      if(response.data.ok==true){
                        storeThis.$notify({
                          title: '删除成功',
                          type: 'success',
                          message: response.data.message
                        })
                        storeThis.wholePageLoading = false;
                        storeThis.loadFamilyTree(true);
                      }else{
                        storeThis.$notify.error({
                          title: '删除失败',
                          message: response.data.message
                        });
                        storeThis.wholePageLoading = false;
                      }

                    }).catch(e => {
                      storeThis.$notify.error({
                        title: '删除失败',
                        type: 'error',
                        message: '未知错误'
                      });
                      this.panel_loading = false;
                    })
                    done();
                  }).catch(_ => {});


              }
    },{
      title: '人物生平',
      action: function(elm, d, i) {
        storeThis.$router.push('person/'+d.id)
      }
    },{
      title: '添加关系',
      action: function(elm, d, i){
        storeThis.contextName = d.name;
        storeThis.contextId = d.id;
        storeThis.loadRelationBySingleSex(d.sex);
        storeThis.addConnectionVisible = true;
        storeThis.relationType = '';
        storeThis.form.name = '';
      }
    }
    ]

    var menu_readonly=[
      {
        title: '人物生平',
        action: function(elm, d, i) {
          storeThis.$router.push('person/'+d.id)
        }
      }
    ]
    var menu_droplink=[
      {
        title: '删除关系',
        action: function(elm, d, i) {
          var srcId = d.source.id;
          var targetId = d.target.id;

          AXIOS.delete('/api/v1/relation/'+srcId+"/"+targetId).then(response =>{
            if(response.data.ok==true){
              storeThis.$notify({
                title: '成功',
                type: 'success',
                message: response.data.message
              });

              storeThis.loadFamilyTree(true);
            }else{
              storeThis.$notify.error({
                title:"错误",
                message: response.data.message
              });
            }
          }).catch(e=>{
            storeThis.$notify.error({
              title: '错误',
              message: '未知错误'
            })
          })
        }
      }
    ]

    if(this.current_access_right=='viewer'){
      node.on('contextmenu', d3.contextMenu(menu_readonly));
    }else{
      node.on('contextmenu', d3.contextMenu(menu));
      link.on('click', d3.contextMenu(menu_droplink));
    }


    //Zoom functions
    function zoom_actions(){
      svg.attr("transform", d3.event.transform)
      storeThis.svg_scale = Number(d3.event.transform.k);
    }
    function ticked() {
        link.attr("x1", function(d) { return d.source.x+15; })
         .attr("y1", function(d) { return d.source.y+15; })
         .attr("x2", function(d) { return d.target.x+15; })
         .attr("y2", function(d) { return d.target.y+15; });

        node
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
        .attr("cx", function(d) { return d.x = (d.column-1)*storeThis.columnWidth+storeThis.left_margin; })
        .attr("cy", function(d) { return d.y = storeThis.top_margin + storeThis.columnWidth*storeThis.heightWidthRatio*heightPosMap[d.level]; });
    }

    function dragged(d) {
        d.fx = d3.event.x;
        d.fy = d3.event.y;
    }
    function dragended(d) {
        if (!d3.event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    }
    function chooseColor(d){
        if(d.sex==1){
          return "blue";
        }else{
          return "red";
        }
      }
    },
    changeSelect () {
      this.loadFamilyTree(true);
    },
    clearSVG () {
      d3.select("svg").selectAll("*").remove();
    },
    adjustPanelSize () {
      var svg = d3.select("#main-page-svg")
      var graphWidth = (this.familytreedata['column-count']-1)*this.columnWidth+this.left_margin*2;
      var graphHeight = this.columnWidth*this.familytreedata['svgHeightIndex']*this.heightWidthRatio+this.rowDistance;

      svg.attr("width", graphWidth)
      svg.attr("height",  graphHeight)
    },

    loadFamilyTree (loadRemote) {
      if(this.selected_family_tree.length<1)
      {
         return;
      }else{
        if(loadRemote){
          this.panel_loading = true;
          AXIOS.get('/api/v1/familytree/family-tree/'+this.selected_family_tree).then(response => {
            if(response.data.ok==true){
              var raw_family_tree_data = response.data.data;
              this.familytreedata = this.treeAlign(raw_family_tree_data);
              this.clearSVG()
              this.adjustPanelSize()
              this.loadPanelTree()
              this.clickSwitch = false;
              this.panel_loading = false;
            }else{
              this.$notify.error({
                title:"错误",
                message: response.data.message
              });
              this.panel_loading = false;
            }
           }).catch(e => {
              console.log(e);
              this.$notify.error({
                title:"错误",
                message: "未知错误"
              });
              this.$router.push('/')
           });
        }else{
          this.clearSVG()
          this.adjustPanelSize()
          this.loadPanelTree()
          this.clickSwitch = false;
        }

      }
    },
    buildRelation () {
      if(this.relationType.length<1){
        swal ( "提示" ,  "请选择一个关系" ,  "info" );
        return;
      }
      if(this.nodes_connected){
        var parent_relation_id=[1,2];
        var child_relation_id=[5,6];
        if(parent_relation_id.indexOf(this.relationType)!=-1 && this.expect_level_gap>=0){
          swal ( "提示" ,  "不合情理的关系，请重选(づ￣ 3￣)づ" ,  "info" );
          return;
        }
        if(child_relation_id.indexOf(this.relationType)!=-1 && this.expect_level_gap<=0){
          swal ( "提示" ,  "不合情理的关系，请重选(づ￣ 3￣)づ" ,  "info" );
          return;
        }
      }
      AXIOS.post('/api/v1/relation', {
        from_id: this.from_person_id,
        to_id: this.to_person_id,
        family_tree_id: this.selected_family_tree,
        relation_id: this.relationType
      }).then(response => {
      this.$notify({
        title: '成功',
        type: 'success',
        message: '提交成功'
      })
      this.connectionDialogVisible = false;
      this.loadFamilyTree(true)
      }).catch(e => {
        console.log(e)
        this.$notify.error({
          title: '错误',
          message: '提交失败'
        })
      })
    },
    addPerson () {
      if(this.selected_family_tree.length<1)
      {
         swal ( "提示" ,  "请选中一个家谱！" ,  "info" )
         return
      }
      //const reg = /^[A-Za-z0-9()\u4e00-\u9fa5]+$/
      name = this.form.name.trim()
      // if(!reg.test(name))
      // {
      //    swal ( "提示" ,  "请输入中文、数字和英文！" ,  "info" )
      //    return
      // }

      if(name==null || name.length<1){
        swal ( "提示" ,  "姓名不能为空(づ￣ 3￣)づ！" ,  "info" )
        return;
      }

      AXIOS.post('/api/v1/person', {
        name: this.form.name,
        sex: this.form.sex,
        familytreeid: this.selected_family_tree
      }).then(response => {
        if(response.data.ok==true){
          this.$notify({
            title: '成功',
            type: 'success',
            message: response.data.message
          })
          this.loadFamilyTree(true)
        }else{
          this.$notify.error({
            title: '错误',
            message: response.data.message
          })
        }
      }).catch(e => {
        console.log(e)
        this.$notify.error({
          title: '错误',
          message: '未知错误'
        })
      })


      this.dialogVisible = false
    },
    handleClose(done) {
        this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {});
    },
    createFamilyTree () {
      this.$router.push('/create-family-tree')
    },
    verifySnapshot() {
      var valid = this.$verify.check();
      if(this.$verify.$errors.snapshotName.length>0){
        this.snapshotError = true;
        this.snapshotErrorMessage = this.$verify.$errors.snapshotName[0];
      }else{
        this.snapshotError = false;
        this.snapshotErrorMessage = '';
      }
      return valid;
    },
    executeSnapshot () {
      this.snapshotDialogVisible = false;
      var valid = this.verifySnapshot();
      if(!valid){
        return;
      }
      this.wholePageLoading = true;
      AXIOS.post("/api/v1/familytree/snapshot",{
        "family_tree_id": this.selected_family_tree,
        "snapshot_name": this.snapshotName
      }).then(response =>{
        console.log(response.data);
        if(response.data.ok==true){
          this.$notify({
            title: '快照成功',
            type: 'success',
            message: response.data.message
          })
          this.loadMySnapshots();
        }else{
          this.$notify.error({
            title: "错误",
            message: response.data.message
          })
        }
        this.wholePageLoading = false;
      }).catch(e =>{
        console.log(e.response);
        this.$notify.error({
          title: "出错",
          message: "未知错误"
        })
        this.wholePageLoading = false;
      })
    },
    takeSnapshot() {
      // this might be a time consuming task, do not block other users
       // check if current family tree id is available
       if(this.selected_family_tree==null || this.selected_family_tree.length<1){
         swal ( "提示" ,  "请选择要备份的家谱" ,  "info" )
         return;
       }
       if(this.familytreedata.nodes==null || this.familytreedata.nodes.length<1){
         swal ( "提示" ,  "当前家谱没有人物节点" ,  "info" );
         return;
       }
       // pop up box for user to enter snapshot name
       this.snapshotDialogVisible = true;
       // submit the backup request and block the whole page()
       // release the whole page when backup is over
    },
    clearPeople(){
      if(this.selected_family_tree==null || this.selected_family_tree.length<1){
        swal ( "提示" ,  "您尚未选中一个家谱" ,  "info" )
      }else{
        if(this.familytreedata.nodes==null || this.familytreedata.nodes.length<1){
          swal ( "提示" ,  "当前家谱没有人物节点" ,  "info" );
          return;
        }
        this.$confirm('删除本家谱所有人物，请谨慎操作，确认删除？')
        .then(_ => {
          this.wholePageLoading = true;
          AXIOS.delete('/api/v1/familytree/familytreepeople/'+this.selected_family_tree).then(response => {
            this.$notify({
            title: '成功',
            message: '清空家谱人物成功',
            type: 'success'
          });
          this.wholePageLoading = false;
          this.loadFamilyTree(true);
          }).catch(e=>{
            this.$notify.error({
                title: '错误',
                message: '清空发生了错误'
            });
            this.wholePageLoading = false;
          });
          done();
        }).catch(_ => {});
      }
    },
    createPerson () {
      if(this.selected_family_tree==null || this.selected_family_tree.length<1){
        swal ( "提示" ,  "您尚未选中一个家谱" ,  "info" )
      }else{
        this.dialogVisible = true;
        this.form.name = '';
      }
    },
    getMyFamilyTrees (){
      AXIOS.get('/api/v1/familytree/my-family-trees').then(response => {
          this.myfamilytrees = response.data.data
       }).catch(e => {
          this.$router.push('/login')
       })
    },
    loadDefaultIcons (){
      AXIOS.get('/api/v1/user/default-human-icon').then(response => {
          this.manicon = response.data['man_icon']
          this.womanicon = response.data['woman_icon']
          this.panel_loading = false;
          if(this.selected_family_tree!=null && this.selected_family_tree!=''){
            this.changeSelect();
          }
       }).catch(e => {
          this.$router.push('login')
       })
    }

  },
  created() {
    this.getStaticBase();
  },
  mounted () {
    this.selected_family_tree = this.state.currentFt;
    this.state.currentFt = '';
    this.loadDefaultIcons();
    this.getMyFamilyTrees();
    this.loadMySnapshots();
    this.checkLicense();
  },
  updated () {
     //this.whoamifoo()
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.main-page{
  flex-flow: column;
  width: 100%;
  display: flex;
  height: 100%;
  left: 0;
  position:absolute;
  flex: 1;
}
#content {
  flex: 1;
  display: flex;
  flex-direction: row;
  width: 100%;
  max-height: 100%;
}
.sidebar {
  background-size: 100% 100%;
  width: 300px;
  min-width: 300px;
  border: 5px solid #111;
  -moz-box-shadow: 35px 35px 7px #999999;
  -moz-border-bottom-right-radius: 15px;
  -moz-border-radius:25px;
  height: 100%;
  max-height: 100%;
}

#board-panel {
  max-height: 100%;
  overflow: scroll;
  background-size: 100% 100%;
  flex: 1;
  border: 5px solid #111;
  -moz-box-shadow: 35px 35px 7px #999999;
  -moz-border-bottom-right-radius: 15px;
  -moz-border-radius:25px;
  padding: 15px 25px;
  position: relative;
}


.panel-inner-dropdown{
  position: absolute;
  top: 65px;
  left: 349px;
}
.panel-inner-dropdown2{
  position: absolute;
  top: 65px;
  left: 809px;
}

.add-person-plus{
  position: absolute;
  top: 85px;
  left: 310px;
  background:url('~/assets/plus30.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:30px;
  height:30px;
}
#clear-person-plus{
  position: absolute;
  top: 125px;
  left: 310px;
  background:url('~/assets/clear.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:30px;
  height:30px;
}
.take-snapshot-plus{
  position: absolute;
  top: 245px;
  left: 310px;
  background:url('~/assets/snapshot30.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:30px;
  height:30px;
}
.export-img-plus{
  position: absolute;
  top: 200px;
  left: 310px;
  background:url('~/assets/screenshot.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:30px;
  height:30px;
}

.export-excel-plus{
  position: absolute;
  top: 160px;
  left: 310px;
  background:url('~/assets/excel30.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:30px;
  height:30px;
}
.reset-pos{
  position: absolute;
  top: 20px;
  left: 460px;
  background:url('~/assets/resetpos.png') no-repeat;
  background-size: 100% 100%;
  border:none;
  width:30px;
  height:30px;
}

.add-person-plus:hover,#clear-person-plus:hover,.take-snapshot-plus:hover,.export-excel-plus:hover,.export-img-plus:hover, .reset-pos:hover{
  cursor: pointer;
  opacity: 0.8;
}









#search-on-main-panel{
  position: absolute;
  top: 15px;
  left: 310px;
  width: 120px;
  height: 30px;
}
#suofangbi{
  position: absolute;
  top: 23px;
  left: 493px;
  width: 220px;
  height: 30px;
}

.create-ft {
  width: 100%;
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

.side-op{
  margin-left: 50px;
  width: 200px;
}
.snapshot-invalid{
  color: red;
  font: 12px/18px STHeiti,'Microsoft YaHei',arial,\5b8b\4f53;
}


/* 设置滚动条的样式 */
::-webkit-scrollbar {
width: 10px;
}
/* 滚动槽 */
::-webkit-scrollbar-track {
-webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
border-radius: 10px;
}
/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
border-radius: 10px;
background: #bbb;
-webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.5);
}
::-webkit-scrollbar-thumb:window-inactive {
background: rgba(255,0,0,0.4);
}

</style>
