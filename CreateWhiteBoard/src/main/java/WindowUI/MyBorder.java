package WindowUI;

import javax.swing.*;
import java.awt.*;

/**
 * Define the style of my border used in this program
 */
public interface MyBorder {
    int thickness = 3;
    Color borderColor = Color.BLACK;
    Color fontColor = Color.DARK_GRAY;
    void creatPaneBorder(JComponent component, String title);
}
