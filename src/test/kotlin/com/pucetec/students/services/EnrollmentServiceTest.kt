package com.pucetec.students.services

import com.pucetec.students.dto.EnrollmentRequest
import com.pucetec.students.entities.Enrollment
import com.pucetec.students.entities.Student
import com.pucetec.students.entities.Subject
import com.pucetec.students.exceptions.DuplicateEnrollmentException
import com.pucetec.students.exceptions.EnrollmentNotFoundException
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.exceptions.SubjectNotFoundException
import com.pucetec.students.repositories.EnrollmentRepository
import com.pucetec.students.repositories.StudentRepository
import com.pucetec.students.repositories.SubjectRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class EnrollmentServiceTest {

    @Mock
    private lateinit var enrollmentRepository: EnrollmentRepository

    @Mock
    private lateinit var studentRepository: StudentRepository

    @Mock
    private lateinit var subjectRepository: SubjectRepository

    @InjectMocks
    private lateinit var enrollmentService: EnrollmentService

    @Test
    fun `enrollStudent retorna respuesta cuando estudiante y materia existen`() {

        val request = EnrollmentRequest(studentId = 1L, subjectId = 1L, grade = 9.5)
        val student = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")
        val subject = Subject(id = 1L, name = "Matematicas", credits = 4, professorId = 1L)
        val savedEnrollment = Enrollment(id = 1L, studentId = 1L, subjectId = 1L, grade = 9.5)

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(subjectRepository.findById(1L)).thenReturn(Optional.of(subject))
        whenever(enrollmentRepository.findByStudentIdAndSubjectId(1L, 1L)).thenReturn(Optional.empty())
        whenever(enrollmentRepository.save(any<Enrollment>())).thenReturn(savedEnrollment)


        val response = enrollmentService.enrollStudent(request)


        assertEquals(1L, response.id)
        assertEquals(1L, response.studentId)
        assertEquals(1L, response.subjectId)
        assertEquals(9.5, response.grade)
    }

    @Test
    fun `enrollStudent retorna respuesta sin calificacion cuando grade es null`() {

        val request = EnrollmentRequest(studentId = 1L, subjectId = 1L, grade = null)
        val student = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")
        val subject = Subject(id = 1L, name = "Matematicas", credits = 4, professorId = 1L)
        val savedEnrollment = Enrollment(id = 1L, studentId = 1L, subjectId = 1L, grade = null)

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(subjectRepository.findById(1L)).thenReturn(Optional.of(subject))
        whenever(enrollmentRepository.findByStudentIdAndSubjectId(1L, 1L)).thenReturn(Optional.empty())
        whenever(enrollmentRepository.save(any<Enrollment>())).thenReturn(savedEnrollment)


        val response = enrollmentService.enrollStudent(request)


        assertEquals(1L, response.id)
        assertEquals(1L, response.studentId)
        assertEquals(1L, response.subjectId)
    }

    @Test
    fun `enrollStudent lanza StudentNotFoundException cuando estudiante no existe`() {

        val request = EnrollmentRequest(studentId = 99L, subjectId = 1L)
        whenever(studentRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<StudentNotFoundException> {
            enrollmentService.enrollStudent(request)
        }
    }

    @Test
    fun `enrollStudent lanza SubjectNotFoundException cuando materia no existe`() {

        val request = EnrollmentRequest(studentId = 1L, subjectId = 99L)
        val student = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")
        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(subjectRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<SubjectNotFoundException> {
            enrollmentService.enrollStudent(request)
        }
    }

    @Test
    fun `enrollStudent lanza DuplicateEnrollmentException cuando ya esta matriculado`() {

        val request = EnrollmentRequest(studentId = 1L, subjectId = 1L)
        val student = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")
        val subject = Subject(id = 1L, name = "Matematicas", credits = 4, professorId = 1L)
        val existingEnrollment = Enrollment(id = 1L, studentId = 1L, subjectId = 1L)

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(subjectRepository.findById(1L)).thenReturn(Optional.of(subject))
        whenever(enrollmentRepository.findByStudentIdAndSubjectId(1L, 1L)).thenReturn(Optional.of(existingEnrollment))


        assertThrows<DuplicateEnrollmentException> {
            enrollmentService.enrollStudent(request)
        }
    }

    @Test
    fun `getEnrollmentById retorna matricula cuando existe`() {

        val enrollment = Enrollment(id = 1L, studentId = 1L, subjectId = 1L, grade = 9.5)
        whenever(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment))


        val response = enrollmentService.getEnrollmentById(1L)


        assertEquals(1L, response.id)
        assertEquals(1L, response.studentId)
        assertEquals(1L, response.subjectId)
        assertEquals(9.5, response.grade)
    }

    @Test
    fun `getEnrollmentById lanza EnrollmentNotFoundException cuando no existe`() {

        whenever(enrollmentRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<EnrollmentNotFoundException> {
            enrollmentService.getEnrollmentById(99L)
        }
    }

    @Test
    fun `getStudentEnrollments retorna matriculas del estudiante cuando existe`() {

        val student = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")
        val enrollment1 = Enrollment(id = 1L, studentId = 1L, subjectId = 1L, grade = 9.5)
        val enrollment2 = Enrollment(id = 2L, studentId = 1L, subjectId = 2L, grade = 8.5)

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(enrollmentRepository.findByStudentId(1L)).thenReturn(listOf(enrollment1, enrollment2))


        val response = enrollmentService.getStudentEnrollments(1L)


        assertEquals(2, response.size)
        assertEquals(1L, response[0].studentId)
        assertEquals(1L, response[1].studentId)
    }

    @Test
    fun `getStudentEnrollments lanza StudentNotFoundException cuando estudiante no existe`() {

        whenever(studentRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<StudentNotFoundException> {
            enrollmentService.getStudentEnrollments(99L)
        }
    }

    @Test
    fun `getSubjectEnrollments retorna matriculas de la materia cuando existe`() {

        val subject = Subject(id = 1L, name = "Matematicas", credits = 4, professorId = 1L)
        val enrollment1 = Enrollment(id = 1L, studentId = 1L, subjectId = 1L, grade = 9.5)
        val enrollment2 = Enrollment(id = 2L, studentId = 2L, subjectId = 1L, grade = 8.5)

        whenever(subjectRepository.findById(1L)).thenReturn(Optional.of(subject))
        whenever(enrollmentRepository.findBySubjectId(1L)).thenReturn(listOf(enrollment1, enrollment2))


        val response = enrollmentService.getSubjectEnrollments(1L)


        assertEquals(2, response.size)
        assertEquals(1L, response[0].subjectId)
        assertEquals(1L, response[1].subjectId)
    }

    @Test
    fun `getSubjectEnrollments lanza SubjectNotFoundException cuando materia no existe`() {

        whenever(subjectRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<SubjectNotFoundException> {
            enrollmentService.getSubjectEnrollments(99L)
        }
    }

    @Test
    fun `updateEnrollmentGrade actualiza calificacion cuando existe`() {

        val enrollment = Enrollment(id = 1L, studentId = 1L, subjectId = 1L, grade = 9.5)
        val updatedEnrollment = Enrollment(id = 1L, studentId = 1L, subjectId = 1L, grade = 9.8)

        whenever(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment))
        whenever(enrollmentRepository.save(any<Enrollment>())).thenReturn(updatedEnrollment)


        val response = enrollmentService.updateEnrollmentGrade(1L, 9.8)


        assertEquals(1L, response.id)
        assertEquals(9.8, response.grade)
    }

    @Test
    fun `updateEnrollmentGrade actualiza calificacion a null cuando se proporciona null`() {

        val enrollment = Enrollment(id = 1L, studentId = 1L, subjectId = 1L, grade = 9.5)
        val updatedEnrollment = Enrollment(id = 1L, studentId = 1L, subjectId = 1L, grade = null)

        whenever(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment))
        whenever(enrollmentRepository.save(any<Enrollment>())).thenReturn(updatedEnrollment)


        val response = enrollmentService.updateEnrollmentGrade(1L, null)


        assertEquals(1L, response.id)
    }

    @Test
    fun `updateEnrollmentGrade lanza EnrollmentNotFoundException cuando no existe`() {

        whenever(enrollmentRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<EnrollmentNotFoundException> {
            enrollmentService.updateEnrollmentGrade(99L, 9.5)
        }
    }

    @Test
    fun `removeEnrollment elimina matricula cuando existe`() {

        val enrollment = Enrollment(id = 1L, studentId = 1L, subjectId = 1L)
        whenever(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment))


        val result = enrollmentService.removeEnrollment(1L)


        assertTrue(result)
    }

    @Test
    fun `removeEnrollment lanza EnrollmentNotFoundException cuando no existe`() {

        whenever(enrollmentRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<EnrollmentNotFoundException> {
            enrollmentService.removeEnrollment(99L)
        }
    }
}
