package fr.greweb.rngl;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import java.util.Map;

import javax.annotation.Nullable;

import android.util.Log;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;


public class EXGLViewManager extends SimpleViewManager<EXGLView> {
  public static final String REACT_CLASS = "EXGLView";

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  public EXGLView createViewInstance(ThemedReactContext context) {
    return new EXGLView(context);
  }

  @Override
  public @Nullable Map getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.of(
            "surfaceCreate",
            MapBuilder.of("registrationName", "onSurfaceCreate"));
  }

  @ReactMethod
  public void captureFrame( ReadableMap options ) {
    Log.d(REACT_CLASS, "captureFrame");

    int offsetX = (int)( options.getDouble("offsetX") );
    int offsetY = (int)( options.getDouble("offsetY") );
    int width = (int)( options.getDouble("width") );
    int height = (int)( options.getDouble("height") );

    // TODO: HOW TO GET "gl" FROM HERE?
  }
}
