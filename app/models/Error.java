package models;

public class Error {
	private int error_code;
	private String error_message;
	/**
	 * @param args
	 */
	public Error(int code, String mes){
		this.setError_code(code);
		this.setError_message(mes);
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public int getError_code() {
		return error_code;
	}
	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
	public String getError_message() {
		return error_message;
	}
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}

}
