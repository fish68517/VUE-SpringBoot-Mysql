package com.example.callmarkcollector.service

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.example.callmarkcollector.parser.CallScreenParser
import com.example.callmarkcollector.util.LogTags

/** Window boundaries and card boundaries are preserved; texts from different apps are never mixed. */
class CallWindowReader(private val service: AccessibilityService, private val defaultDialer: () -> String?) {
    private data class Node(
        val text: List<String>, val viewId: String, val className: String,
        val bounds: Rect, val children: List<Node>
    )
    private var visited = 0
    private val callHints = listOf("来电", "接听", "拒绝", "挂断", "incoming call", "answer", "decline")
    private val packageHints = listOf("dialer", "incall", "telecom", "phone", "contacts")

    fun scan(eventWindowId: Int?): List<CaptureCandidate> {
        val result = mutableListOf<CaptureCandidate>()
        val seen = mutableSetOf<Int>()
        runCatching { service.windows }.getOrDefault(emptyList()).forEach { window ->
            val root = runCatching { window.root }.getOrNull() ?: return@forEach
            seen += window.id
            Log.d(LogTags.CAPTURE, "window=" + window.id + ", type=" + window.type +
                ", layer=" + window.layer + ", active=" + window.isActive + ", focused=" + window.isFocused)
            inspect(root, window.id, eventWindowId)?.let(result::add)
        }
        val active = runCatching { service.rootInActiveWindow }.getOrNull()
        if (active != null && active.windowId !in seen) {
            inspect(active, active.windowId, eventWindowId)?.let(result::add)
        }
        if (seen.isEmpty() && active == null) Log.d(LogTags.CAPTURE, "窗口根为空，将在当前会话内重试")
        return result
    }

    private fun inspect(root: AccessibilityNodeInfo, windowId: Int, eventWindowId: Int?): CaptureCandidate? {
        val pkg = root.packageName?.toString().orEmpty()
        if (pkg.isBlank() || pkg == service.packageName) return null
        val lower = pkg.lowercase()
        val dialer = pkg == defaultDialer() || packageHints.any(lower::contains)
        val systemUi = lower.contains("systemui")
        if (!dialer && !systemUi) return null
        visited = 0
        val tree = snapshot(root, 0) ?: return null
        val candidates = mutableListOf<CaptureCandidate>()

        fun inspectRegion(node: Node, depth: Int) {
            val before = candidates.size
            node.children.forEach { inspectRegion(it, depth + 1) }
            // Once an incoming card is found, never expand it to the surrounding notification list.
            if (systemUi && candidates.size > before) return
            val lines = flatten(node)
            val incoming = lines.any { line -> callHints.any { line.contains(it, true) } } ||
                node.className.contains("incall", true) || node.className.contains("incomingcall", true)
            if (incoming) {
                val parsed = CallScreenParser.parse(lines)
                if (parsed != null) {
                    // SystemUI requires an actual local incoming card; never parse its entire notification root.
                    val isSystemCard = !systemUi || depth > 0
                    if (isSystemCard) {
                        val score = (if (dialer) 100 else 60) +
                            (if (windowId == eventWindowId) 5 else 0) +
                            (if (parsed.markType != null) 12 else 0) +
                            (if (parsed.markCount != null) 12 else 0) +
                            depth.coerceAtMost(8)
                        candidates += CaptureCandidate(parsed, pkg, windowId, score)
                    }
                }
            }
        }
        inspectRegion(tree, 0)
        val selected = candidates.maxByOrNull { it.confidence }
        Log.d(LogTags.CAPTURE, "rootPackage=" + pkg + ", window=" + windowId + ", nodes=" + visited +
            ", selected=" + selected?.info + ", raw=" + flatten(tree))
        return selected
    }

    private fun snapshot(node: AccessibilityNodeInfo, depth: Int): Node? {
        if (depth > 35 || visited++ >= 400 || node.packageName?.toString() == service.packageName) return null
        val children = (0 until node.childCount.coerceAtMost(100)).mapNotNull {
            runCatching { node.getChild(it) }.getOrNull()?.let { child -> snapshot(child, depth + 1) }
        }
        val text = if (node.isVisibleToUser) listOfNotNull(
            node.text?.toString(), node.contentDescription?.toString()
        ).map { it.trim().take(240) }.filter { it.isNotBlank() }.distinct() else emptyList()
        val bounds = Rect().also(node::getBoundsInScreen)
        return Node(text, node.viewIdResourceName.orEmpty(), node.className?.toString().orEmpty(), bounds, children)
    }

    private fun flatten(node: Node): List<String> {
        val lines = mutableListOf<String>()
        fun visit(current: Node) {
            lines += current.text
            // Only combine adjacent direct siblings with count semantics and nearby bounds.
            current.children.zipWithNext().forEach { (a, b) ->
                val left = a.text.singleOrNull()
                val right = b.text.singleOrNull()
                if (left != null && right != null &&
                    Regex("""[\d,，]+""").matches(left) &&
                    Regex("""(?:人|次|个用户)(?:标记|举报)""").matches(right) &&
                    kotlin.math.abs(a.bounds.centerY() - b.bounds.centerY()) <=
                    maxOf(a.bounds.height(), b.bounds.height(), 1)) {
                    lines += left + right
                }
            }
            current.children.forEach(::visit)
        }
        visit(node)
        return lines.distinct().take(160)
    }
}
