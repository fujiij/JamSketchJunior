package jp.kthrlab.jamsketch.view

import controlP5.ControlP5
import jp.crestmuse.cmx.filewrappers.SCCDataSet
import jp.crestmuse.cmx.processing.gui.SimplePianoRoll
import jp.kthrlab.jamsketch.config.AccessibleConfig
import jp.kthrlab.jamsketch.config.IConfigAccessible
import jp.kthrlab.jamsketch.controller.IJamSketchController
import jp.kthrlab.jamsketch.controller.JamSketchClientController
import jp.kthrlab.jamsketch.controller.JamSketchController
import jp.kthrlab.jamsketch.controller.JamSketchServerController
import jp.kthrlab.jamsketch.engine.JamSketchEngine
import jp.kthrlab.jamsketch.music.data.GuideData
import jp.kthrlab.jamsketch.music.data.MusicData
import jp.kthrlab.jamsketch.web.ServiceLocator
import processing.core.PApplet
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

class JamSketch : SimplePianoRoll(), IConfigAccessible {

    companion object {
        const val PACKAGE_NAME: String = "jp.kthrlab.jamsketch.engine"
    }

    override val config = AccessibleConfig.config

    // TODO: Delete comment
    //  fullMeasure -> totalMeasures
    //  mCurrentMeasure -> currentMeasureInTotalMeasures
    val totalMeasures: Int = config.music.num_of_measures * config.music.repeat_times
    val timelineWidth: Int = config.general.view_width - config.general.keyboard_width
    private var currentMeasureInTotalMeasures = 0   // TODO: わかりやすい変数名にしたが、もっと短くしたい

    var musicData: MusicData =
        MusicData(
            config.music.midfilename,
            timelineWidth,
            config.music.initial_blank_measures,
            config.music.beats_per_measure,
            config.music.num_of_measures,
            config.music.repeat_times,
            config.music.division,
            config.music.channel_acc,
        )

    var engine: JamSketchEngine = ((Class.forName(PACKAGE_NAME + "." + config.music.jamsketch_engine).getConstructor().newInstance()) as JamSketchEngine).let {
        val target_part: SCCDataSet.Part  = musicData.scc.toDataSet().getFirstPartWithChannel(config.music.channel_acc)
        it.init(musicData.scc, target_part)
        it.resetMelodicOutline()
        it.setFirstMeasure(config.music.initial_blank_measures)
        it
    }

    var controller: IJamSketchController

    var guideData: GuideData? = null

    // Unused
    var username: String = ""

    // A dialogue for JamSketchEventListener
    var panel: JPanel  = JPanel().let {
        val layout = BoxLayout(it, BoxLayout.Y_AXIS)
        it.layout = layout
        it.add(JLabel("The connection is lost."))
        it
    }

    init {
        // TODO: delete comment
        //  initData() から移動。読込は一度だけで良いはず。
        smfread((musicData.scc as SCCDataSet).midiSequence)

        // TODO: Delete comments
        // 20241118 initData() で行われていた処理を移動している
        val part = (musicData.scc as SCCDataSet).getFirstPartWithChannel(config.music.channel_acc)
        dataModel = part.getPianoRollDataModel(config.music.initial_blank_measures,
            config.music.initial_blank_measures + config.music.num_of_measures)
        // init music player ((SMFPlayer)this.musicPlayer[i]).setTickPosition(tick);
        tickPosition = 0

        // init controller
        val listener: JamSketchEventListener =
            JamSketchEventListenerImpl(panel)

        val origController = JamSketchController(
            musicData,
            engine,
            this::setPianoRollDataModelFirstMeasure,
        )

        controller = when (config.general.mode) {
            "server" -> {
                // サーバーで動かす場合に使う操作クラスを設定
                JamSketchServerController(
                    config.general.host,
                    config.general.port,
                    origController
                )
            }
            "client" -> {
                // クライアントで動かす場合に使う操作クラスを設定
                JamSketchClientController(
                    config.general.host,
                    config.general.port,
                    origController,
                    listener
                )
            }
            else -> {
                // スタンドアロンで動かす場合はそのまま
                origController
            }
        }

        val serviceLocator = ServiceLocator.GetInstance()
        serviceLocator.setContoller(controller)

    }

