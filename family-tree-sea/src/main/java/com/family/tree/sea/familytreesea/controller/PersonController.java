package com.family.tree.sea.familytreesea.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.family.tree.sea.familytreesea.config.PathConstants;
import com.family.tree.sea.familytreesea.entity.*;
import com.family.tree.sea.familytreesea.model.FamilyResponse;
import com.family.tree.sea.familytreesea.model.PersonDetailVO;
import com.family.tree.sea.familytreesea.service.*;
import com.family.tree.sea.familytreesea.utils.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(PathConstants.PERSON)
public class PersonController {
    private static final Logger LOG = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private ImageUploadHistoryService imageUploadHistoryService;

    @Autowired
    private Environment env;

    @Autowired
    private FamilyTreeService familyTreeService;

    @Autowired
    private UserService userService;

    @Autowired
    private PersonService personService;

    @Autowired
    private RelationshipService relationshipService;

    private boolean doIownFamilyTree(Long familyTreeId){
        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);
        return doIownFamilyTree(familyTree);
    }

    private boolean doIownFamilyTree(FamilyTree familyTree){
        return Util.currentUserName().equals(familyTree.getCreatedBy().getUsername());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public FamilyResponse deletePerson(@PathVariable("id") Long id){
        FamilyResponse ret = new FamilyResponse();

        Person person = personService.findOne(id);
        User user = userService.findByUsername(Util.currentUserName());
        Long familyTreeId = person.getFamilyTree().getId();

        if(!doIownFamilyTree(person.getFamilyTree()) ){
            ret.setOk(false);
            ret.setMessage("无权限删除该人物节点");
            return ret;
        }

        // delete all it's relationships
        relationshipService.deleteRelationByFromPersonId(id);
        relationshipService.deleteRelationByToPersonId(id);
        // delete upload history
        imageUploadHistoryService.deleteByPersonId(id);
        // delete person
        personService.deleteByPersonId(id);


        ret.setOk(true);
        ret.setMessage("删除人物节点成功");
        ret.setData(id);
        return ret;
    }
    @RequestMapping(value = "/content-image", method = RequestMethod.POST)
    public FamilyResponse uploadBodyImage(@RequestParam("personId") Long personId, @RequestParam("img") MultipartFile uploadFile){
        FamilyResponse ret = new FamilyResponse();

        // check privilege to do the actions
        Person person = personService.findOne(personId);
        User user = userService.findByUsername(Util.currentUserName());

        if(!doIownFamilyTree(person.getFamilyTree()) ){
            ret.setOk(false);
            ret.setMessage("无权限为该人物上传图片描述");
            return ret;
        }
        try {
            // write file to static folder
            String location = env.getProperty("data.static.imagefolder");
            String fileName=RandomStringUtils.randomAlphabetic(16)+".jpg";
            File file = new File(location, fileName);
            FileUtils.writeByteArrayToFile(file, uploadFile.getBytes());


            // record image upload history, who upload what file at when
            ImageUploadHistory imageUploadHistory = new ImageUploadHistory();
            imageUploadHistory.setPerson(person);
            String username = currentUserName();
            User currentUser = userService.findByUsername(username);
            imageUploadHistory.setUploadBy(currentUser);
            imageUploadHistory.setKey(fileName);
            imageUploadHistory.setUploadTime(new Date());
            imageUploadHistoryService.save(imageUploadHistory);

            //generate url springboot serves and return
            String baseUrl = env.getProperty("family.service.base");
            ret.setData(baseUrl+"/"+fileName);
            ret.setOk(true);

            return ret;
        } catch (IOException e) {
            LOG.error("===Error uploading file====="+e.getMessage()+"=======");
            ret.setOk(false);
            ret.setData(null);
            return ret;
        }
    }

    private String currentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }else{
            return null;
        }
    }

    @RequestMapping(value = "/profile-upload", method = RequestMethod.POST)
    public FamilyResponse uploadProfile(@RequestParam("personId") String personId, @RequestParam("img") MultipartFile uploadFile){
        FamilyResponse ret = new FamilyResponse();

        String profile = String.join("", env.getActiveProfiles());

        String key =  "ft_profile_"+profile+"_"+personId+"_fixed.jpg";
        Person person = personService.findOne(Long.valueOf(personId));

        if(!doIownFamilyTree(person.getFamilyTree()) ){
            ret.setOk(false);
            ret.setMessage("无权限为该人物上传头像");
            return ret;
        }

        if(person.getProfileImagePath()!=null&&person.getProfileImagePath().length()>0&&key.equals(person.getProfileImagePath())){
            // a person has fixed profile name
            LOG.info("=====Profile path already there for person: "+personId);
        }else{
            person.setProfileImagePath(key);
            personService.save(person);
        }

        // upload file with name renamed to key
        String location = env.getProperty("data.static.imagefolder");
        File file = new File(location, key);
        try {
            FileUtils.writeByteArrayToFile(file, uploadFile.getBytes());
            ret.setOk(true);
            ret.setData(key);
            ret.setMessage("成功上传头像");
            return ret;
        } catch (IOException e) {
            LOG.error("=============="+e.getMessage()+"========");
            return null;
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public FamilyResponse getPersonById(@PathVariable("id") Long id){
        FamilyResponse ret = new FamilyResponse();

        Person person = personService.findOne(id);

        User user = userService.findByUsername(Util.currentUserName());

        Long familyTreeId = person.getFamilyTree().getId();

        if(!person.getFamilyTree().getCreatedBy().getUsername().equals(Util.currentUserName())){
            ret.setOk(false);
            ret.setMessage("无权限查看该人物信息");
            return ret;
        }

        String baseUrl = env.getProperty("family.service.base");

        ret.setOk(true);
        ret.setMessage("加载人物信息成功");
        ret.setData(new PersonDetailVO(person.getId(), person.getFamilyTree().getId(), person.getName(), person.getSex(), person.getBorn(), person.getDeath(),
                person.getLifeDescription(), baseUrl+"/"+person.getProfileImagePath() , person.getAddress(), person.getPhone(), person.getShortDescription(), person.getZibei(), person.getRank()));
        return ret;
    }

    @RequestMapping(value = "/basic-description/{id}",method = RequestMethod.POST)
    public FamilyResponse updateBasic(@RequestBody String params, @PathVariable("id") Long personId){

        FamilyResponse ret = new FamilyResponse();

        JSONObject job = (JSONObject) JSON.parse(params);
        String name = job.getString("name");
        String phone = job.getString("phone");
        String address = job.getString("address");
        String born = job.getString("born");
        String death = job.getString("death");
        String shortDescription = job.getString("short_description");
        String zibei = job.getString("zibei");
        Integer rank = job.getInteger("rank");


        Person person = personService.findOne(personId);
        FamilyTree familyTree = person.getFamilyTree();

        User user = userService.findByUsername(Util.currentUserName());


        if(!doIownFamilyTree(familyTree) ){
            ret.setOk(false);
            ret.setMessage("没有权限更新人物基本信息");
            return ret;
        }

        person.setName(name);
        person.setPhone(phone);
        person.setAddress(address);
        person.setBorn(born);
        person.setDeath(death);
        person.setShortDescription(shortDescription);
        person.setZibei(zibei);
        person.setRank(rank);
        Long saveId = personService.save(person);

        ret.setOk(true);
        ret.setMessage("成功更新人物基本信息");
        ret.setData(saveId);


        return ret;
    }

    @RequestMapping(value = "/life-description/{id}",method = RequestMethod.POST)
    public FamilyResponse updateLifeDescription(@RequestBody String params, @PathVariable("id") Long id){
        FamilyResponse ret = new FamilyResponse();

        JSONObject job = (JSONObject) JSON.parse(params);
        String lifeDescription = job.getString("life_description");
        Person person = personService.findOne(id);

        User user = userService.findByUsername(Util.currentUserName());

        if(!doIownFamilyTree(person.getFamilyTree()) ){
            ret.setOk(false);
            ret.setMessage("没有权限更新人物生平");
            return ret;
        }
        person.setLifeDescription(lifeDescription);
        Long saveId = personService.save(person);

        ret.setOk(true);
        ret.setMessage("成功更新人物生平");
        ret.setData(saveId);

        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    public FamilyResponse createPersonNode(@RequestBody String params){
        FamilyResponse ret = new FamilyResponse();

        JSONObject job = (JSONObject) JSON.parse(params);
        Long familyTreeId = job.getLong("familytreeid");
        Integer sex = job.getInteger("sex");
        String name = job.getString("name");

        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);

        // do I own the family tree
        User user = userService.findByUsername(Util.currentUserName());

        if(doIownFamilyTree(familyTree) ){
            // i am the creator of this tree, do create
            Person person = new Person();
            person.setBorn(null);
            person.setDeath(null);
            person.setFamilyTree(familyTree);
            person.setLifeDescription("");
            person.setProfileImagePath("");
            person.setSex(sex);
            person.setName(name);
            person.setRank(999);

            personService.save(person);


            ret.setOk(true);
            ret.setMessage("成功创建新人物节点");
            ret.setData(person.getId());

            return ret;
        }else{
            ret.setOk(false);
            ret.setMessage("没有权限为该家谱创建新人物节点");
            return ret;
        }
    }
}
