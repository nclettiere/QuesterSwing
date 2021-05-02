package com.valhalla.NodeEditor;

import org.yaml.snakeyaml.Yaml;

public class NodeSerializer {
    private EditorData editorData;

    public void initialize(EditorData editorData) {
        this.editorData = editorData;
    }

    public boolean serialize() {
        Yaml yaml = new Yaml();
        try {
            String output = yaml.dump(editorData);
            System.out.println(output);
            return true;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void saveToFile(String s) {
    }
}
