package com.valhalla.NodeEditor.Primitive;

import com.valhalla.application.gui.Utils;
import com.valhalla.NodeEditor.Sockets.ISocketData;
import com.valhalla.NodeEditor.Sockets.SocketData;
import com.valhalla.NodeEditor.PropertyBase;

import java.awt.*;
import java.io.File;
import java.util.*;

public class ImageData extends SocketData {

    public ImageData(PropertyBase parentProperty) {
        super(parentProperty);
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
    public boolean isDataBindAvailable(ISocketData nodeData) {
        return !(bindingMap.size() > 0);
    }

    @Override
    public boolean evaluate() {
        Map.Entry<Boolean, String> state;
        String dataFile = (String) data;
            if (data != null) {
                if (!dataFile.isEmpty() && !dataFile.isBlank()) {
                    File file = new File(dataFile);
                    if (file.isFile() && file.exists() && file.canRead()) {
                        if (Utils.isImage(dataFile))
                            state = null;
                        else
                            state = new AbstractMap.SimpleEntry<>(true, "The file is not an image.");
                    }
                    else
                        state = new AbstractMap.SimpleEntry<>(false, "The file does not exist or cannot be read.");
                } else {
                    state = new AbstractMap.SimpleEntry<>(false, "File path is null or empty.");
                }
            } else {
                state = new AbstractMap.SimpleEntry<>(false, "File path is null.");
            }

        FireOnEvaluationStateChanged(state);
        if(state == null)
            return true;
        else
            return state.getKey();
    }
}
