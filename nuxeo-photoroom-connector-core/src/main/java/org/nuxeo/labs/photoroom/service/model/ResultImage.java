package org.nuxeo.labs.photoroom.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultImage {

    public String result_b64;

    public ResultImage() {}

    public ResultImage(@JsonProperty("event")String result_b64) {
        this.result_b64 = result_b64;
    }

    public String getResult_b64() {
        return result_b64;
    }

    public void setResult_b64(String result_b64) {
        this.result_b64 = result_b64;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ResultImage that = (ResultImage) o;
        return Objects.equals(result_b64, that.result_b64);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(result_b64);
    }
}
