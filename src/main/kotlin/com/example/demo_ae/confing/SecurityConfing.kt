package com.example.demo_ae.confing

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfing {

    @Bean
    fun securrityFilletsChain(http: HttpSecurity): SecurityFilterChain {
        http
            //.csrf {it.disable()}
            .csrf { miNombre ->
                miNombre.disable()
            }
            //.sessionManagement{it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)}
            .sessionManagement{miSessionManager->
                miSessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // queremos definit como pulico /api/public y como privado / api/private
            .authorizeHttpRequests {authRequest ->
                authRequest
                    .requestMatchers("api/public").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { authRequest -> authRequest.jwt{} }
        return http.build()
    }
}


