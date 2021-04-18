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
    public void SetBinding(INodeData nData) {
        if(nData.GetUUID() != this.GetUUID()) {
            if (isDataBindAvailable()) {
                nData.AddOnBindingEventListener(new BindingEventListener() {
                    @Override
                    public void OnBindingDataChanged(Object data) {
                        SetData(nData.GetData());
                    }

                    @Override
                    public void OnBindingReleased() {

                    }

                    @Override
                    public void onDataEvaluationChanged(Map.Entry<Boolean, String> evaluationState) {

                    }
                });
            }
        }
        super.SetBinding(nData);
    }

    @Override
    public boolean isDataBindAvailable() {
        return !(bindingMap.size() > 1);
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
                        state = new AbstractMap.SimpleEntry<>(true, "Passing.");
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
        return state.getKey();
    }
}
