package dev.tpcoder.medicalcheckup.appointment.repository

import dev.tpcoder.medicalcheckup.appointment.entity.Appointment
import org.springframework.data.repository.CrudRepository

interface AppointmentRepository: CrudRepository<Appointment, Long>