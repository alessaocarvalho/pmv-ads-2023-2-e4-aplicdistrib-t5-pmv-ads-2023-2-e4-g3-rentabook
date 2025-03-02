package br.puc.projeto.rentabook.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.EnableWebMvc


@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val jwtUtils: JWTUtils,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            csrf { disable() }
            authorizeHttpRequests {
                authorize(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/swagger-ui/*"), permitAll)
                authorize(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/v3/api-docs/**"), permitAll)
                authorize(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/login"), permitAll)
                authorize(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/register"), permitAll)
                authorize(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/recovery/{email}"), permitAll)
                authorize(AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/recovery"), permitAll)
                authorize(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/public/books/*"), permitAll)
                authorize(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/public/user/{id}"), permitAll)
                authorize(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/announcements/find"), permitAll)
                authorize(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/public/image/{id}"), permitAll)
                authorize(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/public/address/{id}"), permitAll)
                authorize(anyRequest, authenticated)
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            headers {
                frameOptions { disable() }
            }
            cors { }
        }
        http.addFilterBefore(
            JTWAuthenticationFilter(jwtUtils = jwtUtils),
            UsernamePasswordAuthenticationFilter::class.java
        )


        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()


        config.allowedOrigins = listOf("*")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("Authorization", "Content-Type")
        config.maxAge = 3600

        source.registerCorsConfiguration("/**", config)

        return CorsFilter(source)
    }

}