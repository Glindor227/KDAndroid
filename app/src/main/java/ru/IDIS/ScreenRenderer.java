package ru.IDIS;

import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ScreenRenderer 
implements 
    Renderer
{
    private static final String TAG = "ScreenRenderer";
    private static final boolean DEBUG_LOG = false;

    private GL10 mGL10;

    /*
    private long mSourcePeer = 0;
    private int mSourceWidth = 0;
    private int mSourceHeight = 0;
    */

    private int mTextureHeight = 256;
    private int mTextureWidth = 512;

    private float[] mVertices = {
        -1.0f,
         1.0f,
        -1.0f,
        -1.0f,
         1.0f,
         1.0f,
         1.0f,
        -1.0f
    };

    private float[] mTextureCoords = {
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        1.0f,
        0.0f,
        1.0f,
        1.0f
    };

    private int[] mTextures = new int[1];
    private FloatBuffer mTexcoords;

    // cleanup resources...
    protected void finalize() throws Throwable
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "finalize()");
        }

        try {
            if (DEBUG_LOG) {
                Log.v(TAG, "entered try block...");
            }

            // delete textures
            mGL10.glDeleteTextures(1, mTextures, 0);

            if (DEBUG_LOG) {
                Log.v(TAG, "end of try block...");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if (DEBUG_LOG) {
                Log.v(TAG, "exception caught...");
            }
        }
        finally {
            super.finalize();
        }
    }
    
    private void setupView()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "START : setupView()");
        }
        
        mGL10.glLoadIdentity();
        //float ratio = (float)mTextureHeight/(float)mTextureWidth;    
        
        mGL10.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        ByteBuffer vbb = ByteBuffer.allocateDirect(mVertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(mVertices);
        vertexBuffer.position(0);

        mGL10.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
        
        // Enable use of the texture
        mGL10.glEnable(GL10.GL_TEXTURE_2D);
        
        mGL10.glGenTextures(1, mTextures, 0);
        
        mGL10.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[0]);
        
        mGL10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        mGL10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);        

        ByteBuffer bb = ByteBuffer.allocateDirect(mTextureCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mTexcoords = bb.asFloatBuffer();
        mTexcoords.put(mTextureCoords);
        mTexcoords.position(0);

        if (DEBUG_LOG) {
            Log.v(TAG, "END : setupView()");
        }
    }
    
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "START : onSurfaceCreated()");
        }

        mGL10 = gl;

        setupView();

        if (DEBUG_LOG) {
            Log.v(TAG, "END : onSurfaceCreated()");
        }
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) 
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "width = " + w);
            Log.v(TAG, "height = " + h);
        }
    
        mGL10 = gl;

        if (DEBUG_LOG) {
            Log.v(TAG, "END : onSurfaceCreated()");
        }

        resetView(0, 0, w, h);
    }

    private void resetView(int x, int y, int w, int h)
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "resetView()");
        }
        
        mGL10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        mGL10.glViewport(0, 0, w, h);        
    }

    // called when requestRender() from ScreenView arrives...
    public void onDrawFrame(GL10 gl)
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "START : onDrawFrame");
        }

        mGL10.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[0]);

        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        gl.glFlush();
        gl.glFinish();
        
        if (DEBUG_LOG) {
            Log.v(TAG, "END : onDrawFrame");
        }
    }

    public void drawImage(ByteBuffer bb)        
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "START : drawImage()");
        }

        mGL10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);        
        
        mGL10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexcoords);        
        
        mGL10.glPixelStorei(GL10.GL_PACK_ALIGNMENT, 1);        
        mGL10.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 1);
        
        mGL10.glTexImage2D(GL10.GL_TEXTURE_2D,  // int target,
                        0,                      // int level,
                        GL10.GL_RGB,            // int internalformat,
                        mTextureWidth, mTextureHeight,  // int width, int height,
                        0,                      // int border,
                        GL10.GL_RGB,            // int format
                        GL10.GL_UNSIGNED_BYTE,  // int type
                        bb);

        mGL10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        mGL10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        mGL10.glFlush();
        mGL10.glFinish();

        if (DEBUG_LOG) {
            Log.v(TAG, "END : drawImage()");
        }
    }

    public void onPause()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "START : onPause()");
        }

        if (DEBUG_LOG) {
            Log.v(TAG, "DO NOTHING in onPause()");
        }

        if (DEBUG_LOG) {
            Log.v(TAG, "END : onPause()");
        }
    }

    public void onResume()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "START : onResume()");
        }

        if (DEBUG_LOG) {
            Log.v(TAG, "DO NOTHING in onResume()");
    }

        if (DEBUG_LOG) {
            Log.v(TAG, "END : onResume()");
        }
    }

    // dirty fix
    public void reset()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "reset()");
        }
    }
}