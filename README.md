# Line Strike (Java Swing Game)

A 2-player turn-based strategy game built using Java Swing where players launch strikers across the board and attempt to cross their opponent's line while avoiding collisions.

## Features

✅ Two-Player Turn-Based Gameplay

✅ Random Starting Player Selection

✅ Mouse Drag and Release Controls

✅ Realistic Striker Movement

✅ Wall Bounce Mechanics

✅ Friction-Based Physics

✅ Dynamic Line Creation After Every Move

✅ Line Crossing Detection

✅ Striker Collision Detection

✅ Automatic Winner Determination

✅ Game Restart Functionality

✅ Smooth Swing-Based Rendering

---

## Game Rules

1. Two players take turns controlling their striker.
2. Drag and release the striker to launch it.
3. After each move, a line is created from the striker's path.
4. The opponent must cross that line on their turn.
5. If a player successfully crosses the opponent's line, they win.
6. If a moving striker collides with the opponent's striker, the moving player loses instantly.
7. Click anywhere after the game ends to start a new match.

---

## Controls

| Action         | Control                        |
| -------------- | ------------------------------ |
| Select Striker | Mouse Click                    |
| Aim Shot       | Mouse Drag                     |
| Launch Striker | Mouse Release                  |
| Restart Game   | Click Anywhere After Game Over |

---

## Technologies Used

* Java
* Java Swing
* JFrame
* JPanel
* Graphics2D
* Mouse Events
* Timer Animation
* Object-Oriented Programming (OOP)
* Collision Detection
* Basic Game Physics

---

## Project Structure

```text
Line Strike
│
├── Main.java
│   └── Creates the game window
│
└── GamePanel.java
    ├── Game Logic
    ├── Player Movement
    ├── Collision Detection
    ├── Line Crossing Detection
    ├── Physics System
    └── Rendering Engine
```

---

## How to Run

1. Clone the repository
2. Open the project in IntelliJ IDEA
3. Run `Main.java`
4. The game window will open
5. Play using mouse controls

---

## What I Learned

* Java Swing GUI Development
* Event-Driven Programming
* Mouse Input Handling
* Collision Detection
* Game Physics Simulation
* Turn-Based Game Logic
* Graphics Rendering using Graphics2D
* Object-Oriented Programming
* Game Development Fundamentals

---

## Future Improvements

* Score Tracking System
* Sound Effects
* Difficulty Levels
* Power-Ups
* Multiplayer Over Network
* Improved Graphics and Animations
* Mobile Version

---

## Author

Ariponnvel Ravi

---

This project was developed to learn Java Swing, game physics, collision detection, and interactive game development concepts.
