package com.family.tree.sea.familytreesea.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ft_snapshot_image_upload")
@Getter
@Setter
@NoArgsConstructor
public class SnapshotImageUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="snapshot_id", referencedColumnName = "id")
    private Snapshot snapshot;

    @ManyToOne
    @JoinColumn(name="upload_by", referencedColumnName = "id")
    private User uploadBy;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "obj_key")
    private String objKey;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="upload_time")
    private Date uploadTime;

    public SnapshotImageUpload(Snapshot snapshot, User uploadBy, Long personId, String objKey, Date uploadTime) {
        this.snapshot = snapshot;
        this.uploadBy = uploadBy;
        this.personId = personId;
        this.objKey = objKey;
        this.uploadTime = uploadTime;
    }
}
