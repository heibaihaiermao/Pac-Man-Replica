import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

// Ghost class: create different types of ghosts with different characteristics
public class Ghost {

    static final int BLINKY = 1;
    static final int PINKY = 2;
    static final int INKY = 3;
    static final int CLYDE = 4;
    boolean drawPath = false;
    int speed = 1;

    int currentDirectX = 1;
    int currentDirectY = 0;

    int ghostHouseTimer;
    int ghostHouseLimit;
    boolean inGhostHouse;

    boolean scatter; //true if the ghost is in scatter mode false if in true mode
    boolean frightened; //true if the ghost is in frightened mode
    int totalFlash = 36;
    int flashCount = 0; //in order to make the ghost flash when frightened this is a counter
    int flashIndex = 0;
    int flashRemain = totalFlash;

    boolean returnHome; //if eaten return home
    boolean eaten;
    int deadCount = 0;
    boolean versusAI;

    int ghostType;

    Image imageU;
    Image imageD;
    Image imageL;
    Image imageR;
    Image deadImage;
    Image currentImage;
    ArrayList<Image> flashImageList;
    int width = 28, height = 28;
    Color color;
    Point center;
    Point pos;
    Point atTile;
    Point ghostHouseSpawnPos;
    Point targetTile;
    Point previousTile;
    Point[] intersects = {new Point(1, 5), new Point(3, 26), new Point(6, 1), new Point(6, 5), new Point(6, 8), new Point(6, 14), new Point(6, 20), new Point(6, 23), new Point(9,17), new Point(9, 5), new Point(9, 14), new Point(9, 23), new Point(9, 20), new Point(12, 5), new Point(12, 11), new Point(12, 23), new Point(12, 29), new Point(15, 5), new Point(15, 11), new Point(15, 23), new Point(15, 29), new Point(18, 5), new Point(18,14), new Point(18,17), new Point(18,20), new Point(18, 23), new Point(21,1), new Point(21, 5), new Point(21, 8), new Point(21, 14), new Point(21, 20), new Point(21, 23), new Point(24, 26), new Point(26, 5)};
    ArrayList<Point> path = new ArrayList<>();

    Tile[][] tiles;
    Map<Point, ArrayList<Point>> mapNeighbors;
    ArrayList<Point> road;
    Pacman pacman;
    AStar aStar;

    Random random = new Random();

    public Ghost(Pacman pacma, Tile[][] tile, Map<Point, ArrayList<Point>> mapNeighbor, ArrayList<Point> roadd, int ghostTyp, boolean AImode){
        pacman = pacma;
        tiles = tile;
        mapNeighbors = mapNeighbor;
        road = roadd;
        aStar = new AStar(mapNeighbors);
        ghostType = ghostTyp;
        versusAI = AImode;
        importImages();
        setColor();
        if(!AImode) {
            setInitialDirect(); // do not set initial direction in AI mode
        }
        getSpawnCenterPos();
        setSpawnPos();
        setGhostHouseLimit();
    }

    // reset: mode, timers, position, direction (in classic mode)
    public void reset(){
        resetFrightenVariables();
        scatter = versusAI ? false:true;    // scatter in classic mode but not in AI mode
        inGhostHouse = versusAI ? false:true;
        returnHome = false;
        eaten = false;
        ghostHouseTimer = 0;
        if(!versusAI) {
            setInitialDirect(); // do not set initial direction in AI mode
        }
        setSpawnPos();
    }

    // set time for each ghost before leaving ghost house
    private void setGhostHouseLimit(){
        int[] limit = {0,0,0,300,600};
        ghostHouseLimit = limit[ghostType];
    }

    // the default position when ghosts are in the ghost house
    private void getSpawnCenterPos(){
        switch (ghostType) {
            case BLINKY:
                ghostHouseSpawnPos = new Point(14, 13);
                break;
            case PINKY:
                ghostHouseSpawnPos = new Point(14, 13);
                break;
            case INKY:
                ghostHouseSpawnPos = new Point(12, 14);
                break;
            case CLYDE:
                ghostHouseSpawnPos = new Point(16, 14);
                break;
        }
    }

    // randomly spawn ghosts in AI mode, spawn ghosts in ghost house in classic mode
    private void setSpawnPos(){
        if(versusAI){   // generate random ghost spawn position for VS AI mode
            road.remove(pacman.atTile);
            atTile = road.get(random.nextInt(299));
            road.add(pacman.atTile);
        }

        else {
            atTile = new Point(ghostHouseSpawnPos);
            if(ghostType==1){   // blinky will be spawned outside the ghost house
                currentDirectY = 0;
                currentDirectX = 1;
                atTile = new Point(14,11);
                inGhostHouse = false;
            }
        }
        center = new Point(atTile.x*16, (atTile.y+3)*16+8);
        pos = new Point(center.x-width/2, center.y-height/2);
    }

