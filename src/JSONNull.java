public class JSONNull {

	public static final JSONNull NULL = new JSONNull();
	
	public static Object getNull() {
		return NULL;
	}
	
	public boolean equals(Object object) {
        return object == null || object == this;
    }

}
