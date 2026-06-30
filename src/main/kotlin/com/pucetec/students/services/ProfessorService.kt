package com.pucetec.students.services

import com.pucetec.students.dto.ProfessorRequest
import com.pucetec.students.dto.ProfessorResponse
import com.pucetec.students.entities.Professor
import com.pucetec.students.exceptions.BlankProfessorNameException
import com.pucetec.students.exceptions.InvalidEmailException
import com.pucetec.students.exceptions.InvalidPhoneException
import com.pucetec.students.exceptions.ProfessorNotFoundException
import com.pucetec.students.repositories.ProfessorRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProfessorService(private val professorRepository: ProfessorRepository) {
    private val logger = LoggerFactory.getLogger(ProfessorService::class.java)

    fun createProfessor(request: ProfessorRequest): ProfessorResponse {
        logger.info("Creating professor ${request.name}")

        if (request.name.isBlank()) {
            throw BlankProfessorNameException("Professor name cannot be blank")
        }

        if (!request.email.contains("@")) {
            throw InvalidEmailException("Email must be valid")
        }

        if (request.phone != null && request.phone.isBlank()) {
            throw InvalidPhoneException("Phone cannot be blank if provided")
        }

        val existingProfessor = professorRepository.findByEmail(request.email)
        if (existingProfessor != null) {
            throw InvalidEmailException("Email ${request.email} is already in use")
        }

        val professorEntity = Professor(
            name = request.name,
            email = request.email,
            phone = request.phone
        )
        val saved = professorRepository.save(professorEntity)
        return saved.toResponse()
    }

    fun getProfessorById(id: Long): ProfessorResponse {
        logger.info("Getting professor with id $id")
        val professor = professorRepository.findById(id)
            .orElseThrow { ProfessorNotFoundException("Professor with id $id not found") }
        return professor.toResponse()
    }

    fun getAllProfessors(): List<ProfessorResponse> {
        logger.info("Getting all professors")
        return professorRepository.findAll().map { it.toResponse() }
    }

    fun updateProfessor(id: Long, request: ProfessorRequest): ProfessorResponse {
        logger.info("Updating professor with id $id")

        if (request.name.isBlank()) {
            throw BlankProfessorNameException("Professor name cannot be blank")
        }

        if (!request.email.contains("@")) {
            throw InvalidEmailException("Email must be valid")
        }

        if (request.phone != null && request.phone.isBlank()) {
            throw InvalidPhoneException("Phone cannot be blank if provided")
        }

        val professor = professorRepository.findById(id)
            .orElseThrow { ProfessorNotFoundException("Professor with id $id not found") }

        val existingProfessor = professorRepository.findByEmail(request.email)
        if (existingProfessor != null && existingProfessor.id != id) {
            throw InvalidEmailException("Email ${request.email} is already used by another professor")
        }

        val updated = professor.copy(
            name = request.name,
            email = request.email,
            phone = request.phone
        )
        val saved = professorRepository.save(updated)
        return saved.toResponse()
    }

    fun deleteProfessor(id: Long): Boolean {
        logger.info("Deleting professor with id $id")
        val professor = professorRepository.findById(id)
            .orElseThrow { ProfessorNotFoundException("Professor with id $id not found") }
        professorRepository.delete(professor)
        return true
    }

    private fun Professor.toResponse() = ProfessorResponse(
        id = this.id,
        name = this.name,
        email = this.email,
        phone = this.phone
    )
}
