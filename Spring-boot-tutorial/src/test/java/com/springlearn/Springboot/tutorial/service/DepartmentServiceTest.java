package com.springlearn.Springboot.tutorial.service;

import com.springlearn.Springboot.tutorial.entity.Department;
import com.springlearn.Springboot.tutorial.entity.UpdatableDepartment;
import com.springlearn.Springboot.tutorial.error.DepartmentNotFoundException;
import com.springlearn.Springboot.tutorial.repository.DepartmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//to tell Springboot that it is a test class
@Slf4j
@SpringBootTest
class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    //this is the layer that we are calling method from
    @MockBean
    private DepartmentRepository departmentRepository;

    private Department department;

    //@BeforeEach - this method will be call for each test case
    @BeforeEach
    void setUp() {
        department = Department.builder()
                .departmentName("IT")
                .departmentAddress("3rd Cross, First Street")
                .departmentCode("IT-006")
                .departmentId(1L)
                .build();
    }

    @Test
    public void shouldSavedDepartmentSuccessfully() {
        Mockito.when(departmentRepository.save(department))
                .thenReturn(department);

        Department found = departmentService.saveDepartment(department);
        log.info(found.toString());
        assertNotNull(found);
    }

    @Test
    public void shouldReturnAllDepartments() {
        List<Department> departmentList = new ArrayList<>();
        Department departmentSecond = Department.builder().departmentId(2L).departmentName("CS").departmentAddress(
                "Second Crossroads, Third Street").departmentCode("CS-022").build();
        departmentList.add(department);
        departmentList.add(departmentSecond);

        Mockito.when(departmentRepository.findAll())
                .thenReturn(departmentList);

        List<Department> foundAll = departmentService.fetchDepartmentList();
        assertEquals(foundAll, departmentList);
    }

    @Test
    public void whenUpdateSuccessfully_thenReturnUpdatedDepartment() throws DepartmentNotFoundException {
        Mockito.when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        UpdatableDepartment updateDepartment = UpdatableDepartment.builder()
                .departmentName("CS")
                .departmentCode("CS-001")
                .build();

        Mockito.when(departmentRepository.save(department))
                .thenReturn(department);

        Department found = departmentService.updateDepartment(department.getDepartmentId(), updateDepartment);
        assertEquals("CS", found.getDepartmentName());
    }

    @Test
    public void whenInvalidIdFromUpdate_thenThrowDepartmentNotFoundException() throws DepartmentNotFoundException {
        UpdatableDepartment updateDepartment = UpdatableDepartment.builder()
                .departmentName("CS")
                .departmentCode("CS-001")
                .build();

        Long departmentId = 5L;
        Exception exception = assertThrows(DepartmentNotFoundException.class, () -> {
            departmentService.updateDepartment(departmentId, updateDepartment);
        });

        String expectedMessage = "Department Not Found";
        String actualMessage = exception.getMessage();
        log.info(exception.getMessage());
        log.info(exception.getLocalizedMessage());
        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    //this returns void return type - verify() method
    public void whenValidIdFromDelete_thenDeleteDepartment() throws DepartmentNotFoundException {
        //verify captured value/paramters
        //doNothing ignore the void method
        /*
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        Mockito.when(departmentRepository.findById(idCapture.capture())).thenReturn(Optional.of(department));
        Mockito.doNothing().when(departmentRepository).deleteById(idCapture.capture());
        departmentService.deleteDepartmentById(1L);
        assertEquals(1L, idCapture.getValue());
         */

        //verify the number of times the method is called
        Mockito.when(departmentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(department));
        Mockito.doNothing().when(departmentRepository).deleteById(Mockito.anyLong());
        departmentService.deleteDepartmentById(1L);
        Mockito.verify(departmentRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void whenInvalidIdFromDelete_thenThrowDepartmentNotFoundException() throws DepartmentNotFoundException {
        Exception exception = assertThrows(DepartmentNotFoundException.class, () -> {
            departmentService.deleteDepartmentById(1L);
        });

        String expectedMessage = "Department Not Found";
        String actualMessage = exception.getMessage();
        log.info(exception.getMessage());
        assertTrue(actualMessage.contains(expectedMessage));
    }


    //give unique test case name so that you can identify what it is doing
    //need to write test cases for all scenarios - positive and negative - so that the entire code coverage will be
    //completed
    //@Test - use as a test function
    @Test
    //@DisplayName("Get Data based on Valid Department Name")
    public void whenValidDepartmentName_thenDepartmentShouldFoundException() throws DepartmentNotFoundException {
        Mockito.when(departmentRepository.findByDepartmentName("IT"))
                .thenReturn(department);

        String departmentName = "IT";
        //departmentService.fetchOneDepartmentByName is calling on another layer's method which is wrong
        //so we need to mock it
        Department found = departmentService.fetchOneDepartmentByName(departmentName);

        //need to validate departmentName and found.getDepartmentName are the same
        //assertEquals - if both are equal then test case will pass, if both are not equal, test case will fail
        assertEquals(departmentName, found.getDepartmentName());
    }

    @Test
    public void whenInvalidDepartmentName_thenThrowDepartmentNotFoundException() throws DepartmentNotFoundException {
        Exception exception = assertThrows(DepartmentNotFoundException.class, () -> {
            departmentService.fetchOneDepartmentByName("invalid");
        });

        String expectedMessage = "Department Not Found";
        String actualMessage = exception.getMessage();
        log.info(exception.getMessage());
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void whenValidDepartmentId_thenReturnDepartment() throws DepartmentNotFoundException {
        Mockito.when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        Long departmentId = 1L;
        Optional<Department> foundOptional = Optional.of(departmentService.fetchDepartmentById(departmentId));
        Department foundDepartment = foundOptional.get();
        assertEquals(departmentId, foundDepartment.getDepartmentId());
    }

    @Test
    public void whenInvalidDepartmentId_thenThrowDepartmentNotFoundException() throws DepartmentNotFoundException {
        Long departmentId = 5L;
        Exception exception = assertThrows(DepartmentNotFoundException.class, () -> {
            departmentService.fetchDepartmentById(departmentId);
        });

        String expectedMessage = "Department Not Found";
        String actualMessage = exception.getMessage();
        log.info(exception.getMessage());
        assertTrue(actualMessage.contains(expectedMessage));
    }


}