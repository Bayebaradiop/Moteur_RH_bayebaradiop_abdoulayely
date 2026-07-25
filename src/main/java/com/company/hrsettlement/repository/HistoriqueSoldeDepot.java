package com.company.hrsettlement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acces aux soldes archives.
 */
public interface HistoriqueSoldeDepot extends JpaRepository<HistoriqueSoldeEntite, Long> {

	List<HistoriqueSoldeEntite> findByMatriculeEmploye(String matriculeEmploye);
}
