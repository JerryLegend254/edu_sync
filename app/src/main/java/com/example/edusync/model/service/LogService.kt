package com.example.edusync.model.service

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}