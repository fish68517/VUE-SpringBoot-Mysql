package com.example.callmarkcollector.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.callmarkcollector.data.CaptureRepository
import com.example.callmarkcollector.network.CallRecordUploader
import org.json.JSONObject

class HistoryActivity : AppCompatActivity() {
    private lateinit var list: ListView
    private lateinit var title: TextView
    private lateinit var previous: Button
    private lateinit var next: Button
    private var page = 0
    private var rows = emptyList<JSONObject>()
    private var detailId: String? = null
    private var detailText: TextView? = null
    private var detailDialog: AlertDialog? = null
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            refresh()
            refreshDetail()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = savedInstanceState?.getInt("page") ?: 0
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val padding = (16 * resources.displayMetrics.density).toInt()
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(padding + bars.left, bars.top, padding + bars.right, bars.bottom)
            insets
        }
        title = TextView(this).apply { textSize = 21f; setPadding(0, padding, 0, padding) }
        root.addView(title)
        val buttons = LinearLayout(this)
        buttons.addView(Button(this).apply { text = "返回"; setOnClickListener { finish() } })
        buttons.addView(Button(this).apply {
            text = "清空记录"
            setOnClickListener {
                AlertDialog.Builder(this@HistoryActivity).setTitle("清空所有本地记录？")
                    .setMessage("已上传到服务器的数据不受影响。待上传记录会取消发送，正在发送的请求无法撤回。")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("清空") { _, _ ->
                        CaptureRepository.deleteAll(this@HistoryActivity)
                        page = 0
                        refresh()
                    }.show()
            }
        })
        root.addView(buttons)
        list = ListView(this)
        root.addView(list, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        list.setOnItemClickListener { _, _, position, _ -> showDetail(rows[position].getString("recordId")) }
        val pager = LinearLayout(this)
        previous = Button(this).apply { text = "上一页"; setOnClickListener { page--; refresh() } }
        next = Button(this).apply { text = "下一页"; setOnClickListener { page++; refresh() } }
        pager.addView(previous)
        pager.addView(next)
        root.addView(pager)
        setContentView(root)
        ViewCompat.requestApplyInsets(root)
        CallRecordUploader.initialize(this)
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter(CaptureRepository.ACTION_CAPTURE_UPDATED), RECEIVER_NOT_EXPORTED)
        refresh()
    }

    override fun onStop() {
        unregisterReceiver(receiver)
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("page", page)
        super.onSaveInstanceState(outState)
    }

    private fun refresh() {
        val count = CaptureRepository.count(this)
        page = page.coerceIn(0, ((count - 1).coerceAtLeast(0) / PAGE_SIZE))
        rows = CaptureRepository.getHistory(this, PAGE_SIZE, page * PAGE_SIZE)
        title.text = if (count == 0) "来电历史：暂无记录" else "来电历史：" + count + " 条（第 " + (page + 1) + " 页）"
        list.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, rows.map(RecordFormatter::summary))
        previous.isEnabled = page > 0
        next.isEnabled = (page + 1) * PAGE_SIZE < count
    }

    private fun showDetail(id: String) {
        val record = CaptureRepository.getRecord(this, id) ?: return
        detailId = id
        val padding = (18 * resources.displayMetrics.density).toInt()
        val text = TextView(this).apply {
            textSize = 15f
            setTextIsSelectable(true)
            setPadding(padding, padding, padding, padding)
            this.text = RecordFormatter.details(record)
        }
        detailText = text
        val scroll = ScrollView(this).apply { addView(text) }
        detailDialog = AlertDialog.Builder(this).setTitle("来电采集详情")
            .setView(scroll).setPositiveButton("关闭", null)
            .setNegativeButton("删除") { _, _ ->
                AlertDialog.Builder(this).setTitle("删除这条本地记录？")
                    .setMessage("已上传的数据不会从服务器删除；正在发送的请求无法撤回。")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("删除") { _, _ -> CaptureRepository.deleteRecord(this, id); refresh() }
                    .show()
            }
            .setNeutralButton("重试上传", null).create().also { dialog ->
                dialog.setOnDismissListener { detailId = null; detailText = null; detailDialog = null }
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                        if (CaptureRepository.getEndpoint(this).isBlank()) {
                            Toast.makeText(this, "请先返回首页配置接口地址", Toast.LENGTH_SHORT).show()
                        } else {
                            CallRecordUploader.retry(this, id)
                            refreshDetail()
                        }
                    }
                    refreshDetail()
                }
                dialog.show()
            }
        refreshDetail()
    }

    private fun refreshDetail() {
        val id = detailId ?: return
        val record = CaptureRepository.getRecord(this, id)
        if (record == null) { detailDialog?.dismiss(); return }
        detailText?.text = RecordFormatter.details(record)
        detailDialog?.getButton(AlertDialog.BUTTON_NEUTRAL)?.isEnabled =
            record.optString("uploadState") in setOf("FAILED", "UNCONFIGURED", "INTERRUPTED")
    }

    companion object { private const val PAGE_SIZE = 50 }
}
