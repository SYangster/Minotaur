import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.*;

public class Engine extends JPanel implements KeyListener{
    private Maze maze;

    Image block;
    Image block2;
    Image hero;
    Image enemy;
    Image invis;
    Image freeze;
    Image heroinvis;
    Image heroteleport;
    Image enemyfreeze;
    Image coin;
    Image doublecoin;

    int screen_width;
    int screen_height;

    private int fps = 2;

    public void loadImages() {
        try {
            this.block = ImageIO.read(new File("BlockCSS.png"));
            this.block2 = ImageIO.read(new File("BlockCSS2.png"));
            this.hero = ImageIO.read(new File("hero.png"));
            this.enemy = ImageIO.read(new File("enemy.png"));
            this.invis = ImageIO.read(new File("invis.png"));
            this.freeze = ImageIO.read(new File("freeze.png"));
            this.heroinvis = ImageIO.read(new File("heroinvis.png"));
            this.heroteleport = ImageIO.read(new File("heroteleport.png"));
            this.enemyfreeze = ImageIO.read(new File("enemyfreeze.png"));
            this.coin = ImageIO.read(new File("coin.png"));
            this.doublecoin = ImageIO.read(new File("doublecoin.png"));
        } catch (IOException i) {
            System.out.println("Error: " + i.getMessage());
        }
    }

    public Engine() {




        this.maze = new Maze();


    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int y = 0; y < maze.map2.length; y++) {
            for (int x = 0; x < maze.map2[0].length; x++) {
                if (maze.map2[y][x].equals(maze.WALL)) {
                    drawBlock(x,y,this.block2,g);
                    //System.out.print(maze.WALL);
                } else if (maze.map2[y][x].equals(maze.FLOOR)) {
                    drawBlock(x,y,this.block,g);
                    //System.out.print(maze.FLOOR);
                } else if (maze.map2[y][x].equals("i")) {
                    drawBlock(x,y,this.block,g);
                    drawItem(x,y,this.invis,g);
                    //System.out.print("i");
                } else if (maze.map2[y][x].equals("f")) {
                    drawBlock(x,y,this.block,g);
                    drawItem(x,y,this.freeze,g);
                    //System.out.print("f");
                } else if (maze.map2[y][x].equals("c")) {
                    drawBlock(x,y,this.block,g);
                    drawItem(x,y,this.coin,g);
                    //System.out.print("c");
                } else if (maze.map2[y][x].equals("d")) {
                    drawBlock(x,y,this.block,g);
                    drawItem(x,y,this.doublecoin,g);
                    //System.out.print("d");
                }
            }
            //System.out.println("");
        }

        if (maze.isTeleporting) {
            drawItem(maze.player.x, maze.player.y, this.heroteleport,g);
            maze.isTeleporting = false;
        } else if (maze.invisTime == 0) {
            drawItem(maze.player.x, maze.player.y, this.hero, g);
        } else {
            drawItem(maze.player.x, maze.player.y, this.heroinvis, g);
        }



        for (Position e : maze.enemies) {
            if (maze.freezeTime > 0) {
                drawItem(e.x, e.y, this.enemyfreeze,g);
            } else {
                drawItem(e.x, e.y, this.enemy,g);
            }
        }



        g.drawString("Score: "+ maze.score, screen_width/2 - 30, screen_height+16);

        g.drawString(maze.invisPotions +" invisibility potion(s)  Press ' I ' to activate", 5, screen_height+32);

        g.drawString(maze.freezePotions +" freeze potion(s)  Press ' F ' to activate", 5, screen_height+48);

        g.drawString(maze.teleports +" teleport potion(s)  Press ' T ' to activate", 5, screen_height+ 16);

        g.drawString("Use arrow keys to move", 5, screen_height+ 80);

        g.drawString("Yellow coins give +30 and purple coins give +100", 5, screen_height+ 96);

