package fr.greweb.rngl;

import android.content.Context;
import static android.opengl.GLES20.*;

import android.graphics.PixelFormat;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static fr.greweb.rngl.EXGL.*;

public class EXGLView extends GLSurfaceView implements GLSurfaceView.Renderer {
  private boolean onSurfaceCreateCalled = false;
  private int exglCtxId = -1;
  private GL10 gl = null;

  public EXGLView(Context context) {
    super(context);

    setEGLContextClientVersion(2);
    setEGLConfigChooser(8, 8, 8, 8, 16, 0);
    getHolder().setFormat(PixelFormat.TRANSLUCENT);
    setRenderer(this);
  }

  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    this.gl = gl;
    EGL14.eglSurfaceAttrib(EGL14.eglGetCurrentDisplay(), EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW),
            EGL14.EGL_SWAP_BEHAVIOR, EGL14.EGL_BUFFER_PRESERVED);

    if (!onSurfaceCreateCalled) {
      // On JS thread, get JavaScriptCore context, create EXGL context, call JS callback
      final ReactContext reactContext = (ReactContext) getContext();
      reactContext.runOnJSQueueThread(new Runnable() {
        @Override
        public void run() {
          exglCtxId = EXGLContextCreate(reactContext.getJavaScriptContext());
          WritableMap arg = Arguments.createMap();
          arg.putInt("exglCtxId", exglCtxId);
          reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "surfaceCreate", arg);
        }
      });
      onSurfaceCreateCalled = true;
    }
  }

  public void onDrawFrame(GL10 unused) {
    // exglCtxId may be unset if we get here (on the GL thread) before EXGLContextCreate(...) is
    // called on the JS thread to create the EXGL context and save its id (see above in
    // the implementation of `onSurfaceCreated(...)`)
    if (exglCtxId > 0) {
      EXGLContextFlush(exglCtxId);
    }
  }

  public void onSurfaceChanged(GL10 unused, int width, int height) {
  }

  public void onDetachedFromWindow() {
    EXGLContextDestroy(exglCtxId);
    super.onDetachedFromWindow();
  }
}
