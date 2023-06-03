package dev.tpcoder.medicalcheckup.common.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("doctors")
data class Doctor(
        @Id val id: Long? = null,
        var firstName: String,
        var lastName: String,
        var email: String,
        var phoneNumber: String
)