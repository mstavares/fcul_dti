package intol.bftmap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class Node implements Serializable {

    private TreeMap<String, Node> nodes = new TreeMap<>();
    private String value;
    private String path;
    private int counter;

    public Node() {

    }

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

    public void addNode(String path, String value) {
        Node node = null;
        String[] folders = path.split("/");
        for(int i = 0; i < folders.length; i++) {
            String key = folders[i] + "/";
            if(node == null) {
                if(!nodes.containsKey(key)) {
                    nodes.put(key, new Node(key));
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
                    nodes.put(key, new Node(key));
                }
                node = nodes.get(key);
            }
        }
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

    public Boolean deleteNode(String path) {
        Node node = getNode(path);
        Node motherNode = getMotherNode(path);
        if(node.nodes.isEmpty()) {
            motherNode.nodes.remove(node.path);
            return true;
        }
        return false;
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
        Node node = getNode(path);
        return node != null ? node.value : "null";
    }

}
