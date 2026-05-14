package animal_escape.forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Game {
    public JPanel getGamePanel() { return gamePanel; }

    private static final int AMPLADA = 800;
    private static final int ALCADA = 600;
    private static final int MIDA_P = 60;
    private static final int MIDA_M = 80;
    private static final int MIDA_MONEDA = 40;
    private static final int MIDA_OBS = 50;
    private static final int VEL_JUGADOR = 5;

    private JPanel gamePanel;
    private String nom, personatge;
    private int idUsuari;
    private int idPersonatge;

    private int jugadorX = 20, jugadorY = 280;
    private int monstreX = 400, monstreY = 280;
    private int vides = 3, punts = 0;
    private boolean amunt, avall, esquerra, dreta;
    private boolean guanyat, perdut;
    private boolean tocantObstacle = false;

    private ImageIcon iconMonstreD, iconMonstreE, iconMonstreA, iconMonstreB;
    private ImageIcon iconJugador, iconMoneda, iconVida, iconObs;

    private List<Rectangle> obstacles = new ArrayList<>();
    private List<Point> monedes = new ArrayList<>();
    private Random random = new Random();
    private Timer timerJoc, timerMonstre;
    private Font fontHUD;

    private JLabel labelJugador;
    private JLabel labelMonstre;
    private JLabel labelPunts;
    private List<JLabel> labelsVides = new ArrayList<>();
    private List<JLabel> labelsMonedes = new ArrayList<>();
    private List<JLabel> panelsObstacles = new ArrayList<JLabel>();

    public Game(String nom, String personatge, int idUsuari, int idPersonatge) {
        this.nom = nom;
        this.personatge = personatge;
        this.idUsuari = idUsuari;
        this.idPersonatge = idPersonatge;

        carregarImatgesFonts();
        generarObstacles();
        generarMonedes();
        showPanel();

        timerJoc = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualitzar();
            }
        });
        timerJoc.start();

        iniciarTimerMonstre();
    }

    private Image img(String ruta) {
        return new ImageIcon(Objects.requireNonNull(getClass().getResource(ruta))).getImage();
    }

    private ImageIcon imgIconEscalat(Image imatge, int w, int h) {
        return new ImageIcon(imatge.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private ImageIcon imgIconGif(String ruta) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(ruta)));
        icon.setImage(icon.getImage().getScaledInstance(Game.MIDA_M, Game.MIDA_M, Image.SCALE_DEFAULT));
        return icon;
    }

    private void carregarImatgesFonts() {
        Image imgJugador;
        switch (personatge) {
            case "gos":
                imgJugador = img("/animal_escape/img/gos.png");
                break;
            case "conill":
                imgJugador = img("/animal_escape/img/conill.png");
                break;
            default:
                imgJugador = img("/animal_escape/img/gat.png");
                break;
        }

        iconJugador  = imgIconEscalat(imgJugador, MIDA_P, MIDA_P);
        iconMonstreD = imgIconGif("/animal_escape/img/skeleton_right.gif");
        iconMonstreE = imgIconGif("/animal_escape/img/skeleton_left.gif");
        iconMonstreA = imgIconGif("/animal_escape/img/skeleton_up.gif");
        iconMonstreB = imgIconGif("/animal_escape/img/skeleton_down.gif");
        Image imgMoneda = img("/animal_escape/img/dollar.png");
        iconMoneda = imgIconEscalat(imgMoneda, MIDA_MONEDA, MIDA_MONEDA);
        Image imgObstacle = img("/animal_escape/img/rock.png");
        iconObs = imgIconEscalat(imgObstacle, MIDA_OBS, MIDA_OBS);
        Image imgVida = img("/animal_escape/img/heart.png");
        iconVida = imgIconEscalat(imgVida, 22, 22);
        fontHUD = carregarFont("/animal_escape/fonts/Inter.ttf", 16f);
    }

    private void showPanel() {
        gamePanel = new JPanel(null);
        gamePanel.setPreferredSize(new Dimension(AMPLADA, ALCADA));
        gamePanel.setBackground(new Color(244, 231, 218));
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(new MovimentListener());

        // Obstacles
        for (Rectangle obs : obstacles) {
            JLabel lblObs = new JLabel(iconObs);
            lblObs.setBounds(obs.x, obs.y, obs.width, obs.height);
            gamePanel.add(lblObs);
            panelsObstacles.add(lblObs);
        }

        // Monedes
        for (Point m : monedes) {
            JLabel lbl = new JLabel(iconMoneda);
            lbl.setBounds(m.x, m.y, MIDA_MONEDA, MIDA_MONEDA);
            gamePanel.add(lbl);
            labelsMonedes.add(lbl);
        }

        // Meta
        JPanel panelMeta = new JPanel();
        panelMeta.setBackground(new Color(100, 200, 100));
        panelMeta.setBounds(AMPLADA - 12, 0, 12, ALCADA);
        gamePanel.add(panelMeta);

        // Jugador
        labelJugador = new JLabel(iconJugador);
        labelJugador.setBounds(jugadorX, jugadorY, MIDA_P, MIDA_P);
        gamePanel.add(labelJugador);

        // Monstre
        labelMonstre = new JLabel(iconMonstreD);
        labelMonstre.setBounds(monstreX, monstreY, MIDA_M, MIDA_M);
        gamePanel.add(labelMonstre);

        // HUD - Nom
        JLabel labelNom = new JLabel(nom);
        labelNom.setFont(fontHUD);
        labelNom.setForeground(new Color(24, 15, 47));
        labelNom.setBounds(10, 5, 200, 20);
        gamePanel.add(labelNom);

        // HUD - Punts
        labelPunts = new JLabel("Punts: " + punts);
        labelPunts.setFont(fontHUD);
        labelPunts.setForeground(new Color(24, 15, 47));
        labelPunts.setBounds(10, 30, 200, 20);
        gamePanel.add(labelPunts);

        // HUD - Vides
        actualitzarVides();
    }

    private void actualitzarVides() {
        for (JLabel v : labelsVides) gamePanel.remove(v);
        labelsVides.clear();
        for (int i = 0; i < vides; i++) {
            JLabel lbl = new JLabel(iconVida);
            lbl.setBounds(10 + i * 28, 55, 22, 22);
            gamePanel.add(lbl);
            labelsVides.add(lbl);
        }
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    private void generarObstacles() {
        obstacles.clear();
        while (obstacles.size() < 5) {
            int obstacleX = 150 + random.nextInt(AMPLADA - 300);
            int obstacleY = 50  + random.nextInt(ALCADA  - 100);
            Rectangle r = new Rectangle(obstacleX, obstacleY, MIDA_OBS, MIDA_OBS);

            if (!r.intersects(new Rectangle(0, 260, 100, 80))) {
                obstacles.add(r);
            }
        }
    }

    private void generarMonedes() {
        monedes.clear();
        while (monedes.size() < 4) {
            afegirMoneda();
        }
    }

    private void afegirMoneda() {
        boolean afegida = false;
        int intents = 0;

        while (!afegida && intents < 100) {
            int monedaX = 100 + random.nextInt(AMPLADA - 150);
            int monedaY = 30 + random.nextInt(ALCADA - 60);
            Point p = new Point(monedaX, monedaY);

            boolean colisiona = false;
            for (Rectangle obs : obstacles) {
                if (obs.contains(p)) {
                    colisiona = true;
                }
            }

            if (!colisiona) {
                monedes.add(p);
                afegida = true;
            }
            intents++;
        }
    }

    private void iniciarTimerMonstre() {
        timerMonstre = new Timer(500, new MonstreTimerListener());
        timerMonstre.start();
    }

    private class MonstreTimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int direccio = random.nextInt(4);
            int pas = 40;
            int nouX = monstreX, nouY = monstreY;
            switch (direccio) {
                case 0:
                    nouY -= pas;
                    labelMonstre.setIcon(iconMonstreA);
                    break;
                case 1:
                    nouY += pas;
                    labelMonstre.setIcon(iconMonstreB);
                    break;
                case 2:
                    nouX -= pas;
                    labelMonstre.setIcon(iconMonstreE);
                    break;
                case 3:
                    nouX += pas;
                    labelMonstre.setIcon(iconMonstreD);
                    break;
            }
            monstreX = Math.max(0, Math.min(nouX, AMPLADA - MIDA_M));
            monstreY = Math.max(0, Math.min(nouY, ALCADA - MIDA_M));
            labelMonstre.setBounds(monstreX, monstreY, MIDA_M, MIDA_M);
        }
    }

    private void actualitzar() {
        if (guanyat || perdut) return;
        movimentJugador();
        comprovarCollisions();
        gamePanel.repaint();
    }

    private void movimentJugador() {
        int nouX = jugadorX;
        int nouY = jugadorY;

        if (dreta) {
            nouX += VEL_JUGADOR;
        } else if (esquerra) {
            nouX -= VEL_JUGADOR;
        }

        if (avall) {
            nouY += VEL_JUGADOR;
        } else if (amunt) {
            nouY -= VEL_JUGADOR;
        }

        nouX = Math.max(0, Math.min(nouX, AMPLADA - MIDA_P));
        nouY = Math.max(0, Math.min(nouY, ALCADA  - MIDA_P));
        Rectangle nou = new Rectangle(nouX, nouY, MIDA_P, MIDA_P);

        boolean colisiona = false;
        for (Rectangle obs : obstacles) {
            if (nou.intersects(obs)) {
                colisiona = true;
            }
        }

        if (colisiona) {
            if (!tocantObstacle) {
                punts = Math.max(0, punts - 1);
                tocantObstacle = true;
                labelPunts.setText("Punts: " + punts);
            }
        } else {
            tocantObstacle = false;
            jugadorX = nouX;
            jugadorY = nouY;
            labelJugador.setBounds(jugadorX, jugadorY, MIDA_P, MIDA_P);
        }
    }

    private void comprovarCollisions() {
        Rectangle rJ = new Rectangle(jugadorX, jugadorY, MIDA_P, MIDA_P);

        // Monstre
        if (rJ.intersects(new Rectangle(monstreX, monstreY, MIDA_M, MIDA_M))) {
            vides--;
            actualitzarVides();

            if (vides <= 0) {
                perdut = true;
                timerJoc.stop();
                timerMonstre.stop();
                guardarPartida();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        mostrarDialogFinal();
                    }
                });
            } else {
                jugadorX = 20; jugadorY = 280;
                labelJugador.setBounds(jugadorX, jugadorY, MIDA_P, MIDA_P);
            }
        }

        // Monedes → suma punts i desapareix
        List<Point> recollides = new ArrayList<>();
        for (Point moneda : new ArrayList<>(monedes)) {
            if (rJ.intersects(new Rectangle(moneda.x, moneda.y, MIDA_MONEDA, MIDA_MONEDA))) {
                punts += 2;
                labelPunts.setText("Punts: " + punts);
                recollides.add(moneda);
            }
        }

        for (Point m : recollides) {
            int index = monedes.indexOf(m);
            if (index >= 0 && index < labelsMonedes.size()) {
                gamePanel.remove(labelsMonedes.get(index));
                labelsMonedes.remove(index);
                monedes.remove(index);
            }

            afegirMoneda();
            Point nova = monedes.getLast();
            JLabel lbl = new JLabel(iconMoneda);
            lbl.setBounds(nova.x, nova.y, MIDA_MONEDA, MIDA_MONEDA);
            gamePanel.add(lbl);
            labelsMonedes.add(lbl);
        }
        gamePanel.revalidate();

        // Meta
        if (jugadorX >= AMPLADA - MIDA_P) {
            guanyat = true;
            timerJoc.stop();
            timerMonstre.stop();
            guardarPartida();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mostrarDialogFinal();
                }
            });
        }
    }

    private void guardarPartida() {
        String db_url = "jdbc:mysql://localhost:3306/animal_escape";
        String db_user = "root";
        String db_password = "mysql";

        String queryInsert = "INSERT INTO partides (id_usuari, id_personatge, monedes, vides_restants, resultat, punts, data_partida) " +
                "VALUES (?, ?, ?, ?, ?, ?, CURDATE())";

        try {
            Connection conn = DriverManager.getConnection(db_url, db_user, db_password);
            PreparedStatement ps = conn.prepareStatement(queryInsert);

            ps.setInt(1, idUsuari);
            ps.setInt(2, idPersonatge);
            ps.setInt(3, monedes.size());
            ps.setInt(4, vides);
            ps.setString(5, guanyat ? "guanyat" : "perdut");
            ps.setInt(6, punts);

            ps.executeUpdate();
            ps.close();
            conn.close();

            System.out.println("Partida guardada correctament a la base de dades.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error en guardar partida a la base de dades");
        }
    }


    private void mostrarDialogFinal() {
        String missatge = guanyat ? "Has guanyat!" : "Has perdut!";
        String[] opcions = {"Tornar a jugar", "Tornar a l'inici"};
        int resposta = JOptionPane.showOptionDialog(gamePanel,
                missatge + "\nPunts: " + punts,
                "Fi del joc",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, opcions, opcions[0]);

        if (resposta == 0) {
            JFrame frameJoc = new JFrame("Animal Escape");
            frameJoc.setContentPane(new Game(nom, personatge, idUsuari, idPersonatge).getGamePanel());
            frameJoc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frameJoc.pack();
            frameJoc.setLocationRelativeTo(null);
            frameJoc.setVisible(true);
            SwingUtilities.getWindowAncestor(gamePanel).dispose();
        } else {
            JFrame frameInici = new JFrame("Inici");
            Main main = new Main();
            frameInici.setContentPane(main.getMainPanel());
            frameInici.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frameInici.addWindowListener(new Main.FrameWindowListener(frameInici));
            frameInici.pack();
            frameInici.setLocationRelativeTo(null);
            Toolkit pantalla = Toolkit.getDefaultToolkit();
            Image img = pantalla.getImage("src/animal_escape/img/isotip.png");
            frameInici.setIconImage(img);
            frameInici.setVisible(true);
            SwingUtilities.getWindowAncestor(gamePanel).dispose();
        }
    }

    private class MovimentListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    amunt = true;
                    break;
                case KeyEvent.VK_DOWN:
                    avall = true;
                    break;
                case KeyEvent.VK_LEFT:
                    esquerra = true;
                    break;
                case KeyEvent.VK_RIGHT:
                    dreta = true;
                    break;
                case KeyEvent.VK_R:
                    reiniciar();
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    amunt = false;
                    break;
                case KeyEvent.VK_DOWN:
                    avall = false;
                    break;
                case KeyEvent.VK_LEFT:
                    esquerra = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    dreta = false;
                    break;
            }
        }
    }

    private void reiniciar() {
        gamePanel.removeAll();
        labelsMonedes.clear();
        labelsVides.clear();
        panelsObstacles.clear();
        jugadorX = 20; jugadorY = 280;
        monstreX = 400; monstreY = 280;
        vides = 3; punts = 0;
        guanyat = false; perdut = false;
        tocantObstacle = false;
        generarObstacles();
        generarMonedes();
        showPanel();
        gamePanel.revalidate();
        gamePanel.repaint();
        timerJoc.start();
        timerMonstre.start();
    }

    private static Font carregarFont(String ruta, float mida) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT,
                    Objects.requireNonNull(Game.class.getResourceAsStream(ruta)));
            return font.deriveFont(mida);
        } catch (Exception e) {
            System.out.println("No s'ha pogut carregar la font: " + ruta);
            return new Font("Arial", Font.PLAIN, (int) mida);
        }
    }
}