package com.pucetec.students.entities

import jakarta.persistence.*

@Entity
@Table(name = "subjects")
data class Subject(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(nullable = false)
    val credits: Int,
    
    @Column(name = "professor_id", nullable = false)
    val professorId: Long
)
