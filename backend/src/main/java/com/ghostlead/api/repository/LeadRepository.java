package com.ghostlead.api.repository;

import com.ghostlead.api.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {
}
