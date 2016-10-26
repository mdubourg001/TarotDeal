package Tarot;

public class Controller {
	private Model model;
	
	public Controller(Model model){
		this.model = model;
	}
	
	public Model getModel(){
		return model;
	}
}
