package dev.tpcoder.medicalcheckup.common.repository

import dev.tpcoder.medicalcheckup.common.entity.Doctor
import org.springframework.data.repository.CrudRepository

interface DoctorRepository: CrudRepository<Doctor, Long>