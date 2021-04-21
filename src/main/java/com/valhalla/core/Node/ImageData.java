package com.valhalla.core.Node;

import javax.swing.event.EventListenerList;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ImageData extends NodeDataBase {

    public ImageData() {
        super();
        SetName("Image");
        SetDisplayName("Image");
        SetData("");
        SetDataColor(new Color(200, 75, 30));
    }

    @Override
    public void SetMultipleBindingAllowed(boolean allowed) {
        super.SetMultipleBindingAllowed(false);
    }

    @Override
    public boolean isDataBindAvailable(INodeData nodeData) {
        return !(bindingMap.size() > 0);
    }

    @Override
    public boolean evaluate() {
        Map.Entry<Boolean, String> state;
        String dataFile = (String) data;
        if(mode == ConnectorMode.INPUT) {
            if (data != null) {
                if (!dataFile.isEmpty() && !dataFile.isBlank()) {
                    File file = new File(dataFile);
                    if (file.isFile() && file.exists() && file.canRead())
                        state = null;
                    else
                        state = new AbstractMap.SimpleEntry<>(false, "The file does not exist or cannot be read.");
                } else {
                    state = new AbstractMap.SimpleEntry<>(false, "File path is null or empty.");
                }
            } else {
                state = new AbstractMap.SimpleEntry<>(false, "File path is null.");
            }
        }else {
            state = new AbstractMap.SimpleEntry<>(true, "Passing.");
        }

        FireOnEvaluationStateChanged(state);
        if(state == null)
            return true;
        else
            return state.getKey();
    }
}
