package com.springlearn.Springboot.tutorial.controller;

import com.springlearn.Springboot.tutorial.entity.Department;
import com.springlearn.Springboot.tutorial.entity.UpdatableDepartment;
import com.springlearn.Springboot.tutorial.error.DepartmentNotFoundException;
import com.springlearn.Springboot.tutorial.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    //implementing Logging with Slf4j
    //from the LoggerFactory, call getLogger method on the DepartmentController class
    //Loggers are helpful for DEBUGGING
    //We can create different policies to aggregate the logs - console, file
    //all the configurations for logging can be added properties file
    //you can use @Slf4j annotation by lombok
    private final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

    //request body - pass the entire json object and convert the entire json object to the Department object
    //whatever json object you're getting, convert it to the Department object
    //@Valid - when a request comes in, the JSON body will be validated against the annotations given
    //that was defined in the Department entity
    @PostMapping("/departments")
    public Department saveDepartment(@Valid @RequestBody Department department) {
        LOGGER.info("Inside saveDepartment of DepartmentController");
        return departmentService.saveDepartment(department);
    }

    @GetMapping("/departments")
    public List<Department> fetchDepartmentList() {
        LOGGER.info("Inside fetchDepartmentList of DepartmentController");
        return departmentService.fetchDepartmentList();
    }

    @GetMapping("/departments/{id}")
    public Department fetchDepartmentById(@PathVariable("id") Long departmentId) throws DepartmentNotFoundException {
        return departmentService.fetchDepartmentById(departmentId);
    }

    @DeleteMapping("/departments/{id}")
    public String deleteDepartmentById(@PathVariable("id") Long departmentId) throws DepartmentNotFoundException {
        departmentService.deleteDepartmentById(departmentId);
        return "Department deleted successfully";
    }

    //update -> need the path variable which is the id and the department object
    //so we need to take the take the new department object and update it with the old department object stored
    //hence why we need to use RequestBody
    @PutMapping("/departments/{id}")
    public Department updateDepartment(
            @PathVariable("id") Long departmentId,
            @Valid @RequestBody UpdatableDepartment department)
            throws DepartmentNotFoundException {
        return departmentService.updateDepartment(departmentId, department);
    }

    @GetMapping("/departments/name/all/{name}")
    public List<Department> fetchDepartmentByName(@PathVariable("name") String departmentName) {
        return departmentService.fetchDepartmentByName(departmentName);
    }

    @GetMapping("/departments/name/one/{name}")
    public Department fetchOneDepartmentByName(@PathVariable("name") String departmentName) throws DepartmentNotFoundException {
        return departmentService.fetchOneDepartmentByName(departmentName);
    }




}
