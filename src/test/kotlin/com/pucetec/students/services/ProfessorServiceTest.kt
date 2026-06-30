package com.pucetec.students.services

import com.pucetec.students.dto.ProfessorRequest
import com.pucetec.students.entities.Professor
import com.pucetec.students.exceptions.BlankProfessorNameException
import com.pucetec.students.exceptions.InvalidEmailException
import com.pucetec.students.exceptions.InvalidPhoneException
import com.pucetec.students.exceptions.ProfessorNotFoundException
import com.pucetec.students.repositories.ProfessorRepository
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
class ProfessorServiceTest {

    @Mock
    private lateinit var professorRepository: ProfessorRepository

    @InjectMocks
    private lateinit var professorService: ProfessorService

    @Test
    fun `createProfessor retorna respuesta cuando datos son validos`() {

        val request = ProfessorRequest(name = "Dr. Lopez", email = "lopez@puce.edu", phone = "0987654321")
        val savedProfessor = Professor(id = 1L, name = "Dr. Lopez", email = "lopez@puce.edu", phone = "0987654321")

        whenever(professorRepository.findByEmail("lopez@puce.edu")).thenReturn(null)
        whenever(professorRepository.save(any<Professor>())).thenReturn(savedProfessor)


        val response = professorService.createProfessor(request)


        assertEquals(1L, response.id)
        assertEquals("Dr. Lopez", response.name)
        assertEquals("lopez@puce.edu", response.email)
        assertEquals("0987654321", response.phone)
    }

    @Test
    fun `createProfessor retorna respuesta sin telefono cuando no es proporcionado`() {

        val request = ProfessorRequest(name = "Dr. Lopez", email = "lopez@puce.edu")
        val savedProfessor = Professor(id = 1L, name = "Dr. Lopez", email = "lopez@puce.edu", phone = null)

        whenever(professorRepository.findByEmail("lopez@puce.edu")).thenReturn(null)
        whenever(professorRepository.save(any<Professor>())).thenReturn(savedProfessor)


        val response = professorService.createProfessor(request)


        assertEquals(1L, response.id)
        assertEquals("Dr. Lopez", response.name)
        assertEquals("lopez@puce.edu", response.email)
    }

    @Test
    fun `createProfessor lanza BlankProfessorNameException cuando nombre es vacio`() {

        val request = ProfessorRequest(name = "", email = "lopez@puce.edu")


        assertThrows<BlankProfessorNameException> {
            professorService.createProfessor(request)
        }
    }

    @Test
    fun `createProfessor lanza InvalidEmailException cuando email no es valido`() {

        val request = ProfessorRequest(name = "Dr. Lopez", email = "invalidemail")


        assertThrows<InvalidEmailException> {
            professorService.createProfessor(request)
        }
    }

    @Test
    fun `createProfessor lanza InvalidPhoneException cuando telefono es vacio`() {

        val request = ProfessorRequest(name = "Dr. Lopez", email = "lopez@puce.edu", phone = "")


        assertThrows<InvalidPhoneException> {
            professorService.createProfessor(request)
        }
    }

    @Test
    fun `createProfessor lanza InvalidEmailException cuando email ya existe`() {

        val request = ProfessorRequest(name = "Dr. Lopez", email = "existing@puce.edu")
        val existingProfessor = Professor(id = 2L, name = "Existing", email = "existing@puce.edu")

        whenever(professorRepository.findByEmail("existing@puce.edu")).thenReturn(existingProfessor)


        assertThrows<InvalidEmailException> {
            professorService.createProfessor(request)
        }
    }

    @Test
    fun `getProfessorById retorna profesor cuando existe`() {

        val professor = Professor(id = 1L, name = "Dr. Lopez", email = "lopez@puce.edu", phone = "0987654321")
        whenever(professorRepository.findById(1L)).thenReturn(Optional.of(professor))


        val response = professorService.getProfessorById(1L)


        assertEquals(1L, response.id)
        assertEquals("Dr. Lopez", response.name)
        assertEquals("lopez@puce.edu", response.email)
        assertEquals("0987654321", response.phone)
    }

