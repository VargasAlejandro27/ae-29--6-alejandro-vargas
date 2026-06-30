package com.pucetec.students.entities

import jakarta.persistence.*

@Entity
@Table(name = "professors")
data class Professor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(nullable = false, unique = true)
    val email: String,
    
    @Column
    val phone: String? = null
)
