package com.example.demo_ae.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class DummyControllers {

    @GetMapping("/public")
    fun dummy() = "Hello World"

    @GetMapping("/private")
        fun dummyPrivate() = "Hello World"

    }
