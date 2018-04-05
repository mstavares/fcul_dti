/**
 * BFT Map implementation (server side).
 *
 */
package intol.bftmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

public class BFTMapServer<K, V> extends DefaultSingleRecoverable {

    Node root = new Node();
    ServiceReplica replica = null;

    //The constructor passes the id of the server to the super class
    public BFTMapServer(int id) {
        root.addNode("/", null);
        replica = new ServiceReplica(id, this, this);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Use: java BFTMapServer <server id>");
            System.exit(-1);
        }
        new BFTMapServer<Integer, String>(Integer.parseInt(args[0]));
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        try {

            ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

            byte[] reply = new byte[0];
            BFTMapRequestType cmd = (BFTMapRequestType) objIn.readObject();
            
            System.out.println("Ordered execution of a "+cmd+" from "+msgCtx.getSender());

            switch (cmd) {
                //write operations on the map
                case PUT: {
                    K key = (K) objIn.readObject();
                    V value = (V) objIn.readObject();
                    root.addNode(key.toString(), value.toString());
                    V ret = value;
                    if (ret != null) {
                        objOut.writeObject(ret);
                        reply = byteOut.toByteArray();
                    }
                    break;
                }
                case SEQUENTIAL: {

                    /*
                    K key = (K) objIn.readObject();
                    V value = (V) objIn.readObject();

                    V ret = root.put(key, value);

                    if (ret != null) {
                        objOut.writeObject(ret);
                        reply = byteOut.toByteArray();
                    }
                    */
                    break;
                }
            }

            objOut.flush();
            byteOut.flush();

            byteIn.close();
            objIn.close();
            objOut.close();
            byteOut.close();

            return reply;
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(BFTMapServer.class.getName()).log(Level.SEVERE, null, ex);
            return new byte[0];
        }
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

            byte[] reply = new byte[0];
            BFTMapRequestType cmd = (BFTMapRequestType) objIn.readObject();

            System.out.println("Ordered execution of a "+cmd+" from "+msgCtx.getSender());
            
            switch (cmd) {
                case GET: {
                    K key = (K) objIn.readObject();
                    V ret =  (V) root.getValue(key.toString());
                    if (ret != null) {
                        objOut.writeObject(ret);
                        reply = byteOut.toByteArray();
                    }
                    break;
                }
                case CHILDREN: {
                    K key = (K) objIn.readObject();
                    HashSet<K> keyset = new HashSet<>(root.getChildren(key.toString()));objOut.writeObject(keyset);
                    objOut.writeObject(keyset);
                    reply = byteOut.toByteArray();
                    break;
                }
                case REMOVE: {
                    K key = (K) objIn.readObject();
                    V ret = (V) root.deleteNode(key.toString());
                    if (ret != null) {
                        objOut.writeObject(ret);
                        reply = byteOut.toByteArray();
                    }
                    break;
                }
            }

            objOut.flush();
            byteOut.flush();

            byteIn.close();
            objIn.close();
            objOut.close();
            byteOut.close();

            return reply;
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(BFTMapServer.class.getName()).log(Level.SEVERE, null, ex);
            return new byte[0];
        }
    }

    @Override
    public byte[] getSnapshot() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(root);
            out.flush();
            bos.flush();
            out.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace(); //debug instruction
            return new byte[0];
        }
    }

    @Override
    public void installSnapshot(byte[] state) {
        try {
            // serialize to byte array and return
            ByteArrayInputStream bis = new ByteArrayInputStream(state);
            ObjectInput in = new ObjectInputStream(bis);
            root = (Node) in.readObject();
            in.close();
            bis.close();
        } catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace(); //debug instruction
        }
    }

}
