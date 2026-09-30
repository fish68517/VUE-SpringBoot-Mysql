package com.example.callmarkcollector.parser

import com.example.callmarkcollector.service.CaptureCandidate
import com.example.callmarkcollector.service.RingGate

/** Pure Kotlin regressions, executable without building the Android application. */
object CaptureRegressionCases {
    fun runAll(): Int {
        var passed = 0
        fun test(name: String, block: () -> Unit) {
            block()
            passed++
            println("PASS " + name)
        }
        test("full screenshot") {
            val result = CallScreenParser.parse(listOf("移动 来电", "16人标记", "广告推销", "广东东莞 0769 2173 8692"))!!
            check(result.phoneNumber == "076921738692")
            check(result.isMarked == true && result.markCount == 16 && result.markType == "广告推销")
        }
        test("own title cannot become mark type or phone record") {
            check(CallScreenParser.parse(listOf("来电标记采集助手", "12123")) == null)
        }
        test("old record mixed with call card is rejected") {
            check(CallScreenParser.parse(listOf("来电标记采集助手", "号码：12123", "0769 2173 8692 广东…", "广告推销")) == null)
        }
        test("landline ranks above unrelated short number") {
            check(CallScreenParser.parse(listOf("号码：12123", "0769 2173 8692 广东…", "广告推销"))!!.phoneNumber == "076921738692")
        }
        test("SIM badge does not prefix number") {
            check(CallScreenParser.parse(listOf("1 0769 2173 8692", "广告推销"))!!.phoneNumber == "076921738692")
        }
        test("narrow nonbreaking spaces") {
            check(CallScreenParser.parse(listOf("0769\u202f2173\u00a08692"))!!.phoneNumber == "076921738692")
        }
        test("short incoming service numbers remain supported") {
            check(CallScreenParser.parse(listOf("来电", "12123", "接听"))!!.phoneNumber == "12123")
        }
        test("unknown mark is distinct from explicitly unmarked") {
            check(CallScreenParser.parse(listOf("13800138000"))!!.isMarked == null)
            check(CallScreenParser.parse(listOf("13800138000", "未标记"))!!.isMarked == false)
            check(CallScreenParser.parse(listOf("13800138000", "0人标记"))!!.markCount == 0)
        }
        test("international number and comma count") {
            val result = CallScreenParser.parse(listOf("+86 139-1234-5678", "已有1,286人标记为骚扰推销"))!!
            check(result.phoneNumber == "+8613912345678" && result.markCount == 1286)
        }
        test("explicit custom mark type only") {
            check(CallScreenParser.parse(listOf("13800138000", "标记类型：物业服务"))!!.markType == "物业服务")
            check(CallScreenParser.parse(listOf("13800138000", "标记此号码"))!!.markType == null)
        }
        test("date and mark counts cannot become phone numbers") {
            check(CallScreenParser.parse(listOf("2026-09-29 14:13:33", "12345人标记")) == null)
        }
        test("partial banner merged with delayed count") {
            val a = CaptureCandidate(CallScreenParser.parse(listOf("076921738692", "广告推销"))!!, "phone", 1, 100)
            val b = CaptureCandidate(CallScreenParser.parse(listOf("076921738692", "16人标记"))!!, "phone", 2, 90)
            val merged = a.merge(b)
            check(merged.info.markCount == 16 && merged.info.markType == "广告推销")
            check(merged.merge(a).info.markCount == 16)
        }
        test("different numbers never share mark fields") {
            val a = CaptureCandidate(CallScreenParser.parse(listOf("076921738692", "16人标记", "广告推销"))!!, "phone", 1, 100)
            val b = CaptureCandidate(CallScreenParser.parse(listOf("13800138000"))!!, "phone", 2, 110)
            val merged = a.merge(b)
            check(merged.info.phoneNumber == "13800138000" && merged.info.markCount == null)
        }
        test("one ring creates one start despite repeated callbacks") {
            val gate = RingGate()
            gate.watch(setOf(1))
            check(gate.update(1, 0) == null)
            check(gate.update(1, 1) == RingGate.Change.START)
            repeat(100) { check(gate.update(1, 1) == null) }
            check(gate.update(1, 2) == RingGate.Change.END)
            check(gate.update(1, 0) == null)
        }
        test("next ring including same number starts a new session") {
            val gate = RingGate()
            gate.watch(setOf(1))
            check(gate.update(1, 1) == RingGate.Change.START)
            check(gate.update(1, 0) == RingGate.Change.END)
            check(gate.update(1, 1) == RingGate.Change.START)
        }
        test("SIM initial callback order does not duplicate restored ring") {
            val gate = RingGate(initialRinging = true)
            gate.watch(setOf(1, 2))
            check(gate.update(2, 0) == null && !gate.ready)
            check(gate.update(1, 1) == null && gate.ready)
            check(gate.update(1, 0) == RingGate.Change.END)
            check(gate.update(2, 1) == RingGate.Change.START)
        }
        test("restored finished call closes when all SIMs idle") {
            val gate = RingGate(initialRinging = true)
            gate.watch(setOf(1, 2))
            check(gate.update(1, 0) == null)
            check(gate.update(2, 0) == RingGate.Change.END)
        }
        test("unregistered subscription is ignored") {
            val gate = RingGate()
            gate.watch(setOf(2))
            check(gate.update(1, 1) == null)
            check(gate.update(2, 0) == null && !gate.ringing)
        }
        return passed
    }
}

fun main() {
    println("Passed " + CaptureRegressionCases.runAll() + " pure Kotlin regression scenarios.")
}
