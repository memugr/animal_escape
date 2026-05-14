# ANIMAL ESCAPE

Joc 2D desenvolupat en Java amb Swing on el jugador controla un animal i ha de travessar la pantalla sense ser atrapat per un monstre.

---

## Com jugar

- Mou el teu animal amb les tecles **← ↑ ↓ →**
- Arriba a la **franja verda** de l'altre costat sense que et toqui el monstre
- Recull **monedes** per sumar punts (+2)
- Evita els **obstacles** o perdràs punts (-1)
- Comences amb **3 vides**

---

## Personatges

| Personatge | Descripció |
|---|---|
| Gat | Personatge per defecte |
| Gos | Personatge alternatiu |
| Conill | Personatge alternatiu |

---

## Configuració de la base de dades

### 1. Executa l'script a MySQL
Connecta't a MySQL i executa el següent script:

```sql
CREATE DATABASE animal_escape;
USE animal_escape;

CREATE TABLE usuaris (
    id_usuari INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    data_registre DATE NOT NULL
);

CREATE TABLE personatges (
    id_personatge INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL
);

CREATE TABLE partides (
    id_partida INT PRIMARY KEY AUTO_INCREMENT,
    id_usuari INT NOT NULL,
    id_personatge INT NOT NULL,
    monedes INT NOT NULL,
    vides_restants INT NOT NULL,
    resultat VARCHAR(20) NOT NULL,
    punts INT NOT NULL,
    data_partida DATE NOT NULL,
    CONSTRAINT fk_usuari_partides FOREIGN KEY (id_usuari) REFERENCES USUARIS(id_usuari),
    CONSTRAINT fk_personatge_partides FOREIGN KEY (id_personatge) REFERENCES PERSONATGES(id_personatge)
);

-- Personatges inicials
INSERT INTO PERSONATGES (nom) VALUES ('gat'), ('gos'), ('conill');
```

### 3. Configura les credencials

A `Main.java` i a`Game.java` modifica les credencials de connexió si cal:

```java
String db_url = "jdbc:mysql://localhost:3306/animal_escape";
String db_user = "root";
String db_password = "mysql";
```

---

## 📁 Estructura del projecte

```
Animal_Escape/
├── src/
│   └── animal_escape/
│       ├── fonts/
│       │   ├── Inter.ttf
│       │   ├── JetBrainsMono.ttf
│       │   └── SpaceGrotesk.ttf
│       ├── forms/
│       │   ├── Game/
│       │   │   ├── Game.java
│       │   │   └── Game.form
│       │   └── Main/
│       │       ├── Main.java
│       │       └── Main.form
│       └── img/
│           ├── conill.png
│           ├── dollar.png
│           ├── gat.png
│           ├── gos.png
│           ├── heart.png
│           ├── isotip.png
│           ├── skeleton_down.gif
│           ├── skeleton_left.gif
│           ├── skeleton_right.gif
│           └── skeleton_up.gif
├── mysql-connector-j-9.7.0.jar
└── Animal_Escape.iml
```
---

## 🛠️ Tecnologies utilitzades

- **Java** amb **Swing** per la interfície gràfica
- **MySQL** per la base de dades
- **JDBC Connector** per la connexió amb la base de dades
- **IntelliJ IDEA** com a IDE
