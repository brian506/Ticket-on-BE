package com.ticketon.ticketon.dto;

public class EnqueuedUser {
    private final String email;
    private final double score;

    public EnqueuedUser(String email, double score) {
        this.email = email;
        this.score = score;
    }

    public String getEmail() {
        return email;
    }

    public double getScore() {
        return score;
    }
}
