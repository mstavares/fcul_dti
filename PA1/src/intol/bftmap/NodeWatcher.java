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
	
/*
	private WatchService watcher; 
	private Map<WatchKey, Node> keyMap;
 
	public NodeWatcher () throws IOException{
		this.watcher = FileSystems.getDefault().newWatchService();
		keyMap = new HashMap<>();
	}

	public void registerNode (Node node){
		
		Path dir = Paths.get(node.getPath());

		try {
			
			WatchKey key = dir.register(watcher, 
					ENTRY_CREATE, 
					ENTRY_DELETE, 
					ENTRY_MODIFY);
			
			keyMap.put(key, node);
			

		} catch (IOException e){
		  System.err.println("Error creating watcher! :)");
		}
		
	}
	
	public void unregisterNode (Node node) {
		WatchKey key = null;
		try {

			Set<Entry<WatchKey, Node>> entrySet = keyMap.entrySet();
			for(Entry entry : entrySet) {
				if (entry.getValue() == node) {
					key = (WatchKey) entry.getKey();
				}
			}
			
		} finally {
			keyMap.remove(key, node);
			key.cancel();
		}
	}

	public void startLongPoll() {
		WatchKey key = null;
		Node node = null;
		Path path = null;
		do {
			try {
				key = watcher.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			node = keyMap.get(key);
			path = Paths.get(node.getPath());
			
			for(WatchEvent<?> event : key.pollEvents()) {
				System.out.println("DEBUG: added " + event.toString());
			}
		} while(key.reset());
	} 
*/
}