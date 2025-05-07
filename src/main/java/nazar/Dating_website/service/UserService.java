package nazar.Dating_website.service;

import jakarta.transaction.Transactional;
import nazar.Dating_website.dto.AuthDTO;
import nazar.Dating_website.dto.InvitationsDTO;
import nazar.Dating_website.dto.UserDTO;
import nazar.Dating_website.model.InvitationModel;
import nazar.Dating_website.model.UserModel;
import nazar.Dating_website.repository.InvitationRepository;
import nazar.Dating_website.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private InvitationRepository invitationRepository;

    //Реєстрація
    public UserModel CreateUser(AuthDTO authDTO) {
        // Перевірка, чи користувач із таким email уже існує
        if (userRepository.findByEmail(authDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        UserModel user = new UserModel();
        user.setEmail(authDTO.getEmail());
        user.setPassword(passwordEncoder.encode(authDTO.getPassword()));
        user.setName(authDTO.getName());
        user.setSurname(authDTO.getSurname());
        user.setBirthday(authDTO.getBirthday());
        user.setGender(authDTO.getGender());
        user.setInterests(authDTO.getInterests());
        user.setAboutMe(authDTO.getAboutMe());
        user.setPhone(authDTO.getPhone());
        user.setPhotoPath(authDTO.getPhotoPath());

        return userRepository.save(user);
    }

    public UserDTO convertToUserDTO(UserModel user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setSurname(user.getSurname());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setGender(user.isGender());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setAbout(user.getAboutMe());
        userDTO.setInterests(user.getInterests());

        LocalDate birthday = user.getBirthday();
        if (birthday != null) {
            int age = Period.between(birthday, LocalDate.now()).getYears();
            userDTO.setAge(age);
        } else {
            userDTO.setAge(0);
        }

        String photoPath = user.getPhotoPath();
        if (photoPath != null && !photoPath.isEmpty()) {
            userDTO.setPhotoUrl("/images/" + photoPath);
        } else {
            userDTO.setPhotoUrl("/images/default-profile.jpg");
        }

        String interests = user.getInterests();
        if (interests != null && !interests.isEmpty()) {
            List<String> interestsList = Arrays.asList(interests.split(","));
            userDTO.setInterestsAsList(interestsList);
        } else {
            userDTO.setInterestsAsList(List.of());
        }

        return userDTO;
    }

    //Головна сторінка
    public List<UserDTO> getUsersForMainPage(String search, String sort, Long currentUserId) {
        List<UserModel> users = fetchUsers(currentUserId, search);
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());

        UserModel currentUser = userRepository.findById(currentUserId).orElse(null);
        List<String> currentUserInterests = (currentUser != null && currentUser.getInterests() != null)
                ? Arrays.asList(currentUser.getInterests().split(","))
                : new ArrayList<>();

        return sortUsers(userDTOs, sort, currentUserInterests);
    }

    private List<UserModel> fetchUsers(Long currentUserId, String search) {
        if (search != null && !search.trim().isEmpty()) {
            String[] searchTerms = search.trim().split("\\s*,\\s*");
            Set<UserModel> filteredUsers = new HashSet<>();

            for (String term : searchTerms) {
                List<UserModel> usersWithInterest = userRepository.findByInterestsContaining(term);
                filteredUsers.addAll(usersWithInterest);
            }

            return filteredUsers.stream()
                    .filter(user -> !user.getId().equals(currentUserId))
                    .collect(Collectors.toList());
        }
        return userRepository.findAllByIdNot(currentUserId);
    }

    private List<UserDTO> sortUsers(List<UserDTO> userDTOs, String sort, List<String> currentUserInterests) {
        if (sort == null || sort.isEmpty()) {
            sort = "alphabet-asc";
        }

        switch (sort) {
            case "alphabet-asc":
                userDTOs.sort(Comparator.comparing(UserDTO::getName));
                break;
            case "alphabet-desc":
                userDTOs.sort(Comparator.comparing(UserDTO::getName).reversed());
                break;
            case "interests-match":
                userDTOs.sort((user1, user2) -> {
                    long matches1 = user1.getInterestsAsList().stream()
                            .filter(interest -> currentUserInterests.contains(interest))
                            .count();
                    long matches2 = user2.getInterestsAsList().stream()
                            .filter(interest -> currentUserInterests.contains(interest))
                            .count();
                    return Long.compare(matches2, matches1);
                });
                break;
            case "registration-date":
                userDTOs.sort(Comparator.comparing(UserDTO::getCreatedAt).reversed());
                break;
            default:
                userDTOs.sort(Comparator.comparing(UserDTO::getName));
                break;
        }

        return userDTOs;
    }

    //Профіль і редагування профілю
    public UserModel getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void updateUserProfile(Long userId, String phone, String about, String interests) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Оновлюємо тільки ті поля, які не null і не порожні
        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone);
        }
        if (about != null && !about.trim().isEmpty()) {
            user.setAboutMe(about);
        }
        if (interests != null && !interests.trim().isEmpty()) {
            user.setInterests(interests);
        }

        userRepository.save(user);
    }

    public InvitationsDTO convertToInvitationDTO(InvitationModel invitation){
        InvitationsDTO invitationDTO = new InvitationsDTO();
        invitationDTO.setId(invitation.getId());
        invitationDTO.setSender(convertToUserDTO(invitation.getSender()));
        invitationDTO.setReceiver(convertToUserDTO(invitation.getReceiver()));
        invitationDTO.setSent(invitation.isSent());
        invitationDTO.setReceived(invitation.isReceived());
        invitationDTO.setAccepted(invitation.isAccepted());
        return invitationDTO;
    }

    @Transactional
    public void deleteUser(Long userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        invitationRepository.deleteBySender(user);
        invitationRepository.deleteByReceiver(user);

        userRepository.delete(user);
    }

}