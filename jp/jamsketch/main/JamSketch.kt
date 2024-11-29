package jp.jamsketch.main

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
import jp.jamsketch.model.Point
import jp.jamsketch.util.Tick
import jp.jamsketch.view.IDisplay
import jp.jamsketch.web.ServiceLocator
import processing.core.PApplet
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

// TODO: Remove temporarily implemented interfaces(IConfigAccessible)
class JamSketch : SimplePianoRoll(), IConfigAccessible {

    companion object {
        const val PACKAGE_NAME: String = "jp.jamsketch.main"
    }

    override val config = AccessibleConfig.config

    // TODO: Delete comment
    //  fullMeasure -> totalMeasures
    //  mCurrentMeasure -> currentMeasureInTotalMeasures
    val totalMeasures: Int = config.num_of_measures * config.repeat_times
    val timelineWidth: Int = config.view_width - config.keyboard_width
    private var currentMeasureInTotalMeasures = 0   // TODO: わかりやすい変数名にしたが、もっと短くしたい

    var musicData: MusicData =
        MusicData(
            config.midfilename,
            timelineWidth,
            config.initial_blank_measures,
            config.beats_per_measure,
            config.num_of_measures,
            config.repeat_times,
            config.division,
            config.channel_acc,
        )

    var engine: JamSketchEngine = ((Class.forName(PACKAGE_NAME + "." + config.jamsketch_engine).getConstructor().newInstance()) as JamSketchEngine).let {
        val target_part: SCCDataSet.Part  = musicData.scc.toDataSet().getFirstPartWithChannel(config.channel_acc)
        it.init(musicData.scc, target_part, config)
        it.resetMelodicOutline()
        it.setFirstMeasure(config.initial_blank_measures)
        it
    }

    var controller: IJamSketchController? = null

    var guideData: GuideData? = null

    var nowDrawing: Boolean = false
    var username: String = ""

    // A dialogue for JamSketchEventListener
    var panel: JPanel  = JPanel().let {
        val layout = BoxLayout(it, BoxLayout.Y_AXIS)
        it.layout = layout
        it.add(JLabel("The connection is lost."))
        it
    }

    // new objects
    var model: JamSketchModel? = null
    var displays: CopyOnWriteArrayList<IDisplay> = CopyOnWriteArrayList()
    var ticker: CopyOnWriteArrayList<Tick> = CopyOnWriteArrayList()

    init {
        // TODO: delete comment
        //  initData() から移動。読込は一度だけで良いはず。
        //  -> 現時点ではresetMusic()でもsmfreadを呼ばないと生成したメロディが残ってしまうが、本来はresetMelodicOutline()で対応すべき処理である。
        smfread((musicData.scc as SCCDataSet).midiSequence)

        // TODO: Delete comments
        // 20241118 initData() で行われていた処理を移動している
        initPianoRollDataModel((musicData.scc as SCCDataSet).getFirstPartWithChannel(config.channel_acc))
        initMusicPlayer()

        // init controller
        val listener: JamSketchEventListener = JamSketchEventListenerImpl(panel)
        //// JamSketch操作クラスを初期化
        val origController = JamSketchController(
            musicData,
            engine,
//            this::initData
        )

        controller = if (config.mode == "server") {
            // サーバーで動かす場合に使う操作クラスを設定
            JamSketchServerController(
                config.host,
                config.port,
                origController
            )
        } else if (config.mode == "client") {
            // クライアントで動かす場合に使う操作クラスを設定
            JamSketchClientController(
                config.host,
                config.port,
                origController,
                listener
            )
        } else {
            // スタンドアロンで動かす場合はそのまま
            origController
        }

        val serviceLocator = ServiceLocator.GetInstance()
        serviceLocator.setContoller(controller!!)

        // ?
//        LunchInitFunction.create(this).setup()
    }