    /**
     * Role: Used for initial graphical settings such as window size and rendering mode.
     * Invocation Timing: Called first when the PApplet is initialized.
     * Usage Example: Functions like size(), fullScreen(), smooth(), and noSmooth().
     * Constraints: This method cannot contain drawing code like background() or fill().
     */
    override fun settings() {
        super.settings()
        size(config.general.view_width, config.general.view_height)
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

        // ControlP5 GUI components
        val p5ctrl = ControlP5(this)
        if (config.general.mode == "client") {
            p5ctrl.addButton("reconnect").setLabel("Reconnect").setPosition(20f, 645f).setSize(120, 40)
        } else {
            p5ctrl.addButton("startMusic").setLabel("Start / Stop").setPosition(20f, 645f).setSize(120, 40)
            p5ctrl.addButton("loadCurve").setLabel("Load").setPosition(300f, 645f).setSize(120, 40)
        }
        p5ctrl.addButton("resetMusic").setLabel("Reset").setPosition(160f, 645f).setSize(120, 40)

        // --------------------
        // init GuideData
        // --------------------
        // beat2x depends on dataModel & PApplet.width
        // GuideData needs to be initialized after calling setDataModel & settings()
        guideData =
            if (config.general.show_guide) {
                GuideData(
                    config.music.midfilename,
                    timelineWidth,
                    config.music.initial_blank_measures,
                    config.music.beats_per_measure,
                    config.music.num_of_measures,
                    config.music.repeat_times,
                    config.general.guide_smoothness,
                    config.music.channel_guide,
                    config.general.keyboard_width,
                    this::notenum2y,
                    this::beat2x,
                )
            } else {
                null
            }

        // add WindowListener (windowClosing) which calls exit();
    }

    /**
     * --------------------
     * Caution!
     * --------------------
     * Don't write your draw() directly here.
     * If you use JamSketch in your research and need to add your own features, write it in drawAdditionalElements().
     */
    override fun draw() {
        super.draw()

        // Manipulating mouseX
        if (config.general.forced_progress) {
            mouseX = beat2x(currentMeasure + config.music.how_in_advance, currentBeat).toInt()
        }

        // Updating the curve and processing related to it
        if (isUpdatable) {
            updateCurve()
        }

        // Drawing visual elements
        drawBasicElements()
        drawAdditionalElements()

        // Process for the last measure of each page
        if (isLastMeasure()) processLastMeasure()
    }

    /**
     * Draw basic visual elements
     */
    private fun drawBasicElements() {
        enhanceCursor()
        drawCurve()
        drawProgress()
    }

    /**
     * Draw additional visual elements
     */
    private fun drawAdditionalElements() {
        if (guideData != null) drawGuideCurve()
    }

    /**
     * Returns true if currentMeasure is the last measure
     */
    private fun isLastMeasure(): Boolean {
        return currentMeasure == config.music.num_of_measures - config.music.num_of_reset_ahead
    }

    /**
     * Draw curves
     */
    private fun drawCurve() {
        strokeWeight(3f)
        stroke(0f, 0f, 255f)
        with(musicData) {
            (0 until curve1.size - 1).forEach { i ->
                if (curve1[i] != null &&  curve1[i+1] != null) {
                    line(
                        (i + config.general.keyboard_width).toDouble(),
                        curve1[i]!!.toDouble(),
                        (i + 1 + config.general.keyboard_width).toDouble(),
                        curve1[i+1]!!.toDouble()
                    )
                }
            }
        }
    }

    /**
     * Draw curves for the guide
     */
    private fun drawGuideCurve() {
        strokeWeight(3f)
        stroke(100f, 200f, 200f)
        with(guideData!!) {
            (0 until (curveGuideView!!.size - 1)).forEach { i ->
                if (curveGuideView!![i] != null &&
                    curveGuideView!![i+1] != null) {
                    line((i + config.general.keyboard_width).toDouble(), curveGuideView!![i]!!.toDouble(),
                        (i+ config.general.keyboard_width + 1).toDouble(), curveGuideView!![i+1]!!.toDouble())
                }
            }
        }
    }

    fun updateCurve() {
        // JamSketch操作クラスを使用して楽譜データを更新する
        if(config.general.keyboard_width < pmouseX && config.general.keyboard_width < mouseX) {
            this.controller.updateCurve(
                pmouseX - config.general.keyboard_width,
                mouseX - config.general.keyboard_width,
                mouseY,
                y2notenum(mouseY.toDouble()),
            )
        }
    }

    // !config.on_drag_only                                 : on_drag_only == false の場合
    // nowDrawing           : mousePressed() でtrueとなっている。mousePressedと同じ
    // m0 = x2measure(pmouseX.toDouble())                   : drag開始位置の小節が0以上か
    // pmouseX < mouseX                                     : 右から左にdragしているか
    // mouseX > beat2x(currentMeasure, currentBeat) + 10    : 演奏の現在位置より先か
    private val isUpdatable: Boolean
        get() {
//            if ((!config.on_drag_only || nowDrawing) && isInside(mouseX, mouseY)
            if ((!config.general.on_drag_only || mousePressed) && isInside(mouseX, mouseY)
            ) {
//                val m1 = x2measure(mouseX.toDouble())
                val m0 = x2measure(pmouseX.toDouble())
                return 0 <= m0
                        && pmouseX < mouseX
                        && mouseX > beat2x(currentMeasure, currentBeat) + 10
            } else {
                return false
            }
        }

