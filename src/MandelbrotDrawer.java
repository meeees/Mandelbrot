import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

//drawer for double precision mandelbrot drawing
public class MandelbrotDrawer {
	
	private double xStart, yStart, xEnd, yEnd;
	private BufferedImage drawn;
	private int width, height;
	private int numCycles;
	int[] pixels;
	int[] histogram;
	
	int[] colorTable;
	int numColors;
	
	public MandelbrotDrawer()
	{
		this(-2, -1, 1, 1, 1500);
	}
	
	public MandelbrotDrawer(double xS, double yS, double xE, double yE, int nC)
	{
		xStart = xS;
		yStart = yS;
		xEnd = xE;
		yEnd = yE;
		width = MandelbrotRunner.WIDTH;
		height = MandelbrotRunner.HEIGHT;
		numCycles = nC;
		histogram = new int[nC];
		
		drawn = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) drawn.getRaster().getDataBuffer()).getData();
	}
	
	public void randomizeColorTable(int size)
	{
		colorTable = new int[size];
		numColors = size;
		Random r = new Random(1);
		for(int i = 0; i < size; i++)
		{
			colorTable[i] = 0xff000000 | r.nextInt(0x00ffffff + 1);
		}
	}
	
	public void lerpHistogramColors(int[] colors, int size)
	{
		colorTable = new int[size];
		numColors = size;
		float lerpStep = (1f * (colors.length - 1)) / (numColors - 1);
		for(int i = 0; i < numColors; i++)
		{
			colorTable[i] = multiLerp(colors, lerpStep * i);
			//System.out.println(String.format("0x%08X", colorTable[i]));
		}
	}
	
	public void drawSet()
	{
		Arrays.fill(pixels, 0);
		Arrays.fill(histogram, 0);
		double w = (xEnd - xStart) / (double) width;
		double h = (yEnd - yStart) / (double) height;
		
		double imag, real;
		for(int j = 0; j < height; j++)
		{
			imag = j * h + yStart;
			for(int i = 0; i < width; i++)
			{
				real = i * w + xStart;
				Complex c = new Complex(real, imag);
				int converges = doesConverge(c, numCycles);
				if (converges != -1)
					histogram[converges] += 1;
				pixels[j * width + i] = converges;
			}
		}
		int total = 0;
		for(int i = 0; i < numCycles; i++)
		{
			total += histogram[i];
		}
		for(int j = 0; j < height; j++)
		{
			for(int i = 0; i < width; i++)
			{
				int iters = pixels[j * width + i];
				if(iters == -1)
				{
					pixels[j * width + i] = 0;
					continue;
				}
				double hue = 0;
				for(int k = 0; k < iters; k++)
				{
					hue += histogram[k] / (double) total;
				}
				//int bw = (int) (hue * 255);
				int target = (int) (hue * numColors);
				pixels[j * width + i] = 0xff000000 | colorTable[target];
				//pixels[j * width + i] = 0xff000000 | (bw << 16) | (bw << 8) | bw;
			}
		}
		System.out.println("Drew set");
	}
	
	public double getFieldWidth()
	{
		return xEnd - xStart;
	}
	
	public double getFieldHeight()
	{
		return yEnd - yStart;
	}
	
	public BufferedImage getImage()
	{
		return drawn;
	}
	
	public void translate(double dX, double dY)
	{
		xStart += dX;
		yStart += dY;
		xEnd+= dX;
		yEnd += dY;
		System.out.println(this);
	}
	
	public void zoom(double factor)
	{
		double w = xEnd - xStart;
		double xMid = xStart + (w / 2);
		//System.out.println(w + " : " + w / factor);
		
		factor *= 2;
		xStart = xMid - (w / factor);
		xEnd = xMid + (w / factor);
		
		double h = yEnd - yStart;
		double yMid = yStart + (h / 2);
		
		yStart = yMid - (h / factor);
		yEnd = yMid + (h / factor);
		
		System.out.println(this);
	}
	
	public void writeSetTo(String img) throws FileNotFoundException, IOException
	{
		ImageIO.write(drawn, "png", new FileOutputStream(new File(img)));
	}
	
	public String toString()
	{
		return String.format("Mandelbrot Drawer: X %f to %f, Y %f to %f", xStart, xEnd, yStart, yEnd);
	}
	
	
	public static int multiLerp(int[] colors, float blend)
	{
		int start = (int) blend;
		if(0f == (blend - start))
			return colors[start];
		return lerpColor(colors[start], colors[start + 1], blend - start);
	}
	
	public static int lerpColor(int c1, int c2, float blend)
	{
		int r1 = (c1 & 0xff0000) >> 16;
		int g1 = (c1 & 0xff00) >> 8;
		int b1 = c1 & 0xff;
		
		int r2 = (c2 & 0xff0000) >> 16;
		int g2 = (c2 & 0xff00) >> 8;
		int b2 = c2 & 0xff;
		
		float inverse = 1f - blend;
		int red = (int) (((float) r2) * blend + ((float) r1) * inverse);
		int green = (int) (((float) g2) * blend + ((float) g1) * inverse);
		int blue = (int) (((float) b2) * blend + ((float) b1) * inverse);
		return (red << 16) | (green << 8) | blue;
	}
	
	public static int doesConverge(Complex c, int numTries)
	{
		if(c.getMagnitudeSq() >= 4)
			return 0;
		
		//cardioid and bulb check
		double real = c.getReal();
		double imag = c.getImag();
		
		//cardioid
		double q = (real - 0.25) * (real - 0.25) + imag * imag;
		if(q * (q + (real - 0.25)) < 0.25 * imag * imag)
		{
			return -1;
		}
		
		//bulb
		if((real + 1) * (real + 1) + imag * imag < 1./16.)
		{
			return -1;
		}
		
		Complex fZ = c;
		//try to catch some repetition
		Complex previous = new Complex();
			
		for(int i = 0; i < numTries; i++)
		{
			fZ = fZ.mult(fZ).add(c);
			
			if(fZ.getMagnitudeSq() >= 4)
				return i;
			if(previous.equals(fZ))
				return -1;
			previous = fZ;
		}
		return -1;
	}

}
