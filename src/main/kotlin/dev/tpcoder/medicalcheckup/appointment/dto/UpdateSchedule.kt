package dev.tpcoder.medicalcheckup.appointment.dto

import java.time.LocalDateTime

data class UpdateSchedule(
        val appointmentId: Long,
        val appointmentStatus: String,
        val appointmentOn: LocalDateTime,
)


