package dev.tpcoder.medicalcheckup

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MySQLContainer

@SpringBootTest
@ContextConfiguration(classes = [TestMedicalCheckupApplication::class])
class ApplicationTests {

	@Autowired
	private lateinit var mySQLContainer: MySQLContainer<*>

	@Test
	fun contextLoads() {
		Assertions.assertTrue(mySQLContainer.isRunning)
	}
}
