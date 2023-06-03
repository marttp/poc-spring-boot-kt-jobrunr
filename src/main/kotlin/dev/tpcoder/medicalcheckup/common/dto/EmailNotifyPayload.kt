package dev.tpcoder.medicalcheckup.common.dto

data class EmailNotifyPayload(
        val to: String,
        val subject: String,
        val body: String)