    @Test
    fun `getProfessorById lanza ProfessorNotFoundException cuando no existe`() {

        whenever(professorRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<ProfessorNotFoundException> {
            professorService.getProfessorById(99L)
        }
    }

    @Test
    fun `getAllProfessors retorna lista de profesores`() {

        val professor1 = Professor(id = 1L, name = "Dr. Lopez", email = "lopez@puce.edu")
        val professor2 = Professor(id = 2L, name = "Dr. Garcia", email = "garcia@puce.edu")
        whenever(professorRepository.findAll()).thenReturn(listOf(professor1, professor2))


        val response = professorService.getAllProfessors()


        assertEquals(2, response.size)
        assertEquals("Dr. Lopez", response[0].name)
        assertEquals("Dr. Garcia", response[1].name)
    }

    @Test
    fun `updateProfessor actualiza profesor cuando existe y datos son validos`() {

        val request = ProfessorRequest(name = "Dr. Lopez Updated", email = "lopez.updated@puce.edu", phone = "9876543210")
        val professor = Professor(id = 1L, name = "Dr. Lopez", email = "lopez@puce.edu")
        val updatedProfessor = Professor(id = 1L, name = "Dr. Lopez Updated", email = "lopez.updated@puce.edu", phone = "9876543210")

        whenever(professorRepository.findById(1L)).thenReturn(Optional.of(professor))
        whenever(professorRepository.findByEmail("lopez.updated@puce.edu")).thenReturn(null)
        whenever(professorRepository.save(any<Professor>())).thenReturn(updatedProfessor)


        val response = professorService.updateProfessor(1L, request)


        assertEquals(1L, response.id)
        assertEquals("Dr. Lopez Updated", response.name)
        assertEquals("lopez.updated@puce.edu", response.email)
        assertEquals("9876543210", response.phone)
    }

    @Test
    fun `updateProfessor lanza BlankProfessorNameException cuando nombre es vacio`() {

        val request = ProfessorRequest(name = "", email = "lopez@puce.edu")


        assertThrows<BlankProfessorNameException> {
            professorService.updateProfessor(1L, request)
        }
    }

    @Test
    fun `updateProfessor lanza InvalidEmailException cuando email no es valido`() {

        val request = ProfessorRequest(name = "Dr. Lopez", email = "invalidemail")


        assertThrows<InvalidEmailException> {
            professorService.updateProfessor(1L, request)
        }
    }

    @Test
    fun `updateProfessor lanza InvalidPhoneException cuando telefono es vacio`() {

        val request = ProfessorRequest(name = "Dr. Lopez", email = "lopez@puce.edu", phone = "")


        assertThrows<InvalidPhoneException> {
            professorService.updateProfessor(1L, request)
        }
    }

    @Test
    fun `updateProfessor lanza ProfessorNotFoundException cuando profesor no existe`() {

        val request = ProfessorRequest(name = "Dr. Lopez", email = "lopez@puce.edu")
        whenever(professorRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<ProfessorNotFoundException> {
            professorService.updateProfessor(99L, request)
        }
    }

    @Test
    fun `updateProfessor lanza InvalidEmailException cuando email esta usado por otro profesor`() {

        val request = ProfessorRequest(name = "Dr. Lopez", email = "other@puce.edu")
        val professor = Professor(id = 1L, name = "Dr. Lopez", email = "lopez@puce.edu")
        val otherProfessor = Professor(id = 2L, name = "Other", email = "other@puce.edu")

        whenever(professorRepository.findById(1L)).thenReturn(Optional.of(professor))
        whenever(professorRepository.findByEmail("other@puce.edu")).thenReturn(otherProfessor)


        assertThrows<InvalidEmailException> {
            professorService.updateProfessor(1L, request)
        }
    }

    @Test
    fun `updateProfessor permite usar el mismo email si es el mismo profesor`() {

        val request = ProfessorRequest(name = "Dr. Lopez Updated", email = "lopez@puce.edu", phone = "9876543210")
        val professor = Professor(id = 1L, name = "Dr. Lopez", email = "lopez@puce.edu")
        val updatedProfessor = Professor(id = 1L, name = "Dr. Lopez Updated", email = "lopez@puce.edu", phone = "9876543210")

        whenever(professorRepository.findById(1L)).thenReturn(Optional.of(professor))
        whenever(professorRepository.findByEmail("lopez@puce.edu")).thenReturn(professor)
        whenever(professorRepository.save(any<Professor>())).thenReturn(updatedProfessor)


        val response = professorService.updateProfessor(1L, request)


        assertEquals("Dr. Lopez Updated", response.name)
    }

    @Test
    fun `deleteProfessor elimina profesor cuando existe`() {

        val professor = Professor(id = 1L, name = "Dr. Lopez", email = "lopez@puce.edu")
        whenever(professorRepository.findById(1L)).thenReturn(Optional.of(professor))


        val result = professorService.deleteProfessor(1L)


        assertTrue(result)
    }

    @Test
    fun `deleteProfessor lanza ProfessorNotFoundException cuando no existe`() {

        whenever(professorRepository.findById(99L)).thenReturn(Optional.empty())


        assertThrows<ProfessorNotFoundException> {
            professorService.deleteProfessor(99L)
        }
    }
}
