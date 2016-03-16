package model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class GraphicLibrary {
	
	public static String BACKGROUND = "background";
	public static String CARDS = "cards";
	
	public static int SUIT_HEARTS = 1;
	public static int SUIT_DIAMOND = 2;
	public static int SUIT_CLUBS = 3;
	public static int SUIT_SPADE = 4;
	public static int SUIT_BLANK = 0;
	
	public static int CARD_ACE = 1;
	public static int CARD_2 = 2;
	public static int CARD_3 = 3;
	public static int CARD_4 = 4;
	public static int CARD_5 = 5;
	public static int CARD_6 = 6;
	public static int CARD_7 = 7;
	public static int CARD_8 = 8;
	public static int CARD_9 = 9;
	public static int CARD_10 = 10;
	public static int CARD_JACK = 11;
	public static int CARD_QUEEN = 12;
	public static int CARD_KING = 13;
	public static int CARD_BLANK = 0;
	
	private static int card_height = 63;
	private static int card_width = 44;
	
	private static HashMap<String, BufferedImage> db;
	
	/**
	 * Constructor
	 */
	public GraphicLibrary() {
		loadAssets();
	}
	
	/**
	 * Load the graphics in memory to save up IO reading constantly from disk
	 */
	public static void loadAssets() {
		
		db = new HashMap<>();
		
		try {
			BufferedImage img = ImageIO.read(GraphicLibrary.class.getResourceAsStream("/graphics/testbg.png"));
			db.put(BACKGROUND,  img);
			
			img = ImageIO.read(GraphicLibrary.class.getResourceAsStream("/graphics/card_spritesheet.png"));
			db.put(CARDS,  img);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get a background image from a hash map
	 * @return BufferedImage an image for background
	 */
	public static BufferedImage getBackgroundGraphic() {
		if(db == null)
			loadAssets();
		
		BufferedImage image = db.get(BACKGROUND);
		return image;
	}
	
	/**
	 * get a card graphic for each card. 
	 * @param suit
	 * @param value
	 * @return BufferedImage image of the requested card
	 */
	public static BufferedImage getCardGraphic(int suit, int value) {
		if(db == null)
			loadAssets();

		BufferedImage image = db.get(CARDS);
		
		int col = 0;
		int row = 0;
		
		if(suit == 0 || value == 0 ){
			col = (5 * card_height) - card_height;
			row = (1 * card_width) - card_width;
		}
		else{
			col = (suit * card_height) - card_height;
			row = (value * card_width) - card_width;
		}
		
		BufferedImage card = image.getSubimage(row, col, card_width, card_height);	
		
		return card;

	}

}
