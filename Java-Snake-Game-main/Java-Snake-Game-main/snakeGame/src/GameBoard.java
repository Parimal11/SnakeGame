
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.imageio.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

public class GameBoard extends JPanel implements ActionListener {
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int DOT_SIZE = 40;
    private final int GAME_UNITS = (WIDTH * HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private final int DELAY = 125;
    
    private final int x[] = new int[GAME_UNITS]; //creates x axis grid
    private final int y[] = new int[GAME_UNITS]; //creates y axis grid

    private Timer GameTimer;

    private Random random;
    private int dots;
    private int xApple;
    private int yApple;
    private int powerX;
    private int powerY;
    private int shortenX;
    private int shortenY;
    private int applesEaten;
    private int powerLimit;
    private int shortenLimit = 3;

    private boolean inPower = false;
    private boolean drawPower = false;
    private boolean drawShorten = false;
    private boolean inGame = true;
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private PriorityQueue<Integer> pq;
    
    public GameBoard(PriorityQueue<Integer> pq) {
        this.pq = pq;
        initBoard();
    }

    public void initBoard() {
        this.addKeyListener(new ButtonAdapter());
        this.setBackground(Color.BLACK); //background colour is black
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT)); //set dimensions of board
        this.setFocusable(true);
        this.random = new Random();
        initGame();

    }
    public void initGame() {
        this.dots = 3; //initially have 3 dots to form the snake
        this.applesEaten = 0;
        this.powerLimit = 2;
        inGame = true;
        //initialise apple
        spawnApple();
        GameTimer = new Timer(DELAY, this);
        GameTimer.start();
    }

    public void spawnApple() {
        xApple = random.nextInt((int)(WIDTH/DOT_SIZE)) * DOT_SIZE;
        yApple = random.nextInt((int)(HEIGHT/DOT_SIZE)) * DOT_SIZE;
    }

    public void spawnPower() {
        int choice = random.nextInt(1000);
        System.out.println(choice);
        if (choice <= 10) {
            powerX = random.nextInt((int)(WIDTH/DOT_SIZE))*DOT_SIZE;
            powerY = random.nextInt((int)(HEIGHT/DOT_SIZE))*DOT_SIZE;
            if (powerX == xApple && powerY == yApple) { //if same coordinates as apples, spawn again
                spawnPower();
            }
            drawPower = true;
        }
    }

    public void spawnShorten() {
        int choice = random.nextInt(1500);
        if (choice <= 10) {
            shortenX = random.nextInt((int)(WIDTH/DOT_SIZE))*DOT_SIZE;
            shortenY = random.nextInt((int)(HEIGHT/DOT_SIZE))*DOT_SIZE;
            if ((shortenX == xApple && shortenY == yApple) || (shortenX == powerX && shortenY == powerY)) {
                spawnShorten();
            }
            drawShorten = true;
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        if (inGame) {
            g.setColor(Color.RED);
            g.fillOval(xApple, yApple, DOT_SIZE, DOT_SIZE); //draw apple

            if (drawPower) {
                g.setColor(Color.ORANGE);
                g.fillOval(powerX, powerY, DOT_SIZE, DOT_SIZE); //draw power-up
            }

            if (drawShorten) {
                g.setColor(Color.PINK);
                g.fillOval(shortenX, shortenY, DOT_SIZE, DOT_SIZE); //draw special apple to shorten snake
            }
            
            for (int j = 0; j < dots; j++) {
                if (!inPower) {
                    if (j == 0) {
                        g.setColor(Color.GREEN); //draw head
                        g.fillRect(x[j], y[j], DOT_SIZE, DOT_SIZE);
                    } else {
                        g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))); //draw body
                        g.fillRect(x[j], y[j], DOT_SIZE, DOT_SIZE);
                    }
                } else {
                    g.setColor(Color.BLUE);
                    g.fillRect(x[j], y[j], DOT_SIZE, DOT_SIZE);
                }
            }
            String score = "Score: " + applesEaten;
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Calibri", Font.BOLD, 40));
            FontMetrics metr = getFontMetrics(g.getFont());
            g.drawString(score, (WIDTH - metr.stringWidth(score)) / 2, g.getFont().getSize());

            String instructions1 = "Red apple: +1 to score";
            g.setColor(Color.RED);
            g.setFont(new Font("Calibri", Font.BOLD, 16));
            g.drawString(instructions1, 0, g.getFont().getSize() + 50);

            String instructions2 = "Orange apple: Slows down speed for a while";
            g.setColor(Color.ORANGE);
            g.drawString(instructions2, 0, g.getFont().getSize() + 75);

            String instructions3 = "Pink apple: Shortens length of snake by 5 dots";
            g.setColor(Color.PINK);
            g.drawString(instructions3, 0, g.getFont().getSize() + 100);
        } else {
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
        String gameOver = "Game Over!";
        Font font = new Font("Helvetica", Font.BOLD, 20);
        FontMetrics metr = getFontMetrics(font);
        g.setColor(Color.RED);
        g.setFont(font);
        g.drawString(gameOver, (WIDTH - metr.stringWidth(gameOver))/2, HEIGHT/2);

        String score = "You ate " + applesEaten + " apples!";
        Font font2 = new Font("Helvetica", Font.BOLD, 20);
        FontMetrics metr2 = getFontMetrics(font2);
        g.setColor(Color.YELLOW);
        g.setFont(font2);
        g.drawString(score, (WIDTH - metr2.stringWidth(score))/2, g.getFont().getSize());

        int highScore = pq.peek();
        String msg = "High Score: " + highScore;
        g.setColor(Color.GREEN);
        g.setFont(font2);
        g.drawString(msg, (WIDTH - metr2.stringWidth(msg))/2, g.getFont().getSize() + 50);
    }

    public void checkApple() {
        if (x[0] == xApple && y[0] == yApple) {
            this.dots++;
            this.applesEaten++;
            if (inPower) {
                this.powerLimit--;
            }
            spawnApple();
        }
    }

    public void checkPower() {
        if (!inPower) {
            if (drawPower && x[0] == powerX && y[0] == powerY) { //eats the power up and speed slows down
                inPower = true;
                drawPower = false;
                GameTimer.setDelay(DELAY + 50);
            } else if (!drawPower && tokenEaten >= 15) { //power ups only spawn after eating 15 apples
                spawnPower();
            } 
        } else {
            if (powerLimit == 0) {
                inPower = false;
                powerLimit = 2;
                GameTimer.setDelay(DELAY);
            }
        }    
    }

    public void checkShorten() {
        if (drawShorten && x[0] == shortenX && y[0] == shortenY) {
            this.dots -= 5;
            this.shortenLimit--;
            drawShorten = false;
        }
        if (!drawShorten && this.tokenEaten >= 30 && this.dots > 25 && this.shortenLimit > 0) {
            spawnShorten();
        }
    }
   
    public void move() {
        //method to move
        for (int j = dots; j > 0; j--) {
            x[j] = x[(j - 1)];
            y[j] = y[(j - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    public void checkCollision() {
        for (int j = dots; j > 0; j--) {
            //if more than 4 dots and the head touches any part of the body, dies
            if (dots > 4 && x[j] == x[0] && y[j] == y[0]) {
                inGame = false;
            }
        }

        if (y[0] >= HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }

        if (!inGame) {
            GameTimer.stop();
            pq.offer(tokenEaten);
            placeRestartButton();
        }
    }

    public void placeRestartButton() {
        JButton restartButton = new JButton("Click to restart");
        restartButton.setBounds(WIDTH/2 - 50, 3 * (HEIGHT/4), 150, 70);
        restartButton.setBackground(Color.GRAY);
        restartButton.setForeground(Color.BLUE);
        restartButton.setVisible(true);
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(() -> {
                    JFrame jframe = new Snake(pq);
                    jframe.setVisible(true);
                });
            }
        });
        setLayout(null);
        add(restartButton);
        revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            move();
            checkApple();
            checkPower();
            checkShorten();
            checkCollision();
        }
        repaint();
    }

    private class ButtonAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            //key that moves right
            if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && !leftDirection) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            //key that moves left
            if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && !rightDirection) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            //key that moves up
            if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && !downDirection) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            //key that moves down
            if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && !upDirection) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }

}

