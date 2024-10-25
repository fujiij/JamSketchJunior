package jp.jamsketch.config

sealed class AbstractConfig {
    abstract fun load()
    abstract fun save()
}
