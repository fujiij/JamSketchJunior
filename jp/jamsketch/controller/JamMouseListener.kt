package jp.jamsketch.controller

import jp.jamsketch.model.Point

interface JamMouseListener {
    /**
     * マウスがクリックされたとき呼び出される
     *
     * @param p
     */
    fun mouseClicked(p: Point?) {
    }

    /**
     * マウスの左クリックを放したとき呼び出される
     *
     * @param p
     */
    fun mouseReleased(p: Point?) {
    }

    /**
     * 左クリックが押された瞬間に呼び出される。
     *
     * @param p
     */
    fun mousePressed(p: Point?) {
    }
}
