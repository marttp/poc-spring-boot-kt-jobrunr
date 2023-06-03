package dev.tpcoder.medicalcheckup.appointment

import dev.tpcoder.medicalcheckup.appointment.dto.CreateAppointmentRequest
import dev.tpcoder.medicalcheckup.appointment.dto.CreateAppointmentResponse
import dev.tpcoder.medicalcheckup.appointment.dto.UpdateSchedule
import dev.tpcoder.medicalcheckup.appointment.entity.Appointment
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/appointments")
class AppointmentController(private val appointmentService: AppointmentService) {

    private val logger = LoggerFactory.getLogger(AppointmentController::class.java)

    // Appointment - For Patient
    @PostMapping
    fun createAppointment(@RequestBody payload: CreateAppointmentRequest): CreateAppointmentResponse {
        val createdAppointment = appointmentService.createAppointment(payload)
        return CreateAppointmentResponse(
                id = createdAppointment.id!!,
                patientId = createdAppointment.patient.id!!,
                doctorId = createdAppointment.doctor.id!!,
                appointmentOn = createdAppointment.appointmentOn,
                appointmentType = createdAppointment.appointmentType,
                appointmentStatus = createdAppointment.appointmentStatus
        )
    }

    // Update State - Reschedule/Delete => For Patient
    @PutMapping("/{id}")
    fun rescheduleAppointment(@PathVariable id: Long, @RequestBody payload: UpdateSchedule): Appointment {
        return appointmentService.updateAppointment(id, payload)
    }

    // Appointment - Confirm for doctor
    @PutMapping("/{appointmentId}/doctors/{doctorId}/confirm")
    fun confirmAppointment(@PathVariable appointmentId: Long, @PathVariable doctorId: Long): Appointment {
        return appointmentService.confirmAppointment(appointmentId, doctorId)
    }
}