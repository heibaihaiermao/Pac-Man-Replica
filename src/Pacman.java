import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;

// Pacman class: create player Pacman or AI Pacman
public class Pacman {
        int size = 30;
        int speed = 4;
        Point center, pos, atTile;

        int currentDirectX, currentDirectY, nextDirectX, nextDirectY;
        boolean movable;

        int score = 0;
        int dotsRemain;
        boolean AI_mode;
        ArrayList<Point> path;

        Tile[][] tiles;
        Map<Point, ArrayList<Point>> mapNeighbors;
        ArrayList<Ghost> ghosts;
        MiniMax miniMax;

    public Pacman(Tile[][] tile, Map<Point, ArrayList<Point>> mapNeighbor, ArrayList<Ghost> ghost, boolean AImode) {
        tiles = tile;
        mapNeighbors = mapNeighbor;
        ghosts = ghost;
        AI_mode = AImode;
        miniMax = new MiniMax(this,ghosts);
        levelUpReset();
    }

    // reset pacman position, direction
    public void resetPosDirect(){
        center = new Point(224,424);
        pos = new Point(center.x-size/2,center.y-size/2);
        atTile = new Point(center.x/16,center.y/16-3);
        currentDirectX = -1;
        currentDirectY = 0;
        nextDirectX = -1;
        nextDirectY = 0;
        movable = true;
    }

    // reset dots count + position and direction
    public void levelUpReset(){
        resetDotsRemain();
        resetPosDirect();
    }

    // update the tile map
    public void updateTiles(Tile[][] tile){
        tiles = tile;
    }
    private void resetDotsRemain(){
        dotsRemain = 246;
    }

    public Point getBlinkyPos(){
        return ghosts.get(0).atTile;
    }

    // get keyboard input for next turning direction
    public void getKeys(boolean[] keys){
        if(keys[KeyEvent.VK_UP]){
            nextDirectY = -1;
            nextDirectX = 0;
        }
        else if(keys[KeyEvent.VK_DOWN]){
            nextDirectY = 1;
            nextDirectX = 0;
        }
        else if(keys[KeyEvent.VK_LEFT]){
            nextDirectX = -1;
            nextDirectY = 0;
        }
        else if(keys[KeyEvent.VK_RIGHT]){
            nextDirectX = 1;
            nextDirectY = 0;
        }
    }

    // move the pacman
    public void move(){
        center.x += speed*currentDirectX;
        center.y += speed*currentDirectY;
        pos.x = center.x-size/2;
        pos.y = center.y-size/2;
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

    // return false if it can not move forward
    public boolean checkPosition() {
        // check if it's at the end of right tunnel
        if(atTile.x>26){
            if(pos.x>448){
                center.x = -1-size/2;
            }
            return true;
        }
        // check if it's at the end of left tunnel
        else if (atTile.x<1) {
            if(pos.x+size<0){
                center.x = 449+size/2;
            }
            return true;
        }
        // check if it can turn to the keyboard input direction
        else if (!tiles[atTile.y+nextDirectY][atTile.x+nextDirectX].wall) {
            currentDirectX = nextDirectX;
            currentDirectY = nextDirectY;
            return true;
        }
        // check if it can move forward
        else if (!tiles[atTile.y+currentDirectY][atTile.x+currentDirectX].wall){
            return true;
        }
        return false;

    }

    // update direction when AI controls the pacman
    private void AIUpdateDirect(){
        if(AI_mode) {
            Point AI_Direct = miniMax.findBestMove(1);
            nextDirectX = AI_Direct.x;
            nextDirectY = AI_Direct.y;
        }
    }

    // eat dots or energizer
    private void eatDots(){
        if (!tiles[atTile.y][atTile.x].eaten) {
            tiles[atTile.y][atTile.x].eaten = true;
            if (tiles[atTile.y][atTile.x].dot) {    // eat a small dot
                score += 10;
            }
            else if (tiles[atTile.y][atTile.x].bigDot) {    // eat a energy dot.
                score+= 50;
                frightenGhost();
            }
            dotsRemain--;
        }
    }

    // set ghosts in Frighten Mode
    private void frightenGhost(){
        for(Ghost ghost: ghosts){
            ghost.frightened();
        }
    }

    // return whether the pacman ate all the dots in the level
    public boolean checkWin(){
        return dotsRemain==0;
    }

    public int returnScore(){
        return score;
    }

    public void eatGhost(){
        score+=200;
    }

    public void update(boolean[] keys){
        // get keyboard input in classic mode
        if(!AI_mode){
            getKeys(keys);
        }

        // if on a critical position
        if ((center.x-8)%16 == 0 && (center.y-8) % 16 == 0) {
            eatDots();  // check if the position has been eaten or not
            if(AI_mode) {
                AIUpdateDirect();
            }
            movable = checkPosition();  // check if it can move forward
        }

        if(movable){
            move();
        }
    }

    public void draw(Graphics g){
        g.setColor(Color.YELLOW);
        g.fillOval(pos.x,pos.y,size,size);
    }
}
