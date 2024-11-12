package jp.jamsketch.main

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import controlP5.ControlP5
import jp.crestmuse.cmx.filewrappers.SCCDataSet
import jp.crestmuse.cmx.processing.gui.SimplePianoRoll
import jp.jamsketch.config.AccessibleConfig
import jp.jamsketch.config.IConfigAccessible
import jp.jamsketch.controller.IJamSketchController
import jp.jamsketch.controller.JamSketchClientController
import jp.jamsketch.controller.JamSketchController
import jp.jamsketch.controller.JamSketchServerController
import jp.jamsketch.model.JamSketchModel
import jp.jamsketch.model.LunchInitFunction
import jp.jamsketch.model.Point
import jp.jamsketch.util.Tick
import jp.jamsketch.view.IDisplay
import jp.jamsketch.web.ServiceLocator
import processing.core.PApplet
import java.io.File
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

// TODO: Remove temporarily implemented interfaces(IConfigAccessible)
class JamSketch : SimplePianoRoll(), IConfigAccessible {

    /**
     * Role: Used for initial graphical settings such as window size and rendering mode.
     * Invocation Timing: Called first when the PApplet is initialized.
     * Usage Example: Functions like size(), fullScreen(), smooth(), and noSmooth().
     * Constraints: This method cannot contain drawing code like background() or fill().
     */
    override fun settings() {
        super.settings()
        size(1200, 700)
    }

    /**
     * Role: Used for general initial setup of your sketch, such as setting the background color,
     * initializing variables, and drawing shapes for the first time.
     * Invocation Timing: Called once after settings().
     * Usage Example: Functions like background(), stroke(), fill(), and initializing objects.
     * Constraints: Should not include graphical settings like size() or smooth().
     */
    override fun setup() {
        super.setup()
        showMidiOutChooser()
        val p5ctrl = ControlP5(this)

        if (config.mode.equals("client")) {
            p5ctrl.addButton("reconnect").setLabel("Reconnect").setPosition(20f, 645f).setSize(120, 40)
        } else {
            p5ctrl.addButton("startMusic").setLabel("Start / Stop").setPosition(20f, 645f).setSize(120, 40)
            p5ctrl.addButton("loadCurve").setLabel("Load").setPosition(300f, 645f).setSize(120, 40)
        }

        p5ctrl.addButton("resetMusic").setLabel("Reset").setPosition(160f, 645f).setSize(120, 40)

        panel = JPanel()
        val layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel!!.layout = layout
        panel!!.add(JLabel("接続が切断されました。"))
        val listner: JamSketchEventListner = JamSketchEventListnerImpl(panel!!)

        val melodyData = initData()

        // JamSketch操作クラスを初期化
        val origController = JamSketchController(melodyData, this::initData)

        controller = if (config.mode.equals("server")) {
            // サーバーで動かす場合に使う操作クラスを設定
            JamSketchServerController(
                config.host,
                config.port,
                origController
            )
        } else if (config.mode.equals("client")) {
            // クライアントで動かす場合に使う操作クラスを設定
            JamSketchClientController(
                config.host,
                config.port,
                origController,
                listner
            )
        } else {
            // スタンドアロンで動かす場合はそのまま
            origController
        }

        val serviceLocator = ServiceLocator.GetInstance()
        serviceLocator.setContoller(controller!!)

        LunchInitFunction.create(this).setup()
        // add WindowListener (windowClosing) which calls exit();
    }

    fun initData(): MelodyData2 {
        melodyData = MelodyData2(config.midfilename, (width - config.keyboard_width) as Int, this, this, config)
        smfread((melodyData.scc as SCCDataSet).midiSequence)
        val part: SCCDataSet.Part = (melodyData.scc as SCCDataSet).getFirstPartWithChannel(config.channel_acc)
        dataModel = part.getPianoRollDataModel(config.initial_blank_measures,
                config.initial_blank_measures + config.num_of_measures)

        if (config.show_guide) guideData =
            GuideData(
                config.midfilename,
                (width - config.keyboard_width),
                this,
                config,
            )

        fullMeasure = (dataModel.measureNum * config.repeat_times)
        tickPosition = 0
        dataModel.firstMeasure = config.initial_blank_measures

        // 初期化済みオブジェクトを返す
        return melodyData
    }

    override fun draw() {
        super.draw()
        if (guideData != null) drawGuideCurve()
        if (config.forced_progress) {
            mouseX = beat2x(currentMeasure + config.how_in_advance, currentBeat).toInt()
        }


        if (pmouseX < mouseX && mouseX > beat2x(currentMeasure, currentBeat) + 10) {
            if (isUpdatable) {
                updateCurve()
            }
        }


        for (d in displays) {
            d.display(this)
        }


        if (currentMeasure.equals(config.num_of_measures - config.num_of_reset_ahead)) processLastMeasure()
        (melodyData.engine as JamSketchEngine).setFirstMeasure(dataModel.firstMeasure)
        enhanceCursor()
        drawProgress()

        for (t in ticker) t.tick()
    }

