package com.example.callmarkcollector.service

/** One device ringing episode, independent of repeated events and callback order across SIMs. */
class RingGate(initialRinging: Boolean = false) {
    enum class Change { START, END }
    private val states = linkedMapOf<Int, Int?>()
    var ringing: Boolean = initialRinging
        private set
    val ready: Boolean get() = states.isNotEmpty() && states.values.all { it != null }

    fun watch(subscriptionIds: Set<Int>) {
        states.clear()
        subscriptionIds.forEach { states[it] = null }
    }

    fun update(subscriptionId: Int, state: Int): Change? {
        if (!states.containsKey(subscriptionId)) return null
        states[subscriptionId] = state
        // Wait for all initial callbacks: an idle SIM must not end another SIM's restored call.
        if (!ready) return null
        val next = states.values.any { it == 1 } // TelephonyManager.CALL_STATE_RINGING
        if (next == ringing) return null
        ringing = next
        return if (next) Change.START else Change.END
    }
}
