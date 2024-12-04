package com.talearnt.auth.verification.repository;

import com.talearnt.auth.verification.Entity.IpTrace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IpTraceRepository extends JpaRepository<IpTrace,Long> {
    Optional<IpTrace> findByIp(String ip);
}
