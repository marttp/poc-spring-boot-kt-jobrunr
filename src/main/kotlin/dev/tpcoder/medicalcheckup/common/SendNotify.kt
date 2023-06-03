package dev.tpcoder.medicalcheckup.common

interface SendNotify<T> {

    fun sendNotify(payload: T)
}