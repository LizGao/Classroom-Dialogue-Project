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
    int stage;
    int numOfClients;
    int numOfNewClients;
    String timeStamp;
    String name;
    String ServerID;



    public HeartBeat(Server server, String timeStamp) {
        this.server = server;
        this.stage = this.server.stage;
        this.numOfClients = server.clients.size();
        this.numOfNewClients = this.server.numNewClients;
        this.timeStamp = timeStamp;
        this.name = this.server.name;
        this.ServerID = this.server.ID;
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
                "<HeartBeat> { \n" +
                        "  >>> @%s\n" +
                        "  >>> %s\n" +
                        "  >>> %s\n" +
                        "  >>> Stage %d (%s)\n" +
                        "  >>> Number of Clients: %d\n" +
                        "  >>> Number of New Clients: %d\n" +
                        "}",
                timeStamp,
                server != null ? server.toString() : "N/A",
                this.server.name,
                stage, stageDescription,
                numOfClients,
                numOfNewClients
        );

    }

    /**
     * Sends HeartBeat to System.out
     */
    public void sendHeartBest() {
        System.out.println(this.toString());
        return;
    }

    public static void main(String[] args) {
        HeartBeat heartBeat = new HeartBeat(new Server("Test_Servr", "ID_RandomeID"),
                "2024/11/23-2:43:08");
        System.out.println(heartBeat.toString());
    }

}
