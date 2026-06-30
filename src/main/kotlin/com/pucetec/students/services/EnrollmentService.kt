package com.pucetec.students.services

import com.pucetec.students.dto.EnrollmentRequest
import com.pucetec.students.dto.EnrollmentResponse
import com.pucetec.students.entities.Enrollment
import com.pucetec.students.exceptions.DuplicateEnrollmentException
import com.pucetec.students.exceptions.EnrollmentNotFoundException
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.exceptions.StudentNotEnrolledException
import com.pucetec.students.exceptions.SubjectNotFoundException
import com.pucetec.students.repositories.EnrollmentRepository
import com.pucetec.students.repositories.StudentRepository
import com.pucetec.students.repositories.SubjectRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EnrollmentService(
    private val enrollmentRepository: EnrollmentRepository,
    private val studentRepository: StudentRepository,
    private val subjectRepository: SubjectRepository
) {
    private val logger = LoggerFactory.getLogger(EnrollmentService::class.java)

    fun enrollStudent(request: EnrollmentRequest): EnrollmentResponse {
        logger.info("Enrolling student ${request.studentId} in subject ${request.subjectId}")

        val student = studentRepository.findById(request.studentId)
            .orElseThrow { StudentNotFoundException("Student with id ${request.studentId} not found") }

        val subject = subjectRepository.findById(request.subjectId)
            .orElseThrow { SubjectNotFoundException("Subject with id ${request.subjectId} not found") }

        val existingEnrollment = enrollmentRepository.findByStudentIdAndSubjectId(request.studentId, request.subjectId)
        if (existingEnrollment.isPresent) {
            throw DuplicateEnrollmentException("Student is already enrolled in this subject")
        }

        val enrollmentEntity = Enrollment(
            studentId = request.studentId,
            subjectId = request.subjectId,
            grade = request.grade
        )
        val saved = enrollmentRepository.save(enrollmentEntity)
        return saved.toResponse()
    }

    fun getEnrollmentById(id: Long): EnrollmentResponse {
        logger.info("Getting enrollment with id $id")
        val enrollment = enrollmentRepository.findById(id)
            .orElseThrow { EnrollmentNotFoundException("Enrollment with id $id not found") }
        return enrollment.toResponse()
    }

    fun getStudentEnrollments(studentId: Long): List<EnrollmentResponse> {
        logger.info("Getting enrollments for student $studentId")
        val student = studentRepository.findById(studentId)
            .orElseThrow { StudentNotFoundException("Student with id $studentId not found") }
        return enrollmentRepository.findByStudentId(studentId).map { it.toResponse() }
    }

    fun getSubjectEnrollments(subjectId: Long): List<EnrollmentResponse> {
        logger.info("Getting enrollments for subject $subjectId")
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { SubjectNotFoundException("Subject with id $subjectId not found") }
        return enrollmentRepository.findBySubjectId(subjectId).map { it.toResponse() }
    }

    fun updateEnrollmentGrade(id: Long, grade: Double?): EnrollmentResponse {
        logger.info("Updating grade for enrollment $id")

        val enrollment = enrollmentRepository.findById(id)
            .orElseThrow { EnrollmentNotFoundException("Enrollment with id $id not found") }

        val updated = enrollment.copy(grade = grade)
        val saved = enrollmentRepository.save(updated)
        return saved.toResponse()
    }

    fun removeEnrollment(id: Long): Boolean {
        logger.info("Removing enrollment $id")
        val enrollment = enrollmentRepository.findById(id)
            .orElseThrow { EnrollmentNotFoundException("Enrollment with id $id not found") }
        enrollmentRepository.delete(enrollment)
        return true
    }

    private fun Enrollment.toResponse() = EnrollmentResponse(
        id = this.id,
        studentId = this.studentId,
        subjectId = this.subjectId,
        grade = this.grade,
        enrollmentDate = this.enrollmentDate
    )
}
