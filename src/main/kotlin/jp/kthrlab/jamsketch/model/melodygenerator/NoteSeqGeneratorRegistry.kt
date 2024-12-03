package jp.kthrlab.jamsketch.model.melodygenerator

object NoteSeqGeneratorRegistry {

    lateinit var noteSeqGenerators: MutableMap<String, Lazy<AbstractNoteSeqGenerator>>

    init {
        noteSeqGenerators[NoteSeqGeneratorSimple::class.simpleName.toString()] =
            lazy {
                NoteSeqGeneratorSimple()
            }
    }
}