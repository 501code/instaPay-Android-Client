package org.a501code.instapay

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import org.a501code.instapay.R
import org.a501code.instapay.barcode.BarcodeCaptureActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mResultTextView: TextView
    private lateinit var mPhonenumberTextView: TextView
    private var b: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mResultTextView = findViewById(R.id.result_textview)
        mPhonenumberTextView = findViewById(R.id.phone_number)

        findViewById<Button>(R.id.scan_barcode_button).setOnClickListener {
            val intent = Intent(applicationContext, BarcodeCaptureActivity::class.java)
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE)
        }

        findViewById<Button>(R.id.make_payment).setOnClickListener{
            if(b == true && mPhonenumberTextView.text.isNotEmpty()) {
                // Make request using volley
                val queue = Volley.newRequestQueue(this)
                val url = "https://c8304c38.ngrok.io/merchant/payment?phone_number=" + mPhonenumberTextView.text + "&item_id=" + mResultTextView.text
                val stringRequest = StringRequest(Request.Method.GET, url,
                        Response.Listener<String> { response ->
                            mResultTextView.text = response.toString()
                        },
                        Response.ErrorListener { mResultTextView.text = "Error" })
                queue.add(stringRequest)
            }else{
                Toast.makeText(this, "Make sure you have entered the phone number and that your QR code scan was successful", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode = data.getParcelableExtra<Barcode>(BarcodeCaptureActivity.BarcodeObject)
                    val p = barcode.cornerPoints
                    b = true
                    mResultTextView.text = barcode.displayValue
                } else
                    mResultTextView.setText(R.string.no_barcode_captured)
            } else
                Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                        CommonStatusCodes.getStatusCodeString(resultCode)))
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
        private val BARCODE_READER_REQUEST_CODE = 1
    }
}
