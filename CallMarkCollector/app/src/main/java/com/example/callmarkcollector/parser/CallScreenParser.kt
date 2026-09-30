package com.example.callmarkcollector.parser

import com.example.callmarkcollector.model.ParsedCallInfo

object CallScreenParser {
    private val countPatterns = listOf(
        Regex("""(?:被|已有|已被)?\s*([\d,，]+)\s*(?:人|次|个用户)\s*(?:标记|举报)"""),
        Regex("""(?:标记|举报)(?:人数|次数)?\s*[:：]\s*([\d,，]+)\s*(?:人|次)?""")
    )
    private val phonePattern = Regex("""(?<![\d+])\+?\d[\d \-()]{3,24}\d(?!\d)""")
    private val knownLabels = listOf(
        "疑似诈骗", "诈骗电话", "骚扰电话", "骚扰推销", "广告推销", "推销电话",
        "房产中介", "保险推销", "贷款营销", "教育培训", "催收电话", "招聘电话",
        "快递电话", "快递送餐", "外卖送餐", "出租车"
    )
    private val explicitType = Regex("""(?:标记为|举报为|标记类型\s*[:：]|标记类别\s*[:：])\s*([\p{L}]{2,12})(?=$|[，,。;；\s])""")
    private val unmarked = Regex("""未被标记|暂无标记|未标记|无标记""")
    private val ownPage = listOf("来电标记采集助手", "来电采集助手", "最近一次采集", "采集时间：", "上传状态：")

    /** Input must already be scoped to a trusted call window/card by the service. */
    fun parse(inputLines: List<String>): ParsedCallInfo? {
        val lines = inputLines.map {
            it.replace('\u00a0', ' ').replace('\u202f', ' ').replace(Regex("\\s+"), " ").trim()
        }.filter { it.isNotBlank() }.distinct()
        if (lines.any { line -> ownPage.any(line::contains) }) return null
        val phone = findPhone(lines) ?: return null
        val count = lines.firstNotNullOfOrNull { line ->
            countPatterns.firstNotNullOfOrNull { pattern ->
                pattern.find(line)?.groupValues?.get(1)?.replace(",", "")?.replace("，", "")?.toIntOrNull()
            }
        }
        val type = lines.firstNotNullOfOrNull { line ->
            knownLabels.firstOrNull(line::contains)
                ?: explicitType.find(line)?.groupValues?.get(1)
        }
        val marked = when {
            type != null || (count != null && count > 0) -> true
            count == 0 || lines.any(unmarked::containsMatchIn) -> false
            lines.any { it.contains("已被标记") || it.contains("已被举报") } -> true
            else -> null
        }
        return ParsedCallInfo(phone, marked, count, type, lines.take(120))
    }

    private fun findPhone(lines: List<String>): String? {
        val candidates = mutableListOf<Pair<String, Int>>()
        lines.forEach { original ->
            if (original.contains("标记") || original.contains("举报") ||
                Regex("""\d{4}[-/]\d{1,2}[-/]\d{1,2}|\d{1,2}:\d{2}""").containsMatchIn(original)) return@forEach
            // Some OEMs expose the SIM badge and phone number as one semantic node.
            val line = original.replace(Regex("""^(?:SIM\s*|卡\s*)?[12]\s+(?=(?:\+?86\s*)?(?:0\d|1[3-9]\d))""", RegexOption.IGNORE_CASE), "")
            phonePattern.findAll(line).forEach phoneLoop@ { match ->
                val raw = match.value.trim()
                val digits = raw.filter(Char::isDigit)
                if (digits.length !in 5..15) return@phoneLoop
                val pure = line == raw
                val labelled = Regex("""来电号码|电话号码|来电\s*[:：]""").containsMatchIn(line)
                val score = when {
                    digits.length == 11 && Regex("""1[3-9]\d{9}""").matches(digits) -> 100
                    digits.startsWith("0") && digits.length in 10..12 -> 95
                    digits.length == 13 && digits.startsWith("86") -> 95
                    (digits.startsWith("400") || digits.startsWith("800")) && digits.length == 10 -> 90
                    raw.startsWith("+") && digits.length in 8..15 -> 85
                    digits.length in 5..6 && (pure || labelled) -> 40
                    digits.length in 7..12 && (pure || labelled) -> 50
                    else -> return@phoneLoop
                }
                candidates += (if (raw.startsWith("+")) "+$digits" else digits) to (score + if (pure) 5 else 0)
            }
        }
        return candidates.maxByOrNull { it.second }?.first
    }
}
