package ru.mentee.power.crm.spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CreateLeadRequest {

  @NotBlank(message = "Email обязателен")
  @Email(message = "Email должен быть в корректном формате")
  private String email;

  @NotBlank(message = "Имя обязательно")
  @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
  private String firstName;

  @NotBlank(message = "Фамилия обязательна")
  @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
  private String lastName;

  @Size(max = 100, message = "Название компании не должно превышать 100 символов")
  private String company;

  public CreateLeadRequest() {
  }
}
