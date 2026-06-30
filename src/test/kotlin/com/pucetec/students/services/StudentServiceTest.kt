package com.pucetec.students.services

import com.pucetec.students.dto.StudentRequest
import com.pucetec.students.entities.Student
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.exceptions.InvalidEmailException
import com.pucetec.students.exceptions.StudentAlreadyExistsException
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.repositories.StudentRepository
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
class StudentServiceTest {

    @Mock
    private lateinit var studentRepository: StudentRepository

    @InjectMocks
    private lateinit var studentService: StudentService

    @Test
    fun `createStudent retorna respuesta cuando el nombre es valido`() {
        val request = StudentRequest(name = "Ana Lopez", email = "ana@puce.edu")
        val savedStudent = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")

        whenever(studentRepository.findByEmail("ana@puce.edu")).thenReturn(null)
        whenever(studentRepository.save(any<Student>())).thenReturn(savedStudent)

        val response = studentService.createStudent(request)

        assertEquals(1L, response.id)
        assertEquals("Ana Lopez", response.name)
        assertEquals("ana@puce.edu", response.email)
    }

    @Test
    fun `createStudent lanza BlankNameException cuando el nombre esta vacio`() {
        val request = StudentRequest(name = "", email = "vacio@puce.edu")

        assertThrows<BlankNameException> {
            studentService.createStudent(request)
        }
    }

    @Test
    fun `createStudent lanza InvalidEmailException cuando el email no es valido`() {
        val request = StudentRequest(name = "John Doe", email = "invalidemail")

        assertThrows<InvalidEmailException> {
            studentService.createStudent(request)
        }
    }

    @Test
    fun `createStudent lanza StudentAlreadyExistsException cuando el email ya existe`() {
        val request = StudentRequest(name = "John Doe", email = "existing@puce.edu")
        val existingStudent = Student(id = 2L, name = "Existing", email = "existing@puce.edu")

        whenever(studentRepository.findByEmail("existing@puce.edu")).thenReturn(existingStudent)

        assertThrows<StudentAlreadyExistsException> {
            studentService.createStudent(request)
        }
    }

    @Test
    fun `getStudentById retorna el estudiante cuando existe`() {
        val student = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")
        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))

        val response = studentService.getStudentById(1L)

        assertEquals(1L, response.id)
        assertEquals("Ana Lopez", response.name)
        assertEquals("ana@puce.edu", response.email)
    }

    @Test
    fun `getStudentById lanza StudentNotFoundException cuando no existe`() {
        whenever(studentRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<StudentNotFoundException> {
            studentService.getStudentById(99L)
        }
    }

    @Test
    fun `getAllStudents retorna lista de estudiantes`() {
        val student1 = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")
        val student2 = Student(id = 2L, name = "Juan Perez", email = "juan@puce.edu")
        whenever(studentRepository.findAll()).thenReturn(listOf(student1, student2))

        val response = studentService.getAllStudents()

        assertEquals(2, response.size)
        assertEquals("Ana Lopez", response[0].name)
        assertEquals("Juan Perez", response[1].name)
    }

    @Test
    fun `updateStudent actualiza estudiante cuando existe y nombre es valido`() {
        val request = StudentRequest(name = "Ana Updated", email = "ana.updated@puce.edu")
        val student = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")
        val updatedStudent = Student(id = 1L, name = "Ana Updated", email = "ana.updated@puce.edu")

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(studentRepository.findByEmail("ana.updated@puce.edu")).thenReturn(null)
        whenever(studentRepository.save(any<Student>())).thenReturn(updatedStudent)

        val response = studentService.updateStudent(1L, request)

        assertEquals(1L, response.id)
        assertEquals("Ana Updated", response.name)
        assertEquals("ana.updated@puce.edu", response.email)
    }

    @Test
    fun `updateStudent lanza BlankNameException cuando nombre es vacio`() {
        val request = StudentRequest(name = "", email = "ana@puce.edu")

        assertThrows<BlankNameException> {
            studentService.updateStudent(1L, request)
        }
    }

    @Test
    fun `updateStudent lanza StudentNotFoundException cuando estudiante no existe`() {
        val request = StudentRequest(name = "Ana Updated", email = "ana.updated@puce.edu")
        whenever(studentRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<StudentNotFoundException> {
            studentService.updateStudent(99L, request)
        }
    }

    @Test
    fun `updateStudent lanza StudentAlreadyExistsException cuando email esta usado por otro estudiante`() {
        val request = StudentRequest(name = "Ana Updated", email = "other@puce.edu")
        val student = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")
        val otherStudent = Student(id = 2L, name = "Other", email = "other@puce.edu")

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(studentRepository.findByEmail("other@puce.edu")).thenReturn(otherStudent)

        assertThrows<StudentAlreadyExistsException> {
            studentService.updateStudent(1L, request)
        }
    }

    @Test
    fun `updateStudent permite usar el mismo email si es el mismo estudiante`() {
        val request = StudentRequest(name = "Ana Updated", email = "ana@puce.edu")
        val student = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")
        val updatedStudent = Student(id = 1L, name = "Ana Updated", email = "ana@puce.edu")

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(studentRepository.findByEmail("ana@puce.edu")).thenReturn(student)
        whenever(studentRepository.save(any<Student>())).thenReturn(updatedStudent)

        val response = studentService.updateStudent(1L, request)

        assertEquals("Ana Updated", response.name)
    }

    @Test
    fun `deleteStudent elimina estudiante cuando existe`() {
        val student = Student(id = 1L, name = "Ana Lopez", email = "ana@puce.edu")
        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))

        val result = studentService.deleteStudent(1L)

        assertTrue(result)
    }

    @Test
    fun `deleteStudent lanza StudentNotFoundException cuando no existe`() {
        whenever(studentRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<StudentNotFoundException> {
            studentService.deleteStudent(99L)
        }
    }
}
