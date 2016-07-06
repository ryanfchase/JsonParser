import java.math.BigInteger;
import java.util.Map;

public class JsonParser {

	public static Map<String, Object>parse(String input) throws JSONException {
		
		
		JSONObject j = new JSONObject(new JsonTokener(input));
		return j.getMap();
	}
	public static void main(String [] args ) {
		
		String json = "{\n\t\"debug\" : \"on\",\n\t\"window\": {\n\t\t\"title\": \"sample\",\n\t\t\"size\": 500\n\t}\n}";
		
		try {
			Map<String, Object> output = parse(json);
			System.out.println(output);
			
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
