package dev.tpcoder.medicalcheckup.newsletter

import org.springframework.data.repository.CrudRepository

interface SubscriptionRepository : CrudRepository<Subscription, Long> {

    fun findByPatient(patientId: Long): Subscription?

    fun deleteByPatient(patientId: Long): Subscription?
}