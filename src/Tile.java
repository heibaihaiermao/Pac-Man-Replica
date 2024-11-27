import java.awt.*;

// Tile class: store the position and the type of tile
public class Tile {
    boolean wall = false;
    boolean dot = false;
    boolean bigDot = false;
    boolean eaten = false;
    boolean ghostHouse = false;
    Point pos = new Point();
    Point center = new Point();
    int tileSize = 16;

    public Tile(int x, int y) {
        center.x = x;
        center.y = y;
        pos.x = x-tileSize/2;
        pos.y = y-tileSize/2;
    }

    public void draw(Graphics g){
        g.setColor(Color.WHITE);

        g.setColor(Color.pink);
        if (dot) {
            if (!eaten) {//draw dot
                g.fillOval(center.x-2,center.y-2,3,3);
            }
        }
        else if (bigDot) {
            if (!eaten) {//draw big dot
                g.fillOval(center.x-6,center.y-6,12,12);
            }
        }
    }

}
