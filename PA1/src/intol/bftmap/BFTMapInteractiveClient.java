/**
 * BFT Map implementation (interactive client).
 *
 */
package intol.bftmap;

import java.io.IOException;
<<<<<<< HEAD
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class BFTMapInteractiveClient extends Thread {
	
	private static NodeWatcher watcher;
	
	public void run() {
		System.out.println("Starting notification thread...");
		while(true) {
			System.out.println("Going to check for updates");
			watcher.checkForUpdates();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

    public static void main(String[] args) throws IOException {

    	
        int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
        BFTMap<String, String> bftMap = new BFTMap<>(clientId);
        watcher = new NodeWatcher(bftMap);
        (new BFTMapInteractiveClient()).start();

=======
import java.util.InputMismatchException;
import java.util.Scanner;

import java.io.Console;
import java.util.Set;

public class BFTMapInteractiveClient {

    public static void main(String[] args) throws IOException {

        int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
        BFTMap<String, String> bftMap = new BFTMap<>(clientId);

        //Console console = System.console();
>>>>>>> fddb2451ceefcf4c7e7a6830270f4cfc51e22b08
        Scanner sc = new Scanner(System.in);

        System.out.println("\nCommands:\n");
        System.out.println("\tPUT: Insert value into the map");
        System.out.println("\tSEQUENCIAL: Insert value into the map");
        System.out.println("\tGET: Retrieve value from the map");
        System.out.println("\tCHILDREN: Retrieve the size of the map");
        System.out.println("\tREMOVE: Removes the value associated with the supplied key");
<<<<<<< HEAD
        System.out.println("\tWATCH: Create Watcher for a value");
=======
>>>>>>> fddb2451ceefcf4c7e7a6830270f4cfc51e22b08
        System.out.println("\tEXIT: Terminate this client\n");

        while (true) {
            System.out.print("\n  > ");
            String cmd = sc.nextLine();
            String key;

            if (cmd.equalsIgnoreCase("PUT")) {

                //String key;
                try {
                    System.out.print("Enter a node name: ");
                    key = sc.nextLine();
                    //sc.nextLine();
                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }
                System.out.print("Enter an alpha-numeric value: ");
                String value = sc.nextLine();

                //invokes the op on the servers
<<<<<<< HEAD
              
=======
>>>>>>> fddb2451ceefcf4c7e7a6830270f4cfc51e22b08
                bftMap.put(key, value);

                System.out.println("\nkey-value pair added to the map\n");
            } else if (cmd.equalsIgnoreCase("GET")) {

                //int key = 0;
                try {
                    System.out.print("Enter a node name: ");
                    key = sc.nextLine();
                    //key = sc.nextInt();
                    //sc.nextLine();

                } catch (NumberFormatException | InputMismatchException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }

                //invokes the op on the servers
                String value = bftMap.get(key);

                System.out.println("\nValue associated with " + key + ": " + value + "\n");

            } else if (cmd.equalsIgnoreCase("SEQUENTIAL")) {

                //String key;
                try {
                    System.out.print("Enter a node name: ");
                    key = sc.nextLine();
                    //sc.nextLine();
                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }
                System.out.print("Enter an alpha-numeric value: ");
                String value = sc.nextLine();

                //invokes the op on the servers
                String name = bftMap.putSequential(key, value);
                System.out.print("A node with name " + name + " was created");

            } else if (cmd.equalsIgnoreCase("CHILDREN")) {

                try {
                    System.out.print("Enter a node name: ");
                    key = sc.nextLine();

                } catch (NumberFormatException | InputMismatchException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }

                Set keySet = bftMap.getChildren(key);
                System.out.println("\n" + key + " content: " + keySet );

            } else if (cmd.equalsIgnoreCase("KEYSET")) {

                Set keySet = bftMap.keySet();

                System.out.println("\nKeyset content: " + keySet );

            } else if (cmd.equalsIgnoreCase("REMOVE")) {

                //int key = 0;
                try {
                    System.out.print("Enter a node name key: ");
                    key = sc.nextLine();
                    //key = sc.nextInt();
                    //sc.nextLine();

                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }

                //invokes the op on the servers
                String value = bftMap.remove(key);

                System.out.println("\nValue associated with " + value + " was removed\n");

<<<<<<< HEAD
            } else if (cmd.equalsIgnoreCase("WATCH")) {
            	 System.out.print("Enter a node name key: ");
            	 key = sc.nextLine();
            	 boolean result = watcher.registerNode(key);
            	 if (result) {
            		 System.out.println("\nCreated watcher for node with key " + key);
            	 } else {
            		 System.out.println("\nWatcher was not created. The node with key " + key + " does not exist.");
            	 }
            	
            	
            } else if (cmd.equalsIgnoreCase("SIZE")) {
            
=======
            } else if (cmd.equalsIgnoreCase("SIZE")) {
>>>>>>> fddb2451ceefcf4c7e7a6830270f4cfc51e22b08

				System.out.println("Size is: " + bftMap.size());

            } else if (cmd.equalsIgnoreCase("EXIT")) {

                System.out.println("\tEXIT: Bye bye!\n");
                System.exit(0);

            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }
    }

}
