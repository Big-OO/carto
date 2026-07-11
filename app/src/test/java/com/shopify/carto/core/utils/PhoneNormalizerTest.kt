package com.shopify.carto.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneNormalizerTest {

    @Test
    fun normalize_egyptianLocalNumber_convertsToInternational() {
        val input = "01226022955"
        val expected = "+201226022955"
        assertEquals(expected, PhoneNormalizer.normalize(input))
    }

    @Test
    fun normalize_egyptianNumberWithPlus_removesPlusAndKeepsInternational() {
        val input = "+201226022955"
        val expected = "+201226022955"
        assertEquals(expected, PhoneNormalizer.normalize(input))
    }

    @Test
    fun normalize_egyptianNumberWithSpacesAndDashes_cleansAndFormats() {
        val input = "+20 122-602-2955"
        val expected = "+201226022955"
        assertEquals(expected, PhoneNormalizer.normalize(input))
    }

    @Test
    fun normalize_egyptianNumberWithoutLeadingZero_prependsCountryCode() {
        val input = "1226022955"
        val expected = "+201226022955"
        assertEquals(expected, PhoneNormalizer.normalize(input))
    }

    @Test
    fun normalize_egyptianNumberWithDoubleZeroPrefix_normalizes() {
        val input = "00201226022955"
        val expected = "+201226022955"
        assertEquals(expected, PhoneNormalizer.normalize(input))
    }

    @Test
    fun normalize_egyptianNumberWithCountryCodeAndLeadingZero_removesExtraZero() {
        val input = "+2001226022955"
        val expected = "+201226022955"
        assertEquals(expected, PhoneNormalizer.normalize(input))
    }
}
