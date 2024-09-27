package jp.jamsketch.util

interface Factory<T, A> {
    A get(T a);
}