package jp.jamsketch.controller

import jp.jamsketch.main.MelodyData2
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
 * @param melodyData 楽譜データ
 * @param initData   初期化メソッド
 */(private var melodyData: MelodyData2, private val initData: Supplier<MelodyData2>) :
    IJamSketchController {
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
     */
    override fun updateCurve(from: Int, thru: Int, y: Int) {
        melodyData.storeCursorPosition(from, thru, y)
        melodyData.updateCurve(from, thru)
    }

    /**
     * リセットする
     */
    override fun reset() {
        // 渡してきた初期化メソッドを実行する
        // 本来は初期化処理をこちらに移植した方が理想であるが、初期化処理が画面の操作と絡まっているため、渡してきたメソッドをそのまま実行することで実現している
        this.melodyData = initData.get()
    }

    private val listeners = CopyOnWriteArrayList<JamMouseListener>()
}
