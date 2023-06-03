package dev.tpcoder.medicalcheckup.appointment

import dev.tpcoder.medicalcheckup.appointment.entity.AppointmentSeries
import dev.tpcoder.medicalcheckup.appointment.repository.AppointmentSeriesRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class AppointmentSeriesService(private val appointmentSeriesRepository: AppointmentSeriesRepository) {

    fun createAppointmentSeries(appointmentSeries: AppointmentSeries): AppointmentSeries {
        return appointmentSeriesRepository.save(appointmentSeries)
    }

    fun getAppointmentSeries(id: Long): Optional<AppointmentSeries> {
        return appointmentSeriesRepository.findById(id)
    }

}