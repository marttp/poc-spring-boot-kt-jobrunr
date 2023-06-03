package dev.tpcoder.medicalcheckup

import dev.tpcoder.medicalcheckup.common.entity.Doctor
import dev.tpcoder.medicalcheckup.common.entity.Patient
import dev.tpcoder.medicalcheckup.common.repository.DoctorRepository
import dev.tpcoder.medicalcheckup.common.repository.PatientRepository
import org.jobrunr.scheduling.BackgroundJob
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.Random

@SpringBootApplication
class Application {

    private val logger = LoggerFactory.getLogger(Application::class.java)

    @Bean
    fun init(doctorRepository: DoctorRepository, patientRepository: PatientRepository) = ApplicationRunner {
        // Insert patients
        val patient1 = Patient(null, "Emma", "Stone", "emmastone@email.com", "123456789")
        val patient2 = Patient(null, "Robert", "Pattinson", "robertpattinson@email.com", "987654321")
        patientRepository.save(patient1)
        patientRepository.save(patient2)

        patientRepository.findAll().forEach { logger.info("Patient: $it") }

        // Insert doctors
        val doctor1 = Doctor(null, "Dr. Martin", "Freeman", "drmartinfreeman@email.com", "234567890")
        val doctor2 = Doctor(null, "Dr. Olivia", "Wilde", "droliviawilde@email.com", "876543210")
        doctorRepository.save(doctor1)
        doctorRepository.save(doctor2)

        doctorRepository.findAll().forEach { logger.info("Doctor: $it") }

        doneSomethingInterest()
    }

    fun doneSomethingInterest() {
        for (i in 1..5000) {
            BackgroundJob.enqueue {
                simulateTask()
            }
        }
    }

    fun simulateTask() {
        if (simulateSuccess()) {
            println("Success")
        } else {
            throw RuntimeException("Failed")
        }
    }

    fun simulateSuccess(): Boolean {
        val randomValue = (0..100).random()
        return randomValue <= 99
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
