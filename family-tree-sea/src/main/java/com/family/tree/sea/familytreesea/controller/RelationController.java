package com.family.tree.sea.familytreesea.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.family.tree.sea.familytreesea.config.PathConstants;
import com.family.tree.sea.familytreesea.config.PrivilegeConstant;
import com.family.tree.sea.familytreesea.entity.*;
import com.family.tree.sea.familytreesea.model.FamilyResponse;
import com.family.tree.sea.familytreesea.service.*;
import com.family.tree.sea.familytreesea.utils.Util;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.*;

@RestController
@RequestMapping(PathConstants.RELATION)
public class RelationController {

    private static final Logger LOG = LoggerFactory.getLogger(RelationController.class);

    @Autowired
    private ImmutableMap<String, String> relationMap;

    @Autowired
    private UserService userService;


    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private FamilyTreeService familyTreeService;

    @Autowired
    private PersonService personService;

    private Integer isHigher(String id){
        switch (id){
            case "1":
                return 1;
            case "2":
                return 1;
            case "5":
                return -1;
            case "6":
                return -1;
            default:
                return 0;
        }
    }

    private boolean doIownFamilyTree(Long familyTreeId){
        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);
        return doIownFamilyTree(familyTree);
    }

    private boolean doIownFamilyTree(FamilyTree familyTree){
        return Util.currentUserName().equals(familyTree.getCreatedBy().getUsername());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{srcId}/{targetId}")
    @Transactional
    public FamilyResponse deleteRelation(@PathVariable(value = "srcId", required = true) Long srcId, @PathVariable(value = "targetId", required = true) Long targetId){
        FamilyResponse ret = new FamilyResponse();
        ret.setOk(false);
        ret.setMessage("出错");

        Relationship relationship = relationshipService.findRelationByFromPersonAndToPerson(srcId, targetId);

        User user = userService.findByUsername(Util.currentUserName());
        Long familyTreeId = relationship.getFamilyTree().getId();

        if(!doIownFamilyTree(relationship.getFamilyTree()) ){
            ret.setOk(false);
            ret.setMessage("没有权限删除关系");
            return ret;
        }

        relationshipService.deleteRelationByFromPersonIdAndToPersonId(srcId, targetId);
        relationshipService.deleteRelationByFromPersonIdAndToPersonId(targetId, srcId);

        ret.setOk(true);
        ret.setMessage("成功删除关系");

        return ret;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/node")
    @Transactional
    public FamilyResponse createAndBuildNewRelationNode(@RequestBody String params){
        FamilyResponse ret = new FamilyResponse();

        JSONObject job = (JSONObject)JSON.parse(params);
        Long fromPersonId = job.getLong("from_person_id");
        String name = job.getString("new_person_name");
        String relationType = job.getString("relation_type");
        Long familyTreeId = job.getLong("family_tree_id");

        User user = userService.findByUsername(Util.currentUserName());


        if(!doIownFamilyTree(familyTreeId) ){
            ret.setOk(false);
            ret.setMessage("没有权限创建关系人物");
            return ret;
        }

        Set<String> maleType = new HashSet<>();
        maleType.addAll(Arrays.asList("1","3","5"));

        Person fromPerson = personService.findOne(fromPersonId);

        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);

        //create new person
        Person person = new Person();
        person.setName(name);
        person.setSex(maleType.contains(relationType)?1:0);
        person.setFamilyTree(familyTree);
        person.setRank(999);
        personService.save(person);

        // build relationship from fromPersonId to person.id of relationType
        Relationship relationship = new Relationship();
        relationship.setHigher(isHigher(relationType));
        relationship.setType(relationMap.get(relationType));
        relationship.setFamilyTree(familyTree);
        relationship.setFromPerson(person);
        relationship.setToPerson(fromPerson);

        Relationship reverseRelationship = new Relationship();
        reverseRelationship.setHigher(0-isHigher(relationType));
        reverseRelationship.setType(relationMap.get(getReverse(relationType, fromPerson.getSex())));
        reverseRelationship.setFamilyTree(familyTree);
        reverseRelationship.setFromPerson(fromPerson);
        reverseRelationship.setToPerson(person);


        relationshipService.save(relationship);
        relationshipService.save(reverseRelationship);

        ret.setOk(true);
        ret.setMessage("成功创建关系人物");
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Long saveRelation(@RequestBody String params){
        JSONObject job = (JSONObject) JSON.parse(params);
        Long fromId = job.getLong("from_id");
        Long toId = job.getLong("to_id");
        if(fromId.equals(toId)){
            LOG.error("===不能建立自己跟自己的关系===");
            throw new RuntimeException("不能建立自己跟自己的关系");
        }
        Long familyTreeId = job.getLong("family_tree_id");
        String relationId = job.getString("relation_id");

        Relationship relationship = relationshipService.findRelationByFromPersonAndToPerson(fromId, toId);
        Relationship reverseRelationship = relationshipService.findRelationByFromPersonAndToPerson(toId, fromId);
        FamilyTree familyTree = familyTreeService.findFamilyTreeById(familyTreeId);
        Person fromPerson = personService.findOne(fromId);
        Person toPerson = personService.findOne(toId);

        if(relationship==null){
            // create new relation
            relationship = new Relationship();
            relationship.setHigher(isHigher(relationId));
            relationship.setType(relationMap.get(relationId));
            relationship.setFamilyTree(familyTree);
            relationship.setFromPerson(fromPerson);
            relationship.setToPerson(toPerson);
        }else{
            // update existing relation
            relationship.setHigher(isHigher(relationId));
            relationship.setType(relationMap.get(relationId));
        }

        if(reverseRelationship==null){
            // create new reverse relation
            reverseRelationship = new Relationship();
            reverseRelationship.setHigher(0-isHigher(relationId));
            reverseRelationship.setType(relationMap.get(getReverse(relationId, toPerson.getSex())));
            reverseRelationship.setFamilyTree(familyTree);
            reverseRelationship.setFromPerson(toPerson);
            reverseRelationship.setToPerson(fromPerson);
        }else{
            // update existing relation
            reverseRelationship.setHigher(0-isHigher(relationId));
            reverseRelationship.setType(relationMap.get(getReverse(relationId, toPerson.getSex())));
        }

        relationshipService.save(relationship);
        relationshipService.save(reverseRelationship);
        User user = userService.findByUsername(Util.currentUserName());

        return relationship.getId();
    }

    private String getReverse(String relationId, Integer toSex){
        switch (relationId){
            case "1":
                return toSex.equals(1)?"5":"6";
            case "2":
                return toSex.equals(1)?"5":"6";
            case "3":
                return "4";
            case "4":
                return "3";
            case "5":
                return toSex.equals(1)?"1":"2";
            case "6":
                return toSex.equals(1)?"1":"2";
            case "7":
                return "3";
            default:
                return null;
        }
    }

    @RequestMapping(value = "/type/{sex}", method = RequestMethod.GET)
    public List<Map<String,Object>> getAvailableRelationBySex(@PathVariable("sex") Integer sex){
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String,Object> father = new HashMap<>();
        father.put("id", 1);
        father.put("name", relationMap.get("1"));

        Map<String,Object> mother = new HashMap<>();
        mother.put("id", 2);
        mother.put("name", relationMap.get("2"));

        Map<String,Object> son = new HashMap<>();
        son.put("id", 5);
        son.put("name", relationMap.get("5"));

        Map<String,Object> daughter = new HashMap<>();
        daughter.put("id", 6);
        daughter.put("name", relationMap.get("6"));

        result.add(father);
        result.add(mother);
        result.add(son);
        result.add(daughter);
        if(sex==1){
            Map<String,Object> wife = new HashMap<>();
            wife.put("id", 4);
            wife.put("name", relationMap.get("4"));

            //Map<String,Object> concubine = new HashMap<>();
            //concubine.put("id", 7);
            //concubine.put("name", relationMap.get("7"));

            result.add(wife);
            //result.add(concubine);
        }else{
            Map<String,Object> husband = new HashMap<>();
            husband.put("id", 3);
            husband.put("name", relationMap.get("3"));

            result.add(husband);
        }
        return result;
    }


    @RequestMapping(value = "/type/{fromsex}/{tosex}", method = RequestMethod.GET)
    public List<Map<String, Object>> getRelations(@PathVariable("fromsex") Integer fromsex, @PathVariable("tosex") Integer tosex ){
        List<Map<String, Object>> result = new ArrayList<>();
        if(fromsex.equals(1) && tosex.equals(1)){
            // all men
            Map<String,Object> father = new HashMap<>();
            father.put("id", 1);
            father.put("name", relationMap.get("1"));

            Map<String,Object> son = new HashMap<>();
            son.put("id", 5);
            son.put("name", relationMap.get("5"));

            result.add(father);
            result.add(son);
            return result;
        }else if(fromsex.equals(0) && tosex.equals(1)){
            //woman to man
            Map<String,Object> mother = new HashMap<>();
            mother.put("id", 2);
            mother.put("name", relationMap.get("2"));

            Map<String,Object> wife = new HashMap<>();
            wife.put("id", 4);
            wife.put("name", relationMap.get("4"));

            Map<String,Object> daughter = new HashMap<>();
            daughter.put("id", 6);
            daughter.put("name", relationMap.get("6"));

//            Map<String,Object> concubine = new HashMap<>();
//            concubine.put("id", 7);
//            concubine.put("name", relationMap.get("7"));

            result.add(mother);
            result.add(wife);
            result.add(daughter);
            //result.add(concubine);
            return result;
        }else if(fromsex.equals(1) && tosex.equals(0)){
            // man to woman
            Map<String,Object> father = new HashMap<>();
            father.put("id", 1);
            father.put("name", relationMap.get("1"));

            Map<String,Object> husband = new HashMap<>();
            husband.put("id", 3);
            husband.put("name", relationMap.get("3"));

            Map<String,Object> son = new HashMap<>();
            son.put("id", 5);
            son.put("name", relationMap.get("5"));

            result.add(father);
            result.add(husband);
            result.add(son);
            return result;
        }else{
            // woman to woman
            Map<String,Object> mother = new HashMap<>();
            mother.put("id", 2);
            mother.put("name", relationMap.get("2"));

            Map<String,Object> daughter = new HashMap<>();
            daughter.put("id", 6);
            daughter.put("name", relationMap.get("6"));

            result.add(mother);
            result.add(daughter);
            return result;
        }
    }
}
