// Pacman1: Sophie Miao:
// The user can play classic Pacman game or Versus AI pacman

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends JFrame{

    String gamePanelName;
    IntroPanel introPanel;
    GamePanelAIMode aiGamePanel;
    GamePanelClassicMode classicGamePanel;
    PausePanel pausePanel;

    CardLayout crd;

    public Main() {
        super("Pacman");

        // manage the panels using card layout
        crd = new CardLayout();
        setLayout(crd);

        Container contentPane = getContentPane();
        introPanel = new IntroPanel(this);
        aiGamePanel = new GamePanelAIMode(this);
        classicGamePanel = new GamePanelClassicMode(this);
        pausePanel = new PausePanel(classicGamePanel,aiGamePanel);

        contentPane.add("intro",introPanel);
        contentPane.add("aiGame", aiGamePanel);
        contentPane.add("classicGame", classicGamePanel);
        contentPane.add("pause", pausePanel);

        pack();  // set the size of my Frame exactly big enough to hold the contents

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


    public static void main(String[] arguments) {
        Main frame = new Main();
    }

    // change the current gamePanelName and show aiGame panel, start ai game
    public void startAIGame(){
        gamePanelName = "aiGame";
        crd.show(getContentPane(), "aiGame");
        aiGamePanel.start();
        pack();
    }

    // change the current gamePanelName and show classicGame panel, start game
    public void startClassicGame(){
        gamePanelName = "classicGame";
        crd.show(getContentPane(), "classicGame");
        classicGamePanel.start();
        pack();
    }

    // show pause panel
    public void showPausePanel(){
        crd.show(getContentPane(),"pause");
        setSize(pausePanel.getSize());
    }

    // show the game panel to resume game
    public void showResumePanel(){
        crd.show(getContentPane(),gamePanelName);
        pack();
    }

    // show current game panel to restart
    public void showRestartPanel(){
        if(gamePanelName.equals("classicGame")){
            crd.show(getContentPane(), "classicGame");
            classicGamePanel.game.requestFocus();
        }
        else {
            crd.show(getContentPane(), "aiGame");
        }
        pack();
    }

    // show intro panel to exit game
    public void showIntroPanel(){
        crd.show(getContentPane(),"intro");
        setSize(pausePanel.getSize());
    }

}


// IntroPanel: to start classic game or AI game
class IntroPanel extends JPanel implements ActionListener {
    JButton startClassicGame;
    JButton startAIGame;
    Main mainFrame;
    Image introBg = new ImageIcon("intro.png").getImage();

    public IntroPanel(Main m) {
        mainFrame = m;
        setBackground(new Color(0, 0, 0));
        setPreferredSize(new Dimension(28*16,36*16));

        setLayout(null);

        startClassicGame = new JButton( new AbstractAction("startClassicGame") {
            @Override
            public void actionPerformed( ActionEvent e ) {
                mainFrame.startClassicGame();
            }
        });
        startClassicGame.setSize(275,35);
        startClassicGame.setLocation(85,345);
        startClassicGame.addActionListener(this);
        startClassicGame.setOpaque(false);
        add(startClassicGame);

        startAIGame = new JButton( new AbstractAction("startAIGame") {
            @Override
            public void actionPerformed( ActionEvent e ) {
                mainFrame.startAIGame();
            }
        });
        startAIGame.setSize(200,35);
        startAIGame.setLocation(125,395);
        startAIGame.addActionListener(this);
        startAIGame.setOpaque(false);
        add(startAIGame);
    }

    public void paint(Graphics g) {
        g.drawImage(introBg, 0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}


// GamePanelAIMode: contains a player game panel + AI game panel
class GamePanelAIMode extends JPanel implements ActionListener{
    int border = 20;
    int middileSpace = 30;
    Main mainFrame;
    PacmanPanel game;
    PacmanAIPanel gameAI;
    JButton pauseButton;

    public GamePanelAIMode(Main m) {
        mainFrame = m;
    }

    // add the components
    public void start(){
        setLayout(null);
        setBackground(Color.BLACK);

        game = new PacmanPanel(true);
        game.setLocation(border,35);
        game.setAIPanel(this);
        add(game);

        gameAI = new PacmanAIPanel();
        gameAI.setLocation(game.getWidth()+border+middileSpace,35);
        add(gameAI);

        pauseButton = new JButton(new AbstractAction() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pauseGame();
            }
        });
        pauseButton.setSize(35,35);
        pauseButton.setLocation(0,0);
        pauseButton.addActionListener(this);

        pauseButton.setIcon(new ImageIcon("pauseIcon.png"));
        pauseButton.setBackground(null);
        pauseButton.setBorder(null);
        add(pauseButton);

        game.requestFocus();

        setPreferredSize(new Dimension(448*2+border*2+middileSpace, getHeight()+35));
        mainFrame.setSize(448*2+border*2+middileSpace, getHeight()+35);
    }

    // pause both game panels + show pause panel
    public void pauseGame(){
        if(game!=null) {
            game.pause();
            gameAI.pause();
            mainFrame.showPausePanel();
        }
    }

    // show the ai game panel + resume game timer
    public void resumeGame(){
        if(game!=null) {
            game.resume();
            gameAI.resume();
            mainFrame.showResumePanel();
            game.requestFocus();
        }
    }

    // restart ai game
    public void restartGame(){
        if(game!=null) {
            removeAll();
            start();
            mainFrame.showRestartPanel();
        }
    }

    // stop ai game + show intro panel
    public void exitGame(){
        if(game!=null) {
            removeAll();
            mainFrame.showIntroPanel();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}


// GamePanelAIMode: contains a classic game panel
class GamePanelClassicMode extends JPanel implements ActionListener{
    Main mainFrame;
    PacmanPanel game;
    JButton pauseButton;

    public GamePanelClassicMode(Main m) {
        mainFrame = m;
    }

    // add the components
    public void start(){
        setLayout(null);

        game = new PacmanPanel(false);
        game.setLocation(0,35);
        game.setClassicPanel(this);
        add(game);
        setBackground(Color.BLACK);


        pauseButton = new JButton(new AbstractAction() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pauseGame();
            }
        });
        pauseButton.setSize(35,35);
        pauseButton.setLocation(0,0);
        pauseButton.addActionListener(this);

        pauseButton.setIcon(new ImageIcon("pauseIcon.png"));
        pauseButton.setBackground(null);
        pauseButton.setBorder(null);
        add(pauseButton);

        game.requestFocus();

        setPreferredSize(new Dimension(game.getWidth(), game.getHeight()+35));
        mainFrame.setSize(game.getWidth(), game.getHeight()+35);
    }

    // pause game + show pause panel
    public void pauseGame(){
        if(game!=null) {
            game.pause();
            mainFrame.showPausePanel();
        }
    }

    // show the game panel + resume game timer
    public void resumeGame(){
        if(game!=null) {
            game.resume();
            mainFrame.showResumePanel();
            game.requestFocus();
        }
    }

    // restart game
    public void restartGame(){
        if(game!=null) {
            removeAll();
            start();
            mainFrame.showRestartPanel();
        }
    }

    // stop game + show intro panel
    public void exitGame() {
        if (game != null) {
            removeAll();
            mainFrame.showIntroPanel();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}


// PausePanel: switch the mainframe panel through game panel methods
class PausePanel extends JPanel implements ActionListener {
    JButton resumeButton;
    JButton restartButton;
    JButton exitButton;

    GamePanelClassicMode classicGamePanel;
    GamePanelAIMode aiGamePanel;
    Image pauseBg = new ImageIcon("pauseBg.png").getImage();

    public PausePanel(GamePanelClassicMode classic, GamePanelAIMode ai) {
        classicGamePanel = classic;
        aiGamePanel = ai;

        setBackground(Color.BLACK);
        setSize(new Dimension(28*16,36*16));

        setLayout(null);

        resumeButton = new JButton(new AbstractAction("resume") {
            @Override
            public void actionPerformed( ActionEvent e ) {
                classicGamePanel.resumeGame();
                aiGamePanel.resumeGame();
            }
        });
        resumeButton.setSize(180,50);
        resumeButton.setLocation(135,150);
        resumeButton.addActionListener(this);
        resumeButton.setOpaque(false);
        add(resumeButton);

        restartButton = new JButton( new AbstractAction("restart") {
            @Override
            public void actionPerformed( ActionEvent e ) {
                classicGamePanel.restartGame();
                aiGamePanel.restartGame();
            }
        });
        restartButton.setSize(200,60);
        restartButton.setLocation(125,245);
        restartButton.addActionListener(this);
        restartButton.setOpaque(false);
        add(restartButton);

        exitButton = new JButton( new AbstractAction("exit") {
            @Override
            public void actionPerformed( ActionEvent e ) {
                classicGamePanel.exitGame();
                aiGamePanel.exitGame();
            }
        });
        exitButton.setSize(130,50);
        exitButton.setLocation(160,350);
        exitButton.addActionListener(this);
        exitButton.setOpaque(false);
        add(exitButton);
    }

    public void paint(Graphics g) {
        g.drawImage(pauseBg, 0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}

