package dev.tpcoder.medicalcheckup.common.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("patients")
data class Patient(
        @Id val id: Long? = null,
        var firstName: String,
        var lastName: String,
        var email: String,
        var phoneNumber: String
)