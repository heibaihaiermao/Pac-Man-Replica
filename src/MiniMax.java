import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MiniMax {
    Pacman pacman;
    ArrayList<Ghost> ghosts;
    AStar aStar;
    ArrayList<Point> path = new ArrayList<>();
    Tile[][] tiles;
    Map<Point, ArrayList<Point>> mapNeighbors;

    public MiniMax(Pacman pacmann, ArrayList<Ghost> ghost){
        pacman = pacmann;
        ghosts = ghost;
        mapNeighbors = pacmann.mapNeighbors;
        aStar = new AStar(mapNeighbors);
        tiles = pacmann.tiles;
    }

    // return a Point that contains the xy direction for next move
    public Point findBestMove(int depthLimit){
        ArrayList<Point> miniMaxPath = new ArrayList<>(depthLimit);
        Move bestMove = miniMax(0,depthLimit,pacman.atTile,miniMaxPath);
        return new Point(bestMove.getNextDirect().x-pacman.atTile.x,bestMove.getNextDirect().y-pacman.atTile.y);
    }

    // return the Move with the highest heuristic evaluation
    // (fake miniMax: doesn't have minimizer)
    private Move miniMax(int depth, int depthLimit,Point atTile,ArrayList<Point> miniMaxPath){
        if(depth==depthLimit){
            return evaluate(miniMaxPath);   // return the Move with path and evaluation score
        }
        else {
            ArrayList<Point> neighbors = new ArrayList<>(mapNeighbors.get(atTile)); // get the neighbors of current tile
            Move bestMove = new Move(null, -99999);
            for (Point atNeighbor : neighbors) {
                ArrayList<Point> newPath = new ArrayList<>(miniMaxPath);
                newPath.add(atNeighbor);    // add this step to the path
                Move move = miniMax(depth+1,depthLimit,atNeighbor,newPath);

                bestMove = move.value > bestMove.value? move:bestMove;  // return the best Move out of neighbors
            }
            return bestMove;    // return the Move with the highest heuristic evaluation
        }

    }

    // (just for experiment) return the shortest path to find a dot and assume ghosts staying still
    public ArrayList<Point> findNextSafeDot(){
        ArrayList<Point> ghostsPos = new ArrayList<>(4);
        for(Ghost ghost: ghosts){
            ghostsPos.add(ghost.atTile);
        }
        return safePathToClosestDot(pacman.atTile, ghostsPos);
    }

    private ArrayList<Point> safePathToClosestDot(Point atTile, ArrayList<Point> ghostsPos){
        HashMap<Point,Point> cameFrom = new HashMap<>();     // track the final path
        ArrayList<Point> visitedTile = new ArrayList<>();   // track the visited tiles
        PriorityQueue queue = new PriorityQueue();  // track the steps taken to reach a tile
        queue.add(atTile,0);
        LNode atTileNode;

        while (!queue.empty()) {
            atTileNode = queue.dequeueNode();   // remove and use this tile as current location
            atTile = atTileNode.getPos();
            visitedTile.add(atTile);

            if(!tiles[atTile.y][atTile.x].eaten){   // check if it found a dot
                ArrayList<Point> path = new ArrayList<>();
                path.add(atTile);
                Point temp = cameFrom.get(atTile);
                System.out.println(cameFrom);
                while (temp!=null){
                    path.add(temp);
                    temp = cameFrom.get(temp);
                }
                return path;
            }

            for (Point neighbor : mapNeighbors.get(atTile)) {   // add the unvisited neighbors if no ghost at that position
                if (!visitedTile.contains(neighbor) && !ghostsPos.contains(neighbor)) {
                    // add the neighbor tile to visiting queue with the steps required to reach this tile
                    queue.add(neighbor,atTileNode.getVal()+1);
                    cameFrom.put(neighbor,atTile);
                }
            }
        }
        return new ArrayList<>();    // no dots can be found
    }

    // heuristic evaluation to find the value of a Move
    public Move evaluate(ArrayList<Point> miniMaxPath){
        double value = 0;
        int tileTraveled = 0;
        ArrayList<Point> dotsEaten = new ArrayList<>(3);

        for(Point atTile: miniMaxPath) {
            value -= 10;
            // check distance to each ghost
            for(Ghost ghost: ghosts){
                path = aStar.AStarSearchPath(miniMaxPath.get(tileTraveled),ghost.atTile,ghost.previousTile);
                if(path.size()<=1){
                    if(ghost.frightened){
                        return new Move(miniMaxPath,500);   // eat ghost
                    }
                    else {
                        return new Move(miniMaxPath, -500); // avoid certain death
                    }
                }
                value = ghost.frightened? value - path.size(): value + path.size();
            }

            // count dots eaten
            if (!dotsEaten.contains(atTile) &&!tiles[atTile.y][atTile.x].eaten) {
                if(tiles[atTile.y][atTile.x].bigDot){
                    value += 50;
                }
                value += 20;
                dotsEaten.add(atTile);
            }
            tileTraveled++;
        }

        // if no dots in planned path, find distance to the nearest dot
        if(dotsEaten.isEmpty()){
            value -= 20*findNearestDot(miniMaxPath.get(0));
        }

        return new Move(miniMaxPath,value);
    }

    // return the number of steps to get the nearest dot using BFS
    private int findNearestDot(Point atTile){
        ArrayList<Point> visitedTile = new ArrayList<>();   // track the visited tiles
        PriorityQueue queue = new PriorityQueue();  // track the steps taken to reach a tile
        queue.add(atTile,0);
        LNode atTileNode;

        while (!queue.empty()) {
            atTileNode = queue.dequeueNode();   // remove and use this tile as current location
            atTile = atTileNode.getPos();
            visitedTile.add(atTile);

            if(!tiles[atTile.y][atTile.x].eaten){   // check if it found a dot
                return atTileNode.getVal();
            }

            for (Point neighbor : mapNeighbors.get(atTile)) {   // add the unvisited neighbors
                if (!visitedTile.contains(neighbor)) {
                    // add the neighbor tile to visiting queue with the steps required to reach this tile
                    queue.add(neighbor,atTileNode.getVal()+1);
                }
            }
        }

        return 9999;    // no dots can be found
    }
}

// Move: store the path and the value
class Move{
    ArrayList<Point> targetTilePath;
    double value;
    public Move(ArrayList<Point> targetTilePat,double val){
        targetTilePath = targetTilePat;
        value = val;
    }

    public Point getNextDirect(){
        return targetTilePath.get(0);
    }
}

