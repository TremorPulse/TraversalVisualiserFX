# VisualiserTraversalFX

A Java application that provides a visual representation of maze generation and pathfinding algorithms.

## Features

- Maze generation using Iterative Backtracking (Depth-First Search).
- Open maze generation for pathfinding algorithms.
- Maze solving using Tree Traversal and Dijkstra's Shortest Path algorithms.
- Real-time visualization of maze generation and solving processes.
- Display of algorithm metrics including visual time, traversal time, memory writes, and complexity.
- Interactive UI with the ability to switch between algorithms.

## Requirements

- Java Development Kit (JDK) 8 or higher
- JavaFX (included in JDK 8, but may need to be added separately for later versions)

## How to Run

1. Ensure you have Java and JavaFX set up on your system.
2. Compile the Java files: 
javac -d bin src/maze/.java src/application/.java
3. Run the application:
java -cp bin application.MazeApp

## Project Structure

- `src/maze/Maze.java`: Contains the core logic for maze generation and solving algorithms.
- `src/application/MazeApp.java`: The main application class that sets up the JavaFX stage.
- `src/application/MazeView.java`: Handles the visualization of the maze and algorithm metrics.

