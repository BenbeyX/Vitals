package io.vitals.game.model.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class AbortMessage {
    @JsonCreator
    public AbortMessage() {
    }
}
