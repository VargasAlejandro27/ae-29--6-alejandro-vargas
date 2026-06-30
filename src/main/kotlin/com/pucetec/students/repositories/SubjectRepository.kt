package com.pucetec.students.repositories

import com.pucetec.students.entities.Subject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubjectRepository : JpaRepository<Subject, Long> {
    fun findByProfessorId(professorId: Long): List<Subject>
}
