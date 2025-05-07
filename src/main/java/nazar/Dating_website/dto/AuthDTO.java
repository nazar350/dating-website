package nazar.Dating_website.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Data
@Getter @Setter
public class AuthDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Repeat password is required")
    private String repeatPassword;

    private String photoPath;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @NotNull(message = "Birthday is required")
    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;

    @NotNull(message = "Gender is required")
    private Boolean gender;

    @NotBlank(message = "Interests are required")
    private String interests;

    @NotBlank(message = "About Me is required")
    private String aboutMe;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+380[0-9]{9}$", message = "Phone number must be in the format +380XXXXXXXXX")
    private String phone;
}
