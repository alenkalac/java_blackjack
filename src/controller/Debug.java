package controller;

public class Debug {
	
	public static boolean isDebug = false;
	
	public static void print(String data) {
		if(isDebug)
			System.out.println(data);
	}
	
	public static void print(String section, String data) {
		if(isDebug)
			System.out.println(section + " --- " + data);
	}
}
