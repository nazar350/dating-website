package nazar.Dating_website.dto;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private LocalDate birthday;
    private Boolean gender;
    private String phone;
    private String about;
    private String interests;
    private int age;
    private String photoUrl;
    private List<String> interestsAsList;
    private LocalDateTime createdAt;
}
