package com.pucetec.students.dto

data class StudentRequest(
    val name: String,
    val email: String
)

data class StudentResponse(
    val id: Long,
    val name: String,
    val email: String,
    val createdAt: Long
)
