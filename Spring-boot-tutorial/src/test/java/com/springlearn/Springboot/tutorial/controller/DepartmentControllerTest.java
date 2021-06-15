package com.springlearn.Springboot.tutorial.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springlearn.Springboot.tutorial.entity.ApiError;
import com.springlearn.Springboot.tutorial.entity.Department;
import com.springlearn.Springboot.tutorial.entity.UpdatableDepartment;
import com.springlearn.Springboot.tutorial.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//controller layer, when called, hits the end point
//so we have to hit the end point and see how it behaves
//WebMvcTest
@Slf4j
@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    private Department department;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .departmentName("CS")
                .departmentAddress("Crossroads")
                .departmentCode("CS-001")
                .departmentId(1L)
                .build();
    }

    //1a. saveDepartment - test for value returned
    @Test
    public void whenValidInputFromSave_thenReturnDepartment() throws Exception {
        Department inputDepartment = Department.builder()
                .departmentName("CS")
                .departmentAddress("Crossroads")
                .departmentCode("CS-001")
                .build();

        Mockito.when(departmentService.saveDepartment(inputDepartment)).thenReturn(department);

        mockMvc.perform(post("/departments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDepartment)))
                .andExpect(jsonPath("$.departmentId").value(1L))
                .andExpect(jsonPath("$.departmentName").value("CS"))
                .andExpect(jsonPath("$.departmentCode").value("CS-001"))
                .andExpect(jsonPath("$.departmentAddress").value("Crossroads"));
    }

    //1b. saveDepartment - test Ok return status
    //http request mapping and deserialization
    @Test
    void whenValidInputFromSave_thenReturnsOk() throws Exception {
        //input object - parameter
        Department inputDepartment = Department.builder()
                .departmentName("CS")
                .departmentAddress("Crossroads")
                .departmentCode("CS-001")
                .build();

        //return the department object set in setUp() when method in departmentService is called
        Mockito.when(departmentService.saveDepartment(inputDepartment))
                .thenReturn(department);

        //post operation is performed
        //hit the endpoint -> take in the parameter -> expect status 200/isOk()
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDepartment)))
                .andExpect(status().isOk());
    }

    //1c. saveDepartment - test invalid input and return bad request
    @Test
    public void whenInvalidInputFromSave_thenReturnsBadRequest() throws Exception {
        Department department = Department.builder().departmentName("CS").departmentAddress("4th Street").build();

        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isBadRequest());
    }

    //1d. saveDepartment - test validation rule and return bad request
    @Test
    public void inValidCheckDepartmentName_LettersOnly() throws Exception {
        Department inputDepartment = Department.builder()
                .departmentName("12345")
                .departmentAddress("Crossroads")
                .departmentCode("CS-001")
                .build();

        ApiError expectedErrorResponse = new ApiError(HttpStatus.BAD_REQUEST,
                "departmentName",
                "Letters only");

        String expectedResponseBody = objectMapper.writeValueAsString(expectedErrorResponse);

        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDepartment)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(expectedResponseBody, result.getResponse().getContentAsString()));
    }

    //1e. saveDepartment - to verify that service layer saveDepartment method is called once and
    //check the arguments passed are correct
    @Test
    public void whenValidInput_thenCallDepServiceMethodSuccessfully() throws Exception {
        Department inputDepartment = Department.builder()
                .departmentName("CS")
                .departmentAddress("Crossroads")
                .departmentCode("CS-001")
                .build();

        Mockito.when(departmentService.saveDepartment(inputDepartment))
                .thenReturn(department);

        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDepartment)))
                .andExpect(status().isOk());

        ArgumentCaptor<Department> departmentCaptor = ArgumentCaptor.forClass(Department.class);
        Mockito.verify(departmentService, Mockito.times(1)).saveDepartment(departmentCaptor.capture());
        assertEquals(departmentCaptor.getValue().getDepartmentName(), "CS");
        assertEquals(departmentCaptor.getValue().getDepartmentCode(), "CS-001");
    }

    //1f. saveDepartment - invalid input and check the error message
    @Test
    public void whenInvalidInput_thenReturn400AndErrorResult() throws Exception {
        Department inputDepartment = Department.builder()
                .departmentAddress("Crossroads")
                .departmentCode("CS-001")
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDepartment)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ApiError expectedErrorResponse = new ApiError(HttpStatus.BAD_REQUEST,
                "departmentName",
                "Department Name is mandatory");

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(expectedErrorResponse);
        assertEquals(actualResponseBody, expectedResponseBody);
    }

    //2a. fetchDepartmentById - check valid id and return status ok and check values returned
    @Test
    void whenValidId_thenFetchDepartmentById() throws Exception {
        Mockito.when(departmentService.fetchDepartmentById(1L))
                .thenReturn(department);

        //expect ok results and expect the department name that you get should match with the
        //department name of the json
        mockMvc.perform(get("/departments/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").value(department.getDepartmentId()))
                .andExpect(jsonPath("$.departmentName").value(department.getDepartmentName()))
                .andExpect(jsonPath("$.departmentAddress").value(department.getDepartmentAddress()))
                .andExpect(jsonPath("$.departmentCode").value(department.getDepartmentCode()));
    }

    //2b. fetchDepartmentById - to verify service layer fetchDepartmentById is called once and
    //checked the arguments passed in the method are correct
    @Test
    void whenValidIdFromFetchDepartmentById_thenCheckMethodCalledOnce() throws Exception {
        Mockito.when(departmentService.fetchDepartmentById(1L))
                .thenReturn(department);

        mockMvc.perform(get("/departments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(departmentService, Mockito.times(1))
                .fetchDepartmentById(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue(), 1L);
    }

    //2c. fetchDepartmentById - check when invalid path variable type and return 400 and check error result
    @Test
    void whenInvalidIdTypeFromFetchDepartmentById_thenReturn400AndCheckErrorResult() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/departments/{id}", "abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ApiError expectedErrorResponse = new ApiError(HttpStatus.BAD_REQUEST,
                "id",
                "id should be of type java.lang.Long");

        log.info(mvcResult.getResponse().getContentAsString());
        log.info(objectMapper.writeValueAsString(expectedErrorResponse));

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(expectedErrorResponse);
        assertEquals(actualResponseBody, expectedResponseBody);
    }

    //3a. updateDepartment - when valid id and return department
    @Test
    void whenValidIdFromUpdate_thenReturnDepartment() throws Exception{
        UpdatableDepartment inputDepartment = UpdatableDepartment.builder()
                .departmentAddress("Crossroads")
                .departmentCode("CS-001")
                .build();

        Mockito.when(departmentService.updateDepartment(1L, inputDepartment)).thenReturn(department);

        mockMvc.perform(put("/departments/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDepartment)))
                .andExpect(jsonPath("$.departmentAddress").value("Crossroads"))
                .andExpect(jsonPath("$.departmentCode").value("CS-001"));
    }

    //3b. updateDepartment - when valid id and return status Ok
    @Test
    void whenValidIdFromUpdate_thenReturnStatusOk() throws Exception {
        UpdatableDepartment inputDepartment = UpdatableDepartment.builder()
                .departmentAddress("Crossroads")
                .departmentCode("CS-001")
                .build();

        Mockito.when(departmentService.updateDepartment(1L, inputDepartment)).thenReturn(department);

        mockMvc.perform(put("/departments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDepartment)))
                .andExpect(status().isOk());
    }

    //3c. updateDepartment - when invalid id type and return status bad request and check error result
    @Test
    void whenInvalidIdTypeFromUpdate_thenReturnBadRequestAndCheckError() throws Exception {
        UpdatableDepartment inputDepartment = UpdatableDepartment.builder()
                .departmentAddress("Crossroads")
                .departmentCode("CS-001")
                .build();

        ApiError errorResponse = new ApiError(HttpStatus.BAD_REQUEST, "id",
                "id should be of type java.lang.Long");

        MvcResult mvcResult = mockMvc.perform(put("/departments/{id}", "abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDepartment)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(errorResponse);
        assertEquals(expectedResponseBody, actualResponseBody);
    }

    //3d. updateDepartment - check the service layer method is called once and check arguments
    @Test
    public void whenValidInputFromUpdate_thenCallDepServiceMethodSuccessfully() throws Exception {
        UpdatableDepartment inputDepartment = UpdatableDepartment.builder()
                .departmentAddress("Crossroads")
                .departmentCode("CS-001")
                .build();

        Mockito.when(departmentService.updateDepartment(1L, inputDepartment))
                .thenReturn(department);

        mockMvc.perform(put("/departments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDepartment)))
                .andExpect(status().isOk());

        ArgumentCaptor<UpdatableDepartment> departmentCaptor = ArgumentCaptor.forClass(UpdatableDepartment.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        Mockito.verify(departmentService, Mockito.times(1))
                .updateDepartment(idCaptor.capture(), departmentCaptor.capture());

        assertEquals(idCaptor.getValue(), 1L);
        assertEquals(departmentCaptor.getValue().getDepartmentAddress(), "Crossroads");
        assertEquals(departmentCaptor.getValue().getDepartmentCode(), "CS-001");
    }

    //3e. saveDepartment - test validation rule and return bad request
    @Test
    public void inValidLettersOnlyCheckDepartmentNameFromUpdate_thenReturnErrorMessage() throws Exception {
        UpdatableDepartment inputDepartment = UpdatableDepartment.builder()
                .departmentName("12345")
                .build();

        ApiError expectedErrorResponse = new ApiError(HttpStatus.BAD_REQUEST,
                "departmentName",
                "Letters only");

        String expectedResponseBody = objectMapper.writeValueAsString(expectedErrorResponse);

        mockMvc.perform(put("/departments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDepartment)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(expectedResponseBody, result.getResponse().getContentAsString()));
    }

    //4a. deleteDepartmentById - when given valid id then valid ok status and method called one time
    @Test
    void whenValidIdFromDelete_thenReturnStatusOk() throws Exception {
        Mockito.doNothing().when(departmentService).deleteDepartmentById(Mockito.anyLong());

        mockMvc.perform(delete("/departments/{id}", 1L))
                .andExpect(status().isOk());

        Mockito.verify(departmentService, Mockito.times(1)).deleteDepartmentById(1L);
    }

    //4b. deleteDepartmentById - when invalid id type then throw error message and bad request status
    @Test
    void whenInvalidIdFromDelete_thenReturnBadStatus() throws Exception {
        ApiError expectedErrorResponse = new ApiError(HttpStatus.BAD_REQUEST,
                "id",
                "id should be of type java.lang.Long");
        String expectedResponseBody = objectMapper.writeValueAsString(expectedErrorResponse);

        mockMvc.perform(delete("/departments/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(expectedResponseBody, result.getResponse().getContentAsString()));
    }

    //5a. fetchDepartmentList - should return list of departments, check content and length
    @Test
    void shouldReturnListOfDepartments() throws Exception {
        Department department2 = Department.builder().departmentId(2L).departmentName("IT")
                .departmentAddress("Foundation Roads").departmentCode("IT-123").build();
        List<Department> departmentList = new ArrayList<>();
        departmentList.add(department);
        departmentList.add(department2);

        Mockito.when(departmentService.fetchDepartmentList()).thenReturn(departmentList);

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(result -> assertEquals(objectMapper.writeValueAsString(departmentList),
                        result.getResponse().getContentAsString()));
    }











}