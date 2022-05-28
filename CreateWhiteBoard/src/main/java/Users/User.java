package Users;

import java.io.*;
import java.net.Socket;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class User {
    private final String username;
    private final Socket socket;

    private final int uid;
    public DataInputStream dataInputStream;
    public DataOutputStream dataOutputStream;
    public ObjectOutputStream objectOutputStream;

    public User(String username, Socket socket, int uid) {
        this.username = username;
        this.socket = socket;

        this.uid = uid;
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            dataInputStream = new DataInputStream(inputStream);
            dataOutputStream = new DataOutputStream(outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setObjectOutputStream() {
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setDataOutputStream() {
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public int getUid() {
        return uid;
    }


}
