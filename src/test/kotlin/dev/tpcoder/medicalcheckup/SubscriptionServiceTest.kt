package dev.tpcoder.medicalcheckup

import dev.tpcoder.medicalcheckup.common.Constants.NEWS_SUBSCRIPTION_MONTHLY
import dev.tpcoder.medicalcheckup.common.Constants.NEWS_SUBSCRIPTION_WEEKLY
import dev.tpcoder.medicalcheckup.newsletter.Subscription
import dev.tpcoder.medicalcheckup.newsletter.SubscriptionRepository
import dev.tpcoder.medicalcheckup.newsletter.SubscriptionRequest
import dev.tpcoder.medicalcheckup.newsletter.SubscriptionService
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.jobrunr.jobs.lambdas.JobLambda
import org.jobrunr.scheduling.BackgroundJob
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.jdbc.core.mapping.AggregateReference

class SubscriptionServiceTest {

    private val subscriptionRepository = mockk<SubscriptionRepository>(relaxed = true)
    private val subscriptionService = SubscriptionService(subscriptionRepository)

    @Test
    fun `it should subscribe and return new subscription when there is no existing subscription`() {
        val patientId = 1L
        val subscriptionType = NEWS_SUBSCRIPTION_WEEKLY
        val subscriptionRequest = SubscriptionRequest(subscriptionType)

        mockkStatic(BackgroundJob::class)
        every { BackgroundJob.scheduleRecurrently(any<String>(), any<String>(), any<JobLambda>()) } returns "jobId"

        every { subscriptionRepository.findByPatient(patientId) } returns null
        every { subscriptionRepository.save(any()) } returns Subscription(null, AggregateReference.to(patientId), subscriptionType, "jobId")

        val result = subscriptionService.subscribe(patientId, subscriptionRequest)

        assertEquals(patientId, result.patient.id)
        assertEquals(subscriptionType, result.subscriptionType)

        verify(exactly = 1) { subscriptionRepository.save(any()) }
    }

    @Test
    fun `it should unsubscribe existing subscription and subscribe new one when there is existing subscription`() {
        val patientId = 1L
        val subscriptionType = NEWS_SUBSCRIPTION_MONTHLY
        val subscriptionRequest = SubscriptionRequest(subscriptionType)
        val existingSubscription = Subscription(1L, AggregateReference.to(patientId), NEWS_SUBSCRIPTION_WEEKLY, "jobId")

        mockkStatic(BackgroundJob::class)
        every { BackgroundJob.delete(any<String>()) } returns Unit
        every { BackgroundJob.scheduleRecurrently(any<String>(), any<String>(), any<JobLambda>()) } returns "jobId"

        every { subscriptionRepository.findByPatient(patientId) } returns existingSubscription
        every { subscriptionRepository.deleteByPatient(patientId) } returns existingSubscription
        every { subscriptionRepository.save(any()) } returns Subscription(null, AggregateReference.to(patientId), subscriptionType, "jobId")

        val result = subscriptionService.subscribe(patientId, subscriptionRequest)

        assertEquals(patientId, result.patient.id)
        assertEquals(subscriptionType, result.subscriptionType)

        verify(exactly = 1) { subscriptionRepository.deleteByPatient(patientId) }
        verify(exactly = 1) { subscriptionRepository.save(any()) }
    }

    @Test
    fun `it should unsubscribe successfully and return existing subscription`() {
        val patientId = 1L
        val existingSubscription = Subscription(1L, AggregateReference.to(patientId), NEWS_SUBSCRIPTION_WEEKLY, "jobId")

        every { subscriptionRepository.findByPatient(patientId) } returns existingSubscription
        every { subscriptionRepository.deleteByPatient(patientId) } returns existingSubscription

        mockkStatic(BackgroundJob::class)
        every { BackgroundJob.delete(any<String>()) } returns Unit

        val result = subscriptionService.unsubscribe(patientId)

        assertEquals(patientId, result.patient.id)

        verify(exactly = 1) { subscriptionRepository.deleteByPatient(patientId) }
    }
}