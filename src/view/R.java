package view;

import java.awt.Color;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * 
 * Values used across different ui components. R = resources
 * 
 * @author acco
 * 
 *         Jul 5, 2016 7:37:06 AM
 *
 */
public class R {

	/*
	 * Colors
	 */
	public static final Color BACKGROUND_COLOR = new Color(161, 212, 144);
	public static final Color GA_BACKGROUND_COLOR = new Color(158, 196, 188);
	public static final Color ANTS_BACKGROUND_COLOR = new Color(247, 200, 207);
	public static final Color BA_BACKGROUND_COLOR = new Color(234, 246, 200);

	/*
	 * Icons
	 */
	public static Icon START_ICON;
	public static Icon START_ICON_SMALL;
	public static Icon STOP_ICON;
	public static Icon EXPORT_ICON;
	public static Icon CLEAR_ICON;
	public static Icon RECORD_ICON;
	public static Icon RECORD_ICON_SMALL;
	public static Icon OPEN_ICON;
	public static Icon REMOVE_ICON;
	public static Icon ADD_ICON;

	
	static {
		try {
						
			START_ICON = new ImageIcon(ImageIO.read(R.class.getResource("/Play.png")));
			START_ICON_SMALL = new ImageIcon(ImageIO.read(R.class.getResource("/PlaySmall.png")));
			STOP_ICON = new ImageIcon(ImageIO.read(R.class.getResource("/Stop.png")));
			EXPORT_ICON = new ImageIcon(ImageIO.read(R.class.getResource("/ExportToDocument.png")));
			CLEAR_ICON = new ImageIcon(ImageIO.read(R.class.getResource("/MinusRedButton.png")));
			RECORD_ICON = new ImageIcon(ImageIO.read(R.class.getResource("/RecordButton.png")));
			RECORD_ICON_SMALL = new ImageIcon(ImageIO.read(R.class.getResource("/RecordButtonSmall.png")));
			OPEN_ICON = new ImageIcon(ImageIO.read(R.class.getResource("/WriteDocumentSmall.png")));
			REMOVE_ICON = new ImageIcon(ImageIO.read(R.class.getResource("/Remove.png")));
			ADD_ICON = new ImageIcon(ImageIO.read(R.class.getResource("/Add.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private R(){}
	
}
