package com.example.nfc

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.example.nfc.databinding.ActivityMainBinding
import me.hgj.jetpackmvvm.base.activity.BaseVmDbActivity

class MainActivity : BaseVmDbActivity<MainViewModel, ActivityMainBinding>() {

    private val TAG = "MainActivity"

    private val mNfcAdapter: NfcAdapter by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }

    private var nfcA: NfcA? = null

    override fun createObserver() {
    }

    override fun dismissLoading() {
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
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

        //写
        mDatabind.btWhite.setOnClickListener {
            val byteValues = mDatabind.inputText.text.toString().split(" ").map { hexString ->
                hexString.substring(2).toInt(16).toByte()
            }.toByteArray()
            Log.d(TAG, "initView: ${byteValues.contentToString()}")
            nfcA?.let {
                //DC-DC enable
                setStatusBody("dcdc enable")
                if (sendAndRes(it, byteValues)) return@setOnClickListener //for Voltage stability

            }
        }

        mDatabind.btWhite1.setOnClickListener {
            val byteValues = mDatabind.inputText1.text.toString().split(" ").map { hexString ->
                hexString.substring(2).toInt(16).toByte()
            }.toByteArray()
            nfcA?.let {
                setStatusBody("Screen reset")
                if (sendAndRes(it, byteValues)) return@setOnClickListener //for Voltage stability


            }

        }

        mDatabind.btWhite2.setOnClickListener {
            val byteValues = mDatabind.inputText2.text.toString().split(" ").map { hexString ->
                hexString.substring(2).toInt(16).toByte()
            }.toByteArray()
            nfcA?.let {
                setStatusBody("Screen working")
                if (sendAndRes(it, byteValues)) return@setOnClickListener //for Voltage stability
            }

        }



        mDatabind.btWhite3.setOnClickListener {
            val byteValues = mDatabind.inputText3.text.toString().split(" ").map { hexString ->
                hexString.substring(2).toInt(16).toByte()
            }.toByteArray()
            nfcA?.let {
                setStatusBody("power init")
                if (sendAndRes(it, byteValues)) return@setOnClickListener //for Voltage stability
//for Voltage stability

            }

        }
        mDatabind.btWhite4.setOnClickListener {
            val byteValues = mDatabind.inputText4.text.toString().split(" ").map { hexString ->
                hexString.substring(2).toInt(16).toByte()
            }.toByteArray()
            nfcA?.let {
                setStatusBody("power init")
                if (sendAndRes(it, byteValues)) return@setOnClickListener //for Voltage stability

            }

        }
        mDatabind.btWhite5.setOnClickListener {
            val byteValues = mDatabind.inputText5.text.toString().split(" ").map { hexString ->
                hexString.substring(2).toInt(16).toByte()
            }.toByteArray()
            nfcA?.let {
                setStatusBody("power init")
                if (sendAndRes(it, byteValues)) return@setOnClickListener //for Voltage stability

            }

        }
        mDatabind.btWhite6.setOnClickListener {
            val byteValues = mDatabind.inputText6.text.toString().split(" ").map { hexString ->
                hexString.substring(2).toInt(16).toByte()
            }.toByteArray()
            nfcA?.let {
                setStatusBody("power init")
                if (sendAndRes(it, byteValues)) return@setOnClickListener //for Voltage stability

            }

        }
        mDatabind.btWhite7.setOnClickListener {
            val byteValues = mDatabind.inputText7.text.toString().split(" ").map { hexString ->
                hexString.substring(2).toInt(16).toByte()
            }.toByteArray()
            nfcA?.let {
                setStatusBody("power init")
                if (sendAndRes(it, byteValues)) return@setOnClickListener //for Voltage stability

            }

        }


        mDatabind.btWhite8.setOnClickListener {
            val byteValues = mDatabind.inputText8.text.toString().split(" ").map { hexString ->
                hexString.substring(2).toInt(16).toByte()
            }.toByteArray()
            nfcA?.let {
                setStatusBody("picture datas transfer command")
                if (sendAndRes(it, byteValues)) return@setOnClickListener //for Voltage stability

            }

        }
    }

    private fun sendAndRes(it: NfcA, byteValues: ByteArray): Boolean {
        val response = it.transceive(byteValues)
        Log.d(TAG, "writeTag: ${response.contentToString()}")

        if (response[0] != 0x00.toByte() || response[1] != 0x00.toByte()) {
            setStatusBody("Updating failed!")
            return true
        }
        setResponseBody(response)
        SystemClock.sleep(10)
        return false
    }

    private fun setResponseBody(response: ByteArray?) {
        mDatabind.tvResponse.text = "response:  ${response.contentToString()}"
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

        if (tech != null) {
            if (tech[1] == "android.nfc.tech.NfcA") {
                nfcA = NfcA.get(tag)
                nfcA?.let {
                    it.connect()
                    it.timeout = 50
                }
                SystemClock.sleep(10)
            }
        }
        Log.d(TAG, "writeTag: $nfcA")

    }

    private fun setStatusBody(s: String) {
        runOnUiThread {
            mDatabind.tvState.text = "状态:   $s"
        }
    }
}