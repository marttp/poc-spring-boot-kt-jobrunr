package dev.tpcoder.medicalcheckup.newsletter

import dev.tpcoder.medicalcheckup.common.Constants.NEWS_SUBSCRIPTION_MONTHLY
import dev.tpcoder.medicalcheckup.common.Constants.NEWS_SUBSCRIPTION_WEEKLY
import org.jobrunr.scheduling.BackgroundJob
import org.slf4j.LoggerFactory
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID


@Service
class SubscriptionService(private val subscriptionRepository: SubscriptionRepository) {

    private val logger = LoggerFactory.getLogger(SubscriptionService::class.java)

    fun subscribe(patientId: Long, subscriptionRequest: SubscriptionRequest): Subscription {
        val existingSubscription = subscriptionRepository.findByPatient(patientId)
        if (existingSubscription != null) {
            unsubscribe(patientId)
        }
        val uuidForJob = UUID.randomUUID().toString()
//        val jobId = if (subscriptionRequest.subscriptionType == NEWS_SUBSCRIPTION_WEEKLY) {
//            BackgroundJob.scheduleRecurrently(Cron.weekly()) { sendNewsletter(NEWS_SUBSCRIPTION_WEEKLY) }
//        } else {
//            BackgroundJob.scheduleRecurrently(Cron.monthly()) { sendNewsletter(NEWS_SUBSCRIPTION_MONTHLY) }
//        }
        val jobId = if (subscriptionRequest.subscriptionType == NEWS_SUBSCRIPTION_WEEKLY) {
            BackgroundJob.scheduleRecurrently(uuidForJob, "*/1 * * * *") { sendNewsletter(NEWS_SUBSCRIPTION_WEEKLY) }
        } else {
            BackgroundJob.scheduleRecurrently(uuidForJob, "*/2 * * * *") { sendNewsletter(NEWS_SUBSCRIPTION_MONTHLY) }
        }
        val newSubscription = Subscription(
                patient = AggregateReference.to(patientId),
                subscriptionType = subscriptionRequest.subscriptionType,
                jobId = jobId
        )
        return subscriptionRepository.save(newSubscription)
    }

    fun unsubscribe(patientId: Long): Subscription {
        val existingSubscription = subscriptionRepository.deleteByPatient(patientId)
                ?: throw IllegalArgumentException("Subscription for patientId $patientId not found")
        val jobId = existingSubscription.jobId
        // Delete Recurring subscription
        BackgroundJob.delete(jobId)
        return existingSubscription
    }

    fun getAllSubscriptions(): MutableIterable<Subscription> {
        return subscriptionRepository.findAll()
    }

    fun sendNewsletter(type: String) {
        if (type == NEWS_SUBSCRIPTION_WEEKLY) {
            logger.info("Send weekly newsletter at ${LocalDateTime.now()}")
        } else {
            logger.info("Send monthly newsletter at ${LocalDateTime.now()}")
        }
    }
}
