package jp.kthrlab.jamsketch.model

class MusicPosition {
    constructor(measure: Int, tick: Int) {
        this.measure = measure
        this.tick = tick
    }

    constructor(mouseX: Double, mouseY: Double)

    override fun equals(obj: Any?): Boolean {
        if (obj is MusicPosition) {
            val mp = obj
            return mp.tick == tick && mp.measure == measure
        } else {
            return false
        }
    }

    private var measure = 0
    private var tick = 0
}
