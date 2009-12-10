package semaGL;

import nehe.GLDisplay;

public class SemaNoGui {
    public static void main(String[] args) {
        GLDisplay neheGLDisplay = GLDisplay.createGLDisplay("SemaSpace");
        SemaSpace space = new SemaSpace("sema.config");
        neheGLDisplay.addGLEventListener(space);
        neheGLDisplay.addMouseMotionListener(space);
        neheGLDisplay.start();
    }
}
