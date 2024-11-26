package com.talearnt.auth.verification.repository;

import com.talearnt.auth.verification.Entity.PhoneVerificationTrace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneVerificationTraceRepository extends JpaRepository<PhoneVerificationTrace,Long> {

}
