package com.family.tree.sea.familytreesea.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.family.tree.sea.familytreesea.config.DeleteResourceAfterDownload;
import com.family.tree.sea.familytreesea.config.PathConstants;
import com.family.tree.sea.familytreesea.config.PrivilegeConstant;
import com.family.tree.sea.familytreesea.entity.*;
import com.family.tree.sea.familytreesea.model.*;
import com.family.tree.sea.familytreesea.service.*;
import com.family.tree.sea.familytreesea.utils.LicenseUtil;
import com.family.tree.sea.familytreesea.utils.Util;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;



@EqualsAndHashCode
@RestController
@RequestMapping(PathConstants.FAMILYTREE)
public class FamilyTreeController {
    private static final Logger LOG = LoggerFactory.getLogger(FamilyTreeController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private LicenseUtil licenseUtil;

    @Autowired
    private ImageUploadHistoryService imageUploadHistoryService;

    @Autowired
    private Environment env;

    @Autowired
    private FamilyTreeService familyTreeService;

    @Autowired
    private PersonService personService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private SnapshotService snapshotService;


    private String filepart(String fullpath){
        // sample: /tmp/some/folder/tmp.txt  -> tmp.txt
        File f = new File(fullpath);
        return f.getName();
    }

    @DeleteResourceAfterDownload
    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public ResponseEntity<?> download(@RequestParam("path") String fullPath) throws Exception{
        File zfile = new File(fullPath);
        Path path = Paths.get(zfile.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+new String(filepart(fullPath).getBytes("UTF-8"),"ISO-8859-1") );
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(zfile.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }


    @RequestMapping(value = "/excel", method = RequestMethod.POST)
    public FamilyResponse generateExcel(@RequestBody String params){
        FamilyResponse ret = new FamilyResponse();
        ret.setOk(false);
        ret.setMessage("Error exporting data");

        JSONObject job = (JSONObject)JSON.parse(params);
        Long familyTreeId = job.getLong("family_tree_id");

        User user = userService.findByUsername(Util.currentUserName());
        if(!doIownFamilyTree(familyTreeId) ){
            ret.setOk(false);
            ret.setMessage("没有权限导出该家谱的excel数据");
            return ret;
        }

        String basePath = env.getProperty("excel.tmpdir");
        String savePath = basePath+"/"+Util.currentUserName();
        File directory = new File(savePath);
        if(!directory.exists()){
            directory.mkdirs();
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheetPerson = workbook.createSheet("人物");
        XSSFSheet sheetRelation = workbook.createSheet("关系");
        String FILE_NAME = savePath+"/家谱导出.xlsx";

        List<PersonDetailVO> personDetailVOS =  personService.findPeopleDetailsByFamilyTreeId(familyTreeId);
        // write personDetailVOS to sheet 1(change sex to string)

        // create header
        Row row = sheetPerson.createRow(0);
        // write header
        String[] personHeader = {"姓名", "性别", "人物生平", "生年", "卒年","排行", "字辈",  "百字简介",  "地址", "电话"};
        int colNum = 0;
        for(String h: personHeader){
            Cell cell = row.createCell(colNum++);
            cell.setCellValue(h);
        }

        int rowNum = 1;
        for (PersonDetailVO personDetail : personDetailVOS) {
            row = sheetPerson.createRow(rowNum++);

            Cell cellName = row.createCell(0);
            Cell cellSex = row.createCell(1);
            Cell cellLifeDescription = row.createCell(2);
            Cell cellBorn = row.createCell(3);
            Cell cellDeath = row.createCell(4);
            Cell cellRank = row.createCell(5);
            Cell cellZibei = row.createCell(6);
            Cell cellShortDescription = row.createCell(7);
            Cell cellAddress = row.createCell(8);
            Cell cellPhone = row.createCell(9);

            cellName.setCellValue(personDetail.getName());
            cellSex.setCellValue(personDetail.getSex()==1?"男":"女");
            cellLifeDescription.setCellValue(personDetail.getLifeDescription());
            cellBorn.setCellValue(personDetail.getBorn());
            cellDeath.setCellValue(personDetail.getDeath());
            cellRank.setCellValue(personDetail.getRank());
            cellZibei.setCellValue(personDetail.getZibei());
            cellShortDescription.setCellValue(personDetail.getShortDesc());
            cellAddress.setCellValue(personDetail.getAddress());
            cellPhone.setCellValue(personDetail.getPhone());
        }

        List<RelationExcelVO> relationExcelVOS = relationshipService.findRelationExcelByFamilyTreeId(familyTreeId);
        //write relationExcelVOS to the second sheet
        // create header
        row = sheetRelation.createRow(0);
        // write header
        String[] relationHeader = {"姓名1", "姓名2", "关系"};
        colNum = 0;
        for(String h: relationHeader){
            Cell cell = row.createCell(colNum++);
            cell.setCellValue(h);
        }

        rowNum = 1;
        for (RelationExcelVO relationExcelVO : relationExcelVOS) {
            row = sheetRelation.createRow(rowNum++);

            Cell cellName1 = row.createCell(0);
            Cell cellName2 = row.createCell(1);
            Cell cellType = row.createCell(2);

            cellName1.setCellValue(relationExcelVO.getFromPersonName());
            cellName2.setCellValue(relationExcelVO.getToPersonName());
            cellType.setCellValue(relationExcelVO.getType());
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ret.setOk(true);
        ret.setMessage("导出 "+FILE_NAME+" 成功!");
        ret.setData(FILE_NAME);

        return ret;
    }

    @RequestMapping(value = "/snapshot/{id}", method = RequestMethod.DELETE)
    @Transactional
    public FamilyResponse deleteSnapshot(@PathVariable("id") Long id){
        FamilyResponse ret = new FamilyResponse();
        // only the one who has privilege can delete the snapshot
        Snapshot snapshot = snapshotService.findOne(id);
        if(!snapshot.getCreatedBy().getUsername().equals(Util.currentUserName())){
            ret.setOk(false);
            ret.setMessage("Do not have the privilege to delete the snapshot");
            return ret;
        }
        // delete data in 4 tables: ft_snapshot_relation, ft_snapshot_person, ft_snapshot_image_upload, ft_snapshot
        snapshotService.deleteSnapshotRelationById(id);
        snapshotService.deleteSnapshotImageUploadById(id);
        snapshotService.deleteSnapshotPersonById(id);

        snapshotService.deleteById(id);

        ret.setOk(true);
        ret.setMessage("Succeed deleting snapshot "+id);
        ret.setData(id);

        return ret;
    }


    @RequestMapping(value = "/snapshot", method = RequestMethod.GET)
    public FamilyResponse getSnapshots(){
        FamilyResponse ret = new FamilyResponse();

        String username = Util.currentUserName();
        User user = userService.findByUsername(username);
        List<Snapshot> snapshots = snapshotService.findByUser(user);
        List<SnapshotVO> snapshotVOS = snapshots.stream().map(r->{return new SnapshotVO(r.getId(), r.getName(), r.getCreatedTime().toString(), r.getOriginFtName());}).collect(toList());
        ret.setOk(true);
        ret.setData(snapshotVOS);
        ret.setMessage("Succeed loading my snapshots");

        return ret;
    }

    @RequestMapping(value = "/snapshot", method = RequestMethod.POST)
    @Transactional
    public FamilyResponse takeSnapshot(@RequestBody String params){
        FamilyResponse ret = new FamilyResponse();

        JSONObject job = (JSONObject) JSON.parse(params);
        Long familyTreeId = job.getLong("family_tree_id");
        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);
        String snapshotName  = job.getString("snapshot_name");

        // check if I am the owner of the current family tree, only the owner can take
        if(!doIownFamilyTree(familyTree)){
            ret.setOk(false);
            ret.setMessage("只有家谱创建者才有权限打快照");
            return ret;
        }

        //check how many snapshots exists for the requested family tree id;
        int count = snapshotService.countSnapshots4FamilyTree(familyTreeId);
        if(count>=3){
            ret.setOk(false);
            ret.setMessage("每个家谱最多只能创建3个快照");
            return ret;
        }

        String currentUserName = Util.currentUserName();
        User user = userService.findByUsername(currentUserName);

        // create a snapshot record in ft_snapshot table
        Snapshot snapshot = new Snapshot();
        snapshot.setCreatedBy(user);
        snapshot.setName(snapshotName);
        snapshot.setCreatedTime(new Date());
        snapshot.setOriginFtName(familyTree.getName());
        snapshot.setOriginFtId(familyTreeId);

        if(snapshotService.findByUserAndSnapshotName(user, snapshotName)!=null){
            ret.setMessage("已存在同名快照");
            ret.setOk(false);
            return ret;
        }
        snapshotService.save(snapshot);

        // insert person snapshot into ft_snapshot_person table
        List<PersonDetailVO> personDetailVOS = personService.findPeopleDetailsByFamilyTreeId(familyTreeId);
        List<SnapshotPerson> snapshotPeople = personDetailVOS.stream().map(r->{return new SnapshotPerson(snapshot, r.getId(), r.getName(), r.getSex(), r.getLifeDescription(), r.getBorn(), r.getDeath(), r.getProfileImgPath(), r.getAddress(), r.getPhone(), r.getShortDesc(), r.getZibei(), r.getRank());}).collect(Collectors.toList());
        snapshotService.saveSnapshotPeople(snapshotPeople);
        // insert relation snapshot in  to ft_snapshot_relation table
        List<RelationVO> relationVOS = relationshipService.findRelationsByFamilyTreeId(familyTreeId);
        List<SnapshotRelation> snapshotRelations = relationVOS.stream().map(r->{return new SnapshotRelation(snapshot, r.getSource(), r.getTarget(),  r.getType(), r.getHigher());}).collect(toList());
        snapshotService.saveSnapshotRelation(snapshotRelations);
        // insert image upload snapshot into ft_snapshot_image_upload table
        List<ImageUploadHistory> imageUploadHistories = imageUploadHistoryService.findImageUploadHistoryByFamilyTreeId(familyTreeId);
        List<SnapshotImageUpload> snapshotImageUploads = imageUploadHistories.stream().map(r->{return new SnapshotImageUpload(snapshot, r.getUploadBy(), r.getPerson().getId(), r.getKey(), r.getUploadTime());}).collect(toList());
        snapshotService.saveImageUploadHitory(snapshotImageUploads);

        ret.setOk(true);
        ret.setMessage("成功创建快照");
        return ret;
    }


    @RequestMapping(value = "/familytreepeople/{id}",method = RequestMethod.DELETE)
    @Transactional
    public FamilyResponse deleteFamilyTreePeople(@PathVariable("id") Long familyTreeId){
        FamilyResponse ret = new FamilyResponse();
        // check if the current user has delete privilige on the current id, if not, return error message imediately
        // if I created this family tree or I am the admin of this family tree
        User user = userService.findByUsername(Util.currentUserName());

        if(!doIownFamilyTree(familyTreeId) ){
            ret.setMessage("当前用户无权限清空家谱人物:"+familyTreeId);
            ret.setOk(false);
            return ret;
        }

        List<Long> ids = personService.findPersonIdsByFamilyTreeId(familyTreeId);


        // delete all relations
        relationshipService.deleteRelationByFamilyTreeId(familyTreeId);
        // delete image upload history
        imageUploadHistoryService.deleteByPersonIds(ids);
        // delete all nodes
        personService.deleteByFamilyTreeId(familyTreeId);

        // return ok message
        ret.setOk(true);
        ret.setMessage("清空家谱人物成功");

        return ret;
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    @Transactional
    public FamilyResponse deleteFamilyTree(@PathVariable("id") Long familyTreeId){
        FamilyResponse ret = new FamilyResponse();
        // check if the current user has delete privilige on the current id, if not, return error message imediately
        // if I created this family tree or I am the admin of this family tree
        if(!doIownFamilyTree(familyTreeId)){
            ret.setMessage("当前用户无权限删除家谱:"+familyTreeId);
            ret.setOk(false);
            return ret;
        }

        List<Long> ids = personService.findPersonIdsByFamilyTreeId(familyTreeId);

        // delete all relations
        relationshipService.deleteRelationByFamilyTreeId(familyTreeId);
        // delete image upload history
        imageUploadHistoryService.deleteByPersonIds(ids);
        // delete all nodes
        personService.deleteByFamilyTreeId(familyTreeId);
        // delete family tree itself
        familyTreeService.deleteById(familyTreeId);
        // return ok message
        ret.setOk(true);
        ret.setMessage("删除家谱成功");

        return ret;
    }

    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        Iterator<K> keyIter = keys.iterator();
        Iterator<V> valIter = values.iterator();
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(_i -> keyIter.next(), _i -> valIter.next()));
    }


    @RequestMapping(method = RequestMethod.POST, value = "/snapshot-restore")
    @Transactional
    public FamilyResponse restoreSnapshot4FamilyTree(@RequestBody String params){
        FamilyResponse ret = new FamilyResponse();

        JSONObject job = (JSONObject)JSON.parse(params);
        Long snapshotId = job.getLong("snapshot_id");
        Long familyTreeId = job.getLong("family_tree_id");

        // only if i am the owner of the family tree can I do this
        User user = userService.findByUsername(Util.currentUserName());
        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);
        if(!doIownFamilyTree(familyTree, user)){
            ret.setOk(false);
            ret.setMessage("只有家谱的创建者才能从快照恢复之");
            return ret;
        }

        // step 1:  delete all current family tree data if any, including person, relation, image upload history
        List<Long> ids = personService.findPersonIdsByFamilyTreeId(familyTreeId);

        relationshipService.deleteRelationByFamilyTreeId(familyTreeId);
        imageUploadHistoryService.deleteByPersonIds(ids);
        personService.deleteByFamilyTreeId(familyTreeId);
        // step 2: restore family tree info from snapshot taken
        // restore person first, and save new old id and new id in a map<Long,Long>
        List<SnapshotPerson> snapshotPeople = snapshotService.findPersonBySnapshotId(snapshotId);
        List<Long> oldPersonIds = snapshotPeople.stream().map(r->{return r.getPersonId();}).collect(toList());
        List<Person> personList = snapshotPeople.stream().map(r->{return new Person(r.getName(), r.getSex(), familyTree, r.getLifeDescription(), r.getShortDescription(), r.getBorn(), r.getDeath(), r.getProfileImagePath(), r.getAddress(), r.getPhone(), r.getZibei(), r.getRank());}).collect(toList());
        personService.save(personList);
        List<Long> newPersonIds = personList.stream().map(r->{return r.getId();}).collect(toList());
        Map<Long,Long> personIdMap = zipToMap(oldPersonIds, newPersonIds);

        // restore relationship from snapshot_relation table, remapp the personid part
        List<SnapshotRelation> snapshotRelations = snapshotService.findRelationsBySnapshotId(snapshotId);
        List<Relationship> relationships = snapshotRelations.stream().map(r->{return new Relationship(familyTree, personService.findOne(personIdMap.get(r.getFromPersonId())), personService.findOne(personIdMap.get(r.getToPersonId())), r.getType(), r.getHigher());}).collect(toList());
        relationshipService.save(relationships);
        // restore image upload history from snapshot_image_upload_history, remap the personid part
        List<SnapshotImageUpload> snapshotImageUploads = snapshotService.findImageUploadsBySnapshotId(snapshotId);
        List<ImageUploadHistory> imageUploadHistories = snapshotImageUploads.stream().map(r->{return new ImageUploadHistory(r.getUploadBy(), personService.findOne(personIdMap.get(r.getPersonId())), r.getObjKey(), r.getUploadTime());}).collect(toList());
        imageUploadHistoryService.save(imageUploadHistories);

        ret.setOk(true);
        ret.setMessage("Snapshot restore succeed");
        ret.setData(snapshotId);

        return ret;

    }

    private boolean doIownFamilyTree(Long familyTreeId){
        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);
        return doIownFamilyTree(familyTree);
    }

