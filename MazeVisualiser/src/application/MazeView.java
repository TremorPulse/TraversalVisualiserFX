package application;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import maze.Maze;

import java.util.List;
import java.util.Set;

public class MazeView extends BorderPane {
    private Canvas canvas;
    private Maze maze;
    private List<int[]> steps;
    private int currentStep = 0;
    private long startTime;
    private long elapsedTime;
    private long traversalTime;
    private boolean isMazeSolved = false;
    private boolean animationFinished = false;
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 100;
    private String currentAlgorithm = "Tree Traversal";

    public MazeView(Maze maze) {
        this.maze = maze;
        this.steps = maze.getSteps();

        // Calculate cell size based on maze dimensions
        int cellSize = calculateCellSize(maze.getRow(), maze.getCol());
        int canvasWidth = maze.getCol() * cellSize + 300; 
        int canvasHeight = maze.getRow() * cellSize + 250;

        canvas = new Canvas(canvasWidth, canvasHeight);

        Button switchAlgorithmButton = new Button("Switch Algorithm");
        switchAlgorithmButton.setOnAction(e -> switchAlgorithm());

        HBox buttonBox = new HBox(switchAlgorithmButton);
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        buttonBox.setSpacing(10);

        StackPane stackPane = new StackPane(canvas, buttonBox);
        stackPane.setStyle("-fx-background-color: #0e1111;");

        setCenter(stackPane);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#0e1111"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        startTime = System.currentTimeMillis();

        startAnimation();
    }

    private int calculateCellSize(int rows, int cols) {
        int maxSize = 800; 
        return Math.min(maxSize / Math.max(rows, cols), 8);
    }

    private void switchAlgorithm() {
        maze.resetMaze();
        if (currentAlgorithm.equals("Tree Traversal")) {
            currentAlgorithm = "Dijkstra's Shortest Path";
            maze.generateOpenMaze();
        } else {
            currentAlgorithm = "Tree Traversal";
            maze.generateMazeDFS();
        }

        isMazeSolved = false;
        elapsedTime = System.currentTimeMillis() - startTime;
        steps = maze.getSteps();
        startTime = System.currentTimeMillis();
        traversalTime = 0;
        currentStep = 0;
        animationFinished = false;
    }

