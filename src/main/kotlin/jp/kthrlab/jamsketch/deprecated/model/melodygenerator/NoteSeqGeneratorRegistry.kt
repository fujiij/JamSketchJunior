package jp.kthrlab.jamsketch.deprecated.model.melodygenerator

import jp.kthrlab.jamsketch.music.generator.NoteSeqGeneratorSimple

object NoteSeqGeneratorRegistry {

    lateinit var noteSeqGenerators: MutableMap<String, Lazy<AbstractNoteSeqGenerator>>

    init {
        noteSeqGenerators[NoteSeqGeneratorSimple::class.simpleName.toString()] =
            lazy {
                NoteSeqGeneratorSimple()
            }
    }
}