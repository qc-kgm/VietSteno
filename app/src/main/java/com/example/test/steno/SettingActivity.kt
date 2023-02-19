package com.example.test.steno

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test.steno.utils.AppPrefs
import com.example.test.steno.utils.AppPrefs.set
import com.example.test.steno.utils.AppPrefs.get
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val sharedPreferences = AppPrefs.customPrefs(this,"VIET_STENO")
        cbHint.setOnCheckedChangeListener { compoundButton, b ->
            sharedPreferences["ENABLE_HINT"] = !b
        }
        cbHint.isChecked = !(sharedPreferences["ENABLE_HINT",true] ?: true)
    }
}
