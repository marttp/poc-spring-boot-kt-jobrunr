package dev.tpcoder.medicalcheckup.appointment.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.jdbc.core.mapping.AggregateReference
import java.time.LocalDateTime

@Table("checkup_results")
data class CheckupResult(
        @Id val id: Long? = null,
        var appointment: AggregateReference<Appointment, Long>,
        var checkupDate: LocalDateTime,
        var notes: String?,
        var diagnosis: String?,
        var recommendations: String?
)
