package dto;

public class UsuarioGlobal {
	
	private static UsuarioGlobal instance = null;
	private String dniGlobal;
	private String tabla;
	
	

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
	public String getTabla() {
		return tabla;
	}
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	
	

}
