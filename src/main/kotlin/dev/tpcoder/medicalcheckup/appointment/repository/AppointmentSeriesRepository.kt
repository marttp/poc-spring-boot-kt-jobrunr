package dev.tpcoder.medicalcheckup.appointment.repository

import dev.tpcoder.medicalcheckup.appointment.entity.AppointmentSeries
import org.springframework.data.repository.CrudRepository

interface AppointmentSeriesRepository: CrudRepository<AppointmentSeries, Long>