package nazar.Dating_website.controller;

import jakarta.servlet.http.HttpSession;
import nazar.Dating_website.dto.InvitationsDTO;
import nazar.Dating_website.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @GetMapping("/invitations")
    public String invitations(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        List<InvitationsDTO> sentInvitations = invitationService.getSentInvitations(userId);
        List<InvitationsDTO> receivedInvitations = invitationService.getReceivedInvitations(userId);
        List<InvitationsDTO> acceptedInvitations = invitationService.getAcceptedInvitations(userId);

        model.addAttribute("sentInvitations", sentInvitations);
        model.addAttribute("receivedInvitations", receivedInvitations);
        model.addAttribute("acceptedInvitations", acceptedInvitations);

        return "invitations";
    }

    @PostMapping("/invitations/send")
    public String sendInvitation(@RequestParam("senderId") Long senderId,
                                 @RequestParam("receiverId") Long receiverId,
                                 @RequestParam(value = "search", required = false) String search,
                                 @RequestParam(value = "sort", required = false) String sort,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(senderId)) {
            return "redirect:/login";
        }

        try {
            invitationService.sendInvitation(senderId, receiverId);
            redirectAttributes.addFlashAttribute("message", "Invitation sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // Передаємо search і sort у перенаправленні
        String redirectUrl = "redirect:/mainPage";
        if (search != null && !search.isEmpty()) {
            redirectUrl += "?search=" + search;
        }
        if (sort != null && !sort.isEmpty()) {
            redirectUrl += (search != null && !search.isEmpty() ? "&" : "?") + "sort=" + sort;
        }
        return redirectUrl;
    }

    @PostMapping("invitations/accept")
    public String acceptInvitation(@RequestParam("receiverId") Long receiverId,
                                   @RequestParam("invitationId") Long invitationId,
                                   HttpSession session,
                                   Model model) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(receiverId)) {
            return "redirect:/login";
        }
        try {
            invitationService.acceptInvitation(receiverId,invitationId);
            model.addAttribute("message", "Invitation accepted successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("sentInvitations", invitationService.getSentInvitations(receiverId));
        model.addAttribute("receivedInvitations", invitationService.getReceivedInvitations(receiverId));
        model.addAttribute("acceptedInvitations", invitationService.getAcceptedInvitations(receiverId));
        return "invitations";
    }

    @PostMapping("/invitations/cancel")
    public String cancelInvitation(@RequestParam("senderId") Long senderId,
                                   @RequestParam("invitationId") Long invitationId,
                                   HttpSession session,
                                   Model model) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(senderId)) {
            return "redirect:/login";
        }

        try {
            invitationService.cancelInvitation(senderId, invitationId);
            model.addAttribute("message", "Invitation cancelled successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("sentInvitations", invitationService.getSentInvitations(senderId));
        model.addAttribute("receivedInvitations", invitationService.getReceivedInvitations(senderId));
        model.addAttribute("acceptedInvitations", invitationService.getAcceptedInvitations(senderId));
        return "invitations";
    }

    @PostMapping("/invitations/decline")
    public String declineInvitation(@RequestParam("receiverId") Long receiverId,
                                    @RequestParam("invitationId") Long invitationId,
                                    HttpSession session,
                                    Model model) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(receiverId)) {
            return "redirect:/login";
        }

        try {
            invitationService.declineInvitation(receiverId, invitationId);
            model.addAttribute("message", "Invitation declined successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("sentInvitations", invitationService.getSentInvitations(receiverId));
        model.addAttribute("receivedInvitations", invitationService.getReceivedInvitations(receiverId));
        model.addAttribute("acceptedInvitations", invitationService.getAcceptedInvitations(receiverId));
        return "invitations";
    }
}
