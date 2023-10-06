
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.PriorityQueue;

import javax.imageio.*;
import javax.swing.*;

public class Snake extends JFrame {
    private PriorityQueue<Integer> pq;

    public Snake() {
        this.pq = new PriorityQueue<>((x, y) -> y - x);
        initUI();
    }

    public Snake(PriorityQueue<Integer> pq) {
        this.pq = pq;
        initUI();
    }

    public void initUI() {
        add(new GameBoard(pq));
        setResizable(true);
        pack();
        setTitle("Snake :)");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main (String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame jframe = new Snake();
            jframe.setVisible(true);
        });
    }
}
