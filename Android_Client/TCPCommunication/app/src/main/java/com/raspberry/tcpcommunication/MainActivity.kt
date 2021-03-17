package com.raspberry.tcpcommunication

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.raspberry.tcpcommunication.TcpClient.OnMessageReceived

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var mTcpClient: TcpClient? = null
    lateinit var connectDisconnect: Button
    lateinit var sendMsgButton: Button
    lateinit var editMsg: EditText
    lateinit var msgList: LinearLayout
    lateinit var connectionStatus: TextView
    lateinit var scrollView: ScrollView
    private var red = 0
    private var green = 0
    private var white = 0
    private var connected = 0

    enum class Alignment {
        LEFT, RIGHT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this.supportActionBar != null) this.supportActionBar!!.hide()
        setContentView(R.layout.activity_main)

        connectDisconnect = findViewById(R.id.connectDisconnect)
        sendMsgButton = findViewById(R.id.send_data)

        connectionStatus = findViewById(R.id.connectionStatus)
        editMsg = findViewById(R.id.edMessage)
        msgList = findViewById(R.id.msgList)
        scrollView = findViewById(R.id.scrollView1)

        red = ContextCompat.getColor(this, R.color.red)
        green = ContextCompat.getColor(this, R.color.green)
        white = ContextCompat.getColor(this, R.color.white)

        connectDisconnect.setOnClickListener(this)
        sendMsgButton.setOnClickListener(this)
    }

    fun textView(msg: String?, value: Alignment): TextView {
        var msg = msg
        if (msg == null) {
            msg = "<Empty Message>"
        }
        val tv = TextView(this)
        tv.text = msg
        tv.textSize = 18F
        tv.setPadding(0, 20, 0, 0)
        tv.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        if (value == Alignment.LEFT) {
            tv.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        } else if (value == Alignment.RIGHT) {
            tv.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
        }
        return tv
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.connectDisconnect -> if (connected == 0) {
                ConnectTask().execute("")
                connectionStatus.text = "CONNECTED to Server(Raspberry Pi)"
                connectionStatus.setTextColor(green)
                connectDisconnect.text = "DISCONNECT"
                connected = 1
            } else {
                if (mTcpClient != null) {
                    mTcpClient!!.stopClient()
                    connectionStatus.text = "Server DISCONNECTED"
                    connectionStatus.setTextColor(red)
                }
                connectDisconnect.text = "CONNECT TO SERVER"
                connected = 0
            }
            R.id.send_data -> if (mTcpClient != null) {
                if (connected == 1) {
                    val msg = editMsg.text.toString()
                    if (msg != "") {
                        mTcpClient!!.sendMessage(msg)
                        msgList.addView(textView(msg, Alignment.RIGHT))
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                        editMsg.setText("")
                    }
                } else {
                    Toast.makeText(this, "Connect to Server first", Toast.LENGTH_SHORT).show()
                }
            }
            }
    }

    inner class ConnectTask : AsyncTask<String?, String?, TcpClient?>() {

        override fun doInBackground(vararg p0: String?): TcpClient? {
            //we create a TCPClient object
            mTcpClient = TcpClient(object : OnMessageReceived {
                override fun messageReceived(message: String?) {
                    //here the messageReceived method is implemented
                    //this method calls the onProgressUpdate
                    publishProgress(message)
                }
            })
            mTcpClient!!.run()
            return null
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            //response received from server
            Log.d("test", "response " + values[0])
            msgList.addView(textView(values[0], Alignment.LEFT))
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }


    }
}