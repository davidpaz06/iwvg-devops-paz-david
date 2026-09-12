package es.upm.miw.devops.rest;

import es.upm.miw.devops.persistence.User;
import es.upm.miw.devops.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {

    public static final String USERS = "/user";
    public static final String ID_ID = "/{id}";

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
}
