package es.upm.miw.devops.rest.dto;

public record UserActiveRequest(
        Long id,
        boolean active
) {
}
