package com.family.tree.sea.familytreesea.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ft_snapshot")
@Getter
@Setter
@NoArgsConstructor
public class Snapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "origin_ft_name")
    private String originFtName;

    @Column(name = "origin_ft_id")
    private Long originFtId;

    @ManyToOne
    @JoinColumn(name="created_by", referencedColumnName = "id")
    private User createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_time")
    private Date createdTime;
}
