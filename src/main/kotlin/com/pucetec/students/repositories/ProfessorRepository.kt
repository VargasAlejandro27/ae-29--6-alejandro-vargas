package com.pucetec.students.repositories

import com.pucetec.students.entities.Professor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfessorRepository : JpaRepository<Professor, Long> {
    fun findByEmail(email: String): Professor?
}
