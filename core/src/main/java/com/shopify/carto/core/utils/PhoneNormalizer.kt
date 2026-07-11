package com.shopify.carto.core.utils

object PhoneNormalizer {
    /**
     * Normalizes a phone number to match country-coded formats, e.g. converting local Egyptian number 01xxxxxxxxx to 201xxxxxxxxx.
     */
    fun normalize(phone: String): String {
        // Remove all non-digit characters
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        if (cleaned.isBlank()) return ""
        
        // Extract local 10-digit Egyptian number (without leading 0 or country code 20)
        val localNumber = when {
            cleaned.startsWith("00201") && cleaned.length == 14 -> cleaned.substring(4)
            cleaned.startsWith("2001") && cleaned.length == 13 -> cleaned.substring(3)
            cleaned.startsWith("201") && cleaned.length == 12 -> cleaned.substring(2)
            cleaned.startsWith("01") && cleaned.length == 11 -> cleaned.substring(1)
            cleaned.length == 10 -> cleaned
            cleaned.length > 10 -> cleaned.takeLast(10)
            else -> cleaned
        }
        
        return if (localNumber.length == 10) {
            "+20$localNumber"
        } else {
            if (cleaned.startsWith("20")) "+$cleaned" else "+20$cleaned"
        }
    }
}
