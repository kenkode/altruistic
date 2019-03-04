package com.kenkode.altruistic;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusesResponse {
    @SerializedName("results")
    private List<Model> results;

    public List<Model> getResults() {
        return results;
    }

    public void setResults(List<Model> results) {
        this.results = results;
    }
}
