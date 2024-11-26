package com.talearnt.auth.verification.repository;

import com.talearnt.auth.verification.Entity.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<PhoneVerification, String> {
    PhoneVerification findByPhone(String phone);
}
