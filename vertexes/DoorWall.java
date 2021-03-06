package vertexes;

public class DoorWall {

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
                0.f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.5f, 0.0f,  // 6 bottom right
                0.0f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.5f, 1.0f,  // 7 top right

                // Window Vertices
                -0.25f, 0.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.25f, 0.5f,  // 8 top left window
                -0.25f, 0.0f,  0.25f,  0.0f, 1.0f, 0.0f,  0.25f, 0.5f,  // 9 bottom left window
                0.0f, 0.0f,  0.25f,  0.0f, 1.0f, 0.0f,  0.5f, 0.25f,  // 10 bottom right window
                0.0f, 0.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.5f, 0.5f  // 11 top right window
        };

        public static final int[] indices = {         // Note that we start from 0!
                5,4,0,
                0,1,5,
                4,8,7,
                7,8,11,
                2,7,6,
                2,3,7,

        };

}
