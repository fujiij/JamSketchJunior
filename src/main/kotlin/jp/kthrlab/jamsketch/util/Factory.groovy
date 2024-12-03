package jp.kthrlab.jamsketch.util

interface Factory<T, A> {
    A get(T a);
}