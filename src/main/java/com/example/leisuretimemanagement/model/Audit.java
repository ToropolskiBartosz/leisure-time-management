package com.example.leisuretimemanagement.model;

import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Embeddable
class Audit {
    private LocalDateTime createOn;
    private LocalDateTime updateOn;

    @PrePersist
    void prePersist(){
        /*
        This run when add new object to the db
         */
        createOn = LocalDateTime.now();
    }

    @PreUpdate
    void preMerge(){
        /*
        This run when update new object to the db
         */
        updateOn = LocalDateTime.now();
    }

}
