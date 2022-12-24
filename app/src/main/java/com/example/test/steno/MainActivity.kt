package com.example.test.steno

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class MainActivity : AppCompatActivity() {
    var textFinal: MutableLiveData<String> = MutableLiveData("")
    var now: String = ""
    var state = 0 // 0 : trong -- 1: da co am dau va am chinh
    var code :String =""

    var x = "" // ki tu hien tai ben trai
    var y = "" // ki tu hien tai ben phai

    var stateX = false
    var stateY = false

    var a = "" // vi tri hien tai cua joy3
    var b = "" // vi tri hien tai cua joy2
    var c = "" // vi tri hien tai cua joy1
    var d = "" // vi tri hien tai cua joy6
    var e = "" // vi tri hien tai cua joy5
    var f = "" // vi tri hien tai cua joy4
    var listFromExcel = mutableListOf<String>()
    var listDataOut = mutableListOf<Pair<String, String>>()
    var listFirst = mutableListOf<Pair<String, String>>()
    var listTotal = mutableListOf<Pair<String, String>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initHashMap()
        setupUI()
        setupOnMove()
        btnSpace.setOnClickListener {
            state = 0
            textFinal.value = tvContent.text.toString()+" "
        }

        // set property apache poi
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
        textFinal.observe(this, {
            tvContent.text = it
        })
//        val sheet = readFromExcelFile("tonghopADAC.xlsx")
//        initDataUsed()
//        writeData()
//        println(listFromExcel)
//        println(sheet.lastRowNum)
//        println(sheet.firstRowNum)
//        println("value : ${sheet.getRow(1).getCell(3).stringCellValue}")
//        println("value : ${sheet.getRow(1).firstCellNum}")
        readInputADAC()

    }

    fun setupUI() {
        joy1.setBorderColor(Color.GREEN)
        joy4.setBorderColor(Color.GREEN)
//        joy5.setBorderColor(Color.BLUE)
//        joy2.setBorderColor(Color.BLUE)
//        joy3.setBorderColor(Color.BLUE)
//        joy6.setBorderColor(Color.BLUE)
        joy1.setOnMoveListener { angle, strength ->
            Log.v("TAG", "angle : $angle")
            Log.v("TAG", "strength : $strength")
        }
//        joy1.buttonSizeRatio = 0.3f
//        joy2.buttonSizeRatio = 0.3f
//        joy3.buttonSizeRatio = 0.3f
//        joy4.buttonSizeRatio = 0.3f
        joy2.setFixedCenter(false)
        joy5.setFixedCenter(false)
        btnClear.setOnClickListener {
            var textCurrent = (tvContent.text.toString() ?: "").trim()
            textCurrent = textCurrent.substringBeforeLast(" ","")
            if (textCurrent != "") textCurrent = textCurrent+" "
            textFinal.value = textCurrent
            state = 0

        }
    }

    fun setupOnMove() {
        joy1.setOnMoveListener { angle, strength ->
            if (strength == 0 && stateY) {
                stateX = false
                if(stateY) {
                    state++
                    getString()
                }
                x = ""
            }
            if (strength > 70) {
                stateX = true
                when (true) {
                    angle >= 350 || angle <= 10 -> x = "c1"
                    angle in 35..55 -> x = "c2"
                    angle in 80..100 -> x = "c3"
                    angle in 125..145 -> x = "c4"
                    angle in 170..190 -> x = "c5"
                    angle in 215..235 -> x = "c6"
                    angle in 260..280 -> x = "c7"
                    angle in 305..325 -> x = "c8"
                }
            }
        }
        joy2.setOnMoveListener {     angle, strength ->
            if (strength == 0 && stateY) {
                stateX = false
                if(stateY) {
                    state++
                    getString()
                }
                x = ""
//                val key = a.plus(b)
//                a = ""
//                val res = hashmap.getOrDefault(key, "")
//                if (res.isNotEmpty()) textFinal.value = textFinal.value.plus(res).plus(" ")
            }
            if (strength > 70) {
                stateX = true
                when (true) {
                    angle >= 350 || angle <= 10 -> x = "b1"
                    angle in 35..55 -> x = "b2"
                    angle in 80..100 -> x = "b3"
                    angle in 125..145 -> x = "b4"
                    angle in 170..190 -> x = "b5"
                    angle in 215..235 -> x = "b6"
                    angle in 260..280 -> x = "b7"
                    angle in 305..325 -> x = "b8"
                }
            }
        }
        joy3.setOnMoveListener { angle, strength ->
            if (strength == 0 && stateY) {
                stateX = false
                if(stateY) {
                    state++
                    getString()
                }
                x = ""
            }
            if (strength > 70) {
                stateX = true
                when (true) {
                    angle >= 350 || angle <= 10 -> x = "a1"
                    angle in 35..55 -> x = "a2"
                    angle in 80..100 -> x = "a3"
                    angle in 125..145 -> x = "a4"
                    angle in 170..190 -> x = "a5"
                    angle in 215..235 -> x = "a6"
                    angle in 260..280 -> x = "a7"
                    angle in 305..325 -> x = "a8"
                }
            }
        }
        joy4.setOnMoveListener { angle, strength ->
            if (strength == 0 && stateY) {
                stateY = false
                if(stateX) {
                    state++
                    getString()
                }
                y = ""
            }
            if (strength > 70) {
                stateY = true
                when (true) {
                    angle >= 350 || angle <= 10 -> y = "f1"
                    angle in 35..55 -> y = "f2"
                    angle in 80..100 -> y = "f3"
                    angle in 125..145 -> y = "f4"
                    angle in 170..190 -> y = "f5"
                    angle in 215..235 -> y = "f6"
                    angle in 260..280 -> y = "f7"
                    angle in 305..325 -> y = "f8"
                }
            }
        }
        joy5.setOnMoveListener { angle, strength ->
            if (strength == 0 && stateY) {
                stateY = false
                if(stateX) {
                    state++
                    getString()
                }
                y = ""
            }
            if (strength > 70) {
                stateY = true
                when (true) {
                    angle >= 350 || angle <= 10 -> y = "e1"
                    angle in 35..55 -> y = "e2"
                    angle in 80..100 -> y = "e3"
                    angle in 125..145 -> y = "e4"
                    angle in 170..190 -> y = "e5"
                    angle in 215..235 -> y = "e6"
                    angle in 260..280 -> y = "e7"
                    angle in 305..325 -> y = "e8"
                }
            }
        }
        joy6.setOnMoveListener { angle, strength ->
            if (strength == 0 && stateY) {
                stateY = false
                if(stateX) {
                    state++
                    getString()
                }
                y = ""
            }
            if (strength > 70) {
                stateY = true
                when (true) {
                    angle >= 350 || angle <= 10 -> y = "d1"
                    angle in 35..55 -> y = "d2"
                    angle in 80..100 -> y = "d3"
                    angle in 125..145 -> y = "d4"
                    angle in 170..190 -> y = "d5"
                    angle in 215..235 -> y = "d6"
                    angle in 260..280 -> y = "d7"
                    angle in 305..325 -> y = "d8"
                }
            }
        }
    }

    var hashmap = HashMap<String, String>()
    var listText = listOf<String>(
        "sa", "si", "se", "su", "sư", "sô", "so", "sưa", "suy", "sua", "sơ",
        "ta", "to", "tơ", "te", "ti", "tia", "tu", "tư", "tô", "toa", "tuê", "tuy", "tua", "toe",
        "ca", "cơ", "ke", "kê", "kia", "cu", "cưa", "cư", "cô", "co",
        "ha", "hơ", "he", "hê", "hi", "hu", "hưa", "hư", "hô", "hoa", "ho", "huê", "huy", "hoe",
        "ra", "rơ", "ri", "re", "rê", "ria", "ru", "rưa", "rô", "ro", "rua",
        "ba", "bơ", "bi", "be", "bê", "bia", "bưa", "bu", "bô", "bo", "bua"
    )

    fun getString(){
        if(state == 1) {
            code = x.plus(y)
            // get text ung voi trang thai chi co am dau - am chinh
            now = listFirst.find { it.first == code }?.second ?: ""
            if(now == "") {
                state = 0
                code = ""
            } else tvContent.text = textFinal.value + now

        }
        if(state == 2) {
            code = code+x+y
            val result = listTotal.find { it.first == code }?.second ?: ""
            if(result == ""){
                textFinal.value = tvContent.text.toString()+ " "
            } else {
                textFinal.value = textFinal.value + result + " "
            }
            // get text ung voi truong hop co ca am dau - am chinh - am cuoi
            code = ""
            state = 0
        }
    }

    fun initData(){

    }


    fun initHashMap() {
        hashmap.apply {
            for (i in 1..8) {
                for (j in 1..8) {
                    val key = "a".plus(i).plus("b".plus(j))
                    val index = (i - 1) * 8 + j
                    put(key, listText[index])
                }
            }
//            put("a1b1","")
//            put("a1b2","")
//            put("a1b3","")
//            put("a1b1","")
//            put("a1b1","")
//            put("a1b1","")
//            put("a1b1","")
//            put("a1b1","")
//            put("a1b1","")
//            put("a1b1","")
//            put("a1b1","")
//            put("a1b1","")
        }
    }

    fun readFromExcelFile(filepath: String): Sheet {
        val manager = this.assets
//        val inputStream = this.assets.open(filepath)
        val inputStream = this.resources.openRawResource(R.raw.tonghop)
        //Instantiate Excel workbook using existing file:
//        var xlWb = WorkbookFactory.create(inputStream)
        val xlWb = XSSFWorkbook(inputStream)

        //Row index specifies the row in the worksheet (starting at 0):
        val rowNumber = 0
        //Cell index specifies the column within the chosen row (starting at 0):
        val columnNumber = 0

        //Get reference to first sheet:
        val xlWs = xlWb.getSheetAt(0)
        println(xlWs.lastRowNum)
        println(xlWs.firstRowNum)
        for (i in 0..xlWs.lastRowNum) {
            val row = xlWs.getRow(i)
            listFromExcel.add(row.getCell(3).stringCellValue)
            listDataOut.add(Pair("hihi" , row.getCell(3).stringCellValue))
//            val cell0 = row.getCell(0)
//            val cell1 = row.getCell(1)
//            cell0.setCellValue(listDataOut[i].first)
//            cell1.setCellValue(listDataOut[i].second)
        }
        return xlWs
    }

    fun readInputADAC(): Sheet {
        val manager = this.assets
//        val inputStream = this.assets.open(filepath)
        val inputStream = this.resources.openRawResource(R.raw.dataoutadac)
        //Instantiate Excel workbook using existing file:
//        var xlWb = WorkbookFactory.create(inputStream)
        val xlWb = XSSFWorkbook(inputStream)

        //Row index specifies the row in the worksheet (starting at 0):
        val rowNumber = 0
        //Cell index specifies the column within the chosen row (starting at 0):
        val columnNumber = 0

        //Get reference to first sheet:
        val xlWs = xlWb.getSheetAt(0)
        println(xlWs.lastRowNum)
        println(xlWs.firstRowNum)
        for (i in 0..xlWs.lastRowNum) {
            val row = xlWs.getRow(i)
            listFirst.add(Pair(row.getCell(0).stringCellValue,row.getCell(1).stringCellValue))
//            listFromExcel.add(row.getCell(3).stringCellValue)
//            listDataOut.add(Pair("hihi" , row.getCell(3).stringCellValue))
//            val cell0 = row.getCell(0)
//            val cell1 = row.getCell(1)
//            cell0.setCellValue(listDataOut[i].first)
//            cell1.setCellValue(listDataOut[i].second)
        }
        for(i in 0..84){
            val row = xlWs.getRow(i)
            listTotal.add(Pair(row.getCell(4).stringCellValue , row.getCell(3).stringCellValue))
        }
        return xlWs
    }

    fun initDataUsed(){
        var index = 0
        val max = listDataOut.size
        for( a in listOf<String>("a","b")){
            for( b in listOf<String>("d","e")){
                for (i in 1..8) {
                    for (j in 1..8) {
                        val key = a.plus(i).plus(b.plus(j))
                        listDataOut[index] = Pair(key,listDataOut[index].second)
                        index++
                    }
                }
            }
        }

        for( a in listOf<String>("a","b")){
            for( b in listOf<String>("f")){
                for (i in 1..8) {
                    for (j in 1..5) {
                        val key = a.plus(i).plus(b.plus(j))
                        listDataOut[index] = Pair(key,listDataOut[index].second)
                        index++
                    }
                }
            }
        }
        for( a in listOf<String>("d","e")){
            for( b in listOf<String>("c")){
                for (i in 1..8) {
                    for (j in 1..5) {
                        if(index < max) {
                            val key = a.plus(i).plus(b.plus(j))
                            listDataOut[index] = Pair(key, listDataOut[index].second)
                            index++
                        }
                    }
                }
            }
        }



    }

    fun writeData() {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("ADAC")
        for (i in 0..listDataOut.size - 1) {
            val row = sheet.createRow(i)
            val cell0 = row.createCell(0)
            val cell1 = row.createCell(1)
            cell0.setCellValue(listDataOut[i].first)
            cell1.setCellValue(listDataOut[i].second)
        }
        val t = openFileOutput("tonghop2.xlsx", 0)
//        OutputStreamWriter(openFileOutput("tonghop2",0)).write()
        workbook.write(t)
        t.close()

    }

    fun writeData2(sheet: Sheet) {
        val workbook = XSSFWorkbook()
        val sheet2 = workbook.createSheet()
        for (i in 0..listDataOut.size - 1) {
            val row = sheet.getRow(i)
            val cell0 = sheet2.createRow(i).createCell(0)
            val cell1 = sheet2.createRow(i).createCell(1)
            cell0.setCellValue(listDataOut[i].first)
            cell1.setCellValue(listDataOut[i].second)
        }


        val t = openFileOutput("tonghop2", 0)
//        OutputStreamWriter(openFileOutput("tonghop2",0)).write()
        workbook.write(t)
        t.close()
        return
    }
//    fun createExcel(workbook: Workbook) {
//
//        //Get App Director, APP_DIRECTORY_NAME is a string
//        val t = Environment.getExternalStorageDirectory()
//        val appDirectory = this.getExternalFilesDir(Constants.APP_DIRECTORY_NAME)
//
//        //Check App Directory whether it exists or not, create if not.
//        if (appDirectory != null && !appDirectory.exists()) {
//            appDirectory.mkdirs()
//        }
//
//        //Create excel file with extension .xlsx
//        val excelFile = File(appDirectory,Constants.FILE_NAME)
//
//        //Write workbook to file using FileOutputStream
//        try {
//            val fileOut = FileOutputStream(excelFile)
//            workbook.write(fileOut)
//            fileOut.close()
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }


}