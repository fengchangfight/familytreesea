package com.family.tree.sea.familytreesea.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="ft_snapshot_person")
@Getter
@Setter
@NoArgsConstructor
public class SnapshotPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="snapshot_id", referencedColumnName = "id")
    private Snapshot snapshot;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "name")
    private String name;

    @Column(name = "sex")
    private Integer sex;

    @Column(name = "life_description")
    private String lifeDescription;

    @Column(name = "born")
    private String born;

    @Column(name = "death")
    private String death;

    @Column(name = "profile_img_path")
    private String profileImagePath;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name="zibei")
    private String zibei;

    @Column(name="rank")
    private Integer rank;

    @Column(name = "short_description")
    private String shortDescription;

    public SnapshotPerson(Snapshot snapshot, Long personId, String name, Integer sex, String lifeDescription, String born, String death, String profileImagePath, String address, String phone, String shortDescription, String zibei, Integer rank) {
        this.snapshot = snapshot;
        this.personId = personId;
        this.name = name;
        this.sex = sex;
        this.lifeDescription = lifeDescription;
        this.born = born;
        this.death = death;
        this.profileImagePath = profileImagePath;
        this.address = address;
        this.phone = phone;
        this.shortDescription = shortDescription;
        this.zibei = zibei;
        this.rank = rank;
    }
}
