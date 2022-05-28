import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class test {
    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getLocalHost();
            System.out.println(address.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
