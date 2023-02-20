package com.example.test.steno

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test.steno.utils.AppPrefs
import com.example.test.steno.utils.AppPrefs.set
import com.example.test.steno.utils.AppPrefs.get
import kotlinx.android.synthetic.main.activity_start.*
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class StartActivity : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences
    var isDev = false
    var listFirst = mutableListOf<Pair<String, String>>() // list am dau-am chinh
    var listTotal = mutableListOf<Pair<String, String>>() // list cac tu day du
    var listDictSource = mutableListOf<String>() // luu tat ca cac tu co trong tu dien ban dau (v1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        sharedPreferences = AppPrefs.customPrefs(this,"VIET_STENO")
        // set property for lib apache poi
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        )
        loadData()
        btnTyping.setOnClickListener {
            goToMain()
        }
        btnSetting.setOnClickListener {
            goToSetting()
        }
    }

    fun loadData(){
        if(sharedPreferences["first_use",true] == true){
            sharedPreferences["first_use"] = false
            readInputADAC()
        }

    }



    fun goToMain(){
        startActivity(Intent(this,MainActivity::class.java))
    }
    fun goToSetting(){
        startActivity(Intent(this,SettingActivity::class.java))
    }


    fun readInputADAC(): Sheet {
        val inputStream = this.resources.openRawResource(R.raw.dataoutadac) // file lưu các cặp âm đầu - âm chính
//        val inputDictionary = this.resources.openRawResource(R.raw.tudienfinal)
        val inputSourceV1 =
            this.resources.openRawResource(R.raw.tudiensource) // tu dien chua cac tu theo bo luat ban dau (v1)
        val inputSource =
            this.resources.openRawResource(R.raw.allword4) // tu dien chua cac tu theo bo luat moi (v2)

//        var xlWb = WorkbookFactory.create(inputStream)
        val xlWb = XSSFWorkbook(inputStream)
        val xlWbDict = XSSFWorkbook(inputSource)
        val xlWbDictSource = XSSFWorkbook(inputSourceV1)

        val xlWs = xlWb.getSheetAt(0)
        for (i in 0..xlWs.lastRowNum) {
            val row = xlWs.getRow(i)
            listFirst.add(Pair(row.getCell(7).stringCellValue, row.getCell(8).stringCellValue))
            sharedPreferences.set(row.getCell(7).stringCellValue,row.getCell(8).stringCellValue)
        }
        val dictionary = xlWbDict.getSheetAt(0)
        for (i in 0..dictionary.lastRowNum) {
            val row = dictionary.getRow(i)
            listTotal.add(Pair(row.getCell(1).stringCellValue, row.getCell(0).stringCellValue))
            sharedPreferences.set(row.getCell(1).stringCellValue,row.getCell(0).stringCellValue)
        }

        // code doc tu dien ban dau v1
        if(isDev) {
            val dictionarySource = xlWbDictSource.getSheetAt(0)
            for (i in 0..dictionarySource.lastRowNum) {
                val row = dictionarySource.getRow(i)
                listDictSource.add(row.getCell(1).stringCellValue)
            }
        }
        return xlWs
    }

    fun sosanh(){ // ham dung de test
        var listS = mutableListOf<String>()
        var listD = mutableListOf<String>()
        val inputSourceV1 =
            this.resources.openRawResource(R.raw.tudiensource) // tu dien chua cac tu theo bo luat ban dau (v1)
        val inputSource =
            this.resources.openRawResource(R.raw.allword4)
        val xlWbDict = XSSFWorkbook(inputSource)
        val xlWbDictSource = XSSFWorkbook(inputSourceV1)
        val xlWs = xlWbDictSource.getSheetAt(0)
        for (i in 0..xlWs.lastRowNum) {
            val row = xlWs.getRow(i)
            listS.add(row.getCell(1).stringCellValue)
//            sharedPreferences.set(row.getCell(7).stringCellValue,row.getCell(8).stringCellValue)
        }
        val dictionary = xlWbDict.getSheetAt(0)
        for (i in 0..dictionary.lastRowNum) {
            val row = dictionary.getRow(i)
            listD.add(row.getCell(0).stringCellValue)
//            sharedPreferences.set(row.getCell(1).stringCellValue,row.getCell(0).stringCellValue)
        }
        var listM = listS.filterNot { listD.contains(it) }
        listM.forEach{
            println(it)
        }
    }

}