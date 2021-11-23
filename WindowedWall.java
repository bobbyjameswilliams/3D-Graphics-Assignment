public final class WindowedWall {
  
  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  public static final float[] vertices = {      // position, colour, tex coords
    -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f,  // 0 top left
    -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // 1 bottom left
     0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,  // 2 bottom right
     0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f,  // 3 top right

    // Extra Vertices for creating window
    -0.25f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.25f, 1.0f,  // 4 top left
    -0.25f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.25f, 0.0f,  // 5 bottom left
    0.25f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.75f, 0.0f,  // 6 bottom right
    0.25f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.75f, 1.0f,  // 7 top right

    // Window Vertices
    -0.25f, 0.0f, -0.25f,  0.0f, 1.0f, 0.0f,  0.25f, 0.75f,  // 8 top left window
    -0.25f, 0.0f,  0.25f,  0.0f, 1.0f, 0.0f,  0.25f, 0.25f,  // 9 bottom left window
     0.25f, 0.0f,  0.25f,  0.0f, 1.0f, 0.0f,  0.75f, 0.25f,  // 10 bottom right window
     0.25f, 0.0f, -0.25f,  0.0f, 1.0f, 0.0f,  0.75f, 0.75f  // 11 top right window
  };
  
  public static final int[] indices = {         // Note that we start from 0!

    5,4,0, //left square right triangle
    0,1,5, //left square left triangle

    4,8,7, //top square top triangle
    7,8,11, //top square bottom triangle

    2,7,6, //right square left triangle
    2,3,7, //right square right triangle

    5,6,10, //bottom swuare bottom triangle
    9,5,10 //bottom square bottom triangle
  };

}