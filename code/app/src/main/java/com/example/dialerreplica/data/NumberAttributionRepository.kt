package com.example.dialerreplica.data

import android.content.Context
import com.example.dialerreplica.model.NumberAttribution
import org.json.JSONObject

class NumberAttributionRepository(context: Context) {
    private val exactPrefixes = mutableMapOf<String, NumberAttribution>()
    private val specialPrefixes = mutableMapOf<String, String>()
    private val phoneSegmentDatabase = PhoneSegmentDatabase.load(context.applicationContext)

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
        if (number.length < 3) return NumberAttribution()

        exactPrefixes.keys
            .asSequence()
            .filter(number::startsWith)
            .maxByOrNull(String::length)
            ?.let { return exactPrefixes.getValue(it) }

        // 完整号码优先按前 7 位从离线号段库精确查询归属地与运营商。
        // number_prefixes.json 仍是更高优先级的人工修正规则，便于随时修补个别号段。
        phoneSegmentDatabase?.lookup(number)?.let { return it }

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

private class PhoneSegmentDatabase private constructor(private val data: ByteArray) {
    private val indicesStartOffset = readLittleEndianInt(data, 4)
    private val recordCount = (data.size - indicesStartOffset) / INDEX_LENGTH

    fun lookup(number: String): NumberAttribution? {
        if (number.length < PREFIX_LENGTH) return null
        val targetPrefix = number.take(PREFIX_LENGTH).toIntOrNull() ?: return null
        var low = 0
        var high = recordCount - 1

        while (low <= high) {
            val middle = low + (high - low) / 2
            val indexOffset = indicesStartOffset + middle * INDEX_LENGTH
            val currentPrefix = readLittleEndianInt(data, indexOffset)
            when {
                currentPrefix < targetPrefix -> low = middle + 1
                currentPrefix > targetPrefix -> high = middle - 1
                else -> return readAttribution(indexOffset)
            }
        }
        return null
    }

    private fun readAttribution(indexOffset: Int): NumberAttribution? {
        val recordOffset = readLittleEndianInt(data, indexOffset + INT_LENGTH)
        if (recordOffset !in HEADER_LENGTH until indicesStartOffset) return null

        var recordEnd = recordOffset
        while (recordEnd < indicesStartOffset && data[recordEnd].toInt() != 0) recordEnd++
        if (recordEnd <= recordOffset || recordEnd >= indicesStartOffset) return null

        val fields = String(data, recordOffset, recordEnd - recordOffset, Charsets.UTF_8).split('|')
        if (fields.size < 2) return null
        val operator = operatorFromIspType(data[indexOffset + INT_LENGTH * 2].toInt() and 0xFF)
        return NumberAttribution(
            province = fields[0].trim(),
            city = fields[1].trim(),
            operator = operator,
            numberType = if (operator.isBlank()) "unknown" else "mobile",
        )
    }

    companion object {
        private const val PHONE_DATA_ASSET = "phone.dat"
        private const val HEADER_LENGTH = 8
        private const val INT_LENGTH = 4
        private const val INDEX_LENGTH = 9
        private const val PREFIX_LENGTH = 7

        @Volatile
        private var cachedData: ByteArray? = null

        fun load(context: Context): PhoneSegmentDatabase? = runCatching {
            val bytes = cachedData ?: synchronized(this) {
                cachedData ?: context.assets.open(PHONE_DATA_ASSET).use { it.readBytes() }
                    .also { cachedData = it }
            }
            val indicesStart = readLittleEndianInt(bytes, INT_LENGTH)
            require(indicesStart in HEADER_LENGTH until bytes.size)
            require((bytes.size - indicesStart) % INDEX_LENGTH == 0)
            PhoneSegmentDatabase(bytes)
        }.getOrNull()

        private fun operatorFromIspType(type: Int): String = when (type) {
            1 -> "中国移动"
            2 -> "中国联通"
            3 -> "中国电信"
            4 -> "中国电信虚拟运营商"
            5 -> "中国联通虚拟运营商"
            6 -> "中国移动虚拟运营商"
            7 -> "中国广电"
            8 -> "中国广电虚拟运营商"
            else -> ""
        }
    }
}

private fun readLittleEndianInt(data: ByteArray, offset: Int): Int {
    require(offset >= 0 && offset + 4 <= data.size)
    return (data[offset].toInt() and 0xFF) or
        ((data[offset + 1].toInt() and 0xFF) shl 8) or
        ((data[offset + 2].toInt() and 0xFF) shl 16) or
        ((data[offset + 3].toInt() and 0xFF) shl 24)
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
