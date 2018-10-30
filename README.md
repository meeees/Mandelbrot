# Mandelbrot
A Java Mandelbrot fractal visualizer.

Becuase this is all being run on CPU, the fractal can be regenerated after positioning it correctly, if it was regenerated every time the image is moved it would run very slowly.

WASD moves the image around, Z and X zoom in and out respectively, R recalculates the fractal with the new view.

Pressing P will put the fractal into zooming record mode, where it will zoom out by a constant factor and save an image to the path specified by code. See `outPath` in `MandelbrotRunner.java` if you wish to change the directory.
