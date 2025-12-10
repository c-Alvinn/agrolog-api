package br.com.agrologqueue.api.model.enums;
import lombok.Getter;

@Getter
public enum Role {
    DRIVER("ROLE_DRIVER"),
    CARRIER("ROLE_CARRIER"),
    GATE_KEEPER("ROLE_GATE_KEEPER"),
    SCALE_OPERATOR("ROLE_SCALE_OPERATOR"),
    MANAGER("ROLE_MANAGER"),
    ADMIN("ROLE_ADMIN");

    private String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }
}