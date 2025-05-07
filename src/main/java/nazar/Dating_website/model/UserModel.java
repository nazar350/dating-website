package nazar.Dating_website.model;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;


@Entity
@Table(name = "users")
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name" , nullable = false)
    private String name;

    @Column(name = "surname" , nullable = false)
    private String surname;

    @Column(name = "birthday" , nullable = false)
    private LocalDate birthday;

    @Column(name = "gender" , nullable = false)
    private boolean gender;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "interests" , nullable = false)
    private String interests;

    @Column(name = "about_me" , nullable = false)
    private String aboutMe;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "phone", nullable = false)
    private String phone;
    
}
