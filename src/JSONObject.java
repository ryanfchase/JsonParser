import java.util.HashMap;

public class JSONObject {

	private HashMap<String, Object> map;
	public JSONObject(JsonTokener x) throws JSONException {
		map = new HashMap<String, Object>();
		 
		// must read left curly brace
		 if(x.nextClean() != '{') {
			 throw new JSONException("A JSONObject text must begin with '{'");
		 }
		 
		 // read some number of key-value pairs
		 while(true) {
			 
			 String key;
			 char c;
			 
			 // read first char of potential key
			 c = x.nextClean();
	            switch (c) {
	            case 0:
	            	// if we run out of characters to read, throw an exception
	                throw new JSONException("A JSONObject text must end with '}'");
	            case '}':
	            	// here, right curly brace correctly indicates an empty object
	                return;
	            case '"':
	            	// quotes indicate we should read a string,
	                x.back();
	                key = x.nextValue().toString();
	                break;
	            default:
	            	throw new JSONException("JSONObject keys must be strings.");
	            }
	            
	            // The key is followed by ':'.
	            c = x.nextClean();
	            if (c != ':') {
	                throw new JSONException("Expected a ':' after a key");
	            }
	            
	            // next, obtain the next value
	            Object o = x.nextValue();
	            
	            // add obtained value to map
	            map.put(key, o);
	            
	            // Pairs are separated by ','.
	            switch (x.nextClean()) {
	            case ',':
	                if (x.nextClean() == '}') {
	                	throw new JSONException("Comma not followed by key-value pair");
	                }
	                x.back(); // call back() to undo the '}' check
	                break;
	            case '}':
	                return;
	            default:
	                throw new JSONException("Expected a ',' or '}'");
	            }
		 }//end while
	}
		 
	

	public HashMap<String, Object> getMap() {
		return map;
	}
	
	public static Object parseValue(String strVal) throws JSONException {
		
		//handle stringified value
		
		if(strVal.equals("true")) {
			return Boolean.TRUE;
		}
				
		if(strVal.equals("false")) {
			return Boolean.FALSE;
		}
				
		if(strVal.equals("null")) {
			return JSONNull.getNull();
		}
				
		//handle number
				
		char firstChar = strVal.charAt(0);
				
		// check if the first char is a number, or a negative sign	
		if(Character.isDigit(firstChar) || firstChar == '-') {
			return parseNumberValue(strVal);
		}
		
		throw new JSONException("unquoted string passed as value");
		
	}
	
	private static Number parseNumberValue(String strVal) throws JSONException{
		 
		 try {
			 // check if we should parse as double
			 if (strVal.indexOf('.') > -1 
					 || strVal.indexOf('e') > -1
					 || strVal.indexOf('E') > -1
					 || "-0".equals(strVal)) { //IEEE 754, -0 represents -0.0 from underflow or mult.
					 Double d = Double.valueOf(strVal);
					 // check for invalid values for Doubles
					 if (!d.isInfinite() && !d.isNaN()) {
						 return d;
					 }
			 } else {
				 Long myLong = new Long(strVal);
				 
				 // don't enforce non-padding of 0's, skip to trimming size to Int of we can
				 if (myLong.longValue() == myLong.intValue()) {
					return Integer.valueOf(myLong.intValue());
				 }
				 
				 return myLong;
			 }
		 } 
		 catch (NumberFormatException ignore) {
		 }
		 
		 throw new JSONException("unable to convert value to long or decimal.");
	 
	}
	
	

}//end class
