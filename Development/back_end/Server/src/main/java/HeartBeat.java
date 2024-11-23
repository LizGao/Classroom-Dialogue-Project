/**
 * HeartBeat object that is sent to System.out
 * Usage:
 * System.out.println(new HeartBeat(Server.this).toString());
 */

public class HeartBeat {

    /**
     * Stage Code:
     * 0 = [ZOMBIE] Terminated, process waiting to be killed;
     * 1 = [Healthy] Normally Operating;
     * 2 = [Terminating] Terminating by kernel request;
     * 3 = [Terminating] Terminating by error;
     */

    Server server;
    int heartBeatSeq;           // heartbeat sequence number
    int stage;
    int numOfClients;
    int numOfNewClients;
    String timeStamp;
    String name;
    String ServerID;

    public HeartBeat(Server server, int heartBeatSeq, int numOfClients,
                     int numOfNewClients, String timeStamp, String ServerID) {
        this.server = server;
        this.heartBeatSeq = heartBeatSeq;
        this.stage = this.server.stage;
        this.numOfClients = numOfClients;
        this.numOfNewClients = numOfNewClients;
        this.timeStamp = timeStamp;
        this.name = this.server.name;
        this.ServerID = ServerID;
    }

    @Override
    public String toString() {
        // Map stage codes to descriptions
        String stageDescription;
        switch (stage) {
            case 0:
                stageDescription = "[ZOMBIE] Terminated";
                break;
            case 1:
                stageDescription = "[Healthy] Normally Operating";
                break;
            case 2:
                stageDescription = "[Terminating] By Kernel Request";
                break;
            case 3:
                stageDescription = "[Terminating] By Error";
                break;
            default:
                stageDescription = "[Unknown]";
        }

        return String.format(
                "HeartBeat { \n" +
                        "  >>> %d @%s\n" +
                        "  >>> %s\n" +
                        "  >>> %s\n" +
                        "  >>> Stage %d (%s)\n" +
                        "  >>> Number of Clients: %d\n" +
                        "  >>> Number of New Clients: %d\n" +
                        "}",
                heartBeatSeq,
                timeStamp,
                server != null ? server.toString() : "N/A",
                this.server.name,
                stage, stageDescription,
                numOfClients,
                numOfNewClients
        );

    }

    public static void main(String[] args) {
        HeartBeat heartBeat = new HeartBeat(new Server("Test_Servr"), 1, 100, 20,
                "2024/11/23-2:43:08", "a7c800f1");
        System.out.println(heartBeat.toString());
    }

}
