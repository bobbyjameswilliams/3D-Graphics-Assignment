public final class WindowedWall {
  
  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  public static final float[] vertices = {      // position, colour, tex coords
    -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f,  // top left
    -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
     0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,  // bottom right
     0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f,  // top right

    // Extra Vertices for creating window
    -0.25f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // top left
    -0.25f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
    0.25f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom right
    0.25f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // top right

    // Window Vertices
    -0.25f, 0.0f, -0.25f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // top left window
    -0.25f, 0.0f,  0.25f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left window
     0.25f, 0.0f,  0.25f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom right window
     0.25f, 0.0f, -0.25f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f  // top right window
  };
  
  public static final int[] indices = {         // Note that we start from 0!
    0,4,5,
    0,1,5,
    4,8,7,
    7,11,8,
    


  };

}