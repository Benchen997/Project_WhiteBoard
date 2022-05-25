package WindowUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class Display extends JPanel {

    public Display() {
        this.setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.white);
        g.setFont(new Font("Droid Sans Mono",Font.BOLD,25));

        g.drawString("If you are users:",this.getWidth()/3,this.getHeight()/3);
        g.drawString("Please wait for administrator to open a board",this.getWidth()/3,this.getHeight()/3+26);
        g.drawString("If you are administrator:",this.getWidth()/3,this.getHeight()/3+26*2);
        g.drawString("Go to File to creat new board or open an existing board",
                this.getWidth()/3,this.getHeight()/3+26*3);
    }
}
