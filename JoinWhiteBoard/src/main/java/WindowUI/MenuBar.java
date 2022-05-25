package WindowUI;

import Users.UserGroup;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class MenuBar extends JMenuBar{
    JMenu file = new JMenu("File");
    JMenu edit = new JMenu("Edit");
    JMenu help = new JMenu("Help");
    JMenu tools = new JMenu("Tools");
    JMenu peers = new JMenu("Peers");
    MainWindow mainWindow;


    // The constructor
    public MenuBar(UserGroup userGroup, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        //this.board = mainWindow.board;
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setFile(userGroup);
        this.add(file);
        this.setEdit();
        this.add(edit);
        this.setHelp();
        this.add(help);
        this.setTools();
        this.add(tools);
        this.setPeers();
        this.add(peers);
    }
    private void setFile(UserGroup userGroup) {
        file.setFont(new Font("Droid Sans Mono",Font.PLAIN,18));
        JMenuItem create = new JMenuItem("new");
        JMenuItem open = new JMenuItem("open file");
        JMenuItem save = new JMenuItem("save");
        JMenuItem saveAS = new JMenuItem("save as...");
        JMenuItem close = new JMenuItem("close");

        // add corresponding action listener to each button
        create.addActionListener(e -> creatOnClick());
        save.addActionListener(e -> saveOnClick());
        saveAS.addActionListener(e -> saveAsOnClick());
        // pack buttons to menu
        file.add(create);
        file.addSeparator();
        file.add(open);
        file.addSeparator();
        file.add(save);
        file.addSeparator();
        file.add(saveAS);
        file.addSeparator();
        file.add(close);
        if (userGroup == UserGroup.USER) {
            file.setEnabled(false);
        }
        //button.setBounds(x,y,menuButtonWidth,menuButtonHeight);
        //button.addActionListener(e -> exploreOnClick());
    }
    private void setEdit() {
        edit.setFont(new Font("Droid Sans Mono",Font.PLAIN,18));
        JMenuItem undo = new JMenuItem("undo");
        JMenu pen = new JMenu("Pen");
        JMenuItem size = new JMenuItem("pen size");
        JMenuItem color = new JMenuItem("pen color");
        JMenuItem clear = new JMenuItem("clear screen");

        undo.addActionListener(e -> undoAction());
        clear.addActionListener(e -> clearOnClick());
        size.addActionListener(e -> changeSize());
        color.addActionListener(e -> changeColor());

        pen.add(size);
        pen.add(color);
        edit.add(pen);
        edit.addSeparator();
        edit.add(undo);
        edit.addSeparator();
        edit.add(clear);

    }
    private void setHelp() {
        help.setFont(new Font("Droid Sans Mono",Font.PLAIN,18));
        JMenuItem guide = new JMenuItem("user guide");
        help.add(guide);

    }
    private void setTools() {
        tools.setFont(new Font("Droid Sans Mono",Font.PLAIN,18));
        JMenuItem hide = new JMenuItem("show/hide chat box");
        tools.add(hide);
    }
    private void setPeers() {
        peers.setFont(new Font("Droid Sans Mono",Font.PLAIN,18));
        JMenuItem showPeers = new JMenuItem("Show current online peers");
        peers.add(showPeers);
    }

    public void changeSize() {
        PenSizeDialog penSizeDialog = new PenSizeDialog(mainWindow);
    }
    public void changeColor() {
        Color userSelectedColor = JColorChooser.showDialog(mainWindow,"please choose your color",Color.black);
        mainWindow.board.setPenColor(userSelectedColor);
    }

    public void creatOnClick() {

        // for administrator to create a new board
        if (mainWindow.state == BoardState.NO_PAINT) {
            mainWindow.remove(mainWindow.display);
            mainWindow.add(mainWindow.board,BorderLayout.CENTER);
            mainWindow.state = BoardState.WHITE_PAINT;
            String result = JOptionPane.showInputDialog(mainWindow,"Please name your file.");
            if (result == null) {
                mainWindow.setTitle("untitled - Draw Board");
            }
            else {
                mainWindow.setTitle(result + " - Draw Board");
            }

            SwingUtilities.updateComponentTreeUI(mainWindow);
        }
        // if the board is created but no paint on it.
        else if (mainWindow.state == BoardState.WHITE_PAINT) {
            JOptionPane.showMessageDialog(mainWindow,"Already have board");
        }
        // for painted board
        else if (mainWindow.state == BoardState.HAS_PAINT) {
            int value = JOptionPane.showConfirmDialog(mainWindow,
                    "Do you want to save current painting?",
                    "Current painting not saved!", 0);
            if(value == JOptionPane.YES_OPTION){
                System.out.println("save");
                saveFile(true);
            }
            if(value == JOptionPane.NO_OPTION){
                mainWindow.board.paintListener.getPath().clear();
                mainWindow.board.repaint();
                mainWindow.state = BoardState.WHITE_PAINT;
            }
        }

    }
    public void clearOnClick() {
        mainWindow.board.removeAll();
        mainWindow.board.paintListener.getPath().clear();
        mainWindow.board.repaint();
    }
    public void saveOnClick() {
        if (mainWindow.state == BoardState.HAS_PAINT) {
            saveFile(true);
        }
        else {
            JOptionPane.showMessageDialog(mainWindow,"No paint to save.");
        }

    }
    public void saveAsOnClick() {
        if (mainWindow.state == BoardState.HAS_PAINT) {
            saveFile(false);
        }
        else {
            JOptionPane.showMessageDialog(mainWindow,"No paint to save.");
        }

    }
    private void undoAction() {
        mainWindow.board.removeAll();
        int index = mainWindow.board.paintListener.getPath().size() - 1;
        mainWindow.board.paintListener.getPath().remove(index);
        mainWindow.board.repaint();
    }

    /**
     * @param autoSave true if button [save] clicked, then the file will save to default folder.
     *                 false if button [save as] clicked, user choose where to save.
     *                 this method output points collection as java serialized object.
     */
    public void saveFile(Boolean autoSave){
        //choose file location
        File file;
        if (autoSave) {
           file = new File(mainWindow.getTitle());
        }
        else {
            JFileChooser chooser = new JFileChooser();
            chooser.showSaveDialog(mainWindow);
            file = chooser.getSelectedFile();

        }

        if(file == null) {
            JOptionPane.showMessageDialog(mainWindow, "You have not choose a file");
        }
        else {

            try {
                // serialize object
                FileOutputStream fis = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fis);
                // write out data to given file
                System.out.println(file.getPath());
                oos.writeObject(mainWindow.board.paintListener.getPath());
                JOptionPane.showMessageDialog(mainWindow, "Saved Successful");
                oos.close();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(mainWindow, "Something wrong!\nPlease try again");
            }
        }
    }
}
