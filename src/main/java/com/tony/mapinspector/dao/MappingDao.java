package com.tony.mapinspector.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tony.mapinspector.entity.Mapping;

@Repository
public interface MappingDao extends JpaRepository<Mapping, Long> {
	public List<Mapping> findAllByFromClass(String fromClass);

	public List<Mapping> findAllByToClass(String className);
}
