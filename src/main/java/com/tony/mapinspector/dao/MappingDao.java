package com.tony.mapinspector.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tony.mapinspector.entity.Mapping;

@Repository
public interface MappingDao extends JpaRepository<Mapping, Long> {
	public Mapping findByUid(String uid);
}
