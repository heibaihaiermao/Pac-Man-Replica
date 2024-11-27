import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

// AStar: to find the shortest path for ghosts to reach their target tile
public class AStar {
    PriorityQueue toVisit;
    HashMap<Point,Point> cameFrom;
    Hashtable<Point,Integer> costSoFar;
    Map<Point, ArrayList<Point>> map;

    public AStar(Map<Point, ArrayList<Point>> mapp){
        map = mapp;
    }

    private int heuristic(Point currentP, Point target){
        return Math.abs(target.x- currentP.x) + Math.abs(target.y- currentP.y);
    }

    // return the shortest path of Point ArrayList to reach target tile
    public ArrayList<Point> AStarSearchPath(Point start, Point target, Point previous) {
        // don't consider turning back - remove backward neighbor
        ArrayList<Point> oriNeighbor = new ArrayList<>(map.get(start));
        ArrayList<Point> newNeighbor = map.get(start);
        newNeighbor.remove(previous);
        map.put(start, newNeighbor);

        toVisit = new PriorityQueue();  // order the available next steps based on their cost
        cameFrom = new HashMap<>();     // track the final path
        costSoFar = new Hashtable<>();  // track the costs at each tile
        toVisit.add(start,0);
        cameFrom.put(start,null);
        costSoFar.put(start,0);

        while (!toVisit.empty()){
            Point currentPos = toVisit.dequeue();   // always use the next available step with the lowest cost
            if(currentPos==target){ // check if it reached the target tile
                break;
            }
            for (Point next: map.get(currentPos)){
                int newCost = 1 + heuristic(next,target);   // calculate new cost
                // only add this step when the tile hasn't been visited,
                // or when it has a lower cost than previous attempt to reach here
                if(!costSoFar.containsKey(next)|| newCost < costSoFar.getOrDefault(next,10000)){
                    costSoFar.put(next,newCost);
                    toVisit.add(next,newCost);
                    cameFrom.put(next,currentPos);
                }
            }
        }

        map.put(start,oriNeighbor);

        return generatePath(target);
    }

    // track down the cameFrom to generate the path for ghosts
    private ArrayList<Point> generatePath(Point target){
        ArrayList<Point> path = new ArrayList<>();
        path.add(target);
        Point temp = cameFrom.get(target);
        while (temp!=null){
            path.add(temp);
            temp = cameFrom.get(temp);
        }
        return path;
    }

}

