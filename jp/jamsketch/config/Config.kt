package jp.jamsketch.config

import com.fasterxml.jackson.annotation.JsonIgnore
import jp.crestmuse.cmx.misc.ChordSymbol2

class Config() : ConfigJSON() {
    @JsonIgnore
    var chord_symbols: MutableList<ChordSymbol2> = mutableListOf()

    // The following is a proposal for categorizing setting items

    /**
     * Music
     */
    object music {
        var midfilename: String = ""
        var chordprog: Array<String> = emptyArray()
        var num_of_measures: Int = 0
        var num_of_reset_ahead: Int = 0
        var division: Int = 0
        var beats_per_measure: Int = 0
        var initial_blank_measures: Int = 0
        var repeat_times: Int = 0
        var model_file: String = ""
        var expression: Boolean = false
        var melody_execution_span: Int = 0
        var how_in_advance: Int = 0
        var channel_guide: Int = 0
        var channel_acc: Int = 0
        var input_file_path: String = ""
        var ent_bias: Double = 0.0
    }

    /**
     * Overall application behavior
     */
    object general {
        var jamsketch_engine: String = ""
        var melody_generate_engine: String = ""
        var note_seq_generator: String = ""
        var log_dir: String = ""
        var mode: String = ""
        var host: String = ""
        var port: Int = 0
        var melody_reseting: Boolean = true
        var cursor_enhanced: Boolean = true
        var on_drag_only: Boolean = true
        var forced_progress: Boolean = false
        var keyboard_width: Int = 0
        var guide_smoothness: Int = 0
        var melody_resetting: Boolean = false
        var octave_program: MutableMap<String, String> = mutableMapOf()
        var show_guide: Boolean = false
    }

    /**
     * Genetic Algorithm
     */
    object ga {
        var calc_length: Int = 0
    }

    /**
     * TensorFlow
     */
    object tf {
        var tf_note_con_col_start: Int = 0
    }
}


