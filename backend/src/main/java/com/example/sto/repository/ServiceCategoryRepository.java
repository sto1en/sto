package com.example.sto.repository;

import com.example.sto.model.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

    Optional<ServiceCategory> findByKey(String key);

    Optional<ServiceCategory> findByTitleIgnoreCase(String title);

    boolean existsByKey(String key);

    boolean existsByTitleIgnoreCase(String title);
}