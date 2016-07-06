import java.io.IOException;
import java.io.StringReader;

public class JsonTokener {

	private StringReader reader;
	private char 		 previousChar;
	private boolean 	 usePrevious;
	private long 		 index;
	
	
	/**
	 * 
	 * @param json
	 */
	public JsonTokener(String json) {
		this.reader 		= new StringReader(json);
		this.previousChar 	= 0;
		this.usePrevious 	= false;
		this.index 			= 0;
	}
	
	/**
	 * Finds the next available char within the StringReader.
	 * Uses the previously used char if this.usePrevious
	 * @return Next char in Reader
	 * @throws JSONException
	 */
	public char next() throws JSONException {
		int c;
		
		// if back() was called, use the previous character.
		if(this.usePrevious) {
			c = this.previousChar;
			this.usePrevious = false;
		}
		// otherwise, find the next char in the string
		else {
			try {
				c = this.reader.read();
			}
			catch (IOException e) {
				throw new JSONException("IOException..." + e.getMessage());
			}
				
			//update previous char
			this.previousChar = (char) c;
			this.index += 1;
				
			if (c <= 0) { // End of stream
				c = 0;
			}
			 
		}
		
		return (char) c;
	}
	
	/**
     * Get the next char in the string, skipping whitespace.
     * @throws JSONException
     * @return  A character, or 0 if there are no more characters.
     */
    public char nextClean() throws JSONException {
        while (true) {
            char c = this.next();
            // return this character if we didn't get to end of file OR we got a non-whitespace character
            if (c == 0 || c > ' ') {
                return c;
            }
        }
    }
	
	/**
	 * indicates to use the previous char when this.next() is called
	 * @throws JSONException
	 */
	public void back() throws JSONException {
		
		// prevent illegal call of back()
		if(this.usePrevious || this.index <= 0) {
			throw new JSONException("Illegal use of back(). Tried to call back() before next() was called again");
		}
		
		// indicate to use the previous char
		this.index -= 1;
		this.usePrevious = true;
		
	}
	
	/**
	 * 
	 * @param n, the number of characters to read
	 * @return return the String read by 
	 * @throws JSONException
	 */
	public String next(int n) throws JSONException {
		char[] readIn = new char[n];
		
		try {
			this.reader.read(readIn,0,n);
		} catch (IOException e) {
			throw new JSONException(e.getMessage());
		}
		
		return new String(readIn);
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	public String nextString() throws JSONException {
		
		StringBuilder sb = new StringBuilder();
		
		//we just saw a " char, keep reading
		char c;
		
		while(true) {
			c = this.next();
			switch(c) {
			case '\"':
				// we're done here, don't grab the current token
				return sb.toString();
			case '\\':
				// here, we have to take it out of "escaped text" and insert
				c = this.next();
				sb.append(escapeHelper(c));
				break;
			default:
				if(c == '"') return sb.toString();
				sb.append(c);
				break;
			}
		}//--------------------------------------------------------end while 
		
	}
	
	
	/**
	 * 
	 * @param i
	 * @return
	 * @throws JSONException
	 */
	private char escapeHelper(char i) throws JSONException {
		switch (i) {
        case 'b':
            return '\b';
        case 't':
            return '\t';
        case 'n':
            return '\n';
        case 'f':
            return '\f';
        case 'r':
            return '\r';
        case '"':
        case '\'':
        case '\\':
        case '/':
            return i;
        case 'u':
            try {
            	return (char)Integer.parseInt(this.next(4), 16);
            } 
            catch (NumberFormatException e) {throw new JSONException("Illegal escape.");}
        default:
			throw new JSONException("invalid escape");
		}
	}
	
	public Object nextValue() throws JSONException {
		char c = this.nextClean();
		
		//handle quoted text and objects
		
		switch(c) {
		case '"':
			return this.nextString();
		case '{':
			this.back();
			return new JSONObject(this).getMap();
		case '[':
			this.back();
			return new JSONArray(this).getList();
		default:
			//nothing
				
		}
		
		//tokenize unquoted text (true, false, null, number)
		StringBuilder sb = new StringBuilder();
		
		while(!isStructuralCharacter(c)) {
			sb.append(c);
			c = this.next();
		}
		
		//here, we got to a character that was a structural character
		// (it was important). go back to prepare to get it again
		this.back();
		
		return JSONObject.parseValue(sb.toString().trim());
		
			
		
	}
	
	private boolean isStructuralCharacter(char c) {
		switch(c) {
		case ',':
		case ':':
		case ']':
		case '}':
		case '/':
		case '\\':
		case '"':
		case '[':
		case '{':
		case ';':
		case '=':
		case '#':
			return true;
		default:
			return false;
		}
	}
	
	
	
}
