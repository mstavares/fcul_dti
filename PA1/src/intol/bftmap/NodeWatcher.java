package intol.bftmap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NodeWatcher {
	
	private Map<String, String> watchedNodes;
	private BFTMap<String, String> bftMap;
	private int size;
	
	
	public NodeWatcher(BFTMap<String, String> bftMap) {
		this.watchedNodes = new HashMap<>();
		this.bftMap = bftMap;
		this.size = bftMap.size();
	}
	
	public boolean registerNode(String key) {
		String currentValue = bftMap.get(key);
		if (currentValue != null) {
			System.out.println("Node " + key + " was created");
			watchedNodes.put(key, currentValue);
			return true;
		}
		
		return false;
	}
	
	public void unregisterNode(String key) {
		watchedNodes.remove(key);
	}
	
	public void checkForUpdates() {
		Iterator it = watchedNodes.entrySet().iterator();
		String key;
		String value;
		String newValue;
		int newSize = bftMap.size();
		if (newSize > size) {
			int diff = newSize - size;
			this.size = newSize;
			
			System.out.println(diff + " nodes were created.");
		}
		
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			key = (String) pair.getKey();
			value = (String) pair.getValue();
			newValue = bftMap.get(key);
			if (newValue.equals("null")) {
				System.out.println("Node " + key + " was removed");
				unregisterNode(key);
			} else if (!newValue.equals(value)) {
				watchedNodes.put(key, newValue);
				System.out.println("Node " + key + " was modified");
			}
		}
	}
	
}
