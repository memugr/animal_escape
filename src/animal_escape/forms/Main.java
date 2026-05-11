package animal_escape.forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class Main {
    private JPanel mainPanel;
    private JTextField textNom;
    private JButton botoGat;
    private JButton botoGos;
    private JButton botoConill;
    private JButton botoJugar;
    private JLabel labelInstruccions;
    private String personatgeSeleccionat = "gat"; // per defecte

    public Main() {
        mainPanel.setPreferredSize(new Dimension(600, 400));
        mainPanel.setBackground(new Color(244, 231, 218));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Títol
        JLabel labelTitol = new JLabel("Animal Escape");
        labelTitol.setFont(carregarFont("/animal_escape/fonts/SpaceGrotesk.ttf", 28f).deriveFont(Font.BOLD));
        labelTitol.setForeground(new Color(24, 15, 47));
        labelTitol.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(labelTitol);

        // Instruccions
        labelInstruccions = new JLabel("<html><div>" +
                "<b>Com jugar:</b><br>" +
                "Mou el teu animal amb les tecles ← ↑ ↓ →<br>" +
                "Arriba a l'altre costat sense que et toqui el monstre<br>" +
                "Recull monedes per sumar punts (+2)<br>" +
                "Evita els obstacles o perdràs punts (-1)<br>" +
                "Comences amb 3 vides. Bona sort!" +
                "</div></html>");
        labelInstruccions.setFont(carregarFont("/animal_escape/fonts/Inter.ttf", 14));
        labelInstruccions.setForeground(new Color(24, 15, 47));

        JPanel panelInstruccions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInstruccions.setBackground(new Color(244, 231, 218));
        panelInstruccions.setMaximumSize(new Dimension(600, 120));
        panelInstruccions.add(labelInstruccions);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(panelInstruccions);

        // Nom d'usuari
        JLabel labelNom = new JLabel("Nom d'usuari:");
        labelNom.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelNom.setFont(carregarFont("/animal_escape/fonts/Inter.ttf", 13));
        labelNom.setForeground(new Color(24, 15, 47));
        textNom = new JTextField();
        textNom.setMaximumSize(new Dimension(200, 25));
        textNom.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(labelNom);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(textNom);

        // Botons de personatge
        JLabel labelTriar = new JLabel("Tria el teu personatge:");
        labelTriar.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelTriar.setFont(carregarFont("/animal_escape/fonts/Inter.ttf", 13));
        labelTriar.setForeground(new Color(24, 15, 47));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(labelTriar);

        botoGat = crearBotoPersonatge("Gat");
        botoGos = crearBotoPersonatge("Gos");
        botoConill = crearBotoPersonatge("Conill");

        JPanel panelPersonatges = new JPanel();
        panelPersonatges.setMaximumSize(new Dimension(400, 40));
        panelPersonatges.setBackground(new Color(244, 231, 218));
        panelPersonatges.add(botoGat);
        panelPersonatges.add(botoGos);
        panelPersonatges.add(botoConill);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(panelPersonatges);

        // Selecció de personatge
        seleccionarPersonatge(botoGat, "gat"); // selecció per defecte
        botoGat.addActionListener(new PersonatgeListener(botoGat, "gat"));
        botoGos.addActionListener(new PersonatgeListener(botoGos, "gos"));
        botoConill.addActionListener(new PersonatgeListener(botoConill, "conill"));

        // Botó Jugar
        botoJugar = new JButton("JUGAR");
        botoJugar.setMaximumSize(new Dimension(130, 40));
        botoJugar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botoJugar.setBackground(new Color(247, 239, 162));
        botoJugar.setForeground(new Color(24, 15, 47));
        botoJugar.setFont(carregarFont("/animal_escape/fonts/SpaceGrotesk.ttf", 22f).deriveFont(Font.BOLD));
        botoJugar.setFocusPainted(false);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(botoJugar);

        botoJugar.addActionListener(e -> {
            String nom = textNom.getText().trim();

            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Si us plau, introdueix un nom d'usuari.",
                        "Nom buit", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFrame frameJoc = new JFrame("Animal Escape");
            frameJoc.setContentPane(new Game(nom, personatgeSeleccionat).getGamePanel());
            frameJoc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frameJoc.pack();
            frameJoc.setLocationRelativeTo(null);
            frameJoc.setVisible(true);
            SwingUtilities.getWindowAncestor(mainPanel).dispose();
        });
    }

    private class PersonatgeListener implements ActionListener {
        private JButton boto;
        private String personatge;

        public PersonatgeListener(JButton boto, String personatge) {
            this.boto = boto;
            this.personatge = personatge;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            seleccionarPersonatge(boto, personatge);
        }
    }

    private JButton crearBotoPersonatge(String text) {
        JButton boto = new JButton(text);
        boto.setBackground(new Color(247, 239, 162));
        boto.setFocusPainted(false);
        boto.setForeground(new Color(24, 15, 47));
        return boto;
    }

    private void seleccionarPersonatge(JButton seleccionat, String personatge) {
        personatgeSeleccionat = personatge;
        botoGat.setBackground(null);
        botoGos.setBackground(null);
        botoConill.setBackground(null);
        seleccionat.setBackground(new Color(124, 114, 149));
    }

    private static class FrameWindowListener extends WindowAdapter {
        private JFrame frame;

        public FrameWindowListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void windowClosing(WindowEvent e) {
            int resposta = JOptionPane.showConfirmDialog(frame,
                    "Estàs segur que vols sortir?",
                    "Sortir",
                    JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    private static Font carregarFont(String ruta, float mida) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT,
                    Objects.requireNonNull(Main.class.getResourceAsStream(ruta)));
            return font.deriveFont(mida);
        } catch (Exception e) {
            System.out.println("No s'ha pogut carregar la font: " + ruta);
            return new Font("Arial", Font.PLAIN, (int) mida);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Inici");
        Main main = new Main();
        frame.setContentPane(main.mainPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new FrameWindowListener(frame));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Toolkit pantalla = Toolkit.getDefaultToolkit();
        Image img = pantalla.getImage("src/animal_escape/img/isotip.png");
        frame.setIconImage(img);
    }
}