    private void startAnimation() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!maze.isSolved()) {
                    if (currentAlgorithm.equals("Tree Traversal")) {
                        if (!maze.generateMazeDFS()) {
                            maze.solveMazeRHR();
                            isMazeSolved = true;
                            elapsedTime = System.currentTimeMillis() - startTime;
                            steps = maze.getSolutionSteps();
                            startTime = System.currentTimeMillis();
                            traversalTime = 0;
                        }
                    } else {
                        if (maze.solveMazeDijkstra()) {
                            isMazeSolved = true;
                            elapsedTime = System.currentTimeMillis() - startTime;
                            steps = maze.getSolutionSteps();
                            startTime = System.currentTimeMillis();
                            traversalTime = 0;
                        }
                    }
                }

                // Update the current step at a controlled rate
                if (isMazeSolved && !animationFinished && now - lastUpdate >= UPDATE_INTERVAL * 1_000_000) {
                    traversalTime = System.currentTimeMillis() - startTime;
                    if (currentStep < steps.size() - 1) {
                        currentStep++;
                        maze.mainMemoryWrites++;
                    } else {
                        animationFinished = true;
                    }
                    lastUpdate = now;
                }

                // Redraw the maze at every frame
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.setFill(Color.web("#0e1111"));
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                drawMaze();

                // Draw metrics
                drawMetrics(gc);
            }
        };
        timer.start();
    }

    private void drawMetrics(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(14));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Maze Generation Algorithm: " + (currentAlgorithm.equals("Tree Traversal") ? "Iterative Backtracking (DFS)" : "Open Maze"), 15, 30);
        gc.fillText("Maze Solving Algorithm: " + currentAlgorithm, 15, 50);
        gc.fillText("Grid Size: " + maze.getRow() + "x" + maze.getCol(), 15, 70);
        gc.fillText("Visual Time: " + (isMazeSolved ? elapsedTime : System.currentTimeMillis() - startTime) + " ms", 15, 90);
        gc.fillText("Traversal Time: " + traversalTime + " ms", 15, 110);
        gc.fillText("Number of Writes to Main Memory: " + maze.getMainMemoryWrites(), 15, 130);
        gc.fillText("Number of Writes to Auxiliary Memory: " + maze.getAuxMemoryWrites(), 15, 150);
        gc.fillText("Time Complexity: O(" + (currentAlgorithm.equals("Tree Traversal") ? "V + E" : "V log V + E") + ")", 15, 170);
        gc.fillText("Space Complexity: O(V)", 15, 190);

        // Draw legend
        drawLegend(gc);
    }

    private void drawLegend(GraphicsContext gc) {
        gc.setFill(Color.web("#00FF00"));
        gc.fillRect(15, 220, 10, 10);
        gc.setFill(Color.WHITE);
        gc.fillText(": Start Point", 30, 230);

        gc.setFill(Color.RED);
        gc.fillRect(15, 240, 10, 10);
        gc.setFill(Color.WHITE);
        gc.fillText(": End Point", 30, 250);

        gc.setFill(Color.BLUE);
        gc.fillRect(15, 260, 10, 10);
        gc.setFill(Color.WHITE);
        gc.fillText(": " + (currentAlgorithm.equals("Tree Traversal") ? "Backtracking Path" : "Visited Cells"), 30, 270);

        gc.setFill(Color.PURPLE);
        gc.fillRect(15, 280, 10, 10);
        gc.setFill(Color.WHITE);
        gc.fillText(": Solution Path", 30, 290);
    }

    private void drawMaze() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int[][] grid = maze.getGrid();
        int numRows = grid.length;
        int numCols = grid[0].length;

        int cellSize = calculateCellSize(numRows, numCols);

        // Calculate offsets to position the maze
        double offsetX = 250; // Fixed offset for metrics
        double offsetY = 200; // Fixed offset for metrics and button

        // Draw walls in black and paths in white
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (grid[row][col] == Maze.getWall()) {
                    gc.setFill(Color.web("#0e1111"));
                    gc.fillRect(offsetX + col * cellSize, offsetY + row * cellSize, cellSize, cellSize);
                } else if (grid[row][col] == Maze.PATH) {
                    gc.setFill(Color.WHITE);
                    gc.fillRect(offsetX + col * cellSize, offsetY + row * cellSize, cellSize, cellSize);
                }
            }
        }

        // Draw start and end points
        int[] startCell = maze.getStartCell();
        int[] endCell = maze.getEndCell();
        gc.setFill(Color.web("#00FF00")); 
        gc.fillRect(offsetX + startCell[1] * cellSize, offsetY + startCell[0] * cellSize, cellSize, cellSize);
        gc.setFill(Color.RED);
        gc.fillRect(offsetX + endCell[1] * cellSize, offsetY + endCell[0] * cellSize, cellSize, cellSize);

        // Highlight the steps taken during solving
        if (!isMazeSolved) {
            Set<int[]> visitedCells = maze.getVisitedCells();
            for (int[] cell : visitedCells) {
                gc.setFill(Color.BLUE);
                gc.fillRect(offsetX + cell[1] * cellSize, offsetY + cell[0] * cellSize, cellSize, cellSize);
            }
        }

        // Draw the solution path step by step
        if (isMazeSolved) {
            for (int i = 0; i <= currentStep; i++) {
                if (i < steps.size()) {
                    int[] cell = steps.get(i);
                    gc.setFill(Color.PURPLE);
                    gc.fillRect(offsetX + cell[1] * cellSize, offsetY + cell[0] * cellSize, cellSize, cellSize);
                }
            }
        }
    }
}
