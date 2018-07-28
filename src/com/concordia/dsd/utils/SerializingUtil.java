package com.concordia.dsd.utils;

import com.concordia.dsd.server.generics.FIFORequestQueueModel;

import java.io.*;

public class SerializingUtil {
    private static SerializingUtil ourInstance = new SerializingUtil();

    public static SerializingUtil getInstance() {
        return ourInstance;
    }

    private SerializingUtil() {
    }
    public byte[] getSerializedObject(Object reqObj){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(reqObj);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }
    public byte[] getSerializedFIFOObject(FIFORequestQueueModel reqObj){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(reqObj);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }

    public FIFORequestQueueModel getFIFOObjectFromSerialized(byte[] serializedObj){
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedObj);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (FIFORequestQueueModel)in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }
    public Object getObjectFromSerialized(byte[] serializedObj){
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedObj);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (FIFORequestQueueModel)in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }
}
