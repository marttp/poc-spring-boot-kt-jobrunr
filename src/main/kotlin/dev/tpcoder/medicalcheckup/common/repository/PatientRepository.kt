package dev.tpcoder.medicalcheckup.common.repository

import dev.tpcoder.medicalcheckup.common.entity.Patient
import org.springframework.data.repository.CrudRepository

interface PatientRepository: CrudRepository<Patient, Long>
