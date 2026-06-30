package com.pucetec.students.dto

data class EnrollmentRequest(
    val studentId: Long,
    val subjectId: Long,
    val grade: Double? = null
)

data class EnrollmentResponse(
    val id: Long,
    val studentId: Long,
    val subjectId: Long,
    val grade: Double? = null,
    val enrollmentDate: Long
)
