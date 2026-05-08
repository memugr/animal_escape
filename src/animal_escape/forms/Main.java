package animal_escape.forms;

import javax.swing.*;
import java.awt.*;

public class Main {

    private JPanel mainPanel;

    public Main() {
        mainPanel.setPreferredSize(new Dimension(600, 500));
        mainPanel.setBackground(new Color(244, 231, 218));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Inici");
        frame.setContentPane(new Main().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Toolkit pantalla = Toolkit.getDefaultToolkit();
        Image img = pantalla.getImage("src/animal_escape/img/isotip.png");
        frame.setIconImage(img);
    }
}
