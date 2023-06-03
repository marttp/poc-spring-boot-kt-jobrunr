package dev.tpcoder.medicalcheckup.newsletter

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/subscriptions")
class SubscriptionController(private val subscriptionService: SubscriptionService) {

    @PostMapping("/{patientId}")
    fun subscribe(@PathVariable patientId: Long, @RequestBody subscriptionRequest: SubscriptionRequest): Subscription {
        return subscriptionService.subscribe(patientId, subscriptionRequest)
    }

    @DeleteMapping("/{patientId}")
    fun unsubscribe(@PathVariable patientId: Long): Subscription {
        return subscriptionService.unsubscribe(patientId)
    }

    @GetMapping
    fun getAllSubscriptions(): MutableIterable<Subscription> {
        return subscriptionService.getAllSubscriptions()
    }
}