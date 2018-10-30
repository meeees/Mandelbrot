import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

	private static InputHandler _instance;
	
	public boolean zoomIn;
	public boolean zoomOut;
	public boolean up;
	public boolean down;
	public boolean left;
	public boolean right;
	public boolean recalculate;
	public boolean recalculatePressed;
	public boolean record;
	public boolean recordPressed;
	public boolean escape;
	
	public InputHandler()
	{
		if(_instance != null)
		{
			throw new RuntimeException("Two input handlers initialized!");
		}
		_instance = this;
	}
	
	public void keyPressed(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
		case KeyEvent.VK_W:
			up = true;
			break;
		case KeyEvent.VK_A:
			left = true;
			break;
		case KeyEvent.VK_S:
			down = true;
			break;
		case KeyEvent.VK_D:
			right = true;
			break;
		case KeyEvent.VK_R:
			if(!recalculatePressed)
				recalculate = recalculatePressed =  true;
			break;
		case KeyEvent.VK_Z:
			zoomIn = true;
			break;
		case KeyEvent.VK_X:
			zoomOut = true;
			break;
		case KeyEvent.VK_P:
			if(!recordPressed)
				record = recordPressed = true;
			break;
		case KeyEvent.VK_ESCAPE :
			escape = true;
			break;
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
		case KeyEvent.VK_W:
			up = false;
			break;
		case KeyEvent.VK_A:
			left = false;
			break;
		case KeyEvent.VK_S:
			down = false;
			break;
		case KeyEvent.VK_D:
			right = false;
			break;
		case KeyEvent.VK_R:
			recalculate = recalculatePressed =  false;
			break;
		case KeyEvent.VK_Z:
			zoomIn = false;
			break;
		case KeyEvent.VK_X:
			zoomOut = false;
			break;
		case KeyEvent.VK_P:
			record = recordPressed = false;
			break;
		case KeyEvent.VK_ESCAPE :
			escape = false;
			break;
		}
	}
	
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	public static InputHandler Instance()
	{
		return _instance;
	}
}
