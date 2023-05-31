package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Rights {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
