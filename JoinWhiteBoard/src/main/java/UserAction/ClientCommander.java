package UserAction;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
@Parameters(separators = "=")
public class ClientCommander {

    @Parameter(names = {"-h", "--help"}, help = true,
            description = "To Join a concurrency whiteboard as client." +
                    "To start the window, please provide server IP address and port number")
    public boolean help;

    @Parameter(names = {"-p", "--port"},
            required = true,
            description = "The port that the client socket bind to")
    public Integer port;

    @Parameter(names = {"-a", "--address"},
            required = true,
            description = "The IP address of the server you are looking for.")
    public String hostAddress;


    public boolean isHelp() {
        return help;
    }

    @Override
    public String toString() {
        return "ClientCommander{" +
                "help =" + help +
                ", port =" + port +
                ", hostAddress ='" + hostAddress + '\'' +
                '}';
    }
}
