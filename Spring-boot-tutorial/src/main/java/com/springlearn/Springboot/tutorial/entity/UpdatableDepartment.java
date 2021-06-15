package com.springlearn.Springboot.tutorial.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatableDepartment {

    @Pattern(regexp = "^[A-Za-z]+$", message = "Letters only")
    private String departmentName;

    private String departmentAddress;

    @Size(max = 6, message = "Maximum length of 6 only")
    private String departmentCode;

}