    // initial direction in ghost house
    private void setInitialDirect(){
        int[] y = {0,-1,1,-1,-1};
        currentDirectX = 0;
        currentDirectY = y[ghostType];
    }

    // set path color for ghosts
    private void setColor(){
        switch (ghostType) {
            case BLINKY:
                color = Color.RED;
                break;
            case PINKY:
                color = Color.PINK;
                break;
            case INKY:
                color = Color.CYAN;
                break;
            case CLYDE:
                color = Color.ORANGE;
                break;
        }
    }

    // set images
    private void importImages(){
        String[] ghostNames = {"","blinky","pinky","inky","clyde"};
        imageU = new ImageIcon(ghostNames[ghostType]+"U.png").getImage();
        imageD = new ImageIcon(ghostNames[ghostType]+"D.png").getImage();
        imageL = new ImageIcon(ghostNames[ghostType]+"L.png").getImage();
        imageR = new ImageIcon(ghostNames[ghostType]+"R.png").getImage();
        deadImage = new ImageIcon("deadGhost.png").getImage();
        flashImageList = importAni("frightenedGhost");
    }

    // import animation images from given folder, return an ArrayList that has all images imported
    public ArrayList<Image> importAni(String folderName) {
        ArrayList<Image> imagesArray = new ArrayList<>(2);
        File directoryPath = new File("./"+folderName);
        String contents[] = directoryPath.list();
        for(String path: contents) {
            imagesArray.add(new ImageIcon(folderName+"\\"+path).getImage());
        }
        return imagesArray;
    }

    // pinky target tile: 4 tiles in front of pacman
    private Point pinkyTargetTile(){
        Point targetTile = pacman.atTile;
        for(int i=4; i>0;i--){
            int tileX = pacman.atTile.x+pacman.currentDirectX*i;
            int tileY = pacman.atTile.y+pacman.currentDirectY*i;
            if(inGrid(tileX,tileY) && !tiles[tileY][tileX].wall && !tiles[tileY][tileX].ghostHouse){
                targetTile = new Point(tileX,tileY);
                break;
            }
        }
        return targetTile;
    }

    // inky target tile: vector from Blinky's position to the tile 2 tile infront of pacman,
    // and then doubling the length of the vector
    private Point inkyTargetTile(){
        int tileX;
        int tileY;
        // find 2 tile in front pacman
        Point vectorPoint = pacman.atTile;
        for(int i=2; i>0;i--){
            tileX = pacman.atTile.x+pacman.currentDirectX*i;
            tileY = pacman.atTile.y+pacman.currentDirectY*i;
            if(inGrid(tileX,tileY) && !tiles[tileY][tileX].wall && !tiles[tileY][tileX].ghostHouse){
                vectorPoint = new Point(tileX,tileY);
                break;
            }
        }

        // connect blink and the tile in front pacman
        Point blinkyPos = pacman.getBlinkyPos();
        int x = vectorPoint.x - blinkyPos.x;
        int y = vectorPoint.y - blinkyPos.y;
        int directX = x == Math.abs(x)? 1: -1;
        int directY = y == Math.abs(y)? 1: -1;

        // double length of vector to find final target tile
        tileX = vectorPoint.x + x;
        tileY = vectorPoint.y + y;// y = y/x * x + vectorPoint.y
        double h = Math.sqrt(x*x + y*y);
        double theta = x==0? Math.PI/2 : Math.atan(y/x);
        while (h>0){
            if(inGrid(tileX,tileY) && !tiles[tileY][tileX].wall && !tiles[tileY][tileX].ghostHouse){
                return new Point(tileX,tileY);
            }
            h--;
            tileX = vectorPoint.x + directX*(int)(h*Math.abs(Math.cos(theta)));
            tileY = vectorPoint.y + directY*(int)(h*Math.abs(Math.sin(theta)));
        }
        return pacman.atTile;
    }

    // clyde target tile: chase pacman when pacman is more than 8 tiles away,
    //                    return to the target tile at Scatter Mode
    private Point clydeTargetTile(){
        if(path.size()>8){
            return pacman.atTile;
        }
        else {
            return new Point(1,29);
        }
    }

    // return whether the ghost is in the map
    private boolean inGrid(int x, int y){
        return x>-1&&x<28&&y>-1&&y<31;
    }

