package dev.tpcoder.medicalcheckup.appointment

import dev.tpcoder.medicalcheckup.common.SendNotify
import dev.tpcoder.medicalcheckup.common.dto.EmailNotifyPayload
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EmailService : SendNotify<EmailNotifyPayload> {

    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    override fun sendNotify(payload: EmailNotifyPayload) {
        logger.info("Send email to ${payload.to} with subject: ${payload.subject} on ${LocalDateTime.now()}")
    }

}