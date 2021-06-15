package com.springlearn.Springboot.tutorial.repository;

import com.springlearn.Springboot.tutorial.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest uses in memory H2 database for repository tests
//to use actual DB, disable the auto configuration

@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Department department;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .departmentName("CS")
                .departmentAddress("First Street")
                .departmentCode("CS-001")
                .build();

        entityManager.persist(department);
        entityManager.flush();
    }

    @Test
    public void whenFindById_thenReturnDepartment() {
        Department department = departmentRepository.findById(1L).get();
        assertEquals(department.getDepartmentName(), "CS");
    }

    @Test
    public void whenFindByName_thenReturnDepartment() {
        Department found = departmentRepository.findByDepartmentName("CS");
        assertEquals(found.getDepartmentName(), department.getDepartmentName());
    }

    @Test
    public void whenSaved_thenFindById() {
        departmentRepository.save(department);
        assertNotNull(departmentRepository.findById(1L));
    }

    @Test
    public void whenSaved_thenFindDepartmentName() {
        departmentRepository.save(department);
        assertNotNull(departmentRepository.findByDepartmentName("CS"));
    }

}