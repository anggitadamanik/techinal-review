package com.example.application.data.service;

import com.example.application.data.entity.SampleWebApplication_;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SampleWebApplication_Service {

    private final SampleWebApplication_Repository repository;

    public SampleWebApplication_Service(SampleWebApplication_Repository repository) {
        this.repository = repository;
    }

    public Optional<SampleWebApplication_> get(Long id) {
        return repository.findById(id);
    }

    public SampleWebApplication_ update(SampleWebApplication_ entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<SampleWebApplication_> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<SampleWebApplication_> list(Pageable pageable, Specification<SampleWebApplication_> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
