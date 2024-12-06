package jp.kthrlab.jamsketch.deprecated

interface Factory<T, A> {
    A get(T a);
}