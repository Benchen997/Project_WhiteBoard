package WindowUI;

import javax.swing.*;
import java.awt.*;

public interface MyBorder {
    int thickness = 3;
    Color borderColor = Color.BLACK;
    Color fontColor = Color.DARK_GRAY;
    void creatPaneBorder(JComponent component, String title);
}
