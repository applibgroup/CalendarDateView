package com.yyx.library.utils;

import ohos.agp.utils.Color;
import ohos.app.Context;
import ohos.global.resource.NotExistException;
import ohos.global.resource.ResourceManager;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;

public class ResUtil {
    public static Color getColor(Context context, int id) {
        Color result = null;
        if (context == null) {
            return result;
        }
        ResourceManager manager = context.getResourceManager();
        if (manager == null) {
            return result;
        }
        try {
            result = new Color(manager.getElement(id).getColor());
        } catch (IOException e) {
        } catch (NotExistException e) {
        } catch (WrongTypeException e) {
        }
        return result;
    }
}
