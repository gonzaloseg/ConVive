package dto;

public class UsuarioGlobal {
	
	private static UsuarioGlobal instance = null;
	private String dniGlobal;
	
	

	public static UsuarioGlobal getInstacne(){
		if (instance == null) {
			instance = new UsuarioGlobal();
		}
		return instance;
	}
private UsuarioGlobal() {
		
	}
	public String getDniGlobal() {
		return dniGlobal;
	}

	public void setDniGlobal(String dniGlobal) {
		this.dniGlobal = dniGlobal;
	}
	
	

}
