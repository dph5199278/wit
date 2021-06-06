package com.cs.wit.util;

public class ClassHelper {

    /**
     * 根据类的全名查找是否存在
     *
     * @param classFullName
     * @return
     */
    public static boolean isClassExistByFullName(final String classFullName) {
        try {
            Class.forName(classFullName);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
