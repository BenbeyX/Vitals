package io.vitals.game.model.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vitals.game.model.MovementType;
import io.vitals.game.model.Opponent;
import io.vitals.game.model.Player;
import io.vitals.game.model.Position;
import io.vitals.game.model.ReactionType;
import io.vitals.game.model.Velocity;
import io.vitals.game.model.Vital;
import io.vitals.game.result.SessionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageSerializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    // ==================== Client → Server Messages ====================

    @Nested
    @DisplayName("MoveToMessage serialization")
    class MoveToMessageTests {

        @Test
        @DisplayName("serializes to JSON with x and y fields")
        void serializesToJson() throws Exception {
            MoveToMessage message = new MoveToMessage(150.5, 300.0);

            String json = objectMapper.writeValueAsString(message);

            assertTrue(json.contains("\"x\""));
            assertTrue(json.contains("\"y\""));
            assertTrue(json.contains("150.5"));
            assertTrue(json.contains("300.0"));
        }

        @Test
        @DisplayName("deserializes from JSON with x and y fields")
        void deserializesFromJson() throws Exception {
            String json = "{\"x\":150.5,\"y\":300.0}";

            MoveToMessage result = objectMapper.readValue(json, MoveToMessage.class);

            assertEquals(150.5, result.getX(), 0.001);
            assertEquals(300.0, result.getY(), 0.001);
        }

        @Test
        @DisplayName("round-trip serialization preserves values")
        void roundTrip_preservesValues() throws Exception {
            MoveToMessage original = new MoveToMessage(42.7, 99.2);

            String json = objectMapper.writeValueAsString(original);
            MoveToMessage result = objectMapper.readValue(json, MoveToMessage.class);

            assertEquals(original.getX(), result.getX(), 0.001);
            assertEquals(original.getY(), result.getY(), 0.001);
        }

        @Test
        @DisplayName("handles zero values")
        void handlesZeroValues() throws Exception {
            MoveToMessage message = new MoveToMessage(0, 0);

            String json = objectMapper.writeValueAsString(message);
            MoveToMessage result = objectMapper.readValue(json, MoveToMessage.class);

            assertEquals(0, result.getX(), 0.001);
            assertEquals(0, result.getY(), 0.001);
        }

        @Test
        @DisplayName("handles negative coordinates")
        void handlesNegativeCoordinates() throws Exception {
            MoveToMessage message = new MoveToMessage(-50.0, -75.3);

            String json = objectMapper.writeValueAsString(message);
            MoveToMessage result = objectMapper.readValue(json, MoveToMessage.class);

            assertEquals(-50.0, result.getX(), 0.001);
            assertEquals(-75.3, result.getY(), 0.001);
        }
    }

    @Nested
    @DisplayName("DashMessage serialization")
    class DashMessageTests {

        @Test
        @DisplayName("serializes to JSON with targetX and targetY fields")
        void serializesToJson() throws Exception {
            DashMessage message = new DashMessage(400.0, 200.0);

            String json = objectMapper.writeValueAsString(message);

            assertTrue(json.contains("\"targetX\""));
            assertTrue(json.contains("\"targetY\""));
            assertTrue(json.contains("400.0"));
            assertTrue(json.contains("200.0"));
        }

        @Test
        @DisplayName("deserializes from JSON with targetX and targetY fields")
        void deserializesFromJson() throws Exception {
            String json = "{\"targetX\":400.0,\"targetY\":200.0}";

            DashMessage result = objectMapper.readValue(json, DashMessage.class);

            assertEquals(400.0, result.getTargetX(), 0.001);
            assertEquals(200.0, result.getTargetY(), 0.001);
        }

        @Test
        @DisplayName("round-trip serialization preserves values")
        void roundTrip_preservesValues() throws Exception {
            DashMessage original = new DashMessage(123.4, 567.8);

            String json = objectMapper.writeValueAsString(original);
            DashMessage result = objectMapper.readValue(json, DashMessage.class);

            assertEquals(original.getTargetX(), result.getTargetX(), 0.001);
            assertEquals(original.getTargetY(), result.getTargetY(), 0.001);
        }
    }

    @Nested
    @DisplayName("AbortMessage serialization")
    class AbortMessageTests {

        @Test
        @DisplayName("serializes to empty JSON object")
        void serializesToJson() throws Exception {
            AbortMessage message = new AbortMessage();

            String json = objectMapper.writeValueAsString(message);

            assertEquals("{}", json);
        }

        @Test
        @DisplayName("deserializes from empty JSON object")
        void deserializesFromJson() throws Exception {
            String json = "{}";

            AbortMessage result = objectMapper.readValue(json, AbortMessage.class);

            assertNotNull(result);
        }
    }

    // ==================== Server → Client Messages ====================

    @Nested
    @DisplayName("GameStateMessage serialization")
    class GameStateMessageTests {

        @Test
        @DisplayName("serializes to JSON with player, opponents, timer, score, cooldowns")
        void serializesToJson() throws Exception {
            Player player = new Player(new Position(100, 200), new Velocity(50, 0));
            List<Opponent> opponents = Arrays.asList(
                createOpponent(300, 400)
            );
            Cooldowns cooldowns = new Cooldowns(2.5, true, 1.0);
            GameStateMessage message = new GameStateMessage(player, opponents, 45.0, 150, cooldowns);

            String json = objectMapper.writeValueAsString(message);

            assertTrue(json.contains("\"player\""));
            assertTrue(json.contains("\"opponents\""));
            assertTrue(json.contains("\"timer\""));
            assertTrue(json.contains("\"score\""));
            assertTrue(json.contains("\"cooldowns\""));
        }

        @Test
        @DisplayName("serializes nested player position and velocity")
        void serializesNestedPlayer() throws Exception {
            Player player = new Player(new Position(100, 200), new Velocity(50, 0));
            GameStateMessage message = new GameStateMessage(player, Arrays.asList(), 0, 0,
                new Cooldowns(0, false, 0));

            String json = objectMapper.writeValueAsString(message);

            assertTrue(json.contains("\"position\""));
            assertTrue(json.contains("\"velocity\""));
        }

        @Test
        @DisplayName("serializes nested opponents with all fields")
        void serializesNestedOpponents() throws Exception {
            Opponent opponent = createOpponent(300, 400);
            GameStateMessage message = new GameStateMessage(
                new Player(new Position(0, 0), new Velocity(0, 0)),
                Arrays.asList(opponent),
                0, 0, new Cooldowns(0, false, 0));

            String json = objectMapper.writeValueAsString(message);

            assertTrue(json.contains("\"hp\""));
            assertTrue(json.contains("\"vital\""));
            assertTrue(json.contains("\"position\""));
        }

        @Test
        @DisplayName("serializes nested cooldowns")
        void serializesNestedCooldowns() throws Exception {
            Cooldowns cooldowns = new Cooldowns(3.0, true, 1.5);
            GameStateMessage message = new GameStateMessage(
                new Player(new Position(0, 0), new Velocity(0, 0)),
                Arrays.asList(), 0, 0, cooldowns);

            String json = objectMapper.writeValueAsString(message);

            // Jackson lowercases first letter by default: QCooldownRemaining -> qcooldownRemaining
            assertTrue(json.contains("\"qcooldownRemaining\""), "JSON should contain qcooldownRemaining. Actual: " + json);
            assertTrue(json.contains("\"speedBoostActive\""));
            assertTrue(json.contains("\"speedBoostRemaining\""));
        }

        @Test
        @DisplayName("round-trip serialization preserves all values")
        void roundTrip_preservesValues() throws Exception {
            Player player = new Player(new Position(100, 200), new Velocity(50, 0));
            List<Opponent> opponents = Arrays.asList(createOpponent(300, 400));
            Cooldowns cooldowns = new Cooldowns(2.5, true, 1.0);
            GameStateMessage original = new GameStateMessage(player, opponents, 45.0, 150, cooldowns);

            String json = objectMapper.writeValueAsString(original);
            GameStateMessage result = objectMapper.readValue(json, GameStateMessage.class);

            assertEquals(original.getTimer(), result.getTimer(), 0.001);
            assertEquals(original.getScore(), result.getScore());
            assertNotNull(result.getPlayer());
            assertNotNull(result.getOpponents());
            assertEquals(1, result.getOpponents().size());
            assertNotNull(result.getCooldowns());
        }

        @Test
        @DisplayName("serializes enum vital as string")
        void serializesEnumVitalAsString() throws Exception {
            Opponent opponent = createOpponent(300, 400);
            GameStateMessage message = new GameStateMessage(
                new Player(new Position(0, 0), new Velocity(0, 0)),
                Arrays.asList(opponent),
                0, 0, new Cooldowns(0, false, 0));

            String json = objectMapper.writeValueAsString(message);

            assertTrue(json.contains("\"TOP\"") || json.contains("\"vital\":\"TOP\""),
                "Vital enum should serialize as its name string");
        }
    }

    @Nested
    @DisplayName("SessionEndMessage serialization")
    class SessionEndMessageTests {

        @Test
        @DisplayName("serializes to JSON with results object")
        void serializesToJson() throws Exception {
            SessionResult results = new SessionResult(500, 10, 5, 3, 2, 60.0, 0.667);
            SessionEndMessage message = new SessionEndMessage(results);

            String json = objectMapper.writeValueAsString(message);

            assertTrue(json.contains("\"results\""));
            assertTrue(json.contains("\"score\""));
            assertTrue(json.contains("\"vitalsHit\""));
            assertTrue(json.contains("\"accuracy\""));
        }

        @Test
        @DisplayName("round-trip serialization preserves results")
        void roundTrip_preservesValues() throws Exception {
            SessionResult results = new SessionResult(500, 10, 5, 3, 2, 60.0, 0.667);
            SessionEndMessage original = new SessionEndMessage(results);

            String json = objectMapper.writeValueAsString(original);
            SessionEndMessage result = objectMapper.readValue(json, SessionEndMessage.class);

            assertNotNull(result.getResults());
            assertEquals(500, result.getResults().getScore());
            assertEquals(10, result.getResults().getVitalsHit());
            assertEquals(0.667, result.getResults().getAccuracy(), 0.001);
        }

        @Test
        @DisplayName("serializes all SessionResult fields")
        void serializesAllSessionResultFields() throws Exception {
            SessionResult results = new SessionResult(500, 10, 5, 3, 2, 60.0, 0.667);
            SessionEndMessage message = new SessionEndMessage(results);

            String json = objectMapper.writeValueAsString(message);

            assertTrue(json.contains("\"opponentsSlain\""));
            assertTrue(json.contains("\"nonVitalHits\""));
            assertTrue(json.contains("\"whiffs\""));
            assertTrue(json.contains("\"elapsedTime\""));
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("MoveToMessage with very large coordinates")
        void moveToMessage_largeValues() throws Exception {
            MoveToMessage message = new MoveToMessage(Double.MAX_VALUE, Double.MAX_VALUE);

            String json = objectMapper.writeValueAsString(message);
            MoveToMessage result = objectMapper.readValue(json, MoveToMessage.class);

            assertEquals(Double.MAX_VALUE, result.getX(), 0.0);
        }

        @Test
        @DisplayName("GameStateMessage with empty opponents list")
        void gameStateMessage_emptyOpponents() throws Exception {
            GameStateMessage message = new GameStateMessage(
                new Player(new Position(0, 0), new Velocity(0, 0)),
                Arrays.asList(),
                0, 0, new Cooldowns(0, false, 0));

            String json = objectMapper.writeValueAsString(message);
            GameStateMessage result = objectMapper.readValue(json, GameStateMessage.class);

            assertNotNull(result.getOpponents());
            assertEquals(0, result.getOpponents().size());
        }

        @Test
        @DisplayName("GameStateMessage with multiple opponents")
        void gameStateMessage_multipleOpponents() throws Exception {
            List<Opponent> opponents = Arrays.asList(
                createOpponent(100, 100),
                createOpponent(200, 200),
                createOpponent(300, 300)
            );
            GameStateMessage message = new GameStateMessage(
                new Player(new Position(0, 0), new Velocity(0, 0)),
                opponents, 30.0, 250, new Cooldowns(1.0, false, 0));

            String json = objectMapper.writeValueAsString(message);
            GameStateMessage result = objectMapper.readValue(json, GameStateMessage.class);

            assertEquals(3, result.getOpponents().size());
        }

        @Test
        @DisplayName("Cooldowns with zero values")
        void cooldowns_zeroValues() throws Exception {
            Cooldowns cooldowns = new Cooldowns(0, false, 0);

            String json = objectMapper.writeValueAsString(cooldowns);
            Cooldowns result = objectMapper.readValue(json, Cooldowns.class);

            assertEquals(0, result.getQCooldownRemaining(), 0.001);
            assertFalse(result.isSpeedBoostActive());
            assertEquals(0, result.getSpeedBoostRemaining(), 0.001);
        }
    }

    // ==================== Helper Methods ====================

    private Opponent createOpponent(double x, double y) {
        return new Opponent(
            new Position(x, y),
            new Velocity(0, 0),
            Vital.TOP,
            5, 5, 0, 0, 0,
            MovementType.STATIONARY,
            ReactionType.NONE,
            null, null, true, 0, false, true
        );
    }
}
