package com.example.notforgot.model.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}