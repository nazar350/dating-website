package nazar.Dating_website.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import nazar.Dating_website.model.UserModel;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);

    List<UserModel> findAllByIdNot(Long id);

    List<UserModel> findByInterestsContaining(String interests);
}
