package com.example.callmarkcollector.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CallScreenParserTest {
    @Test
    fun parsesMarkedMobileNumber() {
        val result = CallScreenParser.parse(
            listOf("来电", "138 0013 8000", "被23人标记", "疑似诈骗")
        )
        assertNotNull(result)
        assertEquals("13800138000", result?.phoneNumber)
        assertTrue(result?.isMarked == true)
        assertEquals(23, result?.markCount)
        assertEquals("疑似诈骗", result?.markType)
    }

    @Test
    fun parsesCommaSeparatedMarkCount() {
        val result = CallScreenParser.parse(
            listOf("400-123-4567", "已有1,286人标记为骚扰推销")
        )
        assertEquals("4001234567", result?.phoneNumber)
        assertEquals(1286, result?.markCount)
        assertEquals("骚扰推销", result?.markType)
    }

    @Test
    fun parsesUnmarkedNumber() {
        val result = CallScreenParser.parse(listOf("来电号码", "+86 139-1234-5678", "接听", "拒绝"))
        assertEquals("+8613912345678", result?.phoneNumber)
        assertFalse(result?.isMarked ?: true)
        assertNull(result?.markCount)
        assertNull(result?.markType)
    }

    @Test
    fun rejectsScreenWithoutPhoneNumber() {
        assertNull(CallScreenParser.parse(listOf("设置", "标记此号码", "返回")))
    }
}
