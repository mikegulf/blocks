//---------------------------------------------------------------80 columns---|

/* comp285 Move.java
 * --------------
 */
import java.awt.event.KeyEvent;

public class Move {
	public static final int Undo = -1;

	public static int keyCodeToDirection(int num) {
		switch (num) {
		case KeyEvent.VK_KP_UP:
		case KeyEvent.VK_UP:
			return Location.North;
		case KeyEvent.VK_KP_DOWN:
		case KeyEvent.VK_DOWN:
			return Location.South;
		case KeyEvent.VK_KP_RIGHT:
		case KeyEvent.VK_RIGHT:
			return Location.East;
		case KeyEvent.VK_KP_LEFT:
		case KeyEvent.VK_LEFT:
			return Location.West;
		default:
			throw new IndexOutOfBoundsException();
		}
	}

	public static int keyCodeToDirection(Integer move) {
		return keyCodeToDirection(move.intValue());
	}

	private int direction;

	private boolean isUndo;

	private Thing pushed;

	Move(int direction, boolean isUndo) {
		this.direction = direction;
		this.isUndo = isUndo;
	}

	public int getDirection() {
		return direction;
	}

	public boolean getIsUndo() {
		return isUndo;
	}

	public Thing getPushed() {
		return pushed;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setIsUndo(boolean b) {
		isUndo = b;
	}

	public void setPushed(Thing thing) {
		pushed = thing;
	}

	@Override
	public String toString() {
		//return "[Move: " + direction + "]";
		return "" + direction;
	}
}
