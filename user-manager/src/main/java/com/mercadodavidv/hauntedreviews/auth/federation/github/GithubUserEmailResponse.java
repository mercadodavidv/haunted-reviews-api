package com.mercadodavidv.hauntedreviews.auth.federation.github;

// @formatter:off
public record GithubUserEmailResponse(
    String email,
    boolean primary,
    boolean verified
) { }
// @formatter:on
