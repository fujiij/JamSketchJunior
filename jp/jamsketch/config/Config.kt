package jp.jamsketch.config

import com.fasterxml.jackson.annotation.JsonIgnore
import jp.crestmuse.cmx.misc.ChordSymbol2

class Config() : ConfigJSON() {
    @JsonIgnore
    var chord_symbols: MutableList<ChordSymbol2> = mutableListOf()
}
