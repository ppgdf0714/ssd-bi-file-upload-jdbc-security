package jp.co.ssd.bi.model;

public class MyException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private String Message;

    public MyException(String Message) {
        this.Message = Message;
    }

	public String getMessage() {
		return Message;
	}

	public void setMessgae(String message) {
		Message = message;
	}

}
