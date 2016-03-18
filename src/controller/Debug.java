package controller;

public class Debug {
	
	public static boolean isDebug = false;
	
	/**
	 * Debugging data
	 * @param data
	 */
	public static void print(String data) {
		if(isDebug)
			System.out.println(data);
	}
	
	/**
	 * Debugging data
	 * @param section
	 * @param data
	 */
	public static void print(String section, String data) {
		if(isDebug)
			System.out.println(section + " --- " + data);
	}
}
