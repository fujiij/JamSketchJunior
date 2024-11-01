package jp.jamsketch.model.melodygenerator

object NoteSeqGeneratorRegistry {

    lateinit var noteSeqGenerators: MutableMap<String, Lazy<AbstractNoteSeqGenerator>>

    init {
        noteSeqGenerators[SimpleNoteSeqGenerator::class.simpleName.toString()] =
            lazy {
                SimpleNoteSeqGenerator()
            }
    }
}