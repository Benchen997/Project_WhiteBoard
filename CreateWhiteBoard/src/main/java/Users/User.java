package Users;

import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class User {
    private String username;
    private UserGroup userGroup;
    private Socket socket;
    private int uid;
    public DataInputStream dataInputStream;
    public DataOutputStream dataOutputStream;
    private JSONObject message;

    public User(String username, UserGroup userGroup, Socket socket, int uid) {
        this.username = username;
        this.userGroup = userGroup;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public JSONObject getMessage() {
        return message;
    }

    public void setMessage(JSONObject message) {
        this.message = message;
    }


}
