package dev.tpcoder.medicalcheckup.appointment.entity

import dev.tpcoder.medicalcheckup.common.entity.Doctor
import dev.tpcoder.medicalcheckup.common.entity.Patient
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("appointments")
data class Appointment(
        @Id val id: Long? = null,
        var patient: AggregateReference<Patient, Long>,
        var doctor: AggregateReference<Doctor, Long>,
        var appointmentOn: LocalDateTime,
        var appointmentType: String = "check_up",
        var appointmentStatus: String = "waiting", // could be 'waiting', 'scheduled', 'rescheduled', 'cancelled', 'completed'
        var series: AggregateReference<AppointmentSeries, Long>?
)
