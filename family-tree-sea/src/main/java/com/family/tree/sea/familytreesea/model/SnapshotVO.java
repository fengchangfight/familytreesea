package com.family.tree.sea.familytreesea.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SnapshotVO {
    private Long id;

    private String name;

    private String createdTime;

    private String originFtName;

    public SnapshotVO(Long id, String name, String createdTime, String originFtName) {
        this.id = id;
        this.name = name;
        this.createdTime = createdTime;
        this.originFtName = originFtName;
    }
}