        for (Position enemy: maze.enemies) {
            if (maze.nextTo(enemy, maze.player)) {
                String str = "GAME OVER!";
                g.drawString("GAME OVER!", screen_width/2 -30, screen_height+48);

                g.drawString("Your Score Was: " + maze.score, screen_width/2 -30, screen_height+64);

                g.drawString("Press ' R ' to generate a new random maze and play again!", screen_width/2 -30, screen_height+80);

                maze.gameover = true;
            }
        }

    }




    public void drawBlock(int x, int y, Image image, Graphics g) {
        g.drawImage(image, x*16, y*16, 16, 16,null);
    }

    public void drawItem(int x, int y, Image image, Graphics g) {
        g.drawImage(image, x*16, y*16, 16, 16,null);
    }

    public static void main(String[] args) {




        Engine engine = new Engine();
        engine.loadImages();
        engine.maze.readMap();
        engine.maze.initializeModel();



        JFrame frame = new JFrame("The Minotaurs by Sean Yang");


        int scale = 16;
        engine.screen_width = scale * engine.maze.width;
        engine.screen_height = scale * engine.maze.height;

        frame.setSize(engine.screen_width, engine.screen_height+150);
        //frame.setBounds(0, 0, 800, 600);
        //panel.add(this);
        frame.add(engine);
        //frame.pack();
        //frame.setResizable(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addKeyListener(engine);

        //frame.addKeyListener(new KeyInputHandler());

        frame.requestFocus();

        while (true) {
            engine.run();
        }


    }

    public void run() {
        Random random = new Random();
        Position randPos = new Position( random.nextInt(maze.width - 1) + 1, random.nextInt(maze.height - 1) + 1 );
        if (maze.coin == 1) {
            for (int i = 0; i < 1; i++) {
                boolean validPos = false;
                while (!validPos) {
                    randPos = new Position( random.nextInt(maze.width - 1) + 1, random.nextInt(maze.height - 1) + 1 );
                    if ((maze.map2[randPos.y][randPos.x].equals(maze.FLOOR)) && !(maze.player.contains(randPos, 8))) {
                        validPos = true;
                    }
                }
                maze.map2[randPos.y][randPos.x] = maze.COIN;
                maze.coin -= 1;
            }
        }

        randPos = new Position( random.nextInt(maze.width - 2) + 1, random.nextInt(maze.height - 2) + 1 );
        if (maze.coin == 1) {
            for (int i = 0; i < 1; i++) {
                boolean validPos = false;
                while (!validPos) {
                    randPos = new Position( random.nextInt(maze.width - 2) + 1, random.nextInt(maze.height - 2) + 1 );
                    if (maze.map2[randPos.y][randPos.x].equals(maze.FLOOR) && !(maze.player.contains(randPos, 8))) {
                        validPos = true;
                    }
                }
                maze.map2[randPos.y][randPos.x] = maze.DOUBLE_COIN;
                maze.doublecoincounter = 200;
            }
        }


        if (fps == 3) {
            if (maze.freezeTime == 0) {
                for (Position enemy : maze.enemies) {
                    if (maze.invisTime == 0) {
                        maze.moveTowards(enemy, maze.player);
                    } else {
                        maze.moveTowards(enemy, maze.fakeplayer);
                    }
                }
            }
            fps = 0;
        } else {
            fps += 1;
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            Thread.interrupted();
            return;
        }

        repaint();

        /*
        for (int y = 0; y < maze.map2.length; y++) {
            for (int x = 0; x < maze.map2[0].length; x++) {
                if (new Position(x,y).equalTo(maze.player)) {
                    System.out.print("X");
                } else if (maze.map2[y][x].equals(maze.WALL)) {
                    System.out.print(maze.WALL);
                } else if (maze.map2[y][x].equals(maze.FLOOR)) {
                    System.out.print(maze.FLOOR);
                } else if (maze.map2[y][x].equals("i")) {
                    System.out.print("i");
                } else if (maze.map2[y][x].equals("f")) {
                    System.out.print("f");
                } else if (maze.map2[y][x].equals("c")) {
                    System.out.print("c");
                } else if (maze.map2[y][x].equals("d")) {
                    System.out.print("d");
                }
            }
            System.out.println("");
        }
         **/
    }

    public void keyPressed(KeyEvent e) {
        int event = e.getKeyCode();

        if (event == KeyEvent.VK_Q) {
            System.exit(0);
        } else if (maze.gameover == true) {
            if (event == KeyEvent.VK_R) {
                this.maze.initializeModel();
                maze.gameover = false;
                repaint();
            }
        } else {
            if (event == KeyEvent.VK_UP) {
                maze.prevLocation = maze.move(maze.player, maze.UP, maze.moveDistance);
                maze.moveDistance = 1;
            } else if (event == KeyEvent.VK_LEFT) {
                maze.prevLocation = maze.move(maze.player, maze.LEFT, maze.moveDistance);
                maze.moveDistance = 1;
            } else if (event == KeyEvent.VK_RIGHT) {
                maze.prevLocation = maze.move(maze.player, maze.RIGHT, maze.moveDistance);
                maze.moveDistance = 1;
            } else if (event == KeyEvent.VK_DOWN) {
                maze.prevLocation = maze.move(maze.player, maze.DOWN, maze.moveDistance);
                maze.moveDistance = 1;
            } else if (event == KeyEvent.VK_I) {
                if (maze.invisPotions > 0) {
                    maze.invisPotions -= 1;
                    maze.invisTime = 25;
                    maze.fakeplayer.y = maze.player.y;
                    maze.fakeplayer.x = maze.player.x;
                }
            } else if (event == KeyEvent.VK_F) {
                if (maze.freezePotions > 0) {
                    maze.freezePotions -= 1;
                    maze.freezeTime = 25;
                }
            } else if (event == KeyEvent.VK_T) {
                if (maze.teleports > 0) {
                        maze.teleports -= 1;
                        maze.moveDistance = 5;
                        maze.isTeleporting = true;
                }
            }
            maze.doublecoincounter -= 1;

            if (maze.prevLocation.equals(maze.INVIS_POTION)) {
                maze.invisPotions += 1;
            } else if (maze.prevLocation.equals(maze.FREEZE_POTION)) {
                maze.freezePotions += 1;
            } else if (maze.prevLocation.equals(maze.COIN)) {
                maze.coin = 1;
                maze.score += 30;
            } else if (maze.prevLocation.equals(maze.DOUBLE_COIN)) {
                maze.score += 100;
            }

            maze.score += 1;
            if (maze.invisTime > 0) {
                maze.invisTime -= 1;
            }
            if (maze.freezeTime > 0) {
                maze.freezeTime -= 1;
            }

            /*
            if (maze.freezeTime == 0) {
                for (Position enemy: maze.enemies) {
                    if (maze.invisTime == 0) {
                        maze.moveTowards(enemy, maze.player);
                    } else {
                        maze.moveTowards(enemy, maze.fakeplayer);
                    }
                }
            }
            */



        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}