package com.cx.restclient.sast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataRetentionSettingsDto {
    private int scansToKeep;
    public int getScansToKeep() {
        return scansToKeep;
    }

    public void setScansToKeep(int scansToKeep) {
        this.scansToKeep = scansToKeep;
    }


    public DataRetentionSettingsDto(int scansToKeep ) {
        this.scansToKeep = scansToKeep;
    }

}
