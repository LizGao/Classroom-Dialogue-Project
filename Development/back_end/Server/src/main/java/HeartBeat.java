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
    String ID;

    public String toString() {
        String result = "";

        return result;
    }

}
