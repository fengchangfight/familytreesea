package com.family.tree.sea.familytreesea.model;


import com.family.tree.sea.familytreesea.config.PrivilegeConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FamilyTreeVO {
    private Long id;
    private String name;
    private String accessRight;

    public FamilyTreeVO(Long id, String name) {
        this.id = id;
        this.name = name;
        this.accessRight = PrivilegeConstant.ADMIN;
    }

    public FamilyTreeVO(Long id, String name, String accessRight) {
        this.id = id;
        this.name = name;
        this.accessRight = accessRight;
    }
}
