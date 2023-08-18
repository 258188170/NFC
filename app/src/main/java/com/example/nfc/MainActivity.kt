package com.example.nfc

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.example.nfc.ByteUtils.hexToBytes
import com.example.nfc.ByteUtils.toHexString
import com.example.nfc.databinding.ActivityMainBinding
import me.hgj.jetpackmvvm.base.activity.BaseVmDbActivity

class MainActivity : BaseVmDbActivity<MainViewModel, ActivityMainBinding>() {

    companion object {
        const val ISO_DEP: String = "android.nfc.tech.IsoDep"
        const val TAG = "MainActivity"

    }

    private val mNfcAdapter: NfcAdapter by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }

    private var isoDep: IsoDep? = null

    override fun createObserver() {
    }

    override fun dismissLoading() {
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)

    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter.disableForegroundDispatch(this)
    }

    override fun initView(savedInstanceState: Bundle?) {
        if (!mNfcAdapter.isEnabled) {
            // 设备不支持NFC或NFC未启用
            ToastUtils.showShort("设备不支持NFC或NFC未启用")
        } else {
            // 设备支持NFC且NFC已启用
            Log.d(TAG, "initView: 设备支持NFC且NFC已启用")

        }

    }

    private fun sendAndRes(it: NfcA, byteValues: ByteArray): Boolean {
        val response = it.transceive(byteValues)
        Log.d(TAG, "writeTag: ${response.contentToString()}")

        if (response[0] != 0x00.toByte() || response[1] != 0x00.toByte()) {
            return true
        }
        SystemClock.sleep(10)
        return false
    }


    override fun showLoading(message: String) {
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: ${intent?.action}")
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            // 处理标签数据
            Log.d(TAG, "onNewIntent: ${tag}")
            if (tag == null) {
                Log.d(TAG, "onNewIntent: tag is null")
                return
            }
            writeTag(tag)
        }

    }

    private fun writeTag(tag: Tag?) {
        val tech: Array<out String>? = tag?.techList
        try {
            if (tech != null) {
                Log.d(TAG, "writeTag: ${tech[1]}")
                if (tech[0] == ISO_DEP) {
                    isoDep = IsoDep.get(tag)
                    isoDep?.let {
                        it.connect()
                        Log.d(TAG, "writeTag: connect")
                        val transceive = it.transceive("0084000004".hexToBytes())
                        Log.d(TAG, "writeTag: ${transceive.toHexString()}")
                        val transceive1 = it.transceive("00a4000002DF04".hexToBytes())
                        Log.d(TAG, "writeTag: ${transceive1.toHexString()}")
                        val transceive2 = it.transceive("00a4000002ef01".hexToBytes())
                        Log.d(TAG, "writeTag: ${transceive2.toHexString()}")
                        val transceive3 = it.transceive("00b00000f0".hexToBytes())
                        Log.d(TAG, "writeTag: ${transceive3.toHexString()}")
                    }
                    SystemClock.sleep(10)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isoDep?.close()
        }
    }
}