    fun drawGuideCurve() {
        val xFrom = 100
        strokeWeight(3f)
        stroke(100f, 200f, 200f)
        (0..<((guideData!!.curveGuideView as Array<*>).size -1)).forEach { i ->
            if ((guideData!!.curveGuideView as Array<*>)[i] != null &&
                (guideData!!.curveGuideView as Array<*>)[i+1] != null) {
                line((i+xFrom) as Double, (guideData!!.curveGuideView as Array<*>)[i] as Double,
                    (i+1+xFrom) as Double, (guideData!!.curveGuideView as Array<*>)[i+1] as Double)
            }
        }
    }

    fun updateCurve() {
        // JamSketch操作クラスを使用して楽譜データを更新する
        /*
    if (pmouseX != -1 && mouseX != -1)
      this.controller.updateCurve(pmouseX, mouseX, mouseY)

     */

        val p = Point(mouseX, mouseY)
        model!!.curve.updateCurve(p)
    }

    val isUpdatable: Boolean
        get() {
            if ((!config.on_drag_only || nowDrawing) &&
                    isInside(
                        mouseX,
                        mouseY
                    )
            ) {
                val m1 = x2measure(mouseX.toDouble())
                val m0 = x2measure(pmouseX.toDouble())
                return 0 <= m0 && pmouseX < mouseX
            } else {
                return false
            }
        }

    fun processLastMeasure() {
        makeLog("melody")
        if (config.melody_resetting) {
            if (mCurrentMeasure < (fullMeasure - config.num_of_reset_ahead)) dataModel.shiftMeasure(config.num_of_measures)
            melodyData.resetCurve()
            if (guideData != null) guideData!!.shiftCurve()

            if (controller is JamSketchServerController) {
                (controller as JamSketchServerController).resetClients()
            }
        }
    }

    fun enhanceCursor() {
        if (config.cursor_enhanced) {
            fill(255f, 0f, 0f)
            ellipse(mouseX.toFloat(), mouseY.toFloat(), 10f, 10f)
        }
    }

    fun drawProgress() {
        if (isNowPlaying) {
            val dataModel = dataModel
            mCurrentMeasure =
                (currentMeasure + dataModel.firstMeasure - config.initial_blank_measures + 1)
            val mtotal: Int = dataModel.measureNum * config.repeat_times
            textSize(32f)
            fill(0f, 0f, 0f)
            text(mCurrentMeasure.toString() + " / " + mtotal, 460f, 675f)
        }
    }

    override fun stop() {
        super.stop()
        //featext.stop()
    }

    fun startMusic() {
        if (isNowPlaying) {
            stopMusic()
            makeLog("stop")
        } else {
            playMusic()
            makeLog("play")
        }
    }

    fun resetMusic() {
        // JamSketch操作クラスを使用してリセットする
        controller!!.reset()
        makeLog("reset")
        model!!.curve.clear()
    }

    public override fun musicStopped() {
        super.musicStopped()
        //    if (microsecondPosition >= sequencer.getMicrosecondLength())
//      resetMusic()
    }

    /**
     * 再接続する
     */
    fun reconnect() {
        controller!!.init()
    }

    fun makeLog(action: String) {
        val logname = "output_" + (Date()).toString().replace(" ", "_").replace(":", "-")
        if (action == "melody") {
            val midname: String = config.log_dir + "/" + logname + "_melody.mid"
            (melodyData.scc as SCCDataSet).toWrapper().toMIDIXML().writefileAsSMF(midname)
            println("saved as $midname")

            val sccname: String = config.log_dir + "/" + logname + "_melody.sccxml"
            (melodyData.scc as SCCDataSet).toWrapper().writefile(sccname)
            println("saved as $sccname")

            val jsonname: String = config.log_dir + "/" + logname + "_curve.json"
            saveStrings(
                jsonname,
                arrayOf(jacksonObjectMapper().writeValueAsString(melodyData.curve1))
                )
            println("saved as $jsonname")
            val pngname: String = config.log_dir + "/" + logname + "_screenshot.png"
            save(pngname)
            println("saved as $pngname")

            // for debug
            File("${config.log_dir.plus(File.separator).plus(logname)}_noteList.txt").writeText(
                (melodyData.scc as SCCDataSet).getFirstPartWithChannel(1).noteList.toString()
            )
            //      new File("${CFG.LOG_DIR}/${logname}_noteOnlyList.txt").text = (melodyData.scc as SCCDataSet).getFirstPartWithChannel(1).getNoteOnlyList().toString()
        } else {
            val txtname: String = config.log_dir.toString() + "/" + logname + "_" + action + ".txt"
            saveStrings(txtname, arrayOf(action))
            println("saved as $txtname")
        }
    }

