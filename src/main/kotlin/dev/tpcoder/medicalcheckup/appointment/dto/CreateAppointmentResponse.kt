package dev.tpcoder.medicalcheckup.appointment.dto

import java.time.LocalDateTime

data class CreateAppointmentResponse(
        val id: Long,
        val patientId: Long,
        val doctorId: Long,
        val appointmentOn: LocalDateTime,
        val appointmentType: String,
        val appointmentStatus: String
)
