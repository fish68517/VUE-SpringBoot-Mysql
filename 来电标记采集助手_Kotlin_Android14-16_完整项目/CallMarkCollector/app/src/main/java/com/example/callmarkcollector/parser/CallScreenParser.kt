package com.example.callmarkcollector.parser

import com.example.callmarkcollector.model.ParsedCallInfo

object CallScreenParser {
    private val countPatterns = listOf(
        Regex("(?:被|已有|已被)?\\s*([\\d,，]+)\\s*(?:人|次|个用户)?\\s*(?:标记|举报)"),
        Regex("(?:标记|举报)(?:次数)?\\s*[:：]?\\s*([\\d,，]+)\\s*(?:人|次)?")
    )

    private val loosePhonePattern = Regex("(?<!\\d)(?:\\+?\\d[\\d\\s\\-()]{3,20}\\d)(?!\\d)")
    private val exactPhoneLinePattern = Regex("^[+\\d][\\d\\s\\-()]{3,20}$")

    private val knownMarkLabels = listOf(
        "疑似诈骗", "诈骗电话", "骚扰电话", "骚扰推销", "广告推销", "推销电话",
        "房产中介", "保险推销", "贷款营销", "教育培训", "催收电话", "招聘电话",
        "快递电话", "快递送餐", "外卖送餐", "出租车"
    )

    fun parse(inputLines: List<String>): ParsedCallInfo? {
        val lines = inputLines
            .map { it.replace(Regex("\\s+"), " ").trim() }
            .filter { it.isNotBlank() }
            .distinct()

        val phoneNumber = findPhoneNumber(lines) ?: return null
        val countMatch = findMarkCount(lines)
        val markType = findMarkType(lines)
        val explicitMarked = lines.any {
            Regex("(?:被|已有|已被).{0,12}(?:标记|举报)").containsMatchIn(it) ||
                Regex("[\\d,，]+\\s*(?:人|次).{0,4}(?:标记|举报)").containsMatchIn(it)
        }
        val isMarked = countMatch != null || markType != null || explicitMarked

        return ParsedCallInfo(
            phoneNumber = phoneNumber,
            isMarked = isMarked,
            markCount = countMatch,
            markType = markType,
            rawTextLines = lines.take(80)
        )
    }

    private fun findPhoneNumber(lines: List<String>): String? {
        data class Candidate(val normalized: String, val score: Int)
        val candidates = mutableListOf<Candidate>()

        lines.forEach { line ->
            loosePhonePattern.findAll(line).forEach { match ->
                val raw = match.value.trim()
                val digits = raw.filter(Char::isDigit)
                if (digits.length !in 5..15) return@forEach

                var score = 0
                if (exactPhoneLinePattern.matches(line)) score += 5
                if (line.contains("来电") || line.contains("号码") || line.contains("电话")) score += 3
                if (digits.length == 11 && digits.startsWith("1")) score += 4
                if (digits.length == 13 && digits.startsWith("86")) score += 4
                if (digits.startsWith("400") || digits.startsWith("800")) score += 2
                if (line.contains("标记") || line.contains("举报")) score -= 4

                val normalized = if (raw.startsWith("+") && digits.startsWith("86")) "+$digits" else digits
                candidates += Candidate(normalized, score)
            }
        }

        return candidates.maxByOrNull { it.score }?.normalized
    }

    private fun findMarkCount(lines: List<String>): Int? {
        lines.forEach { line ->
            countPatterns.forEach { pattern ->
                val raw = pattern.find(line)?.groupValues?.getOrNull(1)
                val count = raw?.replace(",", "")?.replace("，", "")?.toIntOrNull()
                if (count != null) return count
            }
        }
        return null
    }

    private fun findMarkType(lines: List<String>): String? {
        lines.forEach { line ->
            knownMarkLabels.firstOrNull { line.contains(it) }?.let { return it }
        }

        lines.forEach { line ->
            if (!line.contains("标记") && !line.contains("举报")) return@forEach
            var remainder = line
            countPatterns.forEach { remainder = remainder.replace(it, "") }
            remainder = remainder
                .replace(Regex("(?:标记|举报)(?:类型|类别|情况)?"), "")
                .replace(Regex("^[为是：:，,、\\-\\s]+|[：:，,、\\-\\s]+$"), "")
                .trim()
            if (remainder.length in 2..24 && remainder.any { it.isLetter() }) return remainder
        }
        return null
    }
}
