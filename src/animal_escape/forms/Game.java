package animal_escape.forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

    private int jugadorX = 20, jugadorY = 280;
    private int monstreX = 400, monstreY = 280;
    private int vides = 3, punts = 0;
    private boolean amunt, avall, esquerra, dreta;
    private boolean guanyat, perdut;
    private boolean tocantObstacle = false;
    private Image imgJugador, imgMonstre, imgMoneda, imgVida;
    private List<Rectangle> obstacles = new ArrayList<>();
    private List<Point> monedes = new ArrayList<>();
    private Random random = new Random();
    private Timer timerJoc, timerMonstre;
    private Font fontHUD, fontFinalTitol, fontFinalText;

    public Game(String nom, String personatge) {
        this.nom = nom;
        this.personatge = personatge;

        carregarImatgesFonts();
        generarObstacles();
        generarMonedes();

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibuixar((Graphics2D) g);
            }
        };
        gamePanel.setPreferredSize(new Dimension(AMPLADA, ALCADA));
        gamePanel.setBackground(new Color(244, 231, 218));
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(new MovimentListener());

        timerJoc = new Timer(50, e -> actualitzar());
        timerJoc.start();

        iniciarTimerMonstre();
    }

    private Image img(String ruta) {
        return new ImageIcon(Objects.requireNonNull(getClass().getResource(ruta))).getImage();
    }

    private void carregarImatgesFonts() {
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
        imgMonstre = img("/animal_escape/img/skeleton_right.gif");
        imgMoneda = img("/animal_escape/img/dollar.png");
        imgVida = img("/animal_escape/img/heart.png");
        fontHUD = carregarFont("/animal_escape/fonts/Inter.ttf", 16f);
        fontFinalTitol = carregarFont("/animal_escape/fonts/SpaceGrotesk.ttf", 32f).deriveFont(Font.BOLD);
        fontFinalText = carregarFont("/animal_escape/fonts/Inter.ttf", 18f);
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
                    imgMonstre = img("/animal_escape/img/skeleton_up.gif");
                    break;
                case 1:
                    nouY += pas;
                    imgMonstre = img("/animal_escape/img/skeleton_down.gif");
                    break;
                case 2:
                    nouX -= pas;
                    imgMonstre = img("/animal_escape/img/skeleton_left.gif");
                    break;
                case 3:
                    nouX += pas;
                    imgMonstre = img("/animal_escape/img/skeleton_right.gif");
                    break;
            }
            monstreX = Math.max(0, Math.min(nouX, AMPLADA - MIDA_M));
            monstreY = Math.max(0, Math.min(nouY, ALCADA - MIDA_M));
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
            }
        } else {
            tocantObstacle = false;
            jugadorX = nouX;
            jugadorY = nouY;
        }
    }

    private void comprovarCollisions() {
        Rectangle rJ = new Rectangle(jugadorX, jugadorY, MIDA_P, MIDA_P);

        // Monstre
        if (rJ.intersects(new Rectangle(monstreX, monstreY, MIDA_M, MIDA_M))) {
            vides--;
            if (vides <= 0) {
                perdut = true;
                timerJoc.stop();
                timerMonstre.stop();
            } else {
                jugadorX = 20; jugadorY = 280;
            }
        }

        // Monedes → suma punts i desapareix
        List<Point> recollides = new ArrayList<>();
        for (Point moneda : new ArrayList<>(monedes)) {
            if (rJ.intersects(new Rectangle(moneda.x, moneda.y, MIDA_MONEDA, MIDA_MONEDA))) {
                punts += 2;
                recollides.add(moneda);
            }
        }
        monedes.removeAll(recollides);
        for (int i = 0; i < recollides.size(); i++) {
            afegirMoneda();
        }

        // Meta
        if (jugadorX >= AMPLADA - MIDA_P) {
            guanyat = true;
            timerJoc.stop();
            timerMonstre.stop();
        }
    }

    private void dibuixar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Obstacles
        g.setColor(new Color(180, 100, 100));
        for (Rectangle obs : obstacles) {
            g.fillRoundRect(obs.x, obs.y, obs.width, obs.height, 8, 8);
        };

        // Monedes
        for (Point m : monedes) {
            g.drawImage(imgMoneda, m.x, m.y, MIDA_MONEDA, MIDA_MONEDA, gamePanel);
        };

        // Jugador i monstre
        g.drawImage(imgJugador, jugadorX, jugadorY, MIDA_P, MIDA_P, gamePanel);
        g.drawImage(imgMonstre, monstreX, monstreY, MIDA_M, MIDA_M, gamePanel);

        // Meta
        g.setColor(new Color(100, 200, 100, 180));
        g.fillRect(AMPLADA - 12, 0, 12, ALCADA);
        g.setColor(new Color(50, 150, 50));
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("META", AMPLADA - 50, ALCADA / 2);

        // HUD
        g.setColor(new Color(24, 15, 47));
        g.setFont(fontHUD);
        g.drawString(nom, 10, 20);
        g.drawString("Punts: " + punts, 10, 45);
        for (int i = 0; i < vides; i++) {
            g.drawImage(imgVida, 10 + i * 28, 55, 22, 22, gamePanel);
        }

        // Pantalla final
        if (guanyat || perdut) {
            g.setColor(new Color(24, 15, 47, 160));
            g.fillRect(0, 0, AMPLADA, ALCADA);
            g.setColor(Color.WHITE);
            g.setFont(fontFinalTitol);
            g.drawString(guanyat ? "Has guanyat!" : "Has perdut!", AMPLADA / 2 - 150, ALCADA / 2 - 20);
            g.setFont(fontFinalText);
            g.drawString("Punts: " + punts, AMPLADA / 2 - 45, ALCADA / 2 + 20);
            g.drawString("Prem R per tornar a jugar", AMPLADA / 2 - 130, ALCADA / 2 + 55);
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
        jugadorX = 20; jugadorY = 280;
        monstreX = 400; monstreY = 280;
        vides = 3; punts = 0;
        guanyat = false; perdut = false;
        generarObstacles();
        generarMonedes();
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