package nazar.Dating_website.repository;
import nazar.Dating_website.model.InvitationModel;
import nazar.Dating_website.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InvitationRepository extends JpaRepository<InvitationModel, Long> {
    List<InvitationModel> findBySenderAndSentTrue(UserModel sender);
    List<InvitationModel> findByReceiverAndReceivedTrueAndAcceptedFalse(UserModel receiver);
    List<InvitationModel> findByAcceptedTrueAndSenderOrAcceptedTrueAndReceiver(UserModel sender, UserModel receiver);

    InvitationModel findByIdAndSender(Long id, UserModel sender);
    InvitationModel findByIdAndReceiver(Long id, UserModel receiver);

    List<InvitationModel> findBySenderAndReceiverOrSenderAndReceiver(UserModel sender, UserModel receiver, UserModel receiver1, UserModel sender1);

    void deleteBySender(UserModel sender);

    void deleteByReceiver(UserModel receiver);
}