    public void switchMode(){
        scatter = !scatter;
    }

    public void frightened(){
        resetFrightenVariables();   // reset timer + index
        frightened = true;
        currentDirectY*=-1; // reverse direction when switched to frightened mode
        currentDirectX*=-1;
    }

    // return whether the ghost is at an intersection
    private boolean atIntersect(){
        for(Point p: intersects){
            if(atTile.equals(p)){
                return true;
            }
        }
        return false;
    }

    // control the direction the ghost will move next
    private void checkDirect() {
        // check if it's at the end of right tunnel
        if(atTile.x>26){
            if(pos.x>448){
                center.x = -1-width/2;
            }
        }
        // check if it's at the end of left tunnel
        else if (atTile.x<1) {
            if(pos.x+width<0){
                center.x = 449+width/2;
            }
        }
        // if at a critical point
        else if ((center.x-8) %16 == 0 && (center.y-8) % 16 == 0) {
            // if not at intersection
            if (!atIntersect()){
                // if hit a wall
                if(tiles[atTile.y+currentDirectY][atTile.x+currentDirectX].wall) {
                    // attempt to turn down/right
                    int nextX = currentDirectX == 0 ? 1 : 0;
                    int nextY = currentDirectY == 0 ? 1 : 0;
                    if (!tiles[atTile.y + nextY][atTile.x + nextX].wall) {
                        currentDirectX = nextX;
                        currentDirectY = nextY;
                    }
                    // turn up/left
                    else {
                        currentDirectX = nextX == 1 ? -1 : 0;
                        currentDirectY = nextY == 1 ? -1 : 0;

                    }
                }
            }
            // if at intersection
            else{
                if(path.size()>1){
                    currentDirectY = path.get(path.size()-2).y - atTile.y;
                    currentDirectX = path.get(path.size()-2).x - atTile.x;
                }
                else {
                    currentDirectY = targetTile.y - atTile.y;
                    currentDirectX = targetTile.x - atTile.x;
                }

            }
        }

        // adjust position when it's eaten in tunnel
        if(eaten){
            center.x = center.x % 8 == 0 ? center.x : center.x/8*8; // adjust position
        }
    }

    private void updatePosition(){
        center.x += speed*currentDirectX;
        center.y += speed*currentDirectY;
        pos.x = center.x-width/2;
        pos.y = center.y-height/2;
        atTile.x = center.x/16;
        atTile.y = center.y/16-3;
        tunnelAtTile();
    }

    // special position when travels in tunnel
    private void tunnelAtTile(){
        if (atTile.x<0){
            atTile.x = 0;
        } else if (atTile.x>27) {
            atTile.x = 27;
        }
    }

    // update target tiles
    private void updateTargetTile(){
        previousTile = new Point(atTile.x-currentDirectX, atTile.y-currentDirectY);

        if(!eaten) {
            // Frightened Mode: random decision
            if (frightened) {
                ArrayList<Point> neighbors = new ArrayList<>(mapNeighbors.get(atTile));
                neighbors.remove(previousTile);
                targetTile = neighbors.get(random.nextInt(0, neighbors.size()));
            }
            // Scatter Mode: set each ghosts default target tile
            else if (scatter) {
                switch (ghostType) {
                    case BLINKY:
                        targetTile = new Point(24, 1);
                        break;
                    case PINKY:
                        targetTile = new Point(3, 1);
                        break;
                    case INKY:
                        targetTile = new Point(20, 29);
                        break;
                    case CLYDE:
                        targetTile = new Point(1, 29);
                        break;
                }
            }
            // Chase Mode
            else {
                switch (ghostType) {
                    case BLINKY:
                        targetTile = pacman.atTile;
                        break;
                    case PINKY:
                        targetTile = pinkyTargetTile();
                        break;
                    case INKY:
                        targetTile = inkyTargetTile();
                        break;
                    case CLYDE:
                        targetTile = clydeTargetTile();
                        break;
                }
            }
        }
    }

    // check if the ghost can leave the ghost house
    private void checkLeaveHouse(){
        if(eaten){
            deadCount++;    // ghost dead timer
            if(deadCount==300){
                deadCount = 0;
                eaten = false;
                moveToLeaveHouse();
            }
        }
        else if(ghostHouseTimer==ghostHouseLimit){
            moveToLeaveHouse();
        }
        else {
            ghostHouseTimer++;
        }
    }

