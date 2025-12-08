package br.com.agrologqueue.api.model.enums;
import lombok.Getter;

@Getter
public enum Role {
    DRIVER("DRIVER"),
    CARRIER("CARRIER"),
    GATE_KEEPER("GATE_KEEPER"),
    SCALE_OPERATOR("SCALE_OPERATOR"),
    MANAGER("MANAGER"),
    ADMIN("ADMIN");

    private String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }
}