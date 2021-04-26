package com.valhalla.NodeEditor;

import com.valhalla.application.gui.Utils;

import java.awt.*;
import java.io.File;

//public class ImageSocket extends NodeSocket<String> {
//    public ImageSocket(SocketDirection direction) {
//        super("", direction);
//        setSocketColor(new Color(144, 50, 50));
//    }
//
//    @Override
//    public boolean evaluate() {
//        SocketState state = new SocketState(this.props.getDirection());
//        String dataFile = props.getData();
//
//        boolean evaluationPassed = true;
//
//        if (dataFile != null) {
//            if (!dataFile.isEmpty() && !dataFile.isBlank()) {
//                File file = new File(dataFile);
//                if (file.isFile() && file.exists() && file.canRead()) {
//                    if (Utils.isImage(dataFile))
//                        state.setErrorLevel(StateErrorLevel.PASSING);
//                    else {
//                        state.setErrorLevel(StateErrorLevel.ERROR);
//                        state.setStateMessage("The file is not an image.");
//                        evaluationPassed = false;
//                    }
//                } else {
//                    state.setErrorLevel(StateErrorLevel.WARNING);
//                    state.setStateMessage("The file does not exist or cannot be read.");
//                    evaluationPassed = false;
//                }
//            }else {
//                state.setErrorLevel(StateErrorLevel.WARNING);
//                state.setStateMessage("File path is null or empty.");
//                evaluationPassed = false;
//            }
//        } else {
//            state.setErrorLevel(StateErrorLevel.WARNING);
//            state.setStateMessage("File path is null.");
//            evaluationPassed = false;
//        }
//        this.props.setState(state);
//
//        return evaluationPassed;
//    }
//
//    @Override
//    public boolean isDataBindAvailable() {
//        return !(props.getBindingCount() > 0);
//    }
//}
//