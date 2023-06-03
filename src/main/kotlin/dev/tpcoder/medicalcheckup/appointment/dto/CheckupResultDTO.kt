package dev.tpcoder.medicalcheckup.appointment.dto

import java.time.LocalDateTime

data class CheckupResultDTO(
        var checkupDate: LocalDateTime,
        var notes: String?,
        var diagnosis: String?,
        var recommendations: String?
)
