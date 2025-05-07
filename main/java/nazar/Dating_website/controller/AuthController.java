package nazar.Dating_website.controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import nazar.Dating_website.dto.AuthDTO;
import nazar.Dating_website.model.UserModel;
import nazar.Dating_website.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityContextRepository securityContextRepository;



    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        return "login";
    }

    @GetMapping("/auth")
    public String showAuthPage(Model model) {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setGender(null); // або false, якщо потрібне значення за замовчуванням
        model.addAttribute("authDTO", authDTO);
        return "auth";
    }

    @PostMapping("/auth")
    public String signupAndCreateProfile(@Valid @ModelAttribute("authDTO") AuthDTO authDTO,
                                         BindingResult result,
                                         HttpSession session,
                                         HttpServletRequest request, // Додаємо request
                                         HttpServletResponse response, // Додаємо response
                                         Model model) {
        if (result.hasErrors()) {
            return "auth";
        }

        if (!authDTO.getPassword().equals(authDTO.getRepeatPassword())) {
            model.addAttribute("passwordMismatch", "Passwords do not match");
            return "auth";
        }

        try {
            UserModel user = userService.CreateUser(authDTO);
            session.setAttribute("userId", user.getId());

            // Аутентифікуємо користувача вручну
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user.getEmail(), authDTO.getPassword(), List.of());

            // Встановлюємо аутентифікацію в SecurityContext
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authToken);

            // Зберігаємо SecurityContext у сесії
            securityContextRepository.saveContext(securityContext, request, response);
        } catch (Exception e) {
            model.addAttribute("signupError", e.getMessage());
            return "auth";
        }

        return "redirect:/mainPage";
    }
}