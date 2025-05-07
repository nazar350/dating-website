package nazar.Dating_website.service;

import nazar.Dating_website.dto.InvitationsDTO;
import nazar.Dating_website.model.InvitationModel;
import nazar.Dating_website.model.UserModel;
import nazar.Dating_website.repository.InvitationRepository;
import nazar.Dating_website.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationService {
    private static final Logger logger = LoggerFactory.getLogger(InvitationService.class);

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public void sendInvitation(Long senderId, Long receiverId) {
        logger.info("Attempting to send invitation from senderId: {} to receiverId: {}", senderId, receiverId);

        UserModel sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        UserModel receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if (senderId.equals(receiverId)) {
            logger.warn("Sender and receiver are the same: {}", senderId);
            throw new IllegalArgumentException("Cannot send invitation to yourself");
        }

        List<InvitationModel> existingSentInvitations = invitationRepository.findBySenderAndSentTrue(sender);
        if (existingSentInvitations.stream().anyMatch(inv -> inv.getReceiver().getId().equals(receiverId))) {
            logger.warn("Invitation already sent from senderId: {} to receiverId: {}", senderId, receiverId);
            throw new IllegalArgumentException("You have already sent an invitation to this user");
        }

        List<InvitationModel> existingReceivedInvitations = invitationRepository.findByReceiverAndReceivedTrueAndAcceptedFalse(sender);
        if (existingReceivedInvitations.stream().anyMatch(inv -> inv.getSender().getId().equals(receiverId))) {
            logger.warn("There is already a received invitation from receiverId: {} to senderId: {}", receiverId, senderId);
            throw new IllegalArgumentException("This user has already sent you an invitation");
        }

        List<InvitationModel> acceptedInvitations = invitationRepository.findByAcceptedTrueAndSenderOrAcceptedTrueAndReceiver(sender, sender);
        if (acceptedInvitations.stream().anyMatch(inv ->
                (inv.getSender().getId().equals(senderId) && inv.getReceiver().getId().equals(receiverId)) ||
                        (inv.getSender().getId().equals(receiverId) && inv.getReceiver().getId().equals(senderId)))) {
            logger.warn("There is already an accepted invitation between senderId: {} and receiverId: {}", senderId, receiverId);
            throw new IllegalArgumentException("You already have an accepted invitation with this user");
        }

        InvitationModel invitation = new InvitationModel();
        invitation.setSender(sender);
        invitation.setReceiver(receiver);
        invitation.setSent(true);
        invitation.setReceived(true);
        invitation.setAccepted(false);

        invitationRepository.save(invitation);
        logger.info("Invitation saved successfully: senderId: {}, receiverId: {}", senderId, receiverId);
    }

    public void cancelInvitation(Long senderId, Long invitationId) {
        UserModel sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        InvitationModel invitation = invitationRepository.findByIdAndSender(invitationId, sender);
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation not found or you are not the sender");
        }

        if (invitation.isAccepted()) {
            throw new IllegalArgumentException("Cannot cancel an accepted invitation");
        }

        invitationRepository.delete(invitation);
        logger.info("Invitation cancelled: invitationId: {}", invitationId);
    }

    public void acceptInvitation(Long receiverId, Long invitationId) {
        UserModel receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        InvitationModel invitation = invitationRepository.findByIdAndReceiver(invitationId, receiver);
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation not found or you are not the receiver");
        }

        if (invitation.isAccepted()) {
            throw new IllegalArgumentException("Invitation already accepted");
        }


        Long senderId = invitation.getSender().getId();


        List<InvitationModel> otherInvitations = invitationRepository.findBySenderAndReceiverOrSenderAndReceiver(
                invitation.getSender(), receiver, receiver, invitation.getSender());
        for (InvitationModel otherInvitation : otherInvitations) {
            if (!otherInvitation.getId().equals(invitationId)) {
                invitationRepository.delete(otherInvitation);
                logger.info("Deleted redundant invitation between senderId: {} and receiverId: {}", senderId, receiverId);
            }
        }

        invitation.setAccepted(true);
        invitation.setSent(false);
        invitation.setReceived(false);
        invitationRepository.save(invitation);
        logger.info("Invitation accepted: invitationId: {}", invitationId);
    }

    public void declineInvitation(Long receiverId, Long invitationId) {
        UserModel receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        InvitationModel invitation = invitationRepository.findByIdAndReceiver(invitationId, receiver);
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation not found or you are not the receiver");
        }

        if (invitation.isAccepted()) {
            throw new IllegalArgumentException("Cannot decline an accepted invitation");
        }

        invitationRepository.delete(invitation);
        logger.info("Invitation declined: invitationId: {}", invitationId);
    }

    public List<InvitationsDTO> getSentInvitations(Long userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<InvitationsDTO> sentInvitations = invitationRepository.findBySenderAndSentTrue(user)
                .stream()
                .map(userService::convertToInvitationDTO)
                .collect(Collectors.toList());
        logger.info("Fetched sent invitations for userId: {}, count: {}", userId, sentInvitations.size());
        return sentInvitations;
    }

    public List<InvitationsDTO> getReceivedInvitations(Long userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<InvitationsDTO> receivedInvitations = invitationRepository.findByReceiverAndReceivedTrueAndAcceptedFalse(user)
                .stream()
                .map(userService::convertToInvitationDTO)
                .collect(Collectors.toList());
        logger.info("Fetched received invitations for userId: {}, count: {}", userId, receivedInvitations.size());
        return receivedInvitations;
    }

    public List<InvitationsDTO> getAcceptedInvitations(Long userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<InvitationsDTO> acceptedInvitations = invitationRepository.findByAcceptedTrueAndSenderOrAcceptedTrueAndReceiver(user, user)
                .stream()
                .map(userService::convertToInvitationDTO)
                .collect(Collectors.toList());
        logger.info("Fetched accepted invitations for userId: {}, count: {}", userId, acceptedInvitations.size());
        return acceptedInvitations;
    }

}