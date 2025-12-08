package br.com.agrologqueue.api.model.enums;

import lombok.Getter;

@Getter
public enum OperationType {
    LOADING("Embarque"),
    UNLOADING("Desembarque");

    private String friendlyName;

    OperationType(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}