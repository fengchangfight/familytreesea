package com.family.tree.sea.familytreesea.service.impl;

import com.family.tree.sea.familytreesea.entity.FamilyTree;
import com.family.tree.sea.familytreesea.entity.User;
import com.family.tree.sea.familytreesea.model.PersonInGraphVO;
import com.family.tree.sea.familytreesea.model.RelationVO;
import com.family.tree.sea.familytreesea.repository.rdbms.FamilyTreeRepository;
import com.family.tree.sea.familytreesea.service.FamilyTreeService;
import com.family.tree.sea.familytreesea.service.PersonService;
import com.family.tree.sea.familytreesea.service.RelationshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class FamilyTreeServiceImpl implements FamilyTreeService {
    private static final Logger LOG = LoggerFactory.getLogger(FamilyTreeServiceImpl.class);

    @Autowired
    private FamilyTreeRepository familyTreeRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private RelationshipService relationshipService;

    @Override
    public Long countFamilyTreeCreatedByUser(User user){
        return familyTreeRepository.countByCreatedBy(user);
    }

    @Override
    public Long save(FamilyTree familyTree){
        FamilyTree fa = familyTreeRepository.save(familyTree);
        return fa.getId();
    }

    @Override
    public List<FamilyTree> findFamilyTreesByUser(User user){
        return familyTreeRepository.findByCreatedBy(user);
    }

    @Override
    public List<FamilyTree> findFamilyTreeByIds(List<Long> ids){
        return familyTreeRepository.findByIdIn(ids);
    }

    @Override
    public FamilyTree findFamilyTreeById(Long id){
        return familyTreeRepository.findOne(id);
    }

    @Override
    public void deleteById(Long id){
        familyTreeRepository.delete(id);
    }
    private PersonInGraphVO selectStartNode(Set<Long> accessSet, List<PersonInGraphVO> nodes){
        for(PersonInGraphVO p: nodes){
            if(!accessSet.contains(p.getId())){
                p.setLevel(10);
                accessSet.add(p.getId());
                return p;
            }
        }
        return null;
    }
    private boolean isLeafNode(PersonInGraphVO node, Map<String, Integer> relationHigher,Map<Long, List<Long>> neighborMap){
        List<Long> neighbors = neighborMap.get(node.getId());
        if(neighbors==null){
            return true;
        }
        for(Long n: neighbors){
            if(relationHigher.get(node.getId()+"-"+n)==1){
                return false;
            }
        }
        return true;
    }
    private List<PersonInGraphVO> getDirectDecendants(PersonInGraphVO node, Map<String, Integer> relationHigher,Map<Long, List<Long>> neighborMap,  Map<Long, PersonInGraphVO> nodesIdMap){
        List<PersonInGraphVO> result = new ArrayList<>();
        List<Long> neighbors = neighborMap.get(node.getId());
        for(Long n: neighbors){
            if(relationHigher.get(node.getId()+"-"+n)==1){
                PersonInGraphVO p = nodesIdMap.get(n);
                result.add(p);
            }
        }

        return result;
    }


    private int countLeafDescendants(PersonInGraphVO node,  Map<String, Integer> relationHigher,Map<Long, List<Long>> neighborMap, Map<Long, PersonInGraphVO> nodesIdMap,Set<Long> accessSet ){
        if(accessSet.contains(node.getId())){
            return node.getLeafDescendantCount();
        }

        if(isLeafNode(node, relationHigher, neighborMap)){
            node.setLeafDescendantCount(1);
            accessSet.add(node.getId());
            return 1;
        }else{
            // get all it's decendants
            List<PersonInGraphVO> directDecendants = getDirectDecendants(node,  relationHigher, neighborMap,   nodesIdMap);
            node.setDirectDescendantCount(directDecendants.size());
            // add up all it's decendants nodes' des count
            int c = 0;
            for(PersonInGraphVO personInGraphVO: directDecendants){
                c+=countLeafDescendants(personInGraphVO, relationHigher, neighborMap, nodesIdMap, accessSet);
            }
            accessSet.add(node.getId());
            node.setLeafDescendantCount(c);
            return c;
        }
    }

    private void recursiveMark(PersonInGraphVO startNode, final Map<Long, List<Long>> neighborMap, Set<Long> accessSet, final List<PersonInGraphVO> nodes, final Map<String, Integer> relationHigher, final Map<Long, PersonInGraphVO> nodesIdMap, Set<Long> startSet){
        if(startNode==null){
            return;
        }
        startSet.add(startNode.getId());
        List<Long> neighbors = neighborMap.get(startNode.getId());
        if(neighbors==null){
            return;
        }
        for(Long n: neighbors){
            if(accessSet.contains(n)){
                continue;
            }
            String key = startNode.getId()+"-"+n;
            // level数字越大，代表越后代
            if(relationHigher.get(key).equals(1)){
                nodesIdMap.get(n).setLevel(startNode.getLevel()+1);
            }else if(relationHigher.get(key).equals(0)){
                nodesIdMap.get(n).setLevel(startNode.getLevel());
            }else{
                nodesIdMap.get(n).setLevel(startNode.getLevel()-1);
            }
            accessSet.add(n);
        }
        for(Long n: neighbors){
            if(!startSet.contains(n)){
                recursiveMark(nodesIdMap.get(n), neighborMap, accessSet, nodes, relationHigher, nodesIdMap, startSet);
            }
        }
    }
    private void setLeafChildrenCount(List<PersonInGraphVO> nodes, Map<Long, List<Long>> neighborMap, Map<String, Integer> relationHigher, Map<Long, PersonInGraphVO> nodesIdMap){
        Set<Long> accessSet = new HashSet<>();

        for(PersonInGraphVO node: nodes){
            countLeafDescendants(node, relationHigher, neighborMap, nodesIdMap, accessSet);
        }
    }


    private void setNodeLevel(List<PersonInGraphVO> nodes, List<RelationVO> links, Map<Integer, Integer> rowDistanceMap){
        if (nodes==null || nodes.size()==0){
            return;
        }
        Map<Long, PersonInGraphVO> nodesIdMap = new HashMap<>();
        Map<Long, List<Long>> neighborMap = new HashMap<>();
        Map<String, Integer> relationHigher = new HashMap<>();

        for(PersonInGraphVO p: nodes){
            nodesIdMap.put(p.getId(), p);
        }

        //set relationHigher and neighborMap
        for(RelationVO relationVO: links){
            relationHigher.put(relationVO.getSource()+"-"+relationVO.getTarget(),relationVO.getHigher());
            Long key = relationVO.getSource();
            if(neighborMap.containsKey(key)){
                neighborMap.get(key).add(relationVO.getTarget());
            }else{
                List<Long> ll = new ArrayList<>();
                ll.add(relationVO.getTarget());
                neighborMap.put(key, ll);
            }
        }

        // record node access set, empty at start
        Set<Long> accessSet = new HashSet<>();
        Set<Long> startSet = new HashSet<>();
        // start from the first node(or randomly pick one node), set its level to be 100 as default

        // for each node, access all it's neighbor node, mark the neighbors' level(node that has relation with)
        // do this recursively until accessSet is the same size with nodes size
        do{
            PersonInGraphVO startNode = selectStartNode(accessSet, nodes);
            recursiveMark(startNode, neighborMap, accessSet, nodes, relationHigher, nodesIdMap, startSet);
        }while (accessSet.size()!=nodes.size());

        // now vertical(hierachical) level is set, next do the normalization of vertical level, i.e. start from number 1
        int minLevel = nodes.get(0).getLevel();
        int maxLevel = minLevel;
        for(PersonInGraphVO p: nodes){
            if(p.getLevel()<minLevel){
                minLevel = p.getLevel();
            }
            if(p.getLevel()>maxLevel){
                maxLevel = p.getLevel();
            }
        }

        setLeafChildrenCount(nodes, neighborMap, relationHigher, nodesIdMap);


        // this map set normalized level as key, and corresponding node list as value, it will be used later to do the x(column) alignment
        //Map<Integer, List<PersonInGraphVO>> levelListMap = new HashMap<>();
        for(PersonInGraphVO p: nodes){
            // do the normalize
            p.setLevel(p.getLevel()-minLevel+1);
            if(rowDistanceMap.get(p.getLevel())==null || rowDistanceMap.get(p.getLevel())<(p.getDirectDescendantCount()<=1?1:p.getLeafDescendantCount())){
                rowDistanceMap.put(p.getLevel(), (p.getDirectDescendantCount()<=1?1:p.getLeafDescendantCount()));

            }
        }

    }

    List<RelationVO> getLinksAmongNodes(List<PersonInGraphVO> nodes){
        List<Long> personIds = nodes.stream().map(r->{return r.getId();}).collect(toList());
        List<RelationVO> relationships = relationshipService.findRelationAmongNodes(personIds);
        return relationships;
    }

    @Override
    public List<FamilyTree> findByUserAndFamilyTreeName(User user, String familyTreeName){
        return familyTreeRepository.findByCreatedByAndName(user, familyTreeName);
    }

    private void sortNodesByRank(List<PersonInGraphVO> nodes){
        Collections.sort(nodes, new Comparator<PersonInGraphVO>(){
            @Override
            public int compare(PersonInGraphVO o1, PersonInGraphVO o2){
                if(o1.getRank()==null || o2.getRank()==null){
                    return 0;
                }
                if(o1.getRank().equals(o2.getRank())){
                    return 0;
                }
                return o1.getRank() < o2.getRank() ? -1 : 1;
            }
        });
    }

    @Override
    public Map<String,Object> loadFtDataPartial(Long familyTreeId, Long personId){
        Map<String, Object> result = new HashMap<>();
        List<PersonInGraphVO> nodes = relationshipService.getNodesByFamilyIdAndPersonId(familyTreeId, personId);
        List<RelationVO> links = getLinksAmongNodes(nodes);

        Map<Integer, Integer> rowDistanceMap = new HashMap<>();
        setNodeLevel(nodes, links, rowDistanceMap);
        result.put("nextRowDistance", rowDistanceMap);

        sortNodesByRank(nodes);

        result.put("nodes", nodes);
        result.put("links", links);

        return result;
    }

    @Override
    public Map<String,Object> loadFtData(Long familyTreeId){
        Map<String, Object> result = new HashMap<>();
        List<PersonInGraphVO> nodes = personService.getNodesByFamilyId(familyTreeId);

        sortNodesByRank(nodes);

        List<RelationVO> links = relationshipService.findRelationsByFamilyTreeId(familyTreeId);
        Map<Integer, Integer> rowDistanceMap = new HashMap<>();
        setNodeLevel(nodes, links, rowDistanceMap);

        result.put("nodes", nodes);
        result.put("links", links);
        result.put("nextRowDistance", rowDistanceMap);

        return result;
    }
}
