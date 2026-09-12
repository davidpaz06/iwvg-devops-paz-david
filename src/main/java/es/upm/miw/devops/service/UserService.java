package es.upm.miw.devops.service;

import es.upm.miw.devops.persistence.User;
import es.upm.miw.devops.persistence.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User readById(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User (" + id + ") not found"));
    }

    public List<User> readAll(Boolean billable) {
        List<User> users = this.userRepository.findAll();
        if (billable == null) {
            return users;
        }
        return users.stream()
                .filter(user -> user.isBillable() == billable)
                .toList();
    }

    public void deleteById(Long id) {
        if (!this.userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User (" + id + ") not found");
        }
        this.userRepository.deleteById(id);
    }
}
