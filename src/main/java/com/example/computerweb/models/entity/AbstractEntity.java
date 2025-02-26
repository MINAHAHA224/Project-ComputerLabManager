package com.example.computerweb.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@MappedSuperclass
public class AbstractEntity implements Serializable {

    @Column(name = "CREATED_AT")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    @UpdateTimestamp
    private Date updatedAt;


}
