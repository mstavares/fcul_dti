package intol.bftmap;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;

import java.time.Instant;

public class Node implements Serializable {

    private static final int FIRST_SEQ = 0;
    private TreeMap<String, Node> nodes = new TreeMap<>();
    private String value;
    private String path;

    //START ephemeral implementation
    private static final int HEARTBEAT = 5000;
    private TreeMap<String, String> ephemeralNodes = new TreeMap<>();
    //END ephemeral implementation

    public Node() {}

    public Node(String path) {
        this.path = path;
    }

    public Node(String path, String value) {
        this.path = path;
        this.value = value;
    }

    public TreeMap<String, Node> getNodes() {
        return nodes;
    }

    public String getPath(){
    	return path;
    }

    //START ephemeral implementation
    public TreeMap<String, String> getEphemeralNodes() {
        return ephemeralNodes;
    }

    public static int getHeartbeat(){
        return HEARTBEAT;
    }

    public String setNodeEphemeral(String path, String timestamp) {
        String[] folders = path.split("/");
        for(int i = 0; i < folders.length; i++) {
            String key = folders[i];
            ephemeralNodes.put(key, timestamp);
        }

        return path + "was marked as ephemeral";
            
    }

    public boolean checkEphemeralAlive(String path){
        boolean alive = false;

        if(ephemeralNodes.containsKey(path)){
            String ephemeralTimestamp = ephemeralNodes.get(path);

            Instant instant = Instant.now();

            if ((instant.toEpochMilli() - Long.parseLong(ephemeralTimestamp)) < HEARTBEAT){
                alive = true;
            } else {
                alive = false;

                ephemeralNodes.remove(path);
                deleteNode(path);
            }
        }

        return alive;
    }
    //END ephemeral implementation

    public String addNodeSequential(String path, String value) {
        for(int i = FIRST_SEQ; ; i++) {
            String sequentialPath = path + i;
            Node node = getNode(sequentialPath);
            if(node == null) {
                addNode(sequentialPath, value);
                return sequentialPath;
            }
        }
    }

    public String addNode(String path, String value) {
        Node node = null;
        String[] folders = path.split("/");
        for(int i = 0; i < folders.length; i++) {
            String key = folders[i] + "/";

            if(ephemeralNodes.containsKey(folders[i])){
                return "can't add node. marked as ephemeral";
            }

            if(node == null) {
                if(!nodes.containsKey(key)) {
                    nodes.put(key, new Node(key, value));
                    node = nodes.get(key);
                    if(i == folders.length - 1) {
                        node.value = value;
                    }
                    continue;
                } else {
                    node = nodes.get(key);
                    if(i == folders.length - 1) {
                        if (node.path.equals(key)) {
                            node.value = value;
                            break;
                        }
                    }
                }
            }
            if(i == folders.length - 1) {
                if (!node.nodes.containsKey(key)) {
                    node.nodes.put(key, new Node(key, value));
                } else {
                    node.nodes.get(key).value = value;
                }
            } else {
                if (!nodes.containsKey(key)) {
                    nodes.put(key, new Node(key, value));
                }
                node = nodes.get(key);
            }
        }

        return "key-value pair added to the map";
    }

    public Node getNode(String path) {
        Node node = null;
        String[] folders = path.split("/");
        if(folders.length == 0) {
            return this;
        }
        for (String folder : folders) {
            if(node == null) {
                node = nodes.get(folder + "/");
            } else {
                node = node.nodes.get(folder + "/");
            }
        }
        if(node != null) {
            return node;
        } else {
            if(nodes.containsKey(path)) {
                return nodes.get(path);
            }
        }
        return null;
    }

    public Set getChildren(String path) {
        Node node = getNode(path);
        return node.nodes.keySet();
    }

    public String deleteNode(String path) {
        Node node = getNode(path);
        Node motherNode = getMotherNode(path);
        if(node.nodes.isEmpty()) {
            if(motherNode != null) {
                motherNode.nodes.remove(node.path);
            } else {
                nodes.remove(node.path);
            }
            return path + " was removed";
        }
        return path + " was not removed, probably has children";
    }

    private Node getMotherNode(String path) {
        StringBuilder motherPath = new StringBuilder();
        String[] pathParts = path.split("/");
        for(int i = 0; i < pathParts.length - 1; i++) {
            motherPath.append(pathParts[i]);
        }
        return getNode(motherPath.toString());
    }

    public String getValue(String path) {
        if(ephemeralNodes.containsKey(path)){
            if(!checkEphemeralAlive(path)){
                return "node no longer alive, deleting...";
            }
        }

        Node node = getNode(path);
        return node != null ? node.value : "null";
    }
}
