package com.springlearn.Springboot.tutorial.service;

import com.springlearn.Springboot.tutorial.entity.Department;
import com.springlearn.Springboot.tutorial.entity.UpdatableDepartment;
import com.springlearn.Springboot.tutorial.error.DepartmentNotFoundException;

import java.util.List;

public interface DepartmentService {
    public Department saveDepartment(Department department);

    public List<Department> fetchDepartmentList();

    public Department fetchDepartmentById(Long departmentId) throws DepartmentNotFoundException;

    public void deleteDepartmentById(Long departmentId) throws DepartmentNotFoundException;

    public Department updateDepartment(Long departmentId, UpdatableDepartment department) throws DepartmentNotFoundException;

    public List<Department> fetchDepartmentByName(String departmentName);

    public Department fetchOneDepartmentByName(String departmentName) throws DepartmentNotFoundException;
}
