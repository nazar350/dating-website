package nazar.Dating_website.model;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invitations")
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InvitationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @ToString.Exclude
    private UserModel sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @ToString.Exclude
    private UserModel receiver;

    @Column(name = "is_sent", nullable = false)
    private boolean sent = false;

    @Column(name = "is_accepted", nullable = false)
    private boolean accepted  = false;

    @Column(name = "is_received", nullable = false)
    private boolean received  = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}
