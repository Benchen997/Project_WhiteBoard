package WebFunction;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */

@Parameters(separators = "=")
public class ServerCommander {
    @Parameter(names = {"-h", "--help"}, help = true,
            description = "To creat a concurrency whiteboard with administrator identification." +
                    "To start the server, please provide port number or by default.")
    public boolean help;

    @Parameter(names = {"-p", "--port"},
            description = "The port that the server bind")
    public Integer port = 8888;


    public boolean isHelp() {
        return help;
    }

    @Override
    public String toString() {
        return "ServerCommander{" +
                "help = " + help +
                ", port = " + port +
                '}';
    }
}
