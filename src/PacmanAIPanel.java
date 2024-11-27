import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// PacmanAIPanel: create level of AI pacman
public class PacmanAIPanel extends JPanel implements ActionListener{
    Level level;
    Timer timer;
    static int panelX = 28*16,panelY = 36*16;

    public PacmanAIPanel(){
        setPreferredSize(new Dimension(panelX,panelY));
        setSize(panelX,panelY);

        level = new Level(true, true);

        timer = new Timer(1000/60, this);
        timer.start();
    }

    public Level returnAILevel(){
        return level;
    }

    public void shutOff(){
        timer.stop();
        setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        update(); 	// never draw in move
        repaint(); 	// only draw
    }

    public void pause(){
        timer.stop();
    }

    public void resume(){
        timer.start();
    }

    public void update(){
        level.update(null);
    }

    @Override
    public void paint(Graphics g){
        level.draw(g);
    }

}
