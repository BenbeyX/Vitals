# Vitals

A server-authoritative web application for practicing League of Legends' Fiora vitals mechanic. Built with Java 11 and Spring Boot as an interview demonstration project.

## Overview

Vitals is a practice tool where players train hitting vitals from different angles under various conditions. The player controls a character with movement and dash-attack abilities, facing AI opponents with configurable movement patterns and reactions. A scoring system tracks vitals hit versus misses, providing detailed feedback after each timed session.

## Features

### Session Configuration
- Customizable session duration (60/90/120/300 seconds)
- Configurable number of opponents (1-5)
- Adjustable Q skill cooldown duration
- Configurable opponent HP values
- Adjustable tick rate for simulation granularity
- Per-opponent movement and reaction settings

### Gameplay
- Right-click movement with instant full-speed response
- Q dash ability with capped distance (max 300 units)
- Auto-attack nearest target after dash landing
- Right-click attacks from current position
- Speed boost reward (+20% for 2 seconds) after hitting vitals
- Speed boost refresh on consecutive vital hits

### Vital System
- One visible vital at a time per opponent
- Vitals alternate between diagonal groups (Top/Right vs Bottom/Left)
- Random selection within each vital group
- 1-second delay before next vital appears

### Opponent AI
- **Stationary**: No movement
- **Patrol**: Back-and-forth movement until hitting arena boundaries
- **Chase**: Pursues player when within detection range
- **Flee**: Retreats when player gets close

### Opponent Reactions
- Retaliation when hit
- Dodge ability to avoid player dashes
- Proximity-triggered behavior activation

### Scoring
- Points for vital hits
- Whiff count (dashes without targets)
- Non-vital hit count (attacks from wrong angle)

## Tech Stack

### Backend
- Java 11 LTS
- Spring Boot
- Spring WebSocket
- Maven
- Embedded Tomcat

### Frontend
- Vanilla JavaScript
- HTML5 Canvas
- Minimalist visual style

## Architecture

The application follows a server-authoritative model where all game logic runs on the backend. The client sends inputs and renders state received from the server.

### Game Logic Layer
Pure Java classes with no framework dependencies:
- **MovementCalculator**: Position updates, collision detection, boundary constraints
- **VitalManager**: Active vital tracking, hit detection, rotation logic
- **CombatResolver**: Attack target determination, damage application, hit/miss tracking
- **SessionManager**: Timer, score, cooldowns, speed boost state
- **OpponentAI**: Movement computation for each behavior type

### Domain Model
- **Player**: position, velocity, cooldown state, speed boost state
- **Opponent**: position, velocity, movement type, reaction type, HP, active vital
- **Vital**: enum (TOP, RIGHT, BOTTOM, LEFT) with group membership and cone angle
- **Session**: duration, elapsed time, score breakdown, configuration

### WebSocket Protocol

**Client → Server:**
- `MOVE_TO { x, y }` — right-click movement command
- `DASH { targetX, targetY }` — Q press with cursor position
- `ABORT` — quit session

**Server → Client:**
- `STATE { player, opponents, timer, score, cooldowns }` — full game state (every tick)
- `SESSION_END { results }` — final score breakdown

## Gameplay Constants

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

## Running the Application

```bash
mvn spring-boot:run
```

Navigate to `http://localhost:8080` to access the application.

## Testing

The project includes unit tests for core game logic focusing on external behavior:
- VitalManager: vital hit detection and rotation
- MovementCalculator: boundary collision handling
- CombatResolver: target selection
- SessionManager: cooldown tracking

Integration tests verify:
- WebSocket endpoint message handling
- Configuration parsing
- Session lifecycle management

Run tests with:
```bash
mvn test
```

## Out of Scope

- Multiplayer support
- Persistence/database storage
- User authentication
- High score tracking
- Difficulty presets
- Sound effects
- Sprite-based graphics (initially)
- Mobile/touch controls

## License

This project is for interview demonstration purposes.
