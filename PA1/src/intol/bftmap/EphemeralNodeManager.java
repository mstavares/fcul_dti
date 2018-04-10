package intol.bftmap;

import java.util.*;

public class EphemeralNodeManager {

    private static final int EPHEMERAL_RENEW = 1000;
    private List<String> ephemerals = new ArrayList();
    private BFTMap<String, String> bftMap;
    private Timer timer;


    public EphemeralNodeManager(BFTMap<String, String> bftMap) {
        this.bftMap = bftMap;
        runRepeatly();
    }

    public synchronized void addEphemeral(String ephemeral) {
        ephemerals.add(ephemeral);
    }

    private void runRepeatly() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new Task(), EPHEMERAL_RENEW, EPHEMERAL_RENEW);
    }

    private class Task extends TimerTask {

        @Override
        public void run() {
            Iterator it = ephemerals.iterator();
            while (it.hasNext()) {
                String ephemeralNode = (String) it.next();
                bftMap.setEphemeral(ephemeralNode);
            }
        }
    }

}
