package WindowUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class PenSizeDialog extends JDialog {
    MainWindow mainWindow;
    JPanel rootPane = new JPanel();
    JSlider slider = new JSlider(1,30,1);

    public PenSizeDialog(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(300,200));
        this.setTitle("Pen size slider");
        this.setContentPane(rootPane);
        this.centerShow();
        this.setSlider();

        rootPane.add(slider,BorderLayout.CENTER);
        this.setVisible(true);

    }

    private void setSlider() {
        slider.addChangeListener(e ->changeSize() );
    }
    private void changeSize() {
        //System.out.println(slider.getValue());
        mainWindow.board.setPenSize(slider.getValue());
    }

    private void centerShow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);
    }
}
