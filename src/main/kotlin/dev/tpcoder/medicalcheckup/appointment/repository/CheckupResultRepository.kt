package dev.tpcoder.medicalcheckup.appointment.repository

import dev.tpcoder.medicalcheckup.appointment.entity.CheckupResult
import org.springframework.data.repository.CrudRepository

interface CheckupResultRepository: CrudRepository<CheckupResult, Long>