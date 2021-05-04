package com.valhalla.NodeEditor;

import org.yaml.snakeyaml.Yaml;

import java.io.*;

public class NodeStateSerializer {
    public boolean serialize(EditorData editorData) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("C:\\tmp\\editorData.ed");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(editorData);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in C:\\tmp\\editorData.ed");
            return true;
        } catch (IOException i) {
            i.printStackTrace();
            return false;
        }
    }

    public EditorData deserialize() {
        EditorData eData = null;
        try {
            FileInputStream fileIn = new FileInputStream("C:\\tmp\\editorData.ed");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            eData = (EditorData) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("EditorData class not found");
            c.printStackTrace();
        }

        return eData;
    }
}
