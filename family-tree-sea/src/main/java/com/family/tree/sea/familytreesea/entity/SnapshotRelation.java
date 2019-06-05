package com.family.tree.sea.familytreesea.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="ft_snapshot_relation")
@Getter
@Setter
@NoArgsConstructor
public class SnapshotRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="snapshot_id", referencedColumnName = "id")
    private Snapshot snapshot;

    @Column(name = "from_person_id")
    private Long fromPersonId;

    @Column(name = "to_person_id")
    private Long toPersonId;

    @Column(name = "type")
    private String type;

    @Column(name = "higher")
    private Integer higher;

    public SnapshotRelation(Snapshot snapshot, Long fromPersonId, Long toPersonId, String type, Integer higher) {
        this.snapshot = snapshot;
        this.fromPersonId = fromPersonId;
        this.toPersonId = toPersonId;
        this.type = type;
        this.higher = higher;
    }
}
