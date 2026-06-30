package com.pucetec.students.dto

data class SubjectRequest(
    val name: String,
    val credits: Int,
    val professorId: Long
)

data class SubjectResponse(
    val id: Long,
    val name: String,
    val credits: Int,
    val professorId: Long
)
