package jp.jamsketch.controller

import jp.jamsketch.main.JamSketchEngine
import jp.jamsketch.main.MusicData
import jp.jamsketch.model.Point
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Supplier

/**
 * JamSketchの操作クラス
 * サーバーやクライアントとして使う場合、このクラスのインスタンスを委譲する
 */
class JamSketchController
    /**
     * コンストラクタ
     *
     * @param musicData 楽譜データ
     * @param initData   初期化メソッド
     */
    (
        private var musicData: MusicData,
        private val engine: JamSketchEngine,
//    private val initData: Supplier<MusicData>,
    ) : IJamSketchController
{
    override fun addListener(listener: JamMouseListener?) {
        listeners.add(listener)
    }

    override fun mouseReleased(p: Point?) {
        for (m in listeners) m.mouseReleased(p)
    }

    /**
     * 初期化する
     */
    override fun init() {
        // do nothing
    }

    /**
     * Curveを更新する
     *
     * @param from 始点
     * @param thru 終点
     * @param y    Y座標
     * @param nn   note number（Y座標をnote numberに変換した値）
     */
    override fun updateCurve(from: Int, thru: Int, y: Int, nn: Double) {

        val size2 = musicData.num_of_measures * musicData.division
        val curveSize = musicData.curve1.size

        for (i in (from..thru)) {
            if (0 <= i) {
                // Store CursorPosition
                storeCursorPosition(i, y)

                // setEvidence (OUTLINE_LAYER)
//                val nn: Double = y2notenum(musicData.curve1[i]!!.toDouble())
                println("var nn: $nn curve1[ii] == ${musicData.curve1[i]}")
                val position: Int = (i * size2 / curveSize)
                if (position >= 0) {
                    setMelodicOutline((position / musicData.division), position % musicData.division, nn)
                }
            }
        }
    }

    override fun storeCursorPosition(i: Int, y: Int) {
        musicData.storeCursorPosition(i, y)
    }

   override fun setMelodicOutline(measure: Int, tick: Int, value: Double) {
        engine.setMelodicOutline(measure, tick, value)
    }

    /**
     * リセットする
     */
    override fun reset() {
        // 渡してきた初期化メソッドを実行する
        // 本来は初期化処理をこちらに移植した方が理想であるが、初期化処理が画面の操作と絡まっているため、渡してきたメソッドをそのまま実行することで実現している
//        this.melodyData = initData.get()

        this.musicData.initCurve()
        engine.initMelodicOutline()

        // need to reset pianoroll datamodel
    }

    private val listeners = CopyOnWriteArrayList<JamMouseListener>()
}
