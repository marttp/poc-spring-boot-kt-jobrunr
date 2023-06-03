package dev.tpcoder.medicalcheckup.appointment.repository

import dev.tpcoder.medicalcheckup.appointment.entity.Reminder
import org.springframework.data.repository.CrudRepository

interface ReminderRepository: CrudRepository<Reminder, Long>