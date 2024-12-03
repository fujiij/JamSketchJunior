package jp.kthrlab.jamsketch.model

import java.util.function.Consumer

interface ICurveContainer {
    /**
     * MODはカーブデータをここで初期化する
     *
     * @param curves Curveクラスが保有するCurveData
     */
    fun initCurveData(curves: HashMap<Int, CurveData>)

    /**
     * 新しく座標が更新されたときに呼び出されるメソッド。
     * 　各クラスが追加されたCurveDataに座標を更新する必要がある。
     *
     * @param p      新しく追加される座標の情報
     * @param curves Curveクラスが保有するCurveData
     */
    fun updateCurve(p: Point, curves: HashMap<Int, CurveData>, endUpdateCurveAction: Consumer<CurveData>)

    /**
     * カーブデータの座標の情報をすべて削除する。
     *
     * @param curves
     */
    fun removeAll(curves: HashMap<Int, CurveData>)

    fun tick(curves: HashMap<Int, CurveData>) {
    }
}
