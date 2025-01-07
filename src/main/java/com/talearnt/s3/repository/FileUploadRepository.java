package com.talearnt.s3.repository;

import com.talearnt.s3.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadRepository extends JpaRepository<FileUpload,Long> {
}