    private boolean doIownFamilyTree(FamilyTree familyTree){
        return Util.currentUserName().equals(familyTree.getCreatedBy().getUsername());
    }

    private boolean doIownFamilyTree(FamilyTree familyTree, User user){
        return user.getUsername().equals(familyTree.getCreatedBy().getUsername());
    }


    @RequestMapping(method = RequestMethod.PUT)
    public FamilyResponse updateFamilyTree(@RequestBody Map<String, String> params){
        FamilyResponse ret = new FamilyResponse();

        Long familyTreeId = Long.valueOf(params.get("family_tree_id"));
        String name = params.get("name");
        String content = params.get("content");
        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);

        User user = userService.findByUsername(Util.currentUserName());
        if(!doIownFamilyTree(familyTree) ){
            ret.setOk(false);
            ret.setMessage("当前用户无权限修改家谱");
            return ret;
        }
        familyTree.setName(name);
        familyTree.setDescription(content);
        familyTreeService.save(familyTree);

        ret.setOk(true);
        ret.setMessage("Update family tree successfully");
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    public FamilyResponse createFamilyTree(@RequestParam Map<String, String> params ){
        FamilyResponse ret = new FamilyResponse();

        String name = params.get("name");
        String description = params.get("description");

        User user = userService.findByUsername(Util.currentUserName());

        List<FamilyTree> existing = familyTreeService.findByUserAndFamilyTreeName(user, name);
        if(existing!=null && existing.size()>0){
            ret.setMessage("已存在同名家谱");
            ret.setOk(false);

            return ret;
        }

        Date dt = new Date();

        FamilyTree familyTree = new FamilyTree();
        familyTree.setCreatedBy(user);
        familyTree.setCreatedOn(dt);
        familyTree.setDescription(description);
        familyTree.setName(name);

        if(familyTreeService.countFamilyTreeCreatedByUser(user)<user.getMaxFamilyCountFromLicenseCode(licenseUtil, user.getLicenseCode())){
            familyTreeService.save(familyTree);
            ret.setOk(true);
            ret.setMessage("Succeed creating family tree with name "+name);
            ret.setData(familyTree.getId());
            return ret;
        }else{
            ret.setOk(false);
            ret.setMessage("Reach the maximum number of trees you can create");
            return ret;
        }
    }



