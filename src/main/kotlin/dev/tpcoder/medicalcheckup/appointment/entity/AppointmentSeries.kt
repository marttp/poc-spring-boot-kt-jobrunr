package dev.tpcoder.medicalcheckup.appointment.entity

import dev.tpcoder.medicalcheckup.common.entity.Patient
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("appointment_series")
data class AppointmentSeries(
        @Id val id: Long? = null,
        var patient: AggregateReference<Patient, Long>,
        var startDate: LocalDateTime,
        var recurrenceFrequency: Int? = 0, // in days
        var status: String = "active" // could be 'active' or 'completed'
)
