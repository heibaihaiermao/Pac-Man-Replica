import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// PacmanPanel: contains the game Level, controls the Level timer, change the panel when game is over
public class PacmanPanel extends JPanel implements KeyListener, ActionListener {
    boolean[] keys;
    Level level;
    Level aiLevel;
    GamePanelClassicMode classicPanel;
    GamePanelAIMode aiPanel;
    Timer timer;
    static int panelX = 28 * 16, panelY = 36 * 16;

    public PacmanPanel(boolean ghostAImode) {
        keys = new boolean[KeyEvent.KEY_LAST + 1];
        setPreferredSize(new Dimension(panelX,panelY));
        setSize(panelX,panelY);

        level = new Level(false, ghostAImode);

        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        timer = new Timer(1000 / 60, this);
        timer.start();
    }

    public void setAIPanelLevel(Level AILevel){
        aiLevel = AILevel;
    }
    public void setClassicPanel(GamePanelClassicMode p){ classicPanel = p; }
    public void setAIPanel(GamePanelAIMode p){
        aiPanel = p;
    }

    @Override
    public void keyReleased(KeyEvent ke){
        int key = ke.getKeyCode();
        keys[key] = false;
    }

    @Override
    public void keyPressed(KeyEvent ke){
        int key = ke.getKeyCode();
        keys[key] = true;
    }

    @Override
    public void keyTyped(KeyEvent ke){}

    @Override
    public void actionPerformed(ActionEvent e){
        update(); 	// never draw in move
        repaint(); 	// only draw
    }

    public void update(){
        level.update(keys);
        if(level.scoreGameOver() != -1){    // if game overed, switch panel
            if(aiPanel==null) {
                classicPanel.pauseGame();
                classicPanel.exitGame();
            }
            else {
                aiPanel.pauseGame();
                aiPanel.exitGame();
            }
        }
    }

    @Override
    public void paint(Graphics g){
        level.draw(g);
    }

    public void pause(){
        timer.stop();
    }

    public void resume(){
        timer.start();
    }

}
