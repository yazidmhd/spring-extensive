package com.springlearn.Springboot.tutorial.repository;

import com.springlearn.Springboot.tutorial.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    public List<Department> findByDepartmentNameIgnoreCase(String departmentName);

    public Department findByDepartmentName(String departmentName);

}
