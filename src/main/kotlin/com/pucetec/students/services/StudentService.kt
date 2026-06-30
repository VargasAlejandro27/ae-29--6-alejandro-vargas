package com.pucetec.students.services

import com.pucetec.students.dto.StudentRequest
import com.pucetec.students.dto.StudentResponse
import com.pucetec.students.entities.Student
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.exceptions.InvalidEmailException
import com.pucetec.students.exceptions.StudentAlreadyExistsException
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.repositories.StudentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StudentService(private val studentRepository: StudentRepository) {
    private val logger = LoggerFactory.getLogger(StudentService::class.java)

    fun createStudent(request: StudentRequest): StudentResponse {
        logger.info("Creating student ${request.name}")

        if (request.name.isBlank()) {
            throw BlankNameException("Name cannot be blank")
        }

        if (!request.email.contains("@")) {
            throw InvalidEmailException("Email must be valid")
        }

        val existingStudent = studentRepository.findByEmail(request.email)
        if (existingStudent != null) {
            throw StudentAlreadyExistsException("Student with email ${request.email} already exists")
        }

        val studentEntity = Student(name = request.name, email = request.email)
        val savedStudent = studentRepository.save(studentEntity)
        return savedStudent.toResponse()
    }

    fun getStudentById(id: Long): StudentResponse {
        logger.info("Getting student with id $id")
        val student = studentRepository.findById(id)
            .orElseThrow { StudentNotFoundException("Student with id $id not found") }
        return student.toResponse()
    }

    fun getAllStudents(): List<StudentResponse> {
        logger.info("Getting all students")
        return studentRepository.findAll().map { it.toResponse() }
    }

    fun updateStudent(id: Long, request: StudentRequest): StudentResponse {
        logger.info("Updating student with id $id")

        if (request.name.isBlank()) {
            throw BlankNameException("Name cannot be blank")
        }

        val student = studentRepository.findById(id)
            .orElseThrow { StudentNotFoundException("Student with id $id not found") }

        val existingStudentWithEmail = studentRepository.findByEmail(request.email)
        if (existingStudentWithEmail != null && existingStudentWithEmail.id != id) {
            throw StudentAlreadyExistsException("Email ${request.email} is already used by another student")
        }

        val updatedStudent = student.copy(name = request.name, email = request.email)
        val saved = studentRepository.save(updatedStudent)
        return saved.toResponse()
    }

    fun deleteStudent(id: Long): Boolean {
        logger.info("Deleting student with id $id")
        val student = studentRepository.findById(id)
            .orElseThrow { StudentNotFoundException("Student with id $id not found") }
        studentRepository.delete(student)
        return true
    }

    private fun Student.toResponse() = StudentResponse(
        id = this.id,
        name = this.name,
        email = this.email,
        createdAt = this.createdAt
    )
}
