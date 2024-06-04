package jp.jamsketch.controller;

/**
 * JamSketchを操作するクラスのインターフェース
 */
public interface IJamSketchController{

	/**
     * Curveを更新する
     * @param from 始点
     * @param thru 終点
     * @param y Y座標
     */
    public void updateCurve(int from, int thru, int y);

    /**
     * リセットする
     */
    public void reset();
}