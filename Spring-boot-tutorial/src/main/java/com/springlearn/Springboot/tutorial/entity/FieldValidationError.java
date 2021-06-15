package com.springlearn.Springboot.tutorial.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FieldValidationError {
    private String field;
    private String message;
}
