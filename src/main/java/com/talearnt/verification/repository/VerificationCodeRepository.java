package com.talearnt.verification.repository;

import com.talearnt.verification.Entity.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<PhoneVerification, String> {
    PhoneVerification findByUserId(String userId);
}
