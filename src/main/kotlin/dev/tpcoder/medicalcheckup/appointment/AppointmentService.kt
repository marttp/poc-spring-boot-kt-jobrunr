package dev.tpcoder.medicalcheckup.appointment

import dev.tpcoder.medicalcheckup.appointment.dto.CreateAppointmentRequest
import dev.tpcoder.medicalcheckup.appointment.dto.UpdateSchedule
import dev.tpcoder.medicalcheckup.appointment.entity.Appointment
import dev.tpcoder.medicalcheckup.appointment.entity.AppointmentSeries
import dev.tpcoder.medicalcheckup.appointment.repository.AppointmentRepository
import dev.tpcoder.medicalcheckup.common.Constants
import dev.tpcoder.medicalcheckup.common.dto.EmailNotifyPayload
import dev.tpcoder.medicalcheckup.common.repository.PatientRepository
import org.jobrunr.scheduling.JobScheduler
import org.slf4j.LoggerFactory
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.math.log

@Service
class AppointmentService(
        private val appointmentRepository: AppointmentRepository,
        private val patientRepository: PatientRepository,
        private val emailService: EmailService,
        private val appointmentSeriesService: AppointmentSeriesService,
        private val jobScheduler: JobScheduler) {

    private val logger = LoggerFactory.getLogger(AppointmentService::class.java)

    fun createAppointment(payload: CreateAppointmentRequest): Appointment {
        // If appointmentSeriesId is provided, use it to fetch the existing AppointmentSeries
        val appointmentSeries: AppointmentSeries? = if (payload.appointmentSeriesId != null) {
            appointmentSeriesService.getAppointmentSeries(payload.appointmentSeriesId)
                    .orElseThrow { IllegalArgumentException("Appointment Series with id ${payload.appointmentSeriesId} not found") }
        } else {
            appointmentSeriesService.createAppointmentSeries(
                    AppointmentSeries(
                            id = null,
                            patient = AggregateReference.to(payload.patientId),
                            startDate = LocalDateTime.now()))
        }
        val appointment = Appointment(
                null,
                patient = AggregateReference.to(payload.patientId),
                doctor = AggregateReference.to(payload.doctorId),
                appointmentType = payload.appointmentType, // Normal by default
                appointmentOn = payload.appointmentOn,
                appointmentStatus = payload.appointmentStatus ?: Constants.APPOINTMENT_STATUS_WAITING,
                series = AggregateReference.to(appointmentSeries?.id!!)
        )
        logger.info("Creating appointment: $appointment")
        // Insert validation and business logic here
        return appointmentRepository.save(appointment)
    }

    fun createAppointmentBaseOnPrevious(appointment: Appointment, nextCheckupRange: Long = 30): Appointment {
        val body = CreateAppointmentRequest(
                patientId = appointment.patient.id!!,
                doctorId = appointment.doctor.id!!,
                appointmentOn = LocalDateTime.now().plusDays(nextCheckupRange),
                appointmentType = appointment.appointmentType,
                appointmentStatus = Constants.APPOINTMENT_STATUS_SCHEDULED
        )
        val newAppointment = this.createAppointment(body)
        return this.confirmAppointment(newAppointment.id!!, newAppointment.doctor.id!!)
    }

    fun updateAppointment(id: Long, payload: UpdateSchedule): Appointment {
        val appointment = appointmentRepository.findById(id)
                .orElseThrow { IllegalArgumentException("Appointment with ID $id not found") }
        when (payload.appointmentStatus) {
            Constants.APPOINTMENT_STATUS_RESCHEDULED -> {
                appointment.appointmentOn = payload.appointmentOn
                appointment.appointmentStatus = Constants.APPOINTMENT_STATUS_RESCHEDULED
            }
            Constants.APPOINTMENT_STATUS_CANCELLED -> {
                appointment.appointmentStatus = Constants.APPOINTMENT_STATUS_CANCELLED
            }
            else -> {
                throw IllegalArgumentException("Invalid appointment status")
            }
        }
        return appointmentRepository.save(appointment)
    }


    fun confirmAppointment(appointmentId: Long, doctorId: Long): Appointment {
        val appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow { IllegalArgumentException("Appointment with ID $appointmentId not found") }
        appointment.appointmentStatus = Constants.APPOINTMENT_STATUS_SCHEDULED
        val savedAppointment = appointmentRepository.save(appointment)
        sendToPatient(savedAppointment)
        return savedAppointment
    }

    fun sendToPatient(appointment: Appointment) {
        val patient = patientRepository.findById(appointment.patient.id!!)
                .orElseThrow { IllegalArgumentException("Patient with ID ${appointment.patient.id} not found") }
        val status = appointment.appointmentStatus.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        val emailNotifyPayload = EmailNotifyPayload(
                to = patient.email,
                subject = "Appointment ${appointment.id} $status",
                body = "Your appointment has been $status"
        )
        sendingEmail(emailNotifyPayload)
    }

    fun sendingEmail(payload: EmailNotifyPayload) {
        // Place to JobRunr without concerns of JobId
        jobScheduler.enqueue {
            emailService.sendNotify(payload)
        }
    }
}