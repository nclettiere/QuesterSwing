package com.valhalla.NodeEditor;

import org.yaml.snakeyaml.Yaml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class NodeStateSerializer {
    private EditorData editorData;

    public void initialize(EditorData editorData) {
        this.editorData = editorData;
    }

    public boolean serialize() {
        //Yaml yaml = new Yaml();
        //try {
        //    String output = yaml.dump(editorData);
        //    System.out.println(output);
        //    return true;
        //}catch (Exception e) {
        //    System.out.println(e.getMessage());
        //    return false;
        //}

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
}
