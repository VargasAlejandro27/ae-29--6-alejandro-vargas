package com.pucetec.students.services

import com.pucetec.students.dto.SubjectRequest
import com.pucetec.students.entities.Professor
import com.pucetec.students.entities.Subject
import com.pucetec.students.exceptions.BlankSubjectNameException
import com.pucetec.students.exceptions.InvalidCreditsException
import com.pucetec.students.exceptions.ProfessorNotFoundException
import com.pucetec.students.exceptions.SubjectNotFoundException
import com.pucetec.students.repositories.ProfessorRepository
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
class SubjectServiceTest {

    @Mock
    private lateinit var subjectRepository: SubjectRepository

    @Mock
    private lateinit var professorRepository: ProfessorRepository

    @InjectMocks
    private lateinit var subjectService: SubjectService

    @Test
    fun `createSubject retorna respuesta cuando datos son validos`() {

        val request = SubjectRequest(name = "Matematicas", credits = 4, professorId = 1L)
        val professor = Professor(id = 1L, name = "Dr. Lopez", email = "lopez@puce.edu")
        val savedSubject = Subject(id = 1L, name = "Matematicas", credits = 4, professorId = 1L)

        whenever(professorRepository.findById(1L)).thenReturn(Optional.of(professor))
        whenever(subjectRepository.save(any<Subject>())).thenReturn(savedSubject)


        val response = subjectService.createSubject(request)


        assertEquals(1L, response.id)
        assertEquals("Matematicas", response.name)
        assertEquals(4, response.credits)
        assertEquals(1L, response.professorId)
    }

    @Test
    fun `createSubject lanza BlankSubjectNameException cuando nombre es vacio`() {

        val request = SubjectRequest(name = "", credits = 4, professorId = 1L)


        assertThrows<BlankSubjectNameException> {
            subjectService.createSubject(request)
        }
    }

    @Test
    fun `createSubject lanza InvalidCreditsException cuando credits es cero`() {

        val request = SubjectRequest(name = "Matematicas", credits = 0, professorId = 1L)


        assertThrows<InvalidCreditsException> {
            subjectService.createSubject(request)
        }
    }

    @Test
    fun `createSubject lanza InvalidCreditsException cuando credits es negativo`() {

        val request = SubjectRequest(name = "Matematicas", credits = -1, professorId = 1L)


        assertThrows<InvalidCreditsException> {
            subjectService.createSubject(request)
        }
    }

