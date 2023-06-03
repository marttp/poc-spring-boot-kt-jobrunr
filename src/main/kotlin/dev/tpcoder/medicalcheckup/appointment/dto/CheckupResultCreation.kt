package dev.tpcoder.medicalcheckup.appointment.dto

data class CheckupResultCreation(
        val result: CheckupResultDTO,
        val needNextCheckup: Boolean,
        val nextCheckupRange: Long? = 30
)
