import java.util.ArrayList;

public class JSONArray {

	private ArrayList<Object> myArrayList;
	public JSONArray(JsonTokener x) throws JSONException {
		myArrayList = new ArrayList<Object>();
		
        if (x.nextClean() != '[') {
            throw new JSONException("A JSONArray text must start with '['");
        }
        if (x.nextClean() != ']') {
            x.back();
            while(true) {
                if (x.nextClean() == ',') {
                    x.back();
                    this.myArrayList.add(JSONNull.getNull());
                } else {
                    x.back();
                    this.myArrayList.add(x.nextValue());
                }
                switch (x.nextClean()) {
                case ',':
                    if (x.nextClean() == ']') {
                        return;
                    }
                    x.back();
                    break;
                case ']':
                    return;
                default:
                    throw new JSONException("Expected a ',' or ']'");
                }
            }
        }
    }
	
	public ArrayList<Object> getList() {
		return myArrayList;
	}
}
