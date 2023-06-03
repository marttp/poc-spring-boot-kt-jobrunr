package dev.tpcoder.medicalcheckup.appointment.entity

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("reminders")
data class Reminder(
        @Id val id: Long? = null,
        var appointment: AggregateReference<Appointment, Long>,
        var reminderDateTime: LocalDateTime,
        var reminderType: String, // could be 'email', 'sms', 'app_notification'
        var reminderTitle: String,
        var reminderUrl: String?
)