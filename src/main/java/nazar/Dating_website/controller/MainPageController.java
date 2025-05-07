package nazar.Dating_website.controller;

import nazar.Dating_website.dto.InvitationsDTO;
import nazar.Dating_website.dto.UserDTO;
import nazar.Dating_website.service.InvitationService;
import nazar.Dating_website.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class MainPageController {

    @Autowired
    private UserService userService;

    @Autowired
    private InvitationService invitationService;

    @GetMapping("/mainPage")
    public String showMainPage(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "sort", required = false) String sort,
                               HttpSession session,
                               Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<UserDTO> users = userService.getUsersForMainPage(search, sort, userId);
        List<InvitationsDTO> sentInvitations = invitationService.getSentInvitations(userId);
        List<InvitationsDTO> acceptedInvitations = invitationService.getAcceptedInvitations(userId);

        // Створюємо набір ID користувачів, яким уже відправлено запрошення
        Set<Long> sentInvitationReceiverIds = sentInvitations.stream()
                .map(invitation -> invitation.getReceiver().getId())
                .collect(Collectors.toSet());

        // Створюємо набір ID користувачів, з якими є прийняте запрошення
        Set<Long> acceptedUserIds = acceptedInvitations.stream()
                .map(invitation -> {
                    if (invitation.getSender().getId().equals(userId)) {
                        return invitation.getReceiver().getId();
                    } else {
                        return invitation.getSender().getId();
                    }
                })
                .collect(Collectors.toSet());

        model.addAttribute("users", users);
        model.addAttribute("sentInvitationReceiverIds", sentInvitationReceiverIds);
        model.addAttribute("acceptedUserIds", acceptedUserIds);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        return "mainPage";
    }
}