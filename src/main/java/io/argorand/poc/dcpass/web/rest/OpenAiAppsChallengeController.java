package io.argorand.poc.dcpass.web.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the OpenAI domain verification challenge at /.well-known/openai-apps-challenge.
 * Configure the challenge string in application.yml or application-prod.yml:
 *   openai:
 *     apps:
 *       challenge: "your-challenge-string-from-openai"
 */
@RestController
public class OpenAiAppsChallengeController {

    @Value("${openai.apps.challenge:}")
    private String challenge;

    @GetMapping(path = "/.well-known/openai-apps-challenge", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> openaiAppsChallenge() {
        if (challenge == null || challenge.isBlank()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(challenge.trim());
    }
}
