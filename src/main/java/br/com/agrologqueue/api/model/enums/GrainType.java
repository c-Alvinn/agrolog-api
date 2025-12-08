package br.com.agrologqueue.api.model.enums;

import lombok.Getter;

@Getter
public enum GrainType {
    SOY("Soja"),
    CORN("Milho");

    private String friendlyName;

    GrainType(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}