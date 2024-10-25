package jp.jamsketch.config
open class ConfigJSON(
    var midfilename: String = "",
    var chordprog: ArrayList<String> = arrayListOf(),
    var num_of_measures: Int = 0,
    var num_of_reset_ahead: Int = 0,
    var division: Int = 0,
    var beats_per_measure: Int = 0,
    var initial_blank_measures: Int = 0,
    var repeat_times: Int = 0,
    var calc_length: Int = 0,
    var model_file: String = "",
    var log_dir: String = "",
    var expression: Boolean = false,
    var melody_reseting: Boolean = true,
    var cursor_enhanced: Boolean = true,
    var on_drag_only: Boolean = true,
    var forced_progress: Boolean = false,
    var melody_execution_span: Int = 0,
    var octave_program: MutableMap<String, String> = mutableMapOf(),
    var jamsketch_engine: String = "",
    var tf_note_con_col_start: Int = 0,
)
