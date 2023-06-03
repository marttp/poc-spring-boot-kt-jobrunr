package dev.tpcoder.medicalcheckup

import dev.tpcoder.medicalcheckup.appointment.AppointmentSeriesService
import dev.tpcoder.medicalcheckup.appointment.AppointmentService
import dev.tpcoder.medicalcheckup.appointment.EmailService
import dev.tpcoder.medicalcheckup.appointment.dto.CreateAppointmentRequest
import dev.tpcoder.medicalcheckup.appointment.dto.UpdateSchedule
import dev.tpcoder.medicalcheckup.appointment.entity.Appointment
import dev.tpcoder.medicalcheckup.appointment.entity.AppointmentSeries
import dev.tpcoder.medicalcheckup.appointment.repository.AppointmentRepository
import dev.tpcoder.medicalcheckup.common.Constants
import dev.tpcoder.medicalcheckup.common.entity.Patient
import dev.tpcoder.medicalcheckup.common.repository.PatientRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.jobrunr.jobs.JobId
import org.jobrunr.scheduling.JobScheduler
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.springframework.data.jdbc.core.mapping.AggregateReference
import java.time.LocalDateTime
import java.util.*

class AppointmentServiceTest {

    private val appointmentRepository = mockk<AppointmentRepository>(relaxed = true)
    private val patientRepository = mockk<PatientRepository>(relaxed = true)
    private val emailService = mockk<EmailService>(relaxed = true)
    private val appointmentSeriesService = mockk<AppointmentSeriesService>(relaxed = true)
    private val jobScheduler = mockk<JobScheduler>(relaxed = true)

    private lateinit var appointmentService: AppointmentService

    @BeforeEach
    fun setup() {
        appointmentService = spyk(AppointmentService(appointmentRepository, patientRepository, emailService, appointmentSeriesService, jobScheduler))
    }

    @AfterEach
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `should create an appointment`() {
        val payload = CreateAppointmentRequest(
                patientId = 1L,
                doctorId = 1L,
                appointmentOn = LocalDateTime.now(),
                appointmentType = "check_up",
                appointmentSeriesId = 1L,
                appointmentStatus = null
        )

        val series = AppointmentSeries(
                id = 1L,
                patient = AggregateReference.to(1L),
                startDate = LocalDateTime.now())

        every { appointmentSeriesService.getAppointmentSeries(any()) } returns Optional.of(series)
        every { appointmentRepository.save(any()) } answers { firstArg() }

        val result = appointmentService.createAppointment(payload)

        assertThat(result).isNotNull()
        verify { appointmentRepository.save(any()) }
    }

    @Test
    fun `should update an appointment`() {
        val payload = UpdateSchedule(
                appointmentId = 1L,
                appointmentOn = LocalDateTime.now(),
                appointmentStatus = Constants.APPOINTMENT_STATUS_RESCHEDULED
        )

        val appointment = Appointment(
                id = 1L,
                patient = AggregateReference.to(1L),
                doctor = AggregateReference.to(1L),
                appointmentOn = LocalDateTime.now(),
                appointmentType = "check_up",
                appointmentStatus = Constants.APPOINTMENT_STATUS_SCHEDULED,
                series = AggregateReference.to(1L)
        )

        every { appointmentRepository.findById(any()) } returns Optional.of(appointment)
        every { appointmentRepository.save(any()) } answers { firstArg() }

        val result = appointmentService.updateAppointment(1L, payload)

        assertThat(result).isNotNull()
        verify { appointmentRepository.save(any()) }
    }

    @Test
    fun `should confirm an appointment`() {
        val appointment = Appointment(
                id = 1L,
                patient = AggregateReference.to(1L),
                doctor = AggregateReference.to(1L),
                appointmentOn = LocalDateTime.now(),
                appointmentType = "check_up",
                appointmentStatus = Constants.APPOINTMENT_STATUS_WAITING,
                series = AggregateReference.to(1L)
        )

        every { appointmentRepository.findById(any()) } returns Optional.of(appointment)
        every { appointmentRepository.save(any()) } answers { firstArg() }

        val patient = Patient(1L, "Emma", "Stone", "emmastone@email.com", "123456789")
        every { patientRepository.findById(any()) } returns Optional.of(patient)
        every { jobScheduler.enqueue(any()) } returns JobId.parse(UUID.randomUUID().toString())

        val result = appointmentService.confirmAppointment(1L, 1L)

        assertThat(result).isNotNull()
        assertThat(result.appointmentStatus).isEqualTo(Constants.APPOINTMENT_STATUS_SCHEDULED)
        verify { appointmentRepository.save(any()) }
    }

    @Test
    fun `should create an appointment based on previous`() {
        val appointment = Appointment(
                id = 1L,
                patient = AggregateReference.to(1L),
                doctor = AggregateReference.to(1L),
                appointmentOn = LocalDateTime.now(),
                appointmentType = "check_up",
                appointmentStatus = Constants.APPOINTMENT_STATUS_SCHEDULED,
                series = AggregateReference.to(1L)
        )

        every { appointmentService.createAppointment(any()) } returns appointment
        every { appointmentService.confirmAppointment(any(), any()) } returns appointment

        val result = appointmentService.createAppointmentBaseOnPrevious(appointment, 30)

        assertThat(result).isNotNull()
        verify { appointmentService.createAppointment(any()) }
        verify { appointmentService.confirmAppointment(any(), any()) }
    }

}
