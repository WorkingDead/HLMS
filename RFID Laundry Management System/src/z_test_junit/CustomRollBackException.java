package z_test_junit;

public class CustomRollBackException extends Exception {

	private static final long serialVersionUID = 4448403521921714212L;

	public CustomRollBackException(String string) {
		super(string);
	}
	
	public CustomRollBackException() {
		super("");
	}
}
