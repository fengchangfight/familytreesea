package com.family.tree.sea.familytreesea.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonDetailVO {
    private Long id;
    private Long familyTreeId;
    private String name;
    private Integer sex;
    private String born;
    private String death;
    private String lifeDescription;
    private String profileImgPath;
    private String address;
    private String phone;
    private String shortDesc;
    private String zibei;
    private Integer rank;


    public PersonDetailVO(Long id, Long familyTreeId,  String name, Integer sex, String born, String death, String lifeDescription, String profileImgPath, String address, String phone, String shortDesc, String zibei, Integer rank) {
        this.id = id;
        this.familyTreeId = familyTreeId;
        this.name = name;
        this.sex = sex;
        this.born = born;
        this.death = death;
        this.lifeDescription = lifeDescription;
        this.profileImgPath = profileImgPath;
        this.address = address;
        this.phone = phone;
        this.shortDesc = shortDesc;
        this.zibei = zibei;
        this.rank = rank;
    }
}
