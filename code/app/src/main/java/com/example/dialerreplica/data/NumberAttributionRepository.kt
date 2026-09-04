package com.example.dialerreplica.data

import android.content.Context
import com.example.dialerreplica.model.NumberAttribution
import org.json.JSONObject

class NumberAttributionRepository(context: Context) {
    private val exactPrefixes = mutableMapOf<String, NumberAttribution>()
    private val specialPrefixes = mutableMapOf<String, String>()

    init {
        runCatching {
            val text = context.assets.open("number_prefixes.json").bufferedReader().use { it.readText() }
            val root = JSONObject(text)
            val records = root.getJSONArray("records")
            for (index in 0 until records.length()) {
                val item = records.getJSONObject(index)
                exactPrefixes[item.getString("prefix")] = NumberAttribution(
                    province = item.optString("province"),
                    city = item.optString("city"),
                    operator = item.optString("operator"),
                    numberType = item.optString("number_type", "mobile"),
                )
            }
            val specials = root.getJSONObject("special_prefixes")
            specials.keys().forEach { specialPrefixes[it] = specials.getString(it) }
        }
    }

    fun lookup(raw: String): NumberAttribution {
        val number = normalizePhoneNumber(raw)
        exactPrefixes.keys
            .asSequence()
            .filter(number::startsWith)
            .maxByOrNull(String::length)
            ?.let { return exactPrefixes.getValue(it) }

        specialPrefixes.entries.firstOrNull { number.startsWith(it.key) }?.let {
            return NumberAttribution(operator = it.value, numberType = "mobile")
        }

        val operator = when (number.take(3)) {
            "134", "135", "136", "137", "138", "139", "147", "148", "150", "151", "152",
            "157", "158", "159", "172", "178", "182", "183", "184", "187", "188", "195",
            "197", "198" -> "中国移动"
            "130", "131", "132", "145", "146", "155", "156", "166", "167", "171", "175",
            "176", "185", "186", "196" -> "中国联通"
            "133", "149", "153", "162", "173", "174", "177", "180", "181", "189", "190",
            "191", "193", "199" -> "中国电信"
            "192" -> "中国广电"
            "165" -> "中国移动转售"
            else -> ""
        }
        return NumberAttribution(operator = operator, numberType = if (operator.isBlank()) "unknown" else "mobile")
    }
}

fun normalizePhoneNumber(raw: String): String {
    val digits = raw.filter(Char::isDigit)
    return when {
        digits.startsWith("0086") && digits.length > 4 -> digits.drop(4)
        digits.startsWith("86") && digits.length > 11 -> digits.drop(2)
        else -> digits
    }.take(15)
}

fun formatPhoneNumber(raw: String): String {
    val value = normalizePhoneNumber(raw)
    return when {
        value.length <= 3 -> value
        value.length <= 7 -> value.take(3) + " " + value.drop(3)
        value.length <= 11 -> value.take(3) + " " + value.substring(3, 7) + " " + value.drop(7)
        else -> value.chunked(4).joinToString(" ")
    }
}
