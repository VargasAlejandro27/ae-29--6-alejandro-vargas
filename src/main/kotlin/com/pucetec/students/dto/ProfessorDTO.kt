package com.pucetec.students.dto

data class ProfessorRequest(
    val name: String,
    val email: String,
    val phone: String? = null
)

data class ProfessorResponse(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String? = null
)
