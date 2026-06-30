package com.pucetec.students.repositories

import com.pucetec.students.entities.Enrollment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface EnrollmentRepository : JpaRepository<Enrollment, Long> {
    fun findByStudentIdAndSubjectId(studentId: Long, subjectId: Long): Optional<Enrollment>
    fun findByStudentId(studentId: Long): List<Enrollment>
    fun findBySubjectId(subjectId: Long): List<Enrollment>
}
