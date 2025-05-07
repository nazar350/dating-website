package nazar.Dating_website.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import nazar.Dating_website.dto.EditProfileDTO;
import nazar.Dating_website.dto.UserDTO;
import nazar.Dating_website.model.UserModel;
import nazar.Dating_website.service.InvitationService;
import nazar.Dating_website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;

    @Autowired
    private InvitationService invitationService;

    // Відображення профілю користувача (/myProfile)
    @GetMapping("/myProfile")
    public String showMyProfile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        UserModel userModel = userService.getUserById(userId);
        if (userModel == null) {
            return "redirect:/login";
        }

        UserDTO userDTO = userService.convertToUserDTO(userModel);
        model.addAttribute("user", userDTO);
        return "myProfile";
    }

    // Відображення форми редагування (/editProfile)
    @GetMapping("/editProfile")
    public String showEditProfile(@RequestParam("userId") Long userId, HttpSession session, Model model) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            return "redirect:/login";
        }

        // Передаємо порожню EditProfileDTO
        EditProfileDTO editProfileDTO = new EditProfileDTO();
        model.addAttribute("editProfileDTO", editProfileDTO);
        return "editProfile";
    }

    // Обробка оновлення профілю (/api/users/update)
    @PostMapping("/api/users/update")
    public String updateProfile(@Valid @ModelAttribute("editProfileDTO") EditProfileDTO editProfileDTO,
                                BindingResult result,
                                @RequestParam("userId") Long userId,
                                HttpSession session,
                                Model model) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("editProfileDTO", editProfileDTO);
            return "editProfile";
        }

        try {
            // Оновлюємо профіль, передаючи тільки заповнені поля
            userService.updateUserProfile(userId, editProfileDTO.getPhone(), editProfileDTO.getAbout(), editProfileDTO.getInterests());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update profile: " + e.getMessage());
            model.addAttribute("editProfileDTO", editProfileDTO);
            return "editProfile";
        }

        return "redirect:/myProfile";
    }

    @GetMapping("/userProfile")
    public String showUserProfile(@RequestParam("userId") Long userId,
                                  HttpSession session,
                                  Model model) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null) {
            return "redirect:/login";
        }

        UserModel userModel = userService.getUserById(userId);
        if (userModel == null) {
            return "redirect:/mainPage";
        }

        UserDTO user = userService.convertToUserDTO(userModel);

        boolean isFriend = invitationService.getAcceptedInvitations(sessionUserId)
                .stream()
                .anyMatch(invitationsDTO ->
                        (invitationsDTO.getSender().getId().equals(sessionUserId) && invitationsDTO.getReceiver().getId().equals(userId)) ||
                                (invitationsDTO.getSender().getId().equals(userId) && invitationsDTO.getReceiver().getId().equals(sessionUserId)));
        model.addAttribute("user", user);
        model.addAttribute("isFriend", isFriend);

        return "userProfile";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(@RequestParam("userId") Long userId, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            return "redirect:/login";
        }
        try {
            userService.deleteUser(userId);
            session.invalidate();
        } catch (Exception e) {
            return "redirect:/myProfile?error=" + e.getMessage();
        }
        return "redirect:/login?deleted=true";
    }

}
