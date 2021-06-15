package com.springlearn.Springboot.tutorial.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "department")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long departmentId;

    //Not Blank - what message it will display when this validation fails
    //Not Blank - when receive blank from the JSON object request
    @NotBlank(message = "Department Name is mandatory")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Letters only")
    private String departmentName;

    @NotBlank(message = "Department Address is mandatory")
    private String departmentAddress;

    @NotBlank(message = "Department Code is mandatory")
    @Size(max = 6, message = "Maximum length of 6 only")
    private String departmentCode;

}

