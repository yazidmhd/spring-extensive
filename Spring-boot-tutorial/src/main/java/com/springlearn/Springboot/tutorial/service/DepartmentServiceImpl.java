package com.springlearn.Springboot.tutorial.service;

import com.springlearn.Springboot.tutorial.entity.Department;
import com.springlearn.Springboot.tutorial.entity.UpdatableDepartment;
import com.springlearn.Springboot.tutorial.error.DepartmentNotFoundException;
import com.springlearn.Springboot.tutorial.repository.DepartmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService{

    //autowired - the object will be attach to the particular reference
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public Department saveDepartment(Department department) {
        log.info("inside SaveDepartment of DepartmentService");
        return departmentRepository.save(department);
    }

    @Override
    public List<Department> fetchDepartmentList() {
        return departmentRepository.findAll();
    }

    @Override
    public Department fetchDepartmentById(Long departmentId) throws DepartmentNotFoundException {
        //if there is no data present -> need to throw Exception
        //returns an optional type -> use .get() to retrieve the value inside the optional
        Optional<Department> department = departmentRepository.findById(departmentId);

        //if department is not present -> throw Exception
        if(!department.isPresent()) {
            throw new DepartmentNotFoundException("Department Not Found");
        }

        //else -> return the department object
        return department.get();
    }

    @Override
    public void deleteDepartmentById(Long departmentId) throws DepartmentNotFoundException {
        Optional<Department> departmentOptional = departmentRepository.findById(departmentId);

        if(!departmentOptional.isPresent()) {
            throw new DepartmentNotFoundException("Department Not Found");
        }

        departmentRepository.deleteById(departmentId);
    }

    //logic
    //1. get the department debDB in the database via departmentId
    //2. whatever changes done in the department variable that was given and update it to the department debDB
    //e.g. 3 fields returned, if we only need to change one field, then we only need to update that field
    //we need to check if the other fields are null, so we can skip it
    @Override
    public Department updateDepartment(Long departmentId, UpdatableDepartment department) throws DepartmentNotFoundException {
        Optional<Department> depOpt = departmentRepository.findById(departmentId);

        if(!depOpt.isPresent()) {
            throw new DepartmentNotFoundException("Department Not Found");
        }

        Department depDB = depOpt.get();

        //Null checks & Blank checks
        //check if value is not null and not blank -> set the value; if one of the conditions does not hit, skip it
        //do it for all the fields that are updatable
        if(Objects.nonNull(department.getDepartmentName()) &&
                !"".equalsIgnoreCase(department.getDepartmentName())) {
            depDB.setDepartmentName(department.getDepartmentName());
        }

        if(Objects.nonNull(department.getDepartmentCode()) &&
                !"".equalsIgnoreCase(department.getDepartmentCode())) {
            depDB.setDepartmentCode(department.getDepartmentCode());
        }

        if(Objects.nonNull(department.getDepartmentAddress()) &&
                !"".equalsIgnoreCase(department.getDepartmentAddress())) {
            depDB.setDepartmentAddress(department.getDepartmentAddress());
        }

        // save into the DB after checking and setting
        return departmentRepository.save(depDB);
    }

    @Override
    public List<Department> fetchDepartmentByName(String departmentName) {
        return departmentRepository.findByDepartmentNameIgnoreCase(departmentName);
    }

    @Override
    public Department fetchOneDepartmentByName(String departmentName) throws DepartmentNotFoundException {
        Department department = departmentRepository.findByDepartmentName(departmentName);

        //check null or empty -> throw DepartmentNotFoundException
        if(Objects.isNull(department) || "".equals(department)) {
            throw new DepartmentNotFoundException("Department Not Found");
        }

        return department;
    }

}
