import model.GameModel;
import model.GraphicLibrary;
import controller.Debug;
import controller.LoginScreenController;
import view.LoginScreen;

public class MainLogin {
	public static void main(String[] args) {
		
		Debug.isDebug = true;
		
		//load assets
		new GraphicLibrary();
		
		//model
		GameModel gameModel = new GameModel();
		
		LoginScreen login = new LoginScreen();
		LoginScreenController lsc = new LoginScreenController(login, gameModel);
	}

}
