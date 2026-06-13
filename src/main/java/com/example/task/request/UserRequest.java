package com.example.task.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest extends BaseRequest {

    @NotBlank(message = "Please provide a name")
    private String name;

    @NotBlank(message = "Please provide an email")
    private String email;

    @NotBlank(message = "Please provide a personal ID")
    private String personalId;

    @NotBlank(message = "Please provide a password")
    private String password;

    @NotBlank(message = "Please provide a default currency")
    private String defaultCurrency;

    @NotBlank(message = "Please provide a phone number")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Please provide a valid phone number")
    private String phone;

    @NotNull(message = "Please provide a date of birth")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Please provide a street")
    private String street;

    @NotBlank(message = "Please provide a city")
    private String city;

    @NotBlank(message = "Please provide a country")
    private String country;

    @NotBlank(message = "Please provide a postal code")
    private String postalCode;
}