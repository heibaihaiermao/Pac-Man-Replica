import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Level {
    int levelNumber = 1;
    int score;
    int lives = 3;

    Pacman pacman;

    Ghost blinky;
    Ghost pinky ;
    Ghost inky ;
    Ghost clyde ;
    ArrayList<Ghost> ghosts = new ArrayList<>(4);

    boolean gameOver = false;
    boolean pacmanAImode;
    boolean ghostsAImode;

    int timer;
    int modeIndex;
    int[] thisLevelModeTimer;
    // modeTimer: ghosts will switch between Chase Mode and Scatter Mode according to the timer
    int[][] modeTimer = {{7*60,20*60,7*60,20*60,5*60,20*60,5*60,-1},{7*60,20*60,7*60,20*60,5*60,1033*60,1,-1},{5*60,20*60,5*60,20*60,5*60,1037*60,1,-1}};

    Tile[][] tiles = new Tile[31][28];  // create all tiles
    Tile[][] tilesTemplate;

    // neighbors of all viable tiles with their tile index
    Map<Point, ArrayList<Point>> mapNeighbors = new HashMap<>();
    ArrayList<Point> road = new ArrayList<>(320);   // tile index of all viable tiles

    int[][] tilesRepresentation = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, 8, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 8, 1},
            {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 6, 1, 1, 6, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 6, 1, 1, 6, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 6, 1, 1, 1, 1, 1, 1, 1, 1, 6, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 6, 1, 7, 7, 7, 7, 7, 7, 1, 6, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 1, 7, 7, 7, 7, 7, 7, 1, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 6, 1, 7, 7, 7, 7, 7, 7, 1, 6, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 6, 1, 1, 1, 1, 1, 1, 1, 1, 6, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 6, 1, 1, 1, 1, 1, 1, 1, 1, 6, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 6, 1, 1, 1, 1, 1, 1, 1, 1, 6, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, 8, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 8, 1},
            {1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1},
            {1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

    Image levelBg = new ImageIcon("levelBg.png").getImage();
    Image liveIcon = new ImageIcon("liveIcon.png").getImage();

    // create the game level in AI mode or classic mode
    public Level(boolean pacmanAImode, boolean ghostsAImode){
        getMazeData();

        // spawn game characters
        pacman = new Pacman(tiles,mapNeighbors,ghosts,pacmanAImode);
        blinky = new Ghost(pacman,tiles,mapNeighbors,road,1, ghostsAImode);
        pinky = new Ghost(pacman,tiles,mapNeighbors,road,2, ghostsAImode);
        inky = new Ghost(pacman,tiles,mapNeighbors,road,3, ghostsAImode);
        clyde = new Ghost(pacman,tiles,mapNeighbors,road,4, ghostsAImode);
        ghosts.add(blinky);
        ghosts.add(pinky);
        ghosts.add(inky);
        ghosts.add(clyde);

        setLevel(levelNumber);
        this.ghostsAImode = ghostsAImode;
        this.pacmanAImode = pacmanAImode;
    }

    // generate tile map
    private void getMazeData(){
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 31; j++) {
                tiles[j][i] = new Tile(16 * i + 8, 16 * (j+3) + 8);
                switch (tilesRepresentation[j][i]) {
                    case 1: //1 is a wall
                        tiles[j][i].wall = true;
                        break;
                    case 0: // 0 is a dot
                        tiles[j][i].dot = true;
                        getNeighbors(i,j,mapNeighbors);
                        road.add(new Point(i,j));
                        break;
                    case 8: // 8 is a big dot
                        tiles[j][i].bigDot = true;
                        getNeighbors(i,j,mapNeighbors);
                        road.add(new Point(i,j));
                        break;
                    case 7: // 7 is ghost house
                        tiles[j][i].eaten = true;
                        tiles[j][i].ghostHouse = true;
                        getNeighbors(i,j,mapNeighbors);
                        break;
                    case 6://6 is a blank space
                        tiles[j][i].eaten = true;
                        getNeighbors(i,j,mapNeighbors);
                        road.add(new Point(i,j));
                        break;
                }
            }
        }
        tilesTemplate = clone(tiles);   // get the default tile map to generate future levels
    }

    // return the deep copy of tile map
    private Tile[][] clone(Tile[][] tiles){
        Tile[][] tilesTemplate = new Tile[31][28];
        for(int y = 0; y<31; y++){
            for(int x = 0; x<28; x++){
                tilesTemplate[y][x] = new Tile(16 * x + 8, 16 * (y+3) + 8);
                tilesTemplate[y][x].wall = tiles[y][x].wall;
                tilesTemplate[y][x].dot = tiles[y][x].dot;
                tilesTemplate[y][x].bigDot = tiles[y][x].bigDot;
                tilesTemplate[y][x].eaten = tiles[y][x].eaten;
                tilesTemplate[y][x].ghostHouse = tiles[y][x].ghostHouse;
            }
        }
        return tilesTemplate;
    }

    // set the ghost behaviour mode timer for current level.
    private int[] getModeTimer(int levelNumber){
        if(levelNumber==1){
            return modeTimer[0];
        } else if (levelNumber<=4) {
            return modeTimer[1];
        } else {
            return modeTimer[2];
        }
    }

    public void setLevel(int levelNumber) {
        // reset all tiles (dots)
        tiles = clone(tilesTemplate);

        // reset pacman position, direction, tile map
        pacman.levelUpReset();
        pacman.updateTiles(tiles);

        // reset timer
        timer = 0;
        modeIndex = 0;
        thisLevelModeTimer = getModeTimer(levelNumber);

        // reset ghosts
        for(Ghost ghost: ghosts){
            ghost.reset();
        }
    }

    // get the viable neighbors of all viable tiles
    private void getNeighbors(int x, int y, Map<Point, ArrayList<Point>> map){
        Point currentPoint = new Point(x,y);
        ArrayList<Point> neighbors = new ArrayList<>(4);
        if(x+1>=0&&y>=0&&x+1<28&&y<31&&tilesRepresentation[y][x+1]!=1){
            neighbors.add(new Point(x+1,y));
        }
        if(x-1>=0&&y>=0&&x-1<28&&y<31&&tilesRepresentation[y][x-1]!=1){
            neighbors.add(new Point(x-1,y));
        }
        if(x>=0&&y+1>=0&&x<28&&y+1<31&&tilesRepresentation[y+1][x]!=1){
            neighbors.add(new Point(x,y+1));
        }
        if(x>=0&&y-1>=0&&x<28&&y-1<31&&tilesRepresentation[y-1][x]!=1){
            neighbors.add(new Point(x,y-1));
        }
        // connect the two ends of the tunnel
        if(x+1>27){
            neighbors.add(new Point(0,y));
        }
        if(x-1<0){
            neighbors.add(new Point(27,y));
        }
        map.put(currentPoint,neighbors);
    }

    // switch the ghosts' mode
    private void trackModeTimer(){
        timer++;
        if(timer==thisLevelModeTimer[modeIndex]){
            timer = 0;
            modeIndex++;
            for(Ghost ghost: ghosts) {
                ghost.switchMode();
            }
        }
    }

    // check collision with ghosts
    private void checkDead(){
        for(Ghost ghost: ghosts){
            if (ghost.atTile.equals(pacman.atTile) && !ghost.eaten) {   // ignore eaten ghosts
                // check if it can eat this ghost
                if (ghost.frightened) {
                    ghost.eaten();
                    pacman.eatGhost();  // add score for pacman
                }
                // or it is eaten by ghost
                else {
                    lives--;
                    if (lives == 0 || ghostsAImode) {    // game over
                        gameOver = true;
                        break;
                    }
                    else {      // reset ghosts and pacman
                        pacman.resetPosDirect();
                        for(Ghost g: ghosts){
                            g.reset();
                        }
                        break;
                    }
                }

            }
        }
    }

    // return the score when game over, return -1 otherwise.
    public int scoreGameOver(){
        if(gameOver){
            return score;
        }
        return -1;
    }

    // return true when finished a level
    private boolean checkFinishedLevel(){
        if(pacman.checkWin()){
            return true;
        }
        return false;
    }

    public void update(boolean[] keys){
        if(!ghostsAImode) {     // only level up + switch ghost mode in classic game
            if(checkFinishedLevel()){   // level up
                levelNumber++;
                setLevel(levelNumber);
            }
            trackModeTimer();   // set modes
        }
        else if(checkFinishedLevel()){
            gameOver = true;    // ends game in AI mode
        }


        pacman.update(keys);
        checkDead();

        for(Ghost ghost: ghosts) {
            ghost.update();
        }
    }

    public void draw(Graphics g){
        // draw level background
        g.drawImage(levelBg,0,0,null);

        // draw energizer + dots
        for(Tile[] row: tiles){
            for(Tile t: row){
                t.draw(g);
            }
        }

        // draw score, level number, lives remaining
        // lives
        for(int i=0; i<lives; i++){
            g.drawImage(liveIcon, 5+28*i,545, null);
        }

        // level number
        g.setColor(Color.WHITE);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        g.drawString("level "+levelNumber,180,20);

        // score
        score = pacman.returnScore();
        g.drawString(""+score,0,35);

        // draw pacman + ghosts
        pacman.draw(g);
        for(Ghost ghost: ghosts) {
            ghost.draw(g);
        }
    }
}
