package com.pucetec.students.entities

import jakarta.persistence.*

@Entity
@Table(name = "enrollments", uniqueConstraints = [UniqueConstraint(columnNames = ["student_id", "subject_id"])])
data class Enrollment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(name = "student_id", nullable = false)
    val studentId: Long,
    
    @Column(name = "subject_id", nullable = false)
    val subjectId: Long,
    
    @Column(nullable = false)
    val grade: Double? = null,
    
    @Column(name = "enrollment_date", nullable = false)
    val enrollmentDate: Long = System.currentTimeMillis()
)
