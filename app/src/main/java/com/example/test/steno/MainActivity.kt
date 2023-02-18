package com.example.test.steno

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class MainActivity : AppCompatActivity() {
    var textFinal: MutableLiveData<String> = MutableLiveData("")
    var now: String = ""
    var state = 0   // 0 : trống
    var isDev = false

    // 1: da co am dau va am chinh
    var code: String = ""
    var listPairADAC = mutableListOf<Pair<String, List<String>>>()

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
    var listFirst = mutableListOf<Pair<String, String>>() // list am dau-am chinh
    var listTotal = mutableListOf<Pair<String, String>>() // list cac tu day du
    var listDictSource = mutableListOf<String>() // luu tat ca cac tu co trong tu dien ban dau (v1)
    var listDataHint =
        mutableListOf<Pair<String, List<String>>>() // luu cac cap am dau- am chinh dung de hien thi goi y

    // khoi tao bien tam de luu gia tri cua am dau va am chinh
    var amdau = MutableLiveData<String>("")
    var amchinh = MutableLiveData<String>("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
        setupOnMove()
        initTextHintState0()
        btnSpace.setOnClickListener {
            state = 0
            textFinal.value = tvContent.text.toString() + " "
            amdau.value = ""
        }
        listDataHint = initListPairADAC()

        amdau.observe(this, { a ->
            val listAC = listDataHint.firstOrNull { it.first == a }
            if (state == 0) {
                setVisibleAllTextViewHintAC(false)
                initTextHintState0()
                if (listAC != null) {
                    setVisibleListSuggestTextViewHintAC(listAC.second)
                }
            }
        })

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
        textFinal.observe(this, {
            tvContent.text = it
        })
        readInputADAC()
        if(isDev) gen()
    }


    fun setupUI() {
        joy2.setFixedCenter(false)
        joy5.setFixedCenter(false)
        btnClear.setOnClickListener {
            var textCurrent = (tvContent.text.toString() ?: "").trim()
            textCurrent = textCurrent.substringBeforeLast(" ", "")
            if (textCurrent != "") textCurrent = textCurrent + " "
            textFinal.value = textCurrent
            state = 0
            amdau.value = ""

        }
        btnClear.setOnLongClickListener {
            textFinal.value = ""
            state = 0
            amdau.value = ""
            true
        }
    }

    fun setVisibleAllTextViewHintAC(b: Boolean) {
        // set visible for all text view of Am Chinh suggest
        tvHintf1.isVisible = b
        tvHintf2.isVisible = b
        tvHintf3.isVisible = b
        tvHintf4.isVisible = b
        tvHintf5.isVisible = b
        tvHintf6.isVisible = b
        tvHintf7.isVisible = b
        tvHintf8.isVisible = b
        tvHinte1.isVisible = b
        tvHinte2.isVisible = b
        tvHinte3.isVisible = b
        tvHinte4.isVisible = b
        tvHinte5.isVisible = b
        tvHinte6.isVisible = b
        tvHinte7.isVisible = b
        tvHinte8.isVisible = b
        tvHintd1.isVisible = b
        tvHintd2.isVisible = b
        tvHintd3.isVisible = b
        tvHintd4.isVisible = b
        tvHintd5.isVisible = b
        tvHintd6.isVisible = b
        tvHintd7.isVisible = b
        tvHintd8.isVisible = b
    }

    // hàm tạo các cặp âm đầu , âm chính có thể có để hiển thị hint khi chọn âm chính
    fun initListPairADAC(): MutableList<Pair<String, List<String>>> {
        val a1 = Pair(
            "a1", listOf(
                "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "f7",
                "f6", "e3", "e2", "e1", "e8", "f3", "e4"
            )
        )
        val a2 = Pair(
            "a2", listOf(
                "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8",
                "f1", "f2", "f4", "f5", "f6", "f7", "f8"
            )
        )
        val a3 = Pair(
            "a3", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e7", "e8",
                "f6", "f7"
            )
        )
        val a4 = Pair(
            "a4", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e7", "e8",
                "f3", "f6", "f7"
            )
        )
        val a5 = Pair(
            "a5", listOf(
                "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e7", "e8",
                "f1", "f2", "f4", "f5", "f6", "f7"
            )
        )
        val a6 = Pair(
            "a6", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e8",
                "f2", "f5", "f6", "f7"
            )
        )
        val a7 = Pair(
            "a7", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e7", "e8",
                "f1", "f5", "f6", "f7"
            )
        )
        val a8 = Pair(
            "a8", listOf(
                "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8",
                "f1", "f3", "f2", "f5", "f6", "f7"
            )
        )
        val b1 = Pair(
            "b1", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8",
                "f2", "f4", "f5", "f6", "f7"
            )
        )
        val b2 = Pair(
            "b2", listOf(
                "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8",
                "e1", "e2", "e3", "e4", "e7", "e8",
                "f3", "f6", "f7"
            )
        )
        val b3 = Pair(
            "b3", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e8",
                "f6", "f7"
            )
        )
        val b4 = Pair(
            "b4", listOf(
                "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8",
                "f2", "f4", "f5", "f6", "f7"
            )
        )
        val b5 = Pair(
            "b5", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8",
                "f2", "f4", "f5", "f6", "f7"
            )
        )
        val b6 = Pair(
            "b6", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8",
                "f5", "f6", "f7"
            )
        )
        val b7 = Pair(
            "b7", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8",
                "f1", "f2", "f4", "f5", "f6", "f7", "f8"
            )
        )
        val b8 = Pair(
            "b8", listOf(
                "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e8",
                "f6", "f7"
            )
        )
        val c1 = Pair(
            "c1", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e7", "e8",
                "f6"
            )
        )
        val c2 = Pair(
            "c2", listOf(
                "e4", "e5",
                "f1", "f2", "f5", "f8",
                "d1"
            )
        ) // gop 2 am dau p va q lai de nhuong cho cho x
        val c3 = Pair(
            "c3", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e6", "e7", "e8", "e1",
                "f1", "f2", "f3", "f4", "f5", "f6", "f7"
            )
        )
        val c4 = Pair(
            "c4", listOf(
                "d1", "d2", "d3", "d4", "d7", "d8",
                "e1", "e2", "e3", "e8",
                "f6"
            )
        )
        val c5 = Pair(
            "c5", listOf(
                "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8",
                "f1", "f6", "f7"
            )
        )
        val c6 = Pair(
            "c6", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e7", "e8",
                "f6", "f7"
            )
        )
        val c7 = Pair(
            "c7", listOf(
                "d1", "d2", "d3", "d4", "d5", "d7", "d8",
                "e1", "e2", "e3", "e4", "e5", "e7", "e8",
                "f2", "f5", "f6", "f7"
            )
        )
        val c8 = Pair(
            "c8", listOf(
                "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8",
                "e1", "e2", "e3", "e4", "e7", "e8",
                "f6", "f7"
            )
        )
        return mutableListOf(
            a1, a2, a3, a4, a5, a6, a7, a8, b1, b2, b3, b4, b5, b6, b7, b8, c1, c2, c3, c4, c5, c6, c7, c8
        )
    }

    fun setVisibleListSuggestTextViewHintAC(list: List<String>) {
        // set visisble = true cho list cac am chinh co trong list ID
        // hien tai chi xu li khi state = 0 , tuc la dang nhap am dau , am chinh ( observer moi khi dang co state = 0 va thay doi gia tri cua am dau )
        list.forEach { t ->
            val id = resources.getIdentifier("tvHint" + t, "id", packageName)
            findViewById<TextView>(id).isVisible = true
        }
    }

    // khởi tạo các text hint cho các vị trí âm chính
    fun initTextHintState0() {
        tvHintf1.text = "uê"
        tvHintf2.text = "uyê/uya"
        tvHintf3.text = "oo"
        tvHintf4.text = "oă"
        tvHintf5.text = "uâ"
        tvHintf6.text = "ươ/ưa"
        tvHintf7.text = "ia/iê/yê"
        tvHintf8.text = "uơ"
        tvHinte1.text = "o"
        tvHinte2.text = "ư"
        tvHinte3.text = "u"
        tvHinte4.text = "ua/uô"
        tvHinte5.text = "uy"
        tvHinte6.text = "oe"
        tvHinte7.text = "oa"
        tvHinte8.text = "ô"
        tvHintd1.text = "a"
        tvHintd2.text = "ă"
        tvHintd3.text = "â"
        tvHintd4.text = "ơ"
        tvHintd5.text = "i"
        tvHintd6.text = "y"
        tvHintd7.text = "e"
        tvHintd8.text = "ê"
        tvHintc1.text = "g/gh"
        tvHintc2.text = "q/p"
        tvHintc3.text = "x"
        tvHintc4.text = "gi"
        tvHintc5.text = "--"
        tvHintc6.text = "đ"
        tvHintc7.text = "d"
        tvHintc8.text = "v"
        tvHintb1.text = "ng/ngh"
        tvHintb2.text = "m"
        tvHintb3.text = "n"
        tvHintb4.text = "l"
        tvHintb5.text = "ch"
        tvHintb6.text = "nh"
        tvHintb7.text = "kh"
        tvHintb8.text = "ph"
        tvHinta1.text = "k/c"
        tvHinta2.text = "h"
        tvHinta3.text = "r"
        tvHinta4.text = "b"
        tvHinta5.text = "th"
        tvHinta6.text = "tr"
        tvHinta7.text = "s"
        tvHinta8.text = "t"
    }

    // đặt lại các text hint cho âm cuối và thanh điệu
    fun initTextHintState1() {
        tvHintf1.text = ""
        tvHintf2.text = ""
        tvHintf3.text = ""
        tvHintf4.text = ""
        tvHintf5.text = ""
        tvHintf6.text = ""
        tvHintf7.text = ""
        tvHintf8.text = ""
        tvHinte1.text = "--"
        tvHinte2.text = "´"
        tvHinte3.text = "`"
        tvHinte4.text = Html.fromHtml("&#1374;")
        tvHinte5.text = "~"
        tvHinte6.text = "."
        tvHinte7.text = ""
        tvHinte8.text = ""
        tvHintd1.text = ""
        tvHintd2.text = ""
        tvHintd3.text = ""
        tvHintd4.text = ""
        tvHintd5.text = ""
        tvHintd6.text = ""
        tvHintd7.text = ""
        tvHintd8.text = ""
        tvHintc1.text = ""
        tvHintc2.text = ""
        tvHintc3.text = ""
        tvHintc4.text = ""
        tvHintc5.text = ""
        tvHintc6.text = ""
        tvHintc7.text = ""
        tvHintc8.text = ""
        tvHintb1.text = "m"
        tvHintb2.text = "n"
        tvHintb3.text = "o"
        tvHintb4.text = "u"
        tvHintb5.text = "i"
        tvHintb6.text = "y"
        tvHintb7.text = "ng"
        tvHintb8.text = "nh"
        tvHinta1.text = "c"
        tvHinta2.text = "p"
        tvHinta3.text = "ch"
        tvHinta4.text = "t"
        tvHinta5.text = "--"
        tvHinta6.text = ""
        tvHinta7.text = ""
        tvHinta8.text = ""
    }

    fun setupOnMove() {
        joy1.setOnMoveListener { angle, strength ->
            if (strength == 0 && stateY) {
                stateX = false
                if (stateY) {
                    state++
                    getString()
                }
                x = ""
            } else if (strength == 0) {
                stateX = false
                amdau.value = ""
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
                amdau.value = x
            }
        }
        joy2.setOnMoveListener { angle, strength ->
            if (strength == 0 && stateY) {
                stateX = false
                if (stateY) {
                    state++
                    getString()
                }
                x = ""
            } else if (strength == 0) {
                stateX = false
                amdau.value = ""
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
                amdau.value = x
            }
        }
        joy3.setOnMoveListener { angle, strength ->
            if (strength == 0 && stateY) {
                stateX = false
                if (stateY) {
                    state++
                    getString()
                }
                x = ""
            } else if (strength == 0) {
                stateX = false
                amdau.value = ""
            }
            if (strength > 70) {
                stateX = true
                when (true) {
                    angle >= 350 || angle <= 10 -> {
                        x = "a1"
                    }
                    angle in 35..55 -> {
                        x = "a2"
                    }
                    angle in 80..100 -> {
                        x = "a3"
                    }
                    angle in 125..145 -> {
                        x = "a4"
                    }
                    angle in 170..190 -> {
                        x = "a5"
                    }
                    angle in 215..235 -> x = "a6"
                    angle in 260..280 -> x = "a7"
                    angle in 305..325 -> x = "a8"
                }
                amdau.value = x
            }
        }
        joy4.setOnMoveListener { angle, strength ->
            if (strength == 0 && stateY) {
                stateY = false
                if (stateX) {
                    state++
                    getString()
                }
                y = ""
            } else if (strength == 0) {
                stateY = false
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
                if (stateX) {
                    state++
                    getString()
                }
                y = ""
            } else if (strength == 0) {
                stateY = false
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
                if (stateX) {
                    state++
                    getString()
                }
                y = ""
            } else if (strength == 0) {
                stateY = false
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

    // hàm get các từ dựa theo code hiện tại
    fun getString() {
        if (state == 1) {
            code = x.plus(y)
            // get text ung voi trang thai chi co am dau - am chinh
            now = listFirst.find { it.first == code }?.second ?: ""
            if (now == "") {
                state = 0
                code = ""
                setVisibleAllTextViewHintAC(false)
            } else {
                tvContent.text = textFinal.value + now
                setVisibleAllTextViewHintAC(true)
                initTextHintState1()
            }

        }
        if (state == 2) {
            code = code + x + y
            val result = listTotal.find { it.first == code }?.second ?: ""
            if (result == "") {
                textFinal.value = textFinal.value + " "
            } else {
                textFinal.value = textFinal.value + result + " "
            }
            // get text ung voi truong hop co ca am dau - am chinh - am cuoi
            code = ""
            state = 0
            setVisibleAllTextViewHintAC(false)
            initTextHintState0()
        }
    }

    // hàm đọc file excel
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
            listDataOut.add(Pair("hihi", row.getCell(3).stringCellValue))
//            val cell0 = row.getCell(0)
//            val cell1 = row.getCell(1)
//            cell0.setCellValue(listDataOut[i].first)
//            cell1.setCellValue(listDataOut[i].second)
        }
        return xlWs
    }

    // hàm gọi lúc khởi động app , dùng để lưu các cặp từ vào các list
    fun readInputADAC(): Sheet {
        val inputStream = this.resources.openRawResource(R.raw.dataoutadac) // file lưu các cặp âm đầu - âm chính
//        val inputDictionary = this.resources.openRawResource(R.raw.tudienfinal)
        val inputSourceV1 =
            this.resources.openRawResource(R.raw.tudiensource) // tu dien chua cac tu theo bo luat ban dau (v1)
        val inputSource =
            this.resources.openRawResource(R.raw.allword3) // tu dien chua cac tu theo bo luat moi (v2)

//        var xlWb = WorkbookFactory.create(inputStream)
        val xlWb = XSSFWorkbook(inputStream)
        val xlWbDict = XSSFWorkbook(inputSource)
        val xlWbDictSource = XSSFWorkbook(inputSourceV1)

        val xlWs = xlWb.getSheetAt(0)
        for (i in 0..xlWs.lastRowNum) {
            val row = xlWs.getRow(i)
            listFirst.add(Pair(row.getCell(7).stringCellValue, row.getCell(8).stringCellValue))
        }
        val dictionary = xlWbDict.getSheetAt(0)
        for (i in 0..dictionary.lastRowNum) {
            val row = dictionary.getRow(i)
            listTotal.add(Pair(row.getCell(1).stringCellValue, row.getCell(0).stringCellValue))
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

    // hàm ghi data vào file excel
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
        workbook.write(t)
        t.close()

    }

    // hàm khởi tạo các từ cần có trong từ điển , chỉ dùng cho dev
    fun gen() {
        val listAD = listOf<Pair<String, String>>(
            Pair("c", "a1"),
            Pair("k", "a1"),
            Pair("h", "a2"),
            Pair("r", "a3"),
            Pair("b", "a4"),
            Pair("th", "a5"),
            Pair("tr", "a6"),
            Pair("s", "a7"),
            Pair("t", "a8"),
            Pair("ng", "b1"),
            Pair("ngh", "b1"),
            Pair("m", "b2"),
            Pair("n", "b3"),
            Pair("l", "b4"),
            Pair("ch", "b5"),
            Pair("nh", "b6"),
            Pair("kh", "b7"),
            Pair("ph", "b8"),
            Pair("g", "c1"),
            Pair("gh", "c1"),
            Pair("q", "c2"),
            Pair("p", "c2"),
            Pair("x", "c3"),
            Pair("gi", "c4"),
            Pair("", "c5"),
            Pair("đ", "c6"),
            Pair("d", "c7"),
            Pair("v", "c8")
        )
        val listADAC = listFirst
        var listAmCuoi = listOf<Pair<String, String>>(
            Pair("c", "a1"),
            Pair("p", "a2"),
            Pair("ch", "a3"),
            Pair("t", "a4"),
            Pair("", "a5"),
            Pair("m", "b1"),
            Pair("n", "b2"),
            Pair("o", "b3"),
            Pair("u", "b4"),
            Pair("i", "b5"),
            Pair("y", "b6"),
            Pair("ng", "b7"),
            Pair("nh", "b8")
        )
        var listResult = mutableListOf<Pair<String, String>>()
        var listDau = listOf<String>("e1", "e2", "e3", "e4", "e5", "e6")
        listADAC.forEach { a ->
            var amdau = ""
            var codeamdau = ""
            var amchinh = ""
            var codeamchinh = ""
            var t: Pair<String, String>? = null
            if (t == null && a.second.length > 3) {
                t = listAD.firstOrNull { it.first == a.second.substring(0, 3) }
            }
            if (t == null && a.second.length > 2) {
                t = listAD.firstOrNull { it.first == a.second.substring(0, 2) }
            }
            if (t == null && a.second.length > 1) {
                t = listAD.firstOrNull { it.first == a.second.substring(0, 1) }
            }
            if (t != null) {
                amdau = t.first
                codeamdau = t.second
            } else {
                amdau = ""
                codeamdau = "c5"
            }
            codeamchinh = a.first.subSequence(2, 4).toString()
            amchinh = a.second.substringAfter(amdau)
            listAmCuoi.forEach { c ->
                listDau.forEach { d ->
                    var k = c.second
                    val e = kethopamchinhvoidau(amchinh, d, amdau, c.first)
                    k += d
                    e.forEach { n ->
                        val textRes = amdau + n + c.first
                        if (listDictSource.contains(textRes)) {
                            val res = Pair(amdau + n + c.first, codeamdau + codeamchinh + k)
                            listResult.add(res)
                        }
                    }
//                    val textRes = amdau+e+c.first
//                    if(listDictSource.contains(textRes)) {
//                        val res = Pair(amdau + e + c.first, codeamdau + codeamchinh + k)
//                        listResult.add(res)
//                    }
                }
            }
        }

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("ALL")
        for (i in 0..listResult.size - 1) {
            val row = sheet.createRow(i)
            val cell0 = row.createCell(0)
            val cell1 = row.createCell(1)
            cell0.setCellValue(listResult[i].first)
            cell1.setCellValue(listResult[i].second)
        }
        val t = openFileOutput("all.xlsx", 0)
//        OutputStreamWriter(openFileOutput("tonghop2",0)).write()
        workbook.write(t)
        t.close()
    }

    // only for dev
    fun kethopamchinhvoidau(
        amchinh: String,
        dau: String,
        amdau: String,
        amcuoi: String
    ): List<String> {
        when (amchinh) {
            "a" -> {
                when (dau) {
                    "e1" -> return listOf("a")
                    "e2" -> return listOf("á")
                    "e3" -> return listOf("à")
                    "e4" -> return listOf("ả")
                    "e5" -> return listOf("ã")
                    "e6" -> return listOf("ạ")
                    else -> return listOf("")
                }
            }
            "ă" -> {
                when (dau) {
                    "e1" -> return listOf("ă")
                    "e2" -> return listOf("ắ")
                    "e3" -> return listOf("ằ")
                    "e4" -> return listOf("ẳ")
                    "e5" -> return listOf("ẵ")
                    "e6" -> return listOf("ặ")
                    else -> return listOf("")
                }
            }
            "â" -> {
                when (dau) {
                    "e1" -> return listOf("â")
                    "e2" -> return listOf("ấ")
                    "e3" -> return listOf("ầ")
                    "e4" -> return listOf("ẩ")
                    "e5" -> return listOf("ẫ")
                    "e6" -> return listOf("ậ")
                    else -> return listOf(
                        ""
                    )
                }
            }
            "ơ" -> {
                when (dau) {
                    "e1" -> return listOf("ơ")
                    "e2" -> return listOf("ớ")
                    "e3" -> return listOf("ờ")
                    "e4" -> return listOf("ở")
                    "e5" -> return listOf("ỡ")
                    "e6" -> return listOf("ợ")
                    else -> return listOf(
                        ""
                    )
                }
            }
            "i" -> {
                when (dau) {
                    "e1" -> return listOf("i")
                    "e2" -> return listOf("í")
                    "e3" -> return listOf("ì")
                    "e4" -> return listOf("ỉ")
                    "e5" -> return listOf("ĩ")
                    "e6" -> return listOf("ị")
                    else -> return listOf(
                        ""
                    )
                }
            }
            "y" -> {
                when (dau) {
                    "e1" -> return listOf("y")
                    "e2" -> return listOf("ý")
                    "e3" -> return listOf("ỳ")
                    "e4" -> return listOf("ỷ")
                    "e5" -> return listOf("ỹ")
                    "e6" -> return listOf("ỵ")
                    else -> return listOf("")
                }
            }
            "e" -> {
                when (dau) {
                    "e1" -> return listOf("e")
                    "e2" -> return listOf("é")
                    "e3" -> return listOf("è")
                    "e4" -> return listOf("ẻ")
                    "e5" -> return listOf("ẽ")
                    "e6" -> return listOf("ẹ")
                    else -> return listOf("")
                }
            }
            "ê" -> {
                when (dau) {
                    "e1" -> return listOf("ê")
                    "e2" -> return listOf("ế")
                    "e3" -> return listOf("ề")
                    "e4" -> return listOf("ể")
                    "e5" -> return listOf("ễ")
                    "e6" -> return listOf("ệ")
                    else -> return listOf("")
                }
            }
            "o" -> {
                when (dau) {
                    "e1" -> return listOf("o")
                    "e2" -> return listOf("ó")
                    "e3" -> return listOf("ò")
                    "e4" -> return listOf("ỏ")
                    "e5" -> return listOf("õ")
                    "e6" -> return listOf("ọ")
                    else -> return listOf("")
                }
            }
            "ư" -> {
                when (dau) {
                    "e1" -> return listOf("ư")
                    "e2" -> return listOf("ứ")
                    "e3" -> return listOf("ừ")
                    "e4" -> return listOf("ử")
                    "e5" -> return listOf("ữ")
                    "e6" -> return listOf("ự")
                    else -> return listOf("")
                }
            }
            "u" -> {
                when (dau) {
                    "e1" -> return listOf("u")
                    "e2" -> return listOf("ú")
                    "e3" -> return listOf("ù")
                    "e4" -> return listOf("ủ")
                    "e5" -> return listOf("ũ")
                    "e6" -> return listOf("ụ")
                    else -> return listOf("")
                }
            }
            "ua/uô" -> {
//                if(amcuoi.isNotEmpty()) {
                when (dau) {
                    "e1" -> return listOf("uô", "ua")
                    "e2" -> return listOf("uố", "uá")
                    "e3" -> return listOf("uồ", "uà")
                    "e4" -> return listOf("uổ", "uả")
                    "e5" -> return listOf("uỗ", "uã")
                    "e6" -> return listOf("uộ", "uộ")
                    else -> return listOf("")
                }
//                } else {
//                    when (dau) {
//                        "e1" -> return "ua"
//                        "e2" -> return "uá"
//                        "e3" -> return "uà"
//                        "e4" -> return " "
//                        "e5" -> return "ũa"
//                        "e6" -> return "ụa"
//                        else -> return ""
//                    }
//                }
            }
            "uy" -> {
                when (dau) {
                    "e1" -> return listOf("uy")
                    "e2" -> return listOf("uý")
                    "e3" -> return listOf("uỳ")
                    "e4" -> return listOf("uỷ")
                    "e5" -> return listOf("uỹ")
                    "e6" -> return listOf("uỵ")
                    else -> return listOf("")
                }
            }
            "oe" -> {
                when (dau) {
                    "e1" -> return listOf("oe")
                    "e2" -> return listOf("oẻ")
                    "e3" -> return listOf("oè")
                    "e4" -> return listOf("oẻ")
                    "e5" -> return listOf("oẽ")
                    "e6" -> return listOf("oẹ")
                    else -> return listOf("")
                }
            }
            "oa" -> {
                when (dau) {
                    "e1" -> return listOf("oa")
                    "e2" -> return listOf("oá")
                    "e3" -> return listOf("oà")
                    "e4" -> return listOf("oả")
                    "e5" -> return listOf("oã")
                    "e6" -> return listOf("oạ")
                    else -> return listOf("")
                }
            }
            "ô" -> {
                when (dau) {
                    "e1" -> return listOf("ô")
                    "e2" -> return listOf("ố")
                    "e3" -> return listOf("ồ")
                    "e4" -> return listOf("ổ")
                    "e5" -> return listOf("ỗ")
                    "e6" -> return listOf("ộ")
                    else -> return listOf("")
                }
            }
            "uê" -> {
                when (dau) {
                    "e1" -> return listOf("uê")
                    "e2" -> return listOf("uế")
                    "e3" -> return listOf("uề")
                    "e4" -> return listOf("uể")
                    "e5" -> return listOf("uễ")
                    "e6" -> return listOf("uệ")
                    else -> return listOf("")
                }
            }
            "uyê/uya" -> {
//                if(amcuoi.isNotEmpty()){
                when (dau) {
                    "e1" -> return listOf("uyê", "uya")
                    "e2" -> return listOf("uyế", "uyá")
                    "e3" -> return listOf("uyề", "uyà")
                    "e4" -> return listOf("uyể", "uyả")
                    "e5" -> return listOf("uyễ", "uyã")
                    "e6" -> return listOf("uyệ", "uyạ")
                    else -> return listOf("")
                }
//                } else {
//                    when (dau) {
//                        "e1" -> return "uya"
//                        "e2" -> return "uýa"
//                        "e3" -> return "uỳa"
//                        "e4" -> return "uỷa"
//                        "e5" -> return "uỹa"
//                        "e6" -> return "uỵa"
//                        else -> return ""
//                    }
//                }
            }
            "oo" -> {
                when (dau) {
                    "e1" -> return listOf("oo")
                    "e2" -> return listOf("óo")
                    "e3" -> return listOf("òo")
                    "e4" -> return listOf("ỏo")
                    "e5" -> return listOf("õo")
                    "e6" -> return listOf("ọo")
                    else -> return listOf("")
                }
            }
            "oă" -> {
                when (dau) {
                    "e1" -> return listOf("oă")
                    "e2" -> return listOf("oắ")
                    "e3" -> return listOf("oằ")
                    "e4" -> return listOf("oẳ")
                    "e5" -> return listOf("oẵ")
                    "e6" -> return listOf("oặ")
                    else -> return listOf("")
                }
            }
            "uâ" -> {
                when (dau) {
                    "e1" -> return listOf("uâ")
                    "e2" -> return listOf("uấ")
                    "e3" -> return listOf("uầ")
                    "e4" -> return listOf("uẩ")
                    "e5" -> return listOf("uẫ")
                    "e6" -> return listOf("uậ")
                    else -> return listOf("")
                }
            }
            "ươ/ưa" -> {
//                if(amcuoi.isNotEmpty()){
                when (dau) {
                    "e1" -> return listOf("ươ", "ưa")
                    "e2" -> return listOf("ướ", "ứa")
                    "e3" -> return listOf("ườ", "ừa")
                    "e4" -> return listOf("ưở", "ửa")
                    "e5" -> return listOf("ưỡ", "ữa")
                    "e6" -> return listOf("ượ", "ựa")
                    else -> return listOf("")
                }
//                } else {
//                    when(dau){
//                        "e1" -> return "ưa"
//                        "e2" -> return "ứa"
//                        "e3" -> return "ừa"
//                        "e4" -> return "ửa"
//                        "e5" -> return "ữa"
//                        "e6" -> return "ựa"
//                        else -> return ""
//                    }
//                }

            }
            "ia/iê/yê" -> {
//                if(amdau.isEmpty() && amcuoi.isNotEmpty()){
                when (dau) {
                    "e1" -> return listOf("yê", "ia", "iê")
                    "e2" -> return listOf("yế", "iá", "iế")
                    "e3" -> return listOf("yề", "ià", "iề")
                    "e4" -> return listOf("yể", "ià", "iề")
                    "e5" -> return listOf("yễ", "iã", "iễ")
                    "e6" -> return listOf("yệ", "iạ", "iệ")
                    else -> return listOf("")
                }
//                } else if(amcuoi.isEmpty()){
//                    when(dau){
//                        "e1" -> return "ia"
//                        "e2" -> return "ía"
//                        "e3" -> return "ìa"
//                        "e4" -> return "ỉa"
//                        "e5" -> return "ĩa"
//                        "e6" -> return "ịa"
//                        else -> return ""
//                    }
//                } else {
//                    when (dau) {
//                        "e1" -> return "iê"
//                        "e2" -> return "iế"
//                        "e3" -> return "iề"
//                        "e4" -> return "iể"
//                        "e5" -> return "iễ"
//                        "e6" -> return "iệ"
//                        else -> return ""
//                    }
//                }
            }
            "uơ" -> {
                when (dau) {
                    "e1" -> return listOf("uơ")
                    "e2" -> return listOf("uớ")
                    "e3" -> return listOf("uờ")
                    "e4" -> return listOf("uở")
                    "e5" -> return listOf("uỡ")
                    "e6" -> return listOf("uợ")
                    else -> return listOf("")
                }
            }
            else -> return listOf("")
        }
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
        workbook.write(t)
        t.close()
        return
    }

}