    @Test
    fun `createSubject lanza ProfessorNotFoundException cuando profesor no existe`() {

        val request = SubjectRequest(name = "Matematicas", credits = 4, professorId = 99L)
        whenever(professorRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<ProfessorNotFoundException> {
            subjectService.createSubject(request)
        }
    }

    @Test
    fun `getSubjectById retorna materia cuando existe`() {

        val subject = Subject(id = 1L, name = "Matematicas", credits = 4, professorId = 1L)
        whenever(subjectRepository.findById(1L)).thenReturn(Optional.of(subject))


        val response = subjectService.getSubjectById(1L)


        assertEquals(1L, response.id)
        assertEquals("Matematicas", response.name)
        assertEquals(4, response.credits)
    }

    @Test
    fun `getSubjectById lanza SubjectNotFoundException cuando no existe`() {

        whenever(subjectRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<SubjectNotFoundException> {
            subjectService.getSubjectById(99L)
        }
    }

    @Test
    fun `getAllSubjects retorna lista de materias`() {

        val subject1 = Subject(id = 1L, name = "Matematicas", credits = 4, professorId = 1L)
        val subject2 = Subject(id = 2L, name = "Fisica", credits = 3, professorId = 2L)
        whenever(subjectRepository.findAll()).thenReturn(listOf(subject1, subject2))


        val response = subjectService.getAllSubjects()


        assertEquals(2, response.size)
        assertEquals("Matematicas", response[0].name)
        assertEquals("Fisica", response[1].name)
    }

    @Test
    fun `getSubjectsByProfessor retorna materias del profesor cuando existe`() {

        val professor = Professor(id = 1L, name = "Dr. Lopez", email = "lopez@puce.edu")
        val subject1 = Subject(id = 1L, name = "Matematicas", credits = 4, professorId = 1L)
        val subject2 = Subject(id = 2L, name = "Algebra", credits = 3, professorId = 1L)

        whenever(professorRepository.findById(1L)).thenReturn(Optional.of(professor))
        whenever(subjectRepository.findByProfessorId(1L)).thenReturn(listOf(subject1, subject2))


        val response = subjectService.getSubjectsByProfessor(1L)


        assertEquals(2, response.size)
        assertEquals("Matematicas", response[0].name)
        assertEquals("Algebra", response[1].name)
    }

    @Test
    fun `getSubjectsByProfessor lanza ProfessorNotFoundException cuando profesor no existe`() {

        whenever(professorRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<ProfessorNotFoundException> {
            subjectService.getSubjectsByProfessor(99L)
        }
    }

    @Test
    fun `updateSubject actualiza materia cuando existe y datos son validos`() {

        val request = SubjectRequest(name = "Matematicas Avanzada", credits = 5, professorId = 2L)
        val subject = Subject(id = 1L, name = "Matematicas", credits = 4, professorId = 1L)
        val professor = Professor(id = 2L, name = "Dr. Garcia", email = "garcia@puce.edu")
        val updatedSubject = Subject(id = 1L, name = "Matematicas Avanzada", credits = 5, professorId = 2L)

        whenever(subjectRepository.findById(1L)).thenReturn(Optional.of(subject))
        whenever(professorRepository.findById(2L)).thenReturn(Optional.of(professor))
        whenever(subjectRepository.save(any<Subject>())).thenReturn(updatedSubject)


        val response = subjectService.updateSubject(1L, request)


        assertEquals(1L, response.id)
        assertEquals("Matematicas Avanzada", response.name)
        assertEquals(5, response.credits)
        assertEquals(2L, response.professorId)
    }

    @Test
    fun `updateSubject lanza BlankSubjectNameException cuando nombre es vacio`() {

        val request = SubjectRequest(name = "", credits = 4, professorId = 1L)


        assertThrows<BlankSubjectNameException> {
            subjectService.updateSubject(1L, request)
        }
    }

    @Test
    fun `updateSubject lanza InvalidCreditsException cuando credits es negativo`() {

        val request = SubjectRequest(name = "Matematicas", credits = -1, professorId = 1L)


        assertThrows<InvalidCreditsException> {
            subjectService.updateSubject(1L, request)
        }
    }

    @Test
    fun `updateSubject lanza SubjectNotFoundException cuando materia no existe`() {

        val request = SubjectRequest(name = "Matematicas", credits = 4, professorId = 1L)
        whenever(subjectRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<SubjectNotFoundException> {
            subjectService.updateSubject(99L, request)
        }
    }

    @Test
    fun `updateSubject lanza ProfessorNotFoundException cuando profesor no existe`() {

        val request = SubjectRequest(name = "Matematicas", credits = 4, professorId = 99L)
        val subject = Subject(id = 1L, name = "Matematicas", credits = 4, professorId = 1L)
        whenever(subjectRepository.findById(1L)).thenReturn(Optional.of(subject))
        whenever(professorRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<ProfessorNotFoundException> {
            subjectService.updateSubject(1L, request)
        }
    }

    @Test
    fun `deleteSubject elimina materia cuando existe`() {

        val subject = Subject(id = 1L, name = "Matematicas", credits = 4, professorId = 1L)
        whenever(subjectRepository.findById(1L)).thenReturn(Optional.of(subject))


        val result = subjectService.deleteSubject(1L)


        assertTrue(result)
    }

    @Test
    fun `deleteSubject lanza SubjectNotFoundException cuando no existe`() {

        whenever(subjectRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<SubjectNotFoundException> {
            subjectService.deleteSubject(99L)
        }
    }
}
