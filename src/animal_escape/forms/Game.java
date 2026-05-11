package animal_escape.forms;

import javax.swing.*;

public class Game {
    private JPanel gamePanel;
    private String nom;
    private String personatge;

    public Game(String nom, String personatge) {
        this.nom = nom;
        this.personatge = personatge;
    }

    public JPanel getGamePanel() {
        return gamePanel;
    }


}
