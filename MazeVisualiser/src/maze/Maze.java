package maze;

import java.util.*;

public class Maze {

    private int[][] grid;
    private int row;
    private int col;
    private static final int WALL = 1;
    public static final int PATH = 0;
    private List<int[]> steps;
    private List<int[]> solutionSteps;
    private boolean solved = false;
    private Deque<int[]> stack = new ArrayDeque<>();
    private Set<int[]> visitedCells = new HashSet<>();

    private int[] startCell;
    private int[] endCell;
    private int currentStep;

    private Node start;
    private Node end;
    private Node current;
    private boolean initialized = false;

    private PriorityQueue<Node> openSet;
    private Set<Node> closedSet;
    private Map<Node, Node> cameFrom;
    private Map<Node, Integer> gScore;

    // Metrics variables
    private String algorithmType;
    private long visualTime;
    public int mainMemoryWrites;
    private int auxMemoryWrites;

    public Maze(int row, int col) {
        this.row = row;
        this.col = col;
        grid = new int[row][col];
        steps = new ArrayList<>();

        // Initialize the grid with walls
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                grid[r][c] = getWall();
            }
        }

        startCell = new int[]{1, 1};
        endCell = new int[]{row - 1, col - 2};
        grid[startCell[0]][startCell[1]] = PATH;
        grid[endCell[0]][endCell[1]] = PATH;

        stack.push(startCell);

        algorithmType = "Depth-First Search";
        visualTime = 0;
        mainMemoryWrites = 0;
        auxMemoryWrites = 0;
        currentStep = 0;

        solutionSteps = new ArrayList<>();
    }

    // Randomized depth-first search implementation (also known as the “recursive backtracker” algorithm)
    public boolean generateMazeDFS() {
        if (stack.isEmpty()) {
            endCell = steps.get(steps.size() - 1); // Set end cell to last visited cell
            return false; // Maze generation is complete
        }

        long startTime = System.currentTimeMillis();

        int[] cell = stack.peek();
        int x = cell[0];
        int y = cell[1];

        // Define directions: right, down, left, up
        int[] dx = {1, 0, -1, 0};
        int[] dy = {0, 1, 0, -1};

        List<Integer> directions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            directions.add(i);
        }
        Collections.shuffle(directions); // Randomize the order of directions

        for (int i : directions) {
            int nx = x + dx[i] * 2; // Calculate the next cell coordinates
            int ny = y + dy[i] * 2;

            if (nx > 0 && ny > 0 && nx < row - 1 && ny < col - 1 && grid[nx][ny] == WALL) {
                grid[x + dx[i]][y + dy[i]] = PATH; // Remove the wall
                grid[nx][ny] = PATH; // Mark the chosen cell as a path
                steps.add(new int[]{nx, ny}); // Add step to the list
                stack.push(new int[]{nx, ny}); // Push the chosen cell onto the stack

                // Update metrics
                mainMemoryWrites += 2; // Two writes to main memory (grid[x + dx[i]][y + dy[i]] and grid[nx][ny])
                auxMemoryWrites++; // One push operation to auxiliary memory (stack.push)

                currentStep++; // Increment step count

                long endTime = System.currentTimeMillis(); // End time for visual time calculation
                visualTime = endTime - startTime; // Calculate visual time

                return true; // A step was generated
            }
        }

        // If no unvisited neighbors, backtrack by popping the current cell
        stack.pop();

        // Update metrics
        auxMemoryWrites++; // One pop operation from auxiliary memory (stack.pop)

        currentStep++; // Increment step count

        return true; // Maze generation is not yet complete
    }

    public void generateOpenMaze() {
        // Generate an open maze with some random obstacles
        // This is a simple implementation, you might want to create a more complex one
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                grid[i][j] = Math.random() < 0.3 ? WALL : PATH;
            }
        }
        grid[startCell[0]][startCell[1]] = PATH;
        grid[endCell[0]][endCell[1]] = PATH;
    }

    public void resetMaze() {
        // Reset the grid to all walls
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                grid[r][c] = WALL;
            }
        }

        // Reset start and end cells
        startCell = new int[]{1, 1};
        endCell = new int[]{row - 1, col - 2};
        grid[startCell[0]][startCell[1]] = PATH;
        grid[endCell[0]][endCell[1]] = PATH;

        // Clear all lists and data structures
        steps.clear();
        solutionSteps.clear();
        stack.clear();
        stack.push(startCell);

        if (visitedCells != null) visitedCells.clear();
        if (openSet != null) openSet.clear();
        if (closedSet != null) closedSet.clear();
        if (cameFrom != null) cameFrom.clear();
        if (gScore != null) gScore.clear();

        // Reset flags and metrics
        solved = false;
        algorithmType = "Depth-First Search";
        visualTime = 0;
        mainMemoryWrites = 0;
        auxMemoryWrites = 0;
        currentStep = 0;
    }

    // Maze-solving method using the right-hand rule
    public void solveMazeRHR() {
        int[] cell = startCell;
        int direction = 0; // 0: right, 1: down, 2: left, 3: up

        // Define directions: right, down, left, up
        int[] dx = {1, 0, -1, 0};
        int[] dy = {0, 1, 0, -1};

        while (!Arrays.equals(cell, endCell)) {
            // Try to turn right (relative to the current direction)
            int newDirection = (direction + 3) % 4;
            int[] newCell = {cell[0] + dx[newDirection], cell[1] + dy[newDirection]};

            if (isPath(newCell)) {
                // If the cell to the right is a path, turn right and move forward
                cell = newCell;
                direction = newDirection;
            } else {
                // Check the cell in the current direction
                newCell = new int[]{cell[0] + dx[direction], cell[1] + dy[direction]};
                if (isPath(newCell)) {
                    // If the cell straight ahead is a path, move forward
                    cell = newCell;
                } else {
                    // If the cells to the right and straight ahead are not paths, turn left
                    direction = (direction + 1) % 4;
                }
            }

            // Add the current cell to the solution path
            solutionSteps.add(new int[]{cell[0], cell[1]});

            // Check if the current cell is the end cell
            if (Arrays.equals(cell, endCell)) {
                solved = true;
                break;
            }
        }

        if (!solved) {
            System.out.println("Maze cannot be solved.");
        }
    }

    public boolean solveMazeDijkstra() {
        if (!initialized) {
            // Initialize the algorithm
            openSet = new PriorityQueue<>();
            closedSet = new HashSet<>();
            cameFrom = new HashMap<>();
            gScore = new HashMap<>();

            start = new Node(startCell[0], startCell[1]);
            end = new Node(endCell[0], endCell[1]);

            openSet.add(start);
            gScore.put(start, 0);

            initialized = true;
        }

        if (openSet.isEmpty()) {
            return true; // No solution found
        }

        current = openSet.poll();
        visitedCells.add(new int[]{current.x, current.y}); // Track visited cells

        if (current.equals(end)) {
            reconstructPath(current);
            solved = true;
            return true; 
            
        }
            
            closedSet.add(current);

            for (Node neighbour : getNeighbors(current)) {
                if (closedSet.contains(neighbour)) {
                    continue;
                }

                int tentativeGScore = gScore.get(current) + 1;

                if (!openSet.contains(neighbour)) {
                    openSet.add(neighbour);
                } else if (tentativeGScore >= gScore.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    continue;
                }

                cameFrom.put(neighbour, current);
                gScore.put(neighbour, tentativeGScore);
            }

            return false; // Still solving
        }
        
        

        public void resetDijkstra() {
            initialized = false;
            if (visitedCells != null) visitedCells.clear();
            if (openSet != null) openSet.clear();
            if (closedSet != null) closedSet.clear();
            if (cameFrom != null) cameFrom.clear();
            if (gScore != null) gScore.clear();
        }

        private List<Node> getNeighbors(Node node) {
            List<Node> neighbors = new ArrayList<>();
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

            for (int[] dir : directions) {
                int newX = node.x + dir[0];
                int newY = node.y + dir[1];

                if (newX >= 0 && newX < row && newY >= 0 && newY < col && grid[newX][newY] == PATH) {
                    neighbors.add(new Node(newX, newY));
                }
            }

            return neighbors;
        }

        private void reconstructPath(Node current) {
            solutionSteps.clear();
            while (current != null) {
                solutionSteps.add(0, new int[]{current.x, current.y});
                current = cameFrom.get(current);
            }
        }

        private class Node implements Comparable<Node> {
            int x, y;

            Node(int x, int y) {
                this.x = x;
                this.y = y;
            }

            @Override
            public int compareTo(Node other) {
                return Integer.compare(gScore.getOrDefault(this, Integer.MAX_VALUE),
                        gScore.getOrDefault(other, Integer.MAX_VALUE));
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Node node = (Node) o;
                return x == node.x && y == node.y;
            }

            @Override
            public int hashCode() {
                return Objects.hash(x, y);
            }
        }

        public Set<int[]> getVisitedCells() {
            return visitedCells;
        }

        private boolean isPath(int[] cell) {
            int x = cell[0];
            int y = cell[1];
            return x >= 0 && y >= 0 && x < row && y < col && grid[x][y] == PATH;
        }

        public List<int[]> getSolutionSteps() {
            return solutionSteps;
        }

        public boolean isSolved() {
            return solved;
        }

        public int[][] getGrid() {
            return grid;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public int[] getStartCell() {
            return startCell;
        }

        public int[] getEndCell() {
            return endCell;
        }

        public List<int[]> getSteps() {
            return steps;
        }

        public String getAlgorithmType() {
            return algorithmType;
        }

        public void setAlgorithmType(String algorithmType) {
            this.algorithmType = algorithmType;
        }

        public long getVisualTime() {
            return visualTime;
        }

        public int getMainMemoryWrites() {
            return mainMemoryWrites;
        }

        public int getAuxMemoryWrites() {
            return auxMemoryWrites;
        }

        public static int getWall() {
            return WALL;
        }

        public int getCurrentStep() {
            return currentStep;
        }

        public void resetMetrics() {
            visualTime = 0;
            mainMemoryWrites = 0;
            auxMemoryWrites = 0;
            currentStep = 0;
        }

        public void printMaze() {
            for (int r = 0; r < row; r++) {
                for (int c = 0; c < col; c++) {
                    if (r == startCell[0] && c == startCell[1]) {
                        System.out.print("S"); // Mark the start point
                    } else if (r == endCell[0] && c == endCell[1]) {
                        System.out.print("E"); // Mark the end point
                    } else {
                        System.out.print(grid[r][c] == getWall() ? "#" : " ");
                    }
                }
                System.out.println();
            }
        }
    }
    
    

            
            