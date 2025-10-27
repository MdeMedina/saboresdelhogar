package com.example.saboresdehogar.util

object ValidationUtils {

    /**
     * Valida un RUT Chileno.
     * Retorna true si es válido, false en caso contrario.
     */
    fun isRutValid(rut: String): Boolean {
        if (rut.isBlank()) return false

        var rutClean = rut.replace(".", "").replace("-", "")
        if (rutClean.length < 2) return false

        val dv = rutClean.last().uppercaseChar()
        val rutBody = rutClean.substring(0, rutClean.length - 1)

        if (!rutBody.all { it.isDigit() }) return false

        try {
            var m = 0
            var s = 1
            for (t in rutBody.reversed()) {
                s = (s + t.toString().toInt() * (m++ % 6 + 2)) % 11
            }
            val dvCalculated = (if (s > 0) (11 - s).toString() else "K")[0]
            return dv == dvCalculated
        } catch (e: Exception) {
            return false
        }
    }
}