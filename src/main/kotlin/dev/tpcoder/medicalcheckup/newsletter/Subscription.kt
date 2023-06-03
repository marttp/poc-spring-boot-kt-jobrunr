package dev.tpcoder.medicalcheckup.newsletter

import dev.tpcoder.medicalcheckup.common.entity.Patient
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Table

@Table("subscriptions")
data class Subscription(
        @Id val id: Long? = null,
        var patient: AggregateReference<Patient, Long>,
        var subscriptionType: String,
        var jobId: String
)
