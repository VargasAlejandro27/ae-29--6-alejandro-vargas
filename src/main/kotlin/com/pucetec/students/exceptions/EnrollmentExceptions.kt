package com.pucetec.students.exceptions

class EnrollmentNotFoundException(message: String) : Exception(message)
class StudentNotEnrolledException(message: String) : Exception(message)
class EnrollmentAlreadyExistsException(message: String) : Exception(message)
class DuplicateEnrollmentException(message: String) : Exception(message)
