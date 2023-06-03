package dev.tpcoder.medicalcheckup.appointment

import dev.tpcoder.medicalcheckup.appointment.dto.CheckupResultCreation
import dev.tpcoder.medicalcheckup.appointment.entity.CheckupResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/checkups")
class CheckupController(private val checkupResultService: CheckupResultService, private val appointmentService: AppointmentService) {

    @PostMapping("/appointments/{appointmentId}/complete-checkup")
    fun completeCheckup(@PathVariable appointmentId: Long, @RequestBody payload: CheckupResultCreation): CheckupResult {
        val pairAppointment = checkupResultService.completeCheckup(appointmentId, payload)
        if (payload.needNextCheckup) {
            val recentAppointMent = pairAppointment.left
            appointmentService.createAppointmentBaseOnPrevious(recentAppointMent)
        }
        return pairAppointment.right
    }
}