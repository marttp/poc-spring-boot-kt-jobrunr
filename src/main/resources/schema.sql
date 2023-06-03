CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS appointment_series (
    id BIGINT AUTO_INCREMENT,
    patient BIGINT,
    start_date TIMESTAMP,
    recurrence_frequency BIGINT, -- in days
    status VARCHAR(20) DEFAULT 'active', -- could be 'active' or 'completed'
    PRIMARY KEY (id),
    FOREIGN KEY (patient) REFERENCES patients(id)
);

CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT,
    patient BIGINT,
    doctor BIGINT,
    appointment_on TIMESTAMP,
    appointment_type VARCHAR(100) NOT NULL,
    appointment_status VARCHAR(20) DEFAULT 'waiting', -- could be 'waiting', 'scheduled', 'rescheduled', 'cancelled', 'completed'
    recurrence_frequency BIGINT, -- in days, NULL for non-recurring appointments
    series BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (patient) REFERENCES patients(id),
    FOREIGN KEY (doctor) REFERENCES doctors(id),
    FOREIGN KEY (series) REFERENCES appointment_series(id)
);

CREATE TABLE reminders (
    id BIGINT AUTO_INCREMENT,
    appointment BIGINT,
    reminder_date_time DATETIME,
    reminder_type VARCHAR(50) NOT NULL, -- could be 'email', 'sms', 'app notification'
    reminder_title VARCHAR(100) NOT NULL,
    reminder_url VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (appointment) REFERENCES appointments(id)
);

CREATE TABLE IF NOT EXISTS checkup_results (
    id BIGINT AUTO_INCREMENT,
    appointment BIGINT,
    checkup_date TIMESTAMP,
    notes TEXT,
    diagnosis TEXT,
    recommendations TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (appointment) REFERENCES appointments(id)
);

CREATE TABLE IF NOT EXISTS `subscriptions` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `patient` BIGINT NOT NULL,
    `subscription_type` VARCHAR(20) NOT NULL DEFAULT 'monthly', -- could be 'weekly' or 'monthly'
    `job_id` VARCHAR(100),
    FOREIGN KEY (`patient`) REFERENCES `patients`(`id`)
);