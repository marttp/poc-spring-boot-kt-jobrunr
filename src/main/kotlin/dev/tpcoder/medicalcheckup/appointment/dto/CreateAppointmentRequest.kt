package dev.tpcoder.medicalcheckup.appointment.dto

import java.time.LocalDateTime

data class CreateAppointmentRequest(
        val patientId: Long,
        val doctorId: Long,
        val appointmentOn: LocalDateTime,
        val appointmentType: String,
        val appointmentStatus: String?,
        val appointmentSeriesId: Long? = null
)
