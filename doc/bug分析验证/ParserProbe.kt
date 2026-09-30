import com.example.callmarkcollector.parser.CallScreenParser

fun main() {
    val cases = linkedMapOf(
        "full_screen_transcription" to listOf("移动 来电", "16人标记", "广告推销", "广东东莞 0769 2173 8692"),
        "floating_banner_transcription" to listOf("广告推销", "0769 2173 8692 广东…"),
        "own_title_and_short_number" to listOf("来电标记采集助手", "12123"),
        "own_title_and_real_number" to listOf("来电标记采集助手", "0769 2173 8692"),
        "own_title_only" to listOf("来电标记采集助手"),
        "split_count_nodes" to listOf("0769 2173 8692", "16", "人标记", "广告推销"),
        "sim_and_number_same_node" to listOf("1 0769 2173 8692", "广告推销"),
        "old_record_and_banner_mixed" to listOf("来电标记采集助手", "号码：12123", "0769 2173 8692 广东…", "广告推销")
    )
    for ((name, lines) in cases) {
        val parsed = CallScreenParser.parse(lines)
        println("CASE=$name")
        println("INPUT=$lines")
        println("RESULT=$parsed")
        println()
    }
}