    /**
     * Role: Used for initial graphical settings such as window size and rendering mode.
     * Invocation Timing: Called first when the PApplet is initialized.
     * Usage Example: Functions like size(), fullScreen(), smooth(), and noSmooth().
     * Constraints: This method cannot contain drawing code like background() or fill().
     */
    override fun settings() {
        super.settings()
        size(config.view_width, config.view_height)
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
        if (config.mode == "client") {
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
        // need to init after setDataModel (initPianoRollDataModel) & settings()
        guideData =
            if (config.show_guide) {
                GuideData(
                    config.midfilename,
                    timelineWidth,
                    config.initial_blank_measures,
                    config.beats_per_measure,
                    config.num_of_measures,
                    config.repeat_times,
                    config.guide_smoothness,
                    config.channel_guide,
                    config.keyboard_width,
                    this::notenum2y,
                    this::beat2x,
                )
            } else {
                null
            }

        // add WindowListener (windowClosing) which calls exit();
    }

    private fun initMusicPlayer() {
        // ((SMFPlayer)this.musicPlayer[i]).setTickPosition(tick);
        tickPosition = 0
    }

    private fun initPianoRollDataModel(part: SCCDataSet.Part) {
        dataModel = part.getPianoRollDataModel(config.initial_blank_measures,
            config.initial_blank_measures + config.num_of_measures)
    }

    /**
     * ----------
     * Caution!
     * ----------
     * Don't write your draw() directly here.
     * If you use JamSketch in your research and need to add your own features, write it in drawAdditionalElements().
     */
    override fun draw() {
        super.draw()

        // Manipulating mouseX
        if (config.forced_progress) {
            mouseX = beat2x(currentMeasure + config.how_in_advance, currentBeat).toInt()
        }

        // Updating the curve and processing related to it
        updateCurveDuringDraw()

        // Drawing visual elements
        drawBasicElements()
        drawAdditionalElements()

        // The last measure of each page
        if (isLastMeasure()) processLastMeasure()
    }

    /**
     * Draw basic visual elements
     */
    private fun drawBasicElements() {
        // new object
//        for (d in displays) {
//            d.display(this)
//        }

        enhanceCursor()
        drawProgress()
    }

    /**
     * Draw additional visual elements
     */
    private fun drawAdditionalElements() {
        if (guideData != null) drawGuideCurve()

        // new object
        for (t in ticker) t.tick()
    }

    private fun isLastMeasure(): Boolean {
        return currentMeasure == config.num_of_measures - config.num_of_reset_ahead
    }

    private fun updateCurveDuringDraw() {
        if (pmouseX < mouseX && mouseX > beat2x(currentMeasure, currentBeat) + 10) {
            if (isUpdatable) {
                updateCurve()
            }
        }
    }


    fun drawGuideCurve() {
        strokeWeight(3f)
        stroke(100f, 200f, 200f)
        (0 until (guideData!!.curveGuideView!!.size)).forEach { i ->
            if (guideData!!.curveGuideView!![i] != null &&
                guideData!!.curveGuideView!![i+1] != null) {
                line((i + config.keyboard_width).toDouble(), guideData!!.curveGuideView!![i]!!.toDouble(),
                    (i+ config.keyboard_width + 1).toDouble(), guideData!!.curveGuideView!![i+1]!!.toDouble())
            }
        }
    }

    fun updateCurve() {
        // JamSketch操作クラスを使用して楽譜データを更新する
        if(config.keyboard_width < pmouseX && config.keyboard_width < mouseX) {
            this.controller!!.updateCurve(
                pmouseX - config.keyboard_width,
                mouseX - config.keyboard_width,
                mouseY,
                y2notenum(mouseY.toDouble()),
            )
        }

        // test JamSketchModel
//        val p = Point(mouseX, mouseY)
//        model!!.curve.updateCurve(p)
    }

    val isUpdatable: Boolean
        get() {
            if ((!config.on_drag_only || nowDrawing) && isInside(mouseX, mouseY)
            ) {
//                val m1 = x2measure(mouseX.toDouble())
                val m0 = x2measure(pmouseX.toDouble())
                return 0 <= m0 && pmouseX < mouseX
            } else {
                return false
            }
        }

    /**
     * The process to be performed in the last measure on the screen
     */
    fun processLastMeasure() {
        makeLog("melody")
        // TODO: melody_resetting の意味を確認
        if (config.melody_resetting) {
            if (currentMeasureInTotalMeasures < (totalMeasures - config.num_of_reset_ahead)) {
                dataModel.shiftMeasure(config.num_of_measures)
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
            dataModel.firstMeasure = config.initial_blank_measures

            // Set GuideData start position back to 0
            resetGuideData()
        }

        // SCCGenerator.firstMeasure = num
        engine.setFirstMeasure(dataModel.firstMeasure)
    }

    fun enhanceCursor() {
        if (config.cursor_enhanced) {
            fill(255f, 0f, 0f)
            ellipse(mouseX.toFloat(), mouseY.toFloat(), 10f, 10f)
        }
    }

    fun drawProgress() {
        if (isNowPlaying) {
            // currentMeasureInTotalMeasures    演奏全体での現在の小節番号
            // currentMeasure                   ページ内の小節番号
            // dataModel.firstMeasure           ページ開始位置
            // initial_blank_measures           ページ開始位置のoffset
            // +1                               0小節目ではなく1小節目とするために加算
            currentMeasureInTotalMeasures =
                (currentMeasure + dataModel.firstMeasure - config.initial_blank_measures + 1)
            // TODO:確認後削除 totalMeasures で良いのでは？
//            val mtotal: Int = dataModel.measureNum * config.repeat_times
            textSize(32f)
            fill(0f, 0f, 0f)
            // TODO:確認後削除 totalMeasures で良いのでは？
//            text(currentMeasureInTotalMeasures.toString() + " / " + mtotal, 460f, 675f)
            text(currentMeasureInTotalMeasures.toString() + " / " + totalMeasures, 460f, 675f)
        }
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

    private fun resetGuideData() {
        if (guideData != null) guideData!!.fromMeasure = 0
        guideData!!.updateCurveGuideView(0, guideData!!.size)
    }

    fun resetMusic() {

        // TODO: controllerが操作する範囲を確認
        // JamSketch操作クラスを使用してリセットする
        controller!!.reset()

        // 元々resetMusic() で呼ばれていたもの
        // initData() を渡すためにinitDataに入っていた
        initMusicPlayer()

        // set new PianoRollDataModel to dataModel
        initPianoRollDataModel((musicData.scc.toDataSet()).getFirstPartWithChannel(config.channel_acc))

        // Set GuideData start position back to 0
        resetGuideData()

        // TODO: [debug] noteをremove & element.setRest(true) してもsmfreadしないとreset前の音が残ってしまう
        smfread((musicData.scc as SCCDataSet).midiSequence)

        // new object
//        model!!.curve.clear()

        makeLog("reset")
    }

    public override fun musicStopped() {
        super.musicStopped()
    }

    /**
     * 再接続する
     */
    fun reconnect() {
        controller!!.init()
    }

    override fun mousePressed() {
        nowDrawing = true
    }

    override fun mouseReleased() {
        nowDrawing = false
        controller!!.mouseReleased(Point(mouseX, mouseY))
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
//            musicData.updateCurve("all")
        }
    }


    override fun exit() {
        println("exit() called.")
        super.exit()
    }

    fun makeLog(action: String) {
// リファクタリング中のため一時的にコメントアウト
// \output_Thu_Nov_21_15-57-46_GMT+09-00_2024_melody.mid (アクセスが拒否されました。)
//        makeLog(action, musicData, config.log_dir, this)
    }

}

fun main() {
    PApplet.runSketch(arrayOf("jp.jamsketch.main.JamSketch"), JamSketch())
}
