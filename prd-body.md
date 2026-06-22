## Problem Statement

As a League of Legends player wanting to improve at Fiora's vitals mechanic, I need a practice tool that lets me repeatedly train hitting vitals from different angles under various conditions. This should be a Java-based web application that I can show interviewers as proof of my Java development abilities.

## Solution

A server-authoritative web application where a player practices hitting vitals on AI opponents in a bounded arena. The player controls a character with movement and dash-attack abilities, while opponents exhibit configurable movement patterns and reactions. A scoring system tracks vitals hit versus misses, providing detailed feedback after each timed session.

## User Stories

1. As a player, I want to configure a practice session with custom duration, so that I can practice for the time I have available.

2. As a player, I want to set the number of opponents in my session, so that I can scale difficulty based on my skill level.

3. As a player, I want to configure opponent movement types (stationary, patrol, chase, flee), so that I can practice against different challenge levels.

4. As a player, I want opponents to share movement settings by default, so that quick session setup doesn't require configuring each opponent individually.

5. As a player, I want to override individual opponent settings, so that I can create varied practice scenarios.

6. As a player, I want to set the skill cooldown duration, so that I can practice at different pacing levels.

7. As a player, I want to configure opponent HP values, so that targets can be durable or fragile based on practice goals.

8. As a player, I want to configure tick rate, so that I can adjust simulation granularity for my hardware.

9. As a player, I want a timed practice session, so that I can measure my improvement over fixed intervals.

10. As a player, I want the timer always visible, so that I can pace myself throughout the session.

11. As a player, I want to move my character by right-clicking, so that positioning feels familiar to the actual game.

12. As a player, I want my character to move at full speed instantly on click, so that movement feels responsive.

13. As a player, I want to dash by pressing Q toward my cursor, so that I can quickly reposition and attack.

14. As a player, I want my dash distance to be capped but allow shorter dashes, so that I have fine control over positioning.

15. As a player, I want my dash to auto-attack the nearest target in range after landing, so that I can focus on positioning rather than targeting.

16. As a player, I want the attack to fire only if a target is in range after the dash, so that I get feedback when my positioning is wrong.

17. As a player, I want the skill to go on cooldown after use, so that I must time my dashes strategically.

18. As a player, I want input ignored while dashing or on cooldown, so that I can't accidentally waste abilities.

19. As a player, I want to right-click on an opponent to attack them from my current position, so that I have an alternative attack option without dashing.

20. As a player, I want right-click attacks to have a short cooldown, so that I can't spam attacks endlessly.

21. As a player, I want right-click attacks to work while moving, so that I can kite opponents.

22. As a player, I want to see only one vital at a time on each opponent, so that I focus on the correct target angle.

23. As a player, I want a brief delay before the next vital appears after hitting one, so that I get visual feedback on success.

24. As a player, I want vitals to alternate between two diagonal groups (Top/Right vs Bottom/Left), so that I practice repositioning across the arena.

25. As a player, I want random selection within the vital group, so that I can't predict the exact next angle.

26. As a player, I want to earn a point for each vital hit, so that I'm rewarded for correct positioning.

27. As a player, I want a speed boost after hitting a vital, so that I'm rewarded for good play with enhanced mobility.

28. As a player, I want my speed boost to refresh on consecutive vital hits, so that I maintain momentum during streaks.

29. As a player, I want opponents to follow configurable movement patterns, so that practice stays varied.

30. As a player, I want patrol opponents to move back-and-forth until hitting arena boundaries, so that their movement is predictable.

31. As a player, I want chase opponents to pursue me when I enter their detection range, so that I practice under pressure.

32. As a player, I want flee opponents to retreat when I get close, so that I practice pursuing targets.

33. As a player, I want opponents to optionally react to my actions, so that practice feels more realistic.

34. As a player, I want opponents to optionally retaliate when hit, so that I'm punished for poor positioning.

35. As a player, I want opponents to optionally dodge my dashes, so that I must bait or time attacks carefully.

36. As a player, I want opponents to optionally activate their behavior only when I approach, so that I can choose which targets to engage.

37. As a player, I want opponents to have configurable HP, so that targets last longer or shorter based on practice goals.

38. As a player, I want defeated opponents to respawn at the furthest point from me, so that I must traverse the arena continuously.

39. As a player, I want opponents to respawn after a brief delay, so that I get a moment of feedback before re-engaging.

40. As a player, I want all opponents to spawn at session start, so that I have multiple targets immediately available.

41. As a player, I want opponents distributed evenly across the arena, so that practice scenarios are balanced.

42. As a player, I want to spawn in the arena center, so that I'm equidistant from all targets at the start.

43. As a player, I want the session to start immediately after configuration, so that I can begin practice without delay.

44. As a player, I want to abort a session mid-game, so that I can restart if configuration is wrong.

45. As a player, I want to see my current score during play, so that I track progress in real-time.

46. As a player, I want to see my cooldown status, so that I know when my next ability is ready.

47. As a player, I want detailed end-of-session results, so that I understand my strengths and weaknesses.

48. As a player, I want to see my vitals hit count, so that I know how many successful procs I achieved.

