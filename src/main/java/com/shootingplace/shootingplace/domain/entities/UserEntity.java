package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(generator = "id")
    @GenericGenerator(name = "id", strategy = "org.hibernate.id.UUIDGenerator")
    private String uuid;

    private String name;
    @OrderBy("dayNow DESC, timeNow DESC")
    @ManyToMany
    private List<ChangeHistoryEntity> changeHistoryEntities;

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChangeHistoryEntity> getList() {
        return changeHistoryEntities;
    }

    public void setList(List<ChangeHistoryEntity> changeHistoryEntities) {
        this.changeHistoryEntities = changeHistoryEntities;
    }
}
