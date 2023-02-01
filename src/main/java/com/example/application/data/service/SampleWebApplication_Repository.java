package com.example.application.data.service;

import com.example.application.data.entity.SampleWebApplication_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SampleWebApplication_Repository
        extends
            JpaRepository<SampleWebApplication_, Long>,
            JpaSpecificationExecutor<SampleWebApplication_> {

}
