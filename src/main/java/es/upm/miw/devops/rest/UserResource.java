package es.upm.miw.devops.rest;

import es.upm.miw.devops.persistence.User;
import es.upm.miw.devops.rest.dto.UserActiveRequest;
import es.upm.miw.devops.rest.dto.UserRequest;
import es.upm.miw.devops.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {

    public static final String USERS = "/user";
    public static final String ID_ID = "/{id}";
    public static final String ID_ACTIVE = "/{id}/active";

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> readAll(@RequestParam(required = false) Boolean billable) {
        return this.userService.readAll(billable);
    }

    @GetMapping(ID_ID)
    public User readById(@PathVariable Long id) {
        return this.userService.readById(id);
    }

    @DeleteMapping(ID_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        this.userService.deleteById(id);
    }

    @PutMapping(ID_ACTIVE)
    public User updateActive(@PathVariable Long id, @RequestBody boolean active) {
        return this.userService.updateActive(id, active);
    }

    @PutMapping(ID_ID)
    public User update(@PathVariable Long id, @RequestBody UserRequest request) {
        return this.userService.update(id, request);
    }

    @PatchMapping
    public List<User> updateActive(@RequestBody List<UserActiveRequest> requests) {
        return this.userService.updateActive(requests);
    }
}
