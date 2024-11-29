package jp.jamsketch.main

import jp.crestmuse.cmx.inference.MusicCalculator
import jp.kthrlab.jamsketch.NoteSeqGeneratorTF1

class JamSketchEngineTF1(
//    private val num_of_measures: Int,
//    private val division: Int,
//    private val melody_execution_span: Int,
//    private val tf_model_dir: String,
//    private val tf_note_num_start: Int,
//    private val tf_model_input_col: Int,
//    private val tf_model_output_col: Int,
//    private val tf_rest_col: Int,
//    private val tf_chord_col_start: Int,
//    private val tf_note_con_col_start: Int,
//    private val tf_num_of_melody_element: Int,

    ) : JamSketchEngineAbstract() {

    private lateinit var noteSeqGeneratorTf1: NoteSeqGeneratorTF1

    override fun initMusicRepresentationLocal() {
            mr.addMusicLayer(MELODY_LAYER, Array(cfg!!.tf_note_con_col_start){it})
        }

        override fun initLocal() {
            noteSeqGeneratorTf1 = NoteSeqGeneratorTF1(
                cfg!!.num_of_measures,
                cfg!!.division,
                cfg!!.melody_execution_span,
                cfg!!.tf_model_dir,
                cfg!!.tf_model_layer,
                cfg!!.tf_note_num_start,
                cfg!!.tf_model_input_col,
                cfg!!.tf_model_output_col,
                cfg!!.tf_rest_col,
                cfg!!.tf_chord_col_start,
                cfg!!.tf_num_of_melody_element,
            )

        }

//    override fun parameters(): Map<String, Double> {
//        return emptyMap()
//    }
//
//    override fun paramDesc(): Map<String, String> {
//        return emptyMap()
//    }

    override fun musicCalculatorForOutline() : MusicCalculator? {
        return null
    }

    override fun outlineUpdated(measure: Int, tick: Int) {
        noteSeqGeneratorTf1.updated(measure, tick, OUTLINE_LAYER, mr)
    }

    override fun automaticUpdate(): Boolean {
        return false
    }
}

