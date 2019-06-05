export default{
  methods: {
    treeAlign(raw_family_tree_data){
      var raw_nodes = raw_family_tree_data.nodes;
      var raw_links = raw_family_tree_data.links;
      var nextRowDistanceMap = raw_family_tree_data.nextRowDistance;

      // build a relation map for later reference
      var linkMap = {};
      for(var l in raw_links){
        var link = raw_links[l];
        var link_key = link.source+'-'+link.target;
        linkMap[link_key] = link.higher;
      }

      var levelListMap = {}
      for(var n in raw_nodes){
        var nd = raw_nodes[n];
        if(levelListMap[nd.level]!=null){
          levelListMap[nd.level].push(nd);
        }else{
          levelListMap[nd.level] = [];
          levelListMap[nd.level].push(nd);
        }
      }


      function directdye(a_nodelist, i, a_linkmap){

        var result = [];
        //result.push(a_nodelist[i]);
        for(var j = 0; j<a_nodelist.length; j++ ){
          if(j==i){
            continue;
          }
          var nodei = a_nodelist[i];
          var nodej = a_nodelist[j];
          var keyij = nodei.id+'-'+nodej.id;
          var keyji = nodej.id+'-'+nodei.id;

          if(a_linkmap.hasOwnProperty(keyij) && a_linkmap.hasOwnProperty(keyji)){
            result.push(j);
          }
        }

        return result;
      }

      function union(setA, setB) {
          var _union = new Set(setA);
          for (var elem of setB) {
              _union.add(elem);
          }
          return _union;
      }

      // 注意返回的集合是id的集合，不是对象的集合
      function dye(a_nodelist, i, a_linkmap, accessRecordSet){
        if(accessRecordSet.has(a_nodelist[i].id)){
          // this is unexpected case
          return [];
        }else{
          var resultIdSet = new Set([]);
          resultIdSet.add(a_nodelist[i].id);
          accessRecordSet.add(a_nodelist[i].id);
          var listOfNeighborsIndex = directdye(a_nodelist, i, a_linkmap);
          for(var m=0;m<listOfNeighborsIndex.length;m++){
            var subDyeResultIdSet = dye(a_nodelist, listOfNeighborsIndex[m], a_linkmap, accessRecordSet)
            resultIdSet = union(resultIdSet, subDyeResultIdSet)
          }

          return resultIdSet;
        }
      }

      function sortBySex(nodeList){
        // put male first, femail after
        nodeList.sort(function(f1, f2) {
           // Ascending: first age less than the previous
           if(f1.sex==f2.sex && f1.sex==0){
             return f1.id-f2.id;
           }
           return f2.sex - f1.sex;
        });
      }

      function subGroupByHusbandWife(a_nodelist, a_linkmap){
        //input : a_nodelist, this is nodes of the same level

        //构建一个id到对象的映射，因为id好做set操作，对象不好做
        var nodeIdMap = {};
        for(var k=0;k<a_nodelist.length;k++){
          nodeIdMap[a_nodelist[k].id]=a_nodelist[k];
        }
        var marriage_group_list = [];
        var accessRecordSet = new Set([]);
        for(var i=0; i < a_nodelist.length; i++){
          if(accessRecordSet.has(a_nodelist[i].id )){
            continue;
          }else{
            var idlistset = dye(a_nodelist, i, a_linkmap, accessRecordSet);
            var idlist = Array.from(idlistset);

            if(idlist.length>0){
              var objlist = []
              for(var n=0;n<idlist.length;n++){
                objlist.push(nodeIdMap[idlist[n]])
              }
              sortBySex(objlist);
              marriage_group_list.push(objlist);
            }
          }
        }
        return marriage_group_list;
      }
      // till now, levelListMap is the node data grouped by level, top level is 1

      // next, subgroup each level by husband-wife relation(same level relation)
      var svgHeightIndex = 0;

      // key is level, value is list of node of that particular level
      for(var key in levelListMap){
        levelListMap[key] = subGroupByHusbandWife(levelListMap[key], linkMap);
      }

      var levels = Object.keys(levelListMap);
      levels.sort(function(f1, f2) {
         // Ascending: first age less than the previous
         return f1 - f2;
      });

      var heightPosMap = {};
      heightPosMap[1] = 0;
      for(var k in levels){
        var key = levels[k];
        svgHeightIndex+=Math.max(parseInt(nextRowDistanceMap[key]/2)*0.5,1);
        if(key<Object.keys(levelListMap).length){
          heightPosMap[Number(key)+1] = svgHeightIndex;
        }
      }


      // till now , each level is filled with subgroups, with each group has husband and wives inside, and husband on the left most

      // next, loop from the top most level, for the first level, grouping by husband and wife is enough, order between groups is not important, keep track of each marriage group and its members for the next level usage
      // from the second level, do second level subgrouping by looping from upper marriage group first and find all each groups decendants, for each level, at the end, is a well ordered list of marriage group, well formed for the next level to use, until it reaches the last level
      //
      function findAncestorGroupIndex (currentMarriageGroup, previousLevel, linkMap, parentIndexMap){
        for(var cindex in currentMarriageGroup){
          for(var pindex in previousLevel){
            for(var pii in previousLevel[pindex]){
              var nodeleft = currentMarriageGroup[cindex];
              var noderight = previousLevel[pindex][pii];
              var key = nodeleft.id+'-'+noderight.id;
              var keyRev = noderight.id+'-'+nodeleft.id;

              if(linkMap.hasOwnProperty(key) && linkMap.hasOwnProperty(keyRev)){
                for(var spreadId in currentMarriageGroup){
                  parentIndexMap[currentMarriageGroup[spreadId].id] = Number(pindex)+1;
                }
                return Number(pindex)+1;
              }
            }
          }
        }
        return -1;
      }

      function maxChildCountOfMarriageGroup(mgp){
        var ret = 0;
        for(var ind in mgp){
          if(mgp[ind].leafDescendantCount>ret){
            ret = mgp[ind].leafDescendantCount;
          }
        }
        return ret;
      }

      var previousLevel = [];
      var maxWidth = 0;
      var parentIndexMap = {};

      for(var m in levels){
        var currentLevel = levels[m];
        if(currentLevel==1){
          var previousColumn = 0;

          var accumuWidth = 0;
          for(var vi in levelListMap[currentLevel]){
            // in this iteration is a marriage group
            var col = 1;
            var childCountOfCurrentGroup = 0;
            for(var wii in levelListMap[currentLevel][vi]){
              if(levelListMap[currentLevel][vi][wii].leafDescendantCount>childCountOfCurrentGroup){
                childCountOfCurrentGroup = levelListMap[currentLevel][vi][wii].leafDescendantCount
              }
            }
            // now we have got the children count of the current group
            for(var wii in levelListMap[currentLevel][vi]){
              levelListMap[currentLevel][vi][wii].column = Math.max(previousColumn+1,accumuWidth + col + parseInt(childCountOfCurrentGroup/2));
              previousColumn = levelListMap[currentLevel][vi][wii].column;
              if(levelListMap[currentLevel][vi][wii].column>maxWidth){
                maxWidth = levelListMap[currentLevel][vi][wii].column;
              }
              col+=1;
            }

            accumuWidth = previousColumn-parseInt(levelListMap[currentLevel][vi].length/2)+parseInt(childCountOfCurrentGroup/2);

          }
          previousLevel = levelListMap[currentLevel];
          continue;
        }else{

          var currentLevelList = levelListMap[currentLevel];
          var currentRowGroupHolder = {};
          for(var cgIndex in currentLevelList){
            var cg = currentLevelList[cgIndex];
            var grp_position = findAncestorGroupIndex(cg, previousLevel, linkMap, parentIndexMap);

            if(currentRowGroupHolder[grp_position]==null){
              currentRowGroupHolder[grp_position]=[];
              currentRowGroupHolder[grp_position].push(cg);
            }else{
              currentRowGroupHolder[grp_position].push(cg)
            }
          }
          // spread from currentRowGroupHolder to currentLevelList
          var parentOrders = Object.keys(currentRowGroupHolder);
          parentOrders.sort(function(f1, f2) {
             // Ascending: first age less than the previous
             return f1 - f2;
          });

          var tmpLevelList = [];
          for(var i=0;i<parentOrders.length;i++){
            tmpLevelList = tmpLevelList.concat(currentRowGroupHolder[parentOrders[i]]);
          }

          var accumuWidth = 0;
          var previousColumn = 0;
          for(var mgroupIndex in tmpLevelList){
            var col = 1;
            var childrenCountOfThisMarriageGroup = 0;

            for(var ndIndex in tmpLevelList[mgroupIndex]){
              if(tmpLevelList[mgroupIndex][ndIndex].leafDescendantCount>childrenCountOfThisMarriageGroup){
                childrenCountOfThisMarriageGroup = tmpLevelList[mgroupIndex][ndIndex].leafDescendantCount;
              }
            }
  // now we got the children count of the current marriage group

            for(var ndIndex in tmpLevelList[mgroupIndex]){
              var parentgroupindex = parentIndexMap[tmpLevelList[mgroupIndex][ndIndex].id]==null?0:parentIndexMap[tmpLevelList[mgroupIndex][ndIndex].id]-1;
              var parentBase = previousLevel[parentgroupindex][0].column - parseInt(maxChildCountOfMarriageGroup(previousLevel[parentgroupindex])/2);

              tmpLevelList[mgroupIndex][ndIndex].column = Math.max(parentBase,Math.max(accumuWidth+col+parseInt(childrenCountOfThisMarriageGroup/2),previousColumn+1));
              previousColumn = tmpLevelList[mgroupIndex][ndIndex].column
              if(tmpLevelList[mgroupIndex][ndIndex].column>maxWidth){
                maxWidth = tmpLevelList[mgroupIndex][ndIndex].column;
              }
              col+=1;
            }

            accumuWidth = previousColumn-parseInt(tmpLevelList[mgroupIndex].length/2)+parseInt(childrenCountOfThisMarriageGroup/2)

          }

          levelListMap[currentLevel] = tmpLevelList;

          previousLevel = tmpLevelList;
        }
      }

      var result = {};
      var rnodes = [];
      for(var li in levelListMap){
        for(var mgi in levelListMap[li]){
          for(var pi in levelListMap[li][mgi]){
            rnodes.push(levelListMap[li][mgi][pi]);
          }
        }
      }

      result.nodes = rnodes;
      result.links = raw_links;
      result['column-count'] = maxWidth;
      result['level-count'] = Object.keys(levelListMap).length;
      result['svgHeightIndex'] = svgHeightIndex;
      result['heightPosMap'] = heightPosMap;
      return result;
    }
  }
}
