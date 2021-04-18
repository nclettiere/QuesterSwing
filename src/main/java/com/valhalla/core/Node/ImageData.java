package com.valhalla.core.Node;

import javax.swing.event.EventListenerList;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        if(data == null) return false;
        String dataFile = (String) data;
        if(dataFile.isEmpty() || dataFile.isBlank()) return false;
        File file = new File(dataFile);
        return file.isFile() && file.exists() && file.canRead();
    }
}