49. As a player, I want to see my whiff count, so that I know how often I dashed without a target.

50. As a player, I want to see my non-vital hit count, so that I know how often I attacked from the wrong angle.

51. As a player, I want all units blocked by walls and each other, so that the arena feels realistic.

52. As a player, I want a minimalist visual style, so that I can focus on gameplay without distraction.

53. As an interviewer, I want to run the application with a single command, so that I can easily evaluate the candidate's work.

54. As an interviewer, I want to read unit tests for core game logic, so that I can assess the candidate's testing practices.

55. As an interviewer, I want to see clean separation between game logic and infrastructure, so that I can evaluate architectural decisions.

## Implementation Decisions

### Architecture
- **Server-authoritative**: All game logic runs on the backend. The client sends inputs (movement clicks, Q presses, mouse position) and renders state received from the server.
- **WebSocket communication**: Persistent bidirectional connection for real-time updates. Spring Boot WebSocket support via `spring-websocket`.
- **Tick-based simulation**: Server runs at configurable tick rate (default 20/s), advancing game state each tick.

### Backend Stack
- **Java 11 LTS** with Maven build system
- **Spring Boot** for dependency injection, WebSocket endpoints, and static resource serving
- **Package**: `io.vitals.game`
- **Standalone JAR** deployment with embedded Tomcat

### Frontend Stack
- **Vanilla JavaScript** with HTML5 Canvas
- **No framework** — keeps frontend simple, focuses showcase on Java
- **Minimalist shapes** — circles for units, wedge shapes for vitals (sprites can be added later)

### Game Logic Layer
Pure Java classes with no framework dependencies:
- **MovementCalculator**: Handles position updates, collision detection, boundary constraints
- **VitalManager**: Manages which vital is active per opponent, handles hit detection, rotation logic
- **CombatResolver**: Determines attack targets, applies damage, tracks hits/misses
- **SessionManager**: Tracks timer, score, cooldowns, speed boost state
- **OpponentAI**: Computes movement for each opponent behavior type

### Domain Model
- **Player**: position, velocity, cooldown state, speed boost state
- **Opponent**: position, velocity, movement type, reaction type, HP, active vital, cooldowns (dodge, retaliation state)
- **Vital**: enum (TOP, RIGHT, BOTTOM, LEFT), group membership, cone angle range
- **Session**: duration, elapsed time, score breakdown, configuration

### Key Gameplay Constants
| Constant | Value |
|----------|-------|
| Player movement speed | 200 units/s |
| Dash speed | 400 units/s |
| Max dash distance | 300 units |
| Q attack range | 150 units |
| Right-click attack range | 20 units |
| Arena size | 800 x 600 units |
| Player radius | 15 units |
| Opponent radius | 20 units |
| Vital cone angle | 90 degrees each |
| Vital switch delay | 1 second |
| Speed boost | +20% for 2 seconds |
| Dodge cooldown | 3 seconds |
| Proximity trigger range | 300 units |
| Default HP | 5 |
| Default Q cooldown | 5 seconds |
| Right-click attack cooldown | 0.5 seconds |
| Respawn delay | 1-2 seconds |

### WebSocket Protocol
Client → Server:
- `MOVE_TO { x, y }` — right-click movement command
- `DASH { targetX, targetY }` — Q press with cursor position
- `ABORT` — quit session

Server → Client:
- `STATE { player, opponents, timer, score, cooldowns }` — full game state (every tick)
- `SESSION_END { results }` — final score breakdown

### Session Configuration Form
Pre-session web form with:
- Duration dropdown: 60/90/120/300 seconds
- Opponent count input (1-5 range)
- Q cooldown input (default 5s)
- Global opponent settings: movement type, reaction type, HP
- Checkbox: "Customize individual opponents" → reveals per-opponent dropdowns

## Testing Decisions

### Primary Seam: Game Logic Layer
Unit tests verify external behavior of pure Java classes:
- **VitalManager**: Given a hit position and active vital, does it correctly detect vital proc? Does it rotate to correct group?
- **MovementCalculator**: Given a unit's position and velocity, does it correctly handle boundary collision?
- **CombatResolver**: Given player position and opponents, does it select the correct target?
- **SessionManager**: Given elapsed time, does it correctly track cooldown expiry?

Tests focus on inputs and outputs, not internal state. Use JUnit 5 with Mockito for any necessary test doubles.

### Secondary Seam: Integration Tests
Small suite verifying:
- WebSocket endpoint accepts valid messages
- Configuration parsing from client request
- Session lifecycle (start → tick → end)

### What Makes a Good Test
- Tests external behavior, not implementation details
- Each test has clear arrange-act-assert structure
- Test names describe the scenario being verified
- Edge cases covered (e.g., boundary positions, cooldown boundaries)

## Out of Scope

- Multiplayer support
- Persistence/database storage
- User authentication
- High score tracking
- Difficulty presets (player can configure manually)
- Sound effects
- Sprite-based graphics (initially)
- Mobile/touch controls

## Further Notes

This is a greenfield project for interview demonstration purposes. The focus is on clean Java architecture and testing practices, not feature completeness. The vital system mirrors Fiora's mechanic from League of Legends for familiar gameplay context.
