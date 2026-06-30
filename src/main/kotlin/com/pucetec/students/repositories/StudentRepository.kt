package com.pucetec.students.repositories

import com.pucetec.students.entities.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : JpaRepository<Student, Long> {
    fun findByEmail(email: String): Student?
}
