package jp.kthrlab.jamsketch.deprecated.model

class Note(private val position: MusicPosition, private val pitch: Int, private val duration: Int) {
    constructor(measure: Int, tick: Int, pitch: Int, duration: Int) : this(
        MusicPosition(measure, tick),
        pitch,
        duration
    )

    override fun equals(obj: Any?): Boolean {
        if (obj is Note) {
            val note = obj

            return note.position == position && note.pitch == pitch && note.duration == duration
        }

        return false
    }
}
