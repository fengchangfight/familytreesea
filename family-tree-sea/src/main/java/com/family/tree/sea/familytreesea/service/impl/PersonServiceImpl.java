package com.family.tree.sea.familytreesea.service.impl;

import com.family.tree.sea.familytreesea.entity.Person;
import com.family.tree.sea.familytreesea.model.PersonDetailVO;
import com.family.tree.sea.familytreesea.model.PersonInGraphVO;
import com.family.tree.sea.familytreesea.repository.rdbms.PersonRepository;
import com.family.tree.sea.familytreesea.service.ImageUploadHistoryService;
import com.family.tree.sea.familytreesea.service.PersonService;
import com.family.tree.sea.familytreesea.service.SnapshotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {
    private static final Logger LOG = LoggerFactory.getLogger(PersonServiceImpl.class);

    @Autowired
    private SnapshotService snapshotService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ImageUploadHistoryService imageUploadHistoryService;

    @Override
    public Long save(Person person){
        personRepository.save(person);
        return person.getId();
    }

    @Override
    public void save(List<Person> person){
       personRepository.save(person);
    }

    @Override
    public Person findOne(Long id){
        return personRepository.findOne(id);
    }


    @Override
    public List<PersonInGraphVO> getNodesByFamilyId(Long familyTreeId){
        List<Person> people = personRepository.findByFamilyTreeId(familyTreeId);
        return people.stream().map(r->{return new PersonInGraphVO(r.getId(), r.getName(), r.getSex(), r.getRank());}).collect(Collectors.toList());
    }


    @Override
    public List<PersonDetailVO> findPeopleDetailsByFamilyTreeId(Long familyTreeId){
        List<Person> people = personRepository.findByFamilyTreeId(familyTreeId);
        return people.stream().map(r->{return new PersonDetailVO(r.getId(), r.getFamilyTree().getId(),
                r.getName(), r.getSex(), r.getBorn(), r.getDeath(),
                r.getLifeDescription(), r.getProfileImagePath(), r.getAddress(), r.getPhone(),
                r.getShortDescription(), r.getZibei(), r.getRank());}).collect(Collectors.toList());
    }

    @Override
    public void deleteByPersonId(Long id){
        personRepository.delete(id);
    }

    @Override
    public void deleteByFamilyTreeId(Long id){
        personRepository.deleteByFamilyTreeId(id);
    }

    @Override
    public List<Long> findPersonIdsByFamilyTreeId(Long familyTreeId){
        return personRepository.findPersonIdsByFamilyTreeId(familyTreeId);
    }



}