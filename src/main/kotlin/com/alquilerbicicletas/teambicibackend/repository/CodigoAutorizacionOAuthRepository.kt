package com.alquilerbicicletas.teambicibackend.repository

import com.alquilerbicicletas.teambicibackend.domain.entity.CodigoAutorizacionOAuth
import org.springframework.data.jpa.repository.JpaRepository

interface CodigoAutorizacionOAuthRepository : JpaRepository<CodigoAutorizacionOAuth, Long> {
    fun findByCodigoHashAndUsadoEnIsNull(codigoHash: String): CodigoAutorizacionOAuth?
}
