package jp.jamsketch.controller

import jp.jamsketch.model.Point

interface JamMouseListener {

    /**
     * マウスがクリックされたとき呼び出される
     * @param p
     */
    default void mouseClicked(Point p){}
    /**
     * マウスの左クリックを放したとき呼び出される
     * @param p
     */
    default void mouseReleased(Point p){}

    /**
     * 左クリックが押された瞬間に呼び出される。
     * @param p
     */
    default void mousePressed(Point p){}
}
