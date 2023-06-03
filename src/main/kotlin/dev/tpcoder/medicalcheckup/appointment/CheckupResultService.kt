package dev.tpcoder.medicalcheckup.appointment

import dev.tpcoder.medicalcheckup.appointment.dto.CheckupResultCreation
import dev.tpcoder.medicalcheckup.appointment.entity.Appointment
import dev.tpcoder.medicalcheckup.appointment.entity.CheckupResult
import dev.tpcoder.medicalcheckup.appointment.repository.AppointmentRepository
import dev.tpcoder.medicalcheckup.appointment.repository.CheckupResultRepository
import dev.tpcoder.medicalcheckup.common.dto.EmailNotifyPayload
import dev.tpcoder.medicalcheckup.common.repository.PatientRepository
import org.jobrunr.scheduling.BackgroundJob
import org.slf4j.LoggerFactory
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Service
import org.testcontainers.shaded.org.apache.commons.lang3.tuple.ImmutablePair
import java.time.Duration
import java.time.Instant

@Service
class CheckupResultService(
        private val checkupResultRepository: CheckupResultRepository,
        private val appointmentRepository: AppointmentRepository,
        private val patientRepository: PatientRepository,
        private val emailService: EmailService
) {

    private val logger = LoggerFactory.getLogger(CheckupResultService::class.java)

    fun completeCheckup(appointmentId: Long, payload: CheckupResultCreation): ImmutablePair<Appointment, CheckupResult> {
        val appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow { IllegalArgumentException("Appointment with ID $appointmentId not found") }
        val checkupResult = CheckupResult(
                checkupDate = payload.result.checkupDate,
                notes = payload.result.notes,
                diagnosis = payload.result.diagnosis,
                recommendations = payload.result.recommendations,
                appointment = appointment.id?.let { AggregateReference.to(it) }!!
        )
        // Save checkup result
        val savedCheckupResult = checkupResultRepository.save(checkupResult)
        sendCheckupResult(appointment, savedCheckupResult)
        return ImmutablePair.of(appointment, savedCheckupResult)
    }

    private fun sendCheckupResult(appointment: Appointment, savedCheckupResult: CheckupResult) {
        val patient = patientRepository.findById(appointment.patient.id!!)
                .orElseThrow { IllegalArgumentException("Patient with ID ${appointment.patient.id} not found") }
        // Schedule a job to send information to patient after 5 minutes
        val now = Instant.now()
        logger.info("At $now: Prepare checkup result to patient ${savedCheckupResult.appointment.id} ${patient.firstName} ${patient.lastName}")
        val delay = now.plus(Duration.ofMinutes(5))
        val emailNotifyPayload = EmailNotifyPayload(
                to = patient.email,
                subject = "Checkup result for appointment ${savedCheckupResult.appointment.id}",
                body = savedCheckupResult.toString()
        )
        BackgroundJob.schedule(delay) {
            emailService.sendNotify(emailNotifyPayload)
        }
    }
}