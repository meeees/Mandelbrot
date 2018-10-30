import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

public class MandelbrotRunner extends Canvas implements Runnable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 1280, HEIGHT = 850;
	
	private long curTime;
	private long culmTime;
	private int frames;
	private int fps;
	//measures per second
	private float measuresPer = 2f;
	
	private Font font = new Font("Arial", Font.PLAIN, 12);
	
	private MandelbrotDrawer drawer;
	private boolean dirty = true;
	
	private double xSpeed = 0.5, ySpeed = 0.3333;
	private double zoomFactor = 1.5;
	private double undrawnX, undrawnY;
	private double unzoomedFactor = 1;
	private double totalZoom = 1;
	private double deltaTime;
	
	private boolean zoomingRecord = false;
	private int outCount = 0;
	private String outPath = "output/img";
	
	public static void main(String args[])
	{
		JFrame frame = new JFrame("Mandelbrot");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		MandelbrotRunner mr = new MandelbrotRunner();
		frame.add(mr);
		frame.setVisible(true);
		frame.repaint();
		frame.setIgnoreRepaint(true);
		mr.start();
	}
	
	public void render(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		if(dirty)
		{
			drawer.drawSet();
			if(zoomingRecord)
			{
				try {
					drawer.writeSetTo(outPath + outCount + ".png");
					System.out.println("Output to " + outPath + outCount + ".png");
					outCount++;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			undrawnX = undrawnY = 0;
			unzoomedFactor = 1;
			dirty = false;
		}
		int xPos = (int) (undrawnX * WIDTH / drawer.getFieldWidth());
		int yPos = (int) (undrawnY * HEIGHT / drawer.getFieldHeight());
		int zoomedWidth = (int) (WIDTH * unzoomedFactor);
		int zoomedHeight = (int) (HEIGHT * unzoomedFactor);
		xPos -= ((zoomedWidth - WIDTH) / 2);
		yPos -= ((zoomedHeight - HEIGHT) / 2);
		g.drawImage(drawer.getImage(), xPos, yPos, zoomedWidth, zoomedHeight, null);
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString("FPS: " + fps, 5, 565);
	}
	
	public void tick()
	{
		deltaTime = (System.currentTimeMillis() - curTime) / 1000.;
		InputHandler input = InputHandler.Instance();
		double xDelt = (xSpeed * deltaTime) / totalZoom;
		double yDelt = (ySpeed * deltaTime) / totalZoom;
		//System.out.println(xDelt + " : " + yDelt);
		double zFactor = ((zoomFactor - 1) * deltaTime) + 1;
		
		if(!zoomingRecord)
		{
			if(input.up)
			{
				undrawnY -= yDelt;
				drawer.translate(0, yDelt);
			}
			if(input.down)
			{
				undrawnY += yDelt;
				drawer.translate(0, -yDelt);
			}
			if(input.left)
			{
				undrawnX -= xDelt;
				drawer.translate(xDelt, 0);
			}
			if(input.right)
			{
				undrawnX += xDelt;
				drawer.translate(-xDelt, 0);
			}
			if(input.zoomIn)
			{
				unzoomedFactor *= zFactor;
				totalZoom *= zFactor;
				drawer.zoom(zFactor);
			}
			if(input.zoomOut)
			{
				unzoomedFactor /= zFactor;
				totalZoom /= zFactor;
				drawer.zoom(1. / zFactor);
			}
			if(input.recalculate)
			{
				input.recalculate = false;
				dirty = true;
			}
			if(input.record)
			{
				input.record = false;
				zoomingRecord = true;
				System.out.println("Zooming record started");
			}
		}
		else
		{
			if(input.escape)
			{
				System.out.println("Zooming record canceled");
				zoomingRecord = false;
			}
			else
			{
				if(totalZoom <= 1)
				{
					System.out.println("Zooming record done");
					zoomingRecord = false;
				}
				else
				{
					double wantedOut = 1.01;
					totalZoom /= wantedOut;
					drawer.zoom(1. / wantedOut);
					dirty = true;
				}
			}
		}
		
		long nowTime = System.currentTimeMillis();
		culmTime += nowTime - curTime;
		
		if(culmTime > 1000 / measuresPer)
		{
			culmTime = 0;
			fps = (int) (frames * measuresPer);
			frames = 0;
		}
		curTime = nowTime;
		frames++;
	}
	
	public void init()
	{
		drawer = new MandelbrotDrawer();
		//drawer.randomizeColorTable(20);
		//purple to blue to green
		//drawer.lerpHistogramColors(new int[] {0x741fa5, 0x3793ef, 0x74ff42}, 30);
		drawer.lerpHistogramColors(new int[] {0xffffff, 0xffff00, 0xffaa00, 0xff0000}, 30);
		
		curTime = System.currentTimeMillis();
		
		addKeyListener(new InputHandler());
		requestFocus();
	}
	
	public void run()
	{
		init();
		while(true)
		{
			BufferStrategy bs = getBufferStrategy();
			if(bs == null)
			{
				createBufferStrategy(3);
				continue;
			}
			
			Graphics2D g = (Graphics2D) bs.getDrawGraphics();
			tick();
			render(g);
			bs.show();
		}
	}
	
	public void start()
	{
		Thread t = new Thread(this);
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}
	
	
	
	

}