    fun loadCurve() {
        selectInput("Select a file to process:", "loadFileSelected")
    }

    fun loadFileSelected(selection: File?) {
        if (selection == null) {
            println("Window was closed or the user hit cancel.")
        } else {
            val absolutePath = selection.absolutePath
            println("User selected $absolutePath")
            if (absolutePath.endsWith(".json")) {
                val objectMapper = jacksonObjectMapper()
                melodyData.curve1 = objectMapper.readValue(selection)
                val count: Int = (melodyData.curve1 as Array<*>).size
                melodyData.updateCurve(0, width - debugModeDraw)
            } else if (selection.canonicalPath.endsWith(".txt")) {
                val table = loadTable(absolutePath, "csv")
                melodyData.curve1 = arrayOfNulls<Int>(width - debugModeDraw)
                val n = table.rowCount
                val m: Int = (melodyData.curve1 as Array<Int>).size
                for (i in IntRange(keyboardWidth.toInt(), (m - 1))) {
                    val from = (i - keyboardWidth) as Int * n / m
                    val thru = ((i + 1) - keyboardWidth) as Int * n / m - 1
                    val range = (from..thru).toList()
                    val collect = range.map { notenum2y(table.getFloat(it, 0) as Double) }
                    val sum = collect.sum()
                    (melodyData.curve1 as Array<Int>)[i] = (sum / range.size).toInt()

                }
                melodyData.updateCurve(0, width - debugModeDraw)

            } else {
                println("File is not supported")
                return
            }
        }
    }

    override fun mousePressed() {
        nowDrawing = true
    }

    override fun mouseReleased() {
        nowDrawing = false
        mouseX = -1
        if (isInside(mouseX, mouseY)) {
            if ((melodyData.engine as JamSketchEngineAbstract).automaticUpdate() as Boolean) {
                (melodyData.engine as JamSketchEngineAbstract).outlineUpdated(
                    x2measure(mouseX.toDouble()) % config.num_of_reset_ahead,
                    config.division - 1
                )
            }
        }
        controller!!.mouseReleased(Point(mouseX, mouseY))
    }

    override fun mouseDragged() {
    }

    override fun keyReleased() {
        if (key.equals(" ")) {
            if (isNowPlaying) {
                stopMusic()
            } else {
                tickPosition = 0
                dataModel.firstMeasure = config.initial_blank_measures
                playMusic()
            }
        } else if (key.equals("b")) {
            isNoteVisible = !isNoteVisible
//        } else if (key.equals("u")) {
//            melodyData!!.updateCurve("all")
        }
    }

    fun loadOutlineLayerData() {
        val jsonFile: File = File(config.input_file_path)
        val mapper = jacksonObjectMapper()
        var jsonData : Array<Int> = mapper.readValue(jsonFile)
        jsonData.forEachIndexed { index, value ->
            (melodyData.curve1 as Array<Int>)[index] = value
        }
        melodyData.updateCurve(0, (width - getKeyboardWidth()) as Int)
    }

    override fun exit() {
        println("exit() called.")
        super.exit()
    }

    fun isNowDrawing(): Boolean {
        return nowDrawing
    }

    fun getmCurrentMeasure(): Int {
        return mCurrentMeasure
    }

    fun setmCurrentMeasure(mCurrentMeasure: Int) {
        this.mCurrentMeasure = mCurrentMeasure
    }

    var guideData: GuideData? = null
    var melodyData: MelodyData2 = initData()
    var model: JamSketchModel? = null
    var displays: CopyOnWriteArrayList<IDisplay> = CopyOnWriteArrayList()
    var ticker: CopyOnWriteArrayList<Tick> = CopyOnWriteArrayList()
    var nowDrawing: Boolean = false
    var username: String = ""
    var fullMeasure: Int = 0
    private var mCurrentMeasure = 0
    var debugModeDraw: Int = 0
    var controller: IJamSketchController? = null
    var panel: JPanel? = null

//    companion object {//        }
////
////    }
//        @JvmStatic
//        fun main(args: Array<String>) {
//            start("jp.jamsketch.main.JamSketch")

    override val config = AccessibleConfig.config

    // TODO: Temporally method
    fun publicNotenum2y(nn: Double) : Double {
        return this.notenum2y(nn)
    }
    fun publicBeat2x(measure: Int, beat: Double) : Double {
        return this.beat2x(measure, beat)
    }
}

fun main() {
    PApplet.main (arrayOf("jp.jamsketch.main.JamSketch"))
}
