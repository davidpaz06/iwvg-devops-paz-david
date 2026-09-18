package es.upm.miw.devops.service;

import es.upm.miw.devops.persistence.User;
import es.upm.miw.devops.persistence.UserRepository;
import es.upm.miw.devops.rest.dto.UserActiveRequest;
import es.upm.miw.devops.rest.dto.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public User updateActive(Long id, boolean active) {
        User user = this.readById(id);
        user.setActive(active);
        return this.userRepository.save(user);
    }

    public User update(Long id, UserRequest request) {
        User user = this.readById(id);
        user.setName(request.name());
        user.setFamilyName(request.familyName());
        user.setEmail(request.email());
        user.setIdentity(request.identity());
        user.setAddress(request.address());
        user.setCity(request.city());
        user.setProvince(request.province());
        user.setPostalCode(request.postalCode());
        return this.userRepository.save(user);
    }

    @Transactional
    public List<User> updateActive(List<UserActiveRequest> requests) {
        return requests.stream()
                .map(request -> this.updateActive(request.id(), request.active()))
                .toList();
    }
}
