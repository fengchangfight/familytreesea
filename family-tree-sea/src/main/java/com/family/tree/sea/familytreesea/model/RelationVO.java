package com.family.tree.sea.familytreesea.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RelationVO {
    private Long id;
    private Long source;
    private Long target;
    private String type;
    private Integer higher;

    public RelationVO(Long id, Long source, Long target, String type, Integer higher) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.type = type;
        this.higher = higher;
    }
}
