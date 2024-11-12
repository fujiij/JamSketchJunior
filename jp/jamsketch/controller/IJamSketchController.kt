package jp.jamsketch.controller

import jp.jamsketch.model.Point

/**
 * JamSketchの操作クラスのインターフェース
 */
interface IJamSketchController {
    /**
     * 初期化する
     */
    fun init()

    /**
     * Curveを更新する
     *
     * @param from 始点
     * @param thru 終点
     * @param y    Y座標
     */
    fun updateCurve(from: Int, thru: Int, y: Int)

    /**
     * リセットする
     */
    fun reset()

    /**
     * 仮実装
     */
    fun addListener(listener: JamMouseListener?)

    fun mouseReleased(p: Point?)
}
