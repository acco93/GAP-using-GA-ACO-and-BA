package view;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * 
 * Values used across different ui components.
 * R = resources
 * @author acco
 * 
 * Jul 5, 2016 7:37:06 AM
 *
 */
public class R {
	
	/*
	 * Colors
	 */
	public static final Color BACKGROUND_COLOR = new Color(161, 212, 144);
	public static final Color GA_BACKGROUND_COLOR = new Color(158, 196, 188);
	public static final Color ANTS_BACKGROUND_COLOR = new Color(247, 200, 207);
	
	/*
	 * Icons
	 */
	public static Icon START_ICON;
	public static Icon STOP_ICON;
	public static Icon EXPORT_ICON;
	public static Icon CLEAR_ICON;
	public static ImageIcon HELP_ICON;
	
	
	static{
		try {
		
			START_ICON = new ImageIcon(ImageIO.read(new File("icons/Play.png")));
			STOP_ICON = new ImageIcon(ImageIO.read(new File("icons/Stop.png")));
			EXPORT_ICON = new ImageIcon(ImageIO.read(new File("icons/ExportToDocument.png")));
			CLEAR_ICON = new ImageIcon(ImageIO.read(new File("icons/MinusRedButton.png")));
			HELP_ICON = new ImageIcon(ImageIO.read(new File("icons/HelpBlueButton.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
