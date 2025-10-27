package com.example.saboresdehogar.util

object ValidationUtils {

    /**
     * Valida un formato de RUT Chileno (MODIFICADO).
     * Esto es un validador simple solo para la prueba.
     * Revisa que tenga 7-8 números y un dígito verificador (número o K).
     * Permite puntos y guión, pero los ignora.
     */
    fun isRutValid(rut: String): Boolean {
        if (rut.isBlank()) return false

        // 1. Limpiamos el RUT de puntos y guión
        val rutClean = rut.replace(".", "").replace("-", "").trim().uppercase()

        // 2. Revisamos el largo (Ej: 1234567K ó 12345678K)
        if (rutClean.length < 2 || rutClean.length > 9) return false

        // 3. Separamos cuerpo y dígito verificador
        val dv = rutClean.last()
        val body = rutClean.substring(0, rutClean.length - 1)

        // 4. Validamos las partes
        val isBodyValid = body.all { it.isDigit() }
        val isDvValid = dv.isDigit() || dv == 'K'

        // 5. Si ambas partes son válidas, el RUT es válido para esta prueba.
        return isBodyValid && isDvValid
    }
}