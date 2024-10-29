package jp.jamsketch.config

abstract class AbstractConfig {
    protected open fun load() {}
    protected open fun save() {}
}
