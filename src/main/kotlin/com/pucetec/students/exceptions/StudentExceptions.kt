package com.pucetec.students.exceptions

class StudentNotFoundException(message: String) : Exception(message)
class BlankNameException(message: String) : Exception(message)
class InvalidEmailException(message: String) : Exception(message)
class StudentAlreadyExistsException(message: String) : Exception(message)
