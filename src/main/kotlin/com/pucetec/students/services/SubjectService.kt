package com.pucetec.students.services

import com.pucetec.students.dto.SubjectRequest
import com.pucetec.students.dto.SubjectResponse
import com.pucetec.students.entities.Subject
import com.pucetec.students.exceptions.BlankSubjectNameException
import com.pucetec.students.exceptions.InvalidCreditsException
import com.pucetec.students.exceptions.ProfessorNotFoundException
import com.pucetec.students.exceptions.SubjectNotFoundException
import com.pucetec.students.repositories.ProfessorRepository
import com.pucetec.students.repositories.SubjectRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SubjectService(
    private val subjectRepository: SubjectRepository,
    private val professorRepository: ProfessorRepository
) {
    private val logger = LoggerFactory.getLogger(SubjectService::class.java)

    fun createSubject(request: SubjectRequest): SubjectResponse {
        logger.info("Creating subject ${request.name}")

        if (request.name.isBlank()) {
            throw BlankSubjectNameException("Subject name cannot be blank")
        }

        if (request.credits <= 0) {
            throw InvalidCreditsException("Credits must be greater than 0")
        }

        val professor = professorRepository.findById(request.professorId)
            .orElseThrow { ProfessorNotFoundException("Professor with id ${request.professorId} not found") }

        val subjectEntity = Subject(
            name = request.name,
            credits = request.credits,
            professorId = request.professorId
        )
        val saved = subjectRepository.save(subjectEntity)
        return saved.toResponse()
    }

    fun getSubjectById(id: Long): SubjectResponse {
        logger.info("Getting subject with id $id")
        val subject = subjectRepository.findById(id)
            .orElseThrow { SubjectNotFoundException("Subject with id $id not found") }
        return subject.toResponse()
    }

    fun getAllSubjects(): List<SubjectResponse> {
        logger.info("Getting all subjects")
        return subjectRepository.findAll().map { it.toResponse() }
    }

    fun getSubjectsByProfessor(professorId: Long): List<SubjectResponse> {
        logger.info("Getting subjects for professor $professorId")
        val professor = professorRepository.findById(professorId)
            .orElseThrow { ProfessorNotFoundException("Professor with id $professorId not found") }
        return subjectRepository.findByProfessorId(professorId).map { it.toResponse() }
    }

    fun updateSubject(id: Long, request: SubjectRequest): SubjectResponse {
        logger.info("Updating subject with id $id")

        if (request.name.isBlank()) {
            throw BlankSubjectNameException("Subject name cannot be blank")
        }

        if (request.credits <= 0) {
            throw InvalidCreditsException("Credits must be greater than 0")
        }

        val subject = subjectRepository.findById(id)
            .orElseThrow { SubjectNotFoundException("Subject with id $id not found") }

        val professor = professorRepository.findById(request.professorId)
            .orElseThrow { ProfessorNotFoundException("Professor with id ${request.professorId} not found") }

        val updated = subject.copy(
            name = request.name,
            credits = request.credits,
            professorId = request.professorId
        )
        val saved = subjectRepository.save(updated)
        return saved.toResponse()
    }

    fun deleteSubject(id: Long): Boolean {
        logger.info("Deleting subject with id $id")
        val subject = subjectRepository.findById(id)
            .orElseThrow { SubjectNotFoundException("Subject with id $id not found") }
        subjectRepository.delete(subject)
        return true
    }

    private fun Subject.toResponse() = SubjectResponse(
        id = this.id,
        name = this.name,
        credits = this.credits,
        professorId = this.professorId
    )
}
