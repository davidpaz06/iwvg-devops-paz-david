package es.upm.miw.devops.rest.dto;

public record UserRequest(
        String name,
        String familyName,
        String email,
        String identity,
        String address,
        String city,
        String province,
        String postalCode
) {
}
