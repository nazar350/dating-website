package nazar.Dating_website.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
public class InvitationsDTO {
    private Long id;
    private UserDTO sender;
    private UserDTO receiver;
    private boolean sent;
    private boolean received;
    private boolean accepted;
}
