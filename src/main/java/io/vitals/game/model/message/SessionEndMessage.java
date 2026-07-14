package io.vitals.game.model.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vitals.game.result.SessionResult;

public class SessionEndMessage {
    private final SessionResult results;

    @JsonCreator
    public SessionEndMessage(@JsonProperty("results") SessionResult results) {
        this.results = results;
    }

    public SessionResult getResults() {
        return results;
    }
}
