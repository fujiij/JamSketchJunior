package jp.jamsketch.controller

import jp.jamsketch.model.Point;

/**
 * JamSketchの操作クラスのインターフェース
 */
public interface IJamSketchController{

    /**
     * 初期化する
     */
    public void init();

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

    /**
     * 仮実装
     */
    public void addListener(JamMouseListener listener);

    def void mouseReleased(Point p);
}