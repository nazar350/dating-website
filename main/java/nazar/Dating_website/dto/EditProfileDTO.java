package nazar.Dating_website.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
public class EditProfileDTO {
    private String phone;
    private String about;
    private String interests;
}