    // control movement to leave the ghost house
    private void moveToLeaveHouse(){
        // move x position
        if (center.x == 224) {
            currentDirectX =0;
            // move y position
            if (center.y == 232) {
                inGhostHouse = false;   // officially out of the ghost house
                updateTargetTile();
                path = aStar.AStarSearchPath(atTile, targetTile, previousTile);
                currentDirectY = 0;
                currentDirectX = -1;
            }
            else{
                currentDirectY = -1;
            }
        }
        else {
            currentDirectY = 0;
            currentDirectX = (224 - center.x)/Math.abs((224 - center.x));
        }
    }

    // control the wandering behaviour within the ghost house
    private void moveInHouse(){
        speed = 1;
        if (center.y % 16 == 0) {
            if (tiles[atTile.y + currentDirectY][atTile.x + currentDirectX].wall) {
                currentDirectY *= -1;
            }
        }
    }

    // reset frighten status and timers to not frightened
    private void resetFrightenVariables(){
        frightened = false;
        flashRemain = totalFlash;
        flashCount = 0;
        flashIndex = 0;
    }

    // set ghost eaten, stop frightened status, change speed, adjust position
    public void eaten(){
        eaten = true;
        resetFrightenVariables();
        targetTile = new Point(14,11);
        center.x = center.x % 8 == 0 ? center.x : center.x/8*8; // adjust position
        center.y = center.y % 8 == 0 ? center.y : center.y/8*8;
        speed = 8;
    }

    // check whether the ghost at the position to enter the ghost house
    private void returningHome(){
        if(center.x==224 && center.y==232){
            returnHome = true;
        }
    }

    // control behaviours to enter the ghost house
    private void moveEnterHouse(){
        speed = 1;
        // check y position
        if (center.y == (ghostHouseSpawnPos.y+3)*16+8) {
            // check x position
            if (center.x == ghostHouseSpawnPos.x*16) {
                inGhostHouse = true;
                setInitialDirect();
                returnHome = false;
            }
            else{
                currentDirectY = 0;
                currentDirectX = (ghostHouseSpawnPos.x*16 - center.x)/Math.abs((ghostHouseSpawnPos.x*16 - center.x));
            }
        }
        else {
            currentDirectX = 0;
            currentDirectY = 1;
        }
    }

    public void move() {
        // wander in ghost house
        if(inGhostHouse){
            moveInHouse();
            checkLeaveHouse();
        }
        // move to enter the ghost house
        else if(returnHome){
            moveEnterHouse();
        }
        // outside of ghost house
        else{
            updateTargetTile();
            path = aStar.AStarSearchPath(atTile, targetTile, previousTile);
            checkDirect(); // check if it needs to change direction for the next move
        }
        updatePosition();
    }

    public void update(){
        // on the way to return ghost house
        if(eaten && !returnHome && !inGhostHouse){
            returningHome();
        }
        move();
    }

    // frighten mode animation
    private void flashAnimation(){
        flashCount++;
        if(flashCount>5){
            flashCount = 0;
            flashRemain--;
            if(flashRemain==0){
                flashRemain=totalFlash;
                frightened = false;
            }
            else if (flashRemain<=totalFlash/2) {
                flashIndex++;
                if(flashIndex>=flashImageList.size()){
                    flashIndex=0;
                }
            }

        }
        currentImage = flashImageList.get(flashIndex);
    }

    // change image based on moving direction
    private void currentImageDirect(){
        if(eaten){
            currentImage = deadImage;
        } else if(currentDirectX==1){
            currentImage = imageR;
        } else if(currentDirectX==-1){
            currentImage = imageL;
        } else if(currentDirectY==1){
            currentImage = imageD;
        } else{
            currentImage = imageU;
        }
    }

    public void draw(Graphics g){
        if(frightened && !eaten){
            flashAnimation();
        }
        else {
            currentImageDirect();
        }

        g.drawImage(currentImage, pos.x, pos.y, null);

        if(drawPath){
            drawPath(g);
        }
    }

    // display the target tile, the path the ghost is taking
    private void drawPath(Graphics g){
        if(!path.isEmpty()){
            g.setColor(color);
            for (int i = 0; i<path.size()-1; i++){
                int px1 = path.get(i).x, py1 = path.get(i).y;
                int px2 = path.get(i+1).x, py2 = path.get(i+1).y;
                g.drawLine(tiles[py1][px1].pos.x,tiles[py1][px1].pos.y,tiles[py2][px2].pos.x,tiles[py2][px2].pos.y);
            }
            g.fillRect(targetTile.x*16, (targetTile.y+3)*16, 16,16);
        }
        g.fillRect(atTile.x*16, (atTile.y+3)*16,16,16);
    }

}

