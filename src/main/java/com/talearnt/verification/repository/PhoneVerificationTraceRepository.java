package com.talearnt.verification.repository;

import com.talearnt.verification.Entity.PhoneVerificationTrace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneVerificationTraceRepository extends JpaRepository<PhoneVerificationTrace,Long> {

}