    @RequestMapping(value = "/my-family-trees", method = RequestMethod.GET)
    public FamilyResponse getMyFamilyTrees(){

        Map<String,String> translator = new HashMap<>();
        translator.put(PrivilegeConstant.CREATOR,"我创建的：");
        translator.put(PrivilegeConstant.VIEWER,"我围观的：");
        translator.put(PrivilegeConstant.ADMIN,"我管理的：");

        FamilyResponse ret = new FamilyResponse();

        User user = userService.findByUsername(Util.currentUserName());

        List<FamilyTree> createdFamilyTrees = familyTreeService.findFamilyTreesByUser(user);
        Map<String,List<FamilyTree>> privilegedFamilyTreeMap = new HashMap<>();
        privilegedFamilyTreeMap.put(PrivilegeConstant.CREATOR, createdFamilyTrees);

        List<FamilyVOHolder> data = new ArrayList<>();


        List<String> prvList = Arrays.asList(PrivilegeConstant.CREATOR, PrivilegeConstant.ADMIN, PrivilegeConstant.VIEWER);
        for(String key: prvList){
            if(privilegedFamilyTreeMap.get(key)==null){
                continue;
            }
            FamilyVOHolder familyVOHolder = new FamilyVOHolder();
            familyVOHolder.setGroupLabel( translator.get(key));
            familyVOHolder.setGroupData( privilegedFamilyTreeMap.get(key).stream().map(r->{return new FamilyTreeVO(r.getId(), r.getName(), key); }).collect(Collectors.toList()));
            data.add(familyVOHolder);
        }

        ret.setOk(true);
        ret.setMessage("成功加载我的家谱列表(分组)");
        ret.setData(data);
        return ret;
    }

    @RequestMapping(value = "/family-tree-info/{id}", method = RequestMethod.GET)
    public FamilyResponse getFamilyTreeInfo(@PathVariable("id") Long familyTreeId){
        FamilyResponse ret = new FamilyResponse();

        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);
        if(!doIownFamilyTree(familyTree)){
            ret.setOk(false);
            ret.setMessage("无权限查看此家谱信息");
            return ret;
        }

        FamilyTreeInfoVO familyTreeInfoVO = new FamilyTreeInfoVO(familyTreeId, familyTree.getName(), familyTree.getDescription(), StringUtils.isNotEmpty(familyTree.getCreatedBy().getNickname())?familyTree.getCreatedBy().getNickname():familyTree.getCreatedBy().getUsername());

        ret.setOk(true);
        ret.setData(familyTreeInfoVO);
        ret.setMessage("Success getting family tree detail info");

        return ret;
    }


    @RequestMapping(value = "/family-tree/{id}", method = RequestMethod.GET)
    public FamilyResponse loadFamilyTreeById(@PathVariable("id") Long familyTreeId){
        FamilyResponse ret = new FamilyResponse();

        // check if I am the creator of the family tree
        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);
        User user = userService.findByUsername(Util.currentUserName());

        if(!doIownFamilyTree(familyTree)){
            ret.setOk(false);
            ret.setMessage("没有权限载入该家谱树");
            return ret;
        }

        Map<String, Object> result = familyTreeService.loadFtData(familyTreeId);

        ret.setOk(true);
        ret.setMessage("成功载入家谱树图");
        ret.setData(result);
        return ret;
    }



    @RequestMapping(value = "/family-tree/{id}/{personId}", method = RequestMethod.GET)
    public FamilyResponse loadPartialFamilyTreeByFamilyTreeIdAndPersonId(@PathVariable("id") Long familyTreeId, @PathVariable("personId") Long personId){
        FamilyResponse ret = new FamilyResponse();

        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);
        User user = userService.findByUsername(Util.currentUserName());
        if(!doIownFamilyTree(familyTree)){
            ret.setOk(false);
            ret.setMessage("无权限查看该家谱人物");
            return ret;
        }

        Map<String, Object> result = familyTreeService.loadFtDataPartial(familyTreeId, personId);


        ret.setOk(true);
        ret.setMessage("局部关系载入成功");
        ret.setData(result);

        return ret;
    }
}
