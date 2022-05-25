import javax.swing.*;
import java.awt.*;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class Test01 {
    public void init() {
        JFrame jFrame = new JFrame("test");
        JPanel jPanel = new JPanel();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(800,600);
        jFrame.setContentPane(jPanel);
        Graphics pen = jPanel.getGraphics();
        //Graphics2D g2d = (Graphics2D) pen;
        pen.setColor(Color.black);
        pen.drawLine(0,0,600,400);
        jFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Test01 test01 = new Test01();
        test01.init();
    }
}