    /**
     * The process to be performed in the last measure on the screen
     */
    private fun processLastMeasure() {
        makeLog("melody")
        // TODO: melody_resetting の意味を確認
        if (config.general.melody_resetting) {
            if (currentMeasureInTotalMeasures < (totalMeasures - config.music.num_of_reset_ahead)) {
                dataModel.shiftMeasure(config.music.num_of_measures)
            }
            musicData.resetCurve()

            // for Guided
            if (guideData != null) guideData!!.shiftCurveGuideView()

            if (controller is JamSketchServerController) {
                (controller as JamSketchServerController).resetClients()
            }
        }

        // 20241121 add
        // the case when playing up to totalMeasures
        if (currentMeasureInTotalMeasures >= totalMeasures) {
            // 生成したメロディを残したまま再スタートするために、measureを戻す
            setPianoRollDataModelFirstMeasure(config.music.initial_blank_measures)

            // Set GuideData start position back to 0
            resetGuideData()
        }

        // SCCGenerator.firstMeasure = num
        engine.setFirstMeasure(dataModel.firstMeasure)
    }

    private fun setPianoRollDataModelFirstMeasure(firstMeasure: Int) {
        dataModel.firstMeasure = firstMeasure
    }

    private fun enhanceCursor() {
        if (config.general.cursor_enhanced) {
            fill(255f, 0f, 0f)
            ellipse(mouseX.toFloat(), mouseY.toFloat(), 10f, 10f)
        }
    }

    private fun drawProgress() {
        if (isNowPlaying) {
            // currentMeasureInTotalMeasures    演奏全体での現在の小節番号（processLastMeasure()でも参照）
            // currentMeasure                   ページ内の小節番号
            // dataModel.firstMeasure           ページ開始位置
            // initial_blank_measures           ページ開始位置のoffset
            // +1                               0小節目ではなく1小節目とするために加算
            currentMeasureInTotalMeasures =
                (currentMeasure + dataModel.firstMeasure - config.music.initial_blank_measures + 1)
            textSize(32f)
            fill(0f, 0f, 0f)
            text(currentMeasureInTotalMeasures.toString() + " / " + totalMeasures, 460f, 675f)
        }
    }

    private fun resetGuideData() {
        if (guideData != null) {
            guideData!!.fromMeasure = 0
            guideData!!.updateCurveGuideView(0, guideData!!.size)
        }
    }

    /**
     * ControlP5
     * Start/Stop cmx music player
     */
    fun startMusic() {
        if (isNowPlaying) {
            stopMusic()
            makeLog("stop")
        } else {
            playMusic()
            makeLog("play")
        }
    }

    /**
     * ControlP5
     * Reset
     */
    fun resetMusic() {

        // JamSketch操作クラスを使用してリセットする
        controller.reset()

        // Set GuideData start position back to 0
        resetGuideData()

        makeLog("reset")
    }

    /**
     * ControlP5
     * Load saved curve
     */
    fun loadCurve() {
        selectInput("Select a file to process:", "loadFileSelected")
    }

    /**
     * ControlP5 callback
     * Load selected file
     */
    fun loadFileSelected(selection: File) {
        jp.kthrlab.jamsketch.view.util.loadFileSelected(
            selection,
            musicData,
            this,
            config.general.view_width,
            config.general.keyboard_width,
            this::notenum2y
        )
    }

    /**
     * ControlP5
     * 再接続する
     */
    fun reconnect() {
        controller.init()
    }

    override fun keyReleased() {
        if (key.equals(" ")) {
            if (isNowPlaying) {
                stopMusic()
            } else {
                tickPosition = 0
                setPianoRollDataModelFirstMeasure(config.music.initial_blank_measures)
                playMusic()
            }
        } else if (key.equals("b")) {
            isNoteVisible = !isNoteVisible
//        } else if (key.equals("u")) {
//            musicData.updateCurve("all")
        }
    }


    override fun exit() {
        println("exit() called.")
        super.exit()
    }

    fun makeLog(action: String) {
        jp.kthrlab.jamsketch.view.util.makeLog(action, musicData, config.general.log_dir, this)
    }

}

fun main() {
    PApplet.runSketch(arrayOf("jp.kthrlab.jamsketch.JamSketch"), JamSketch())
}
