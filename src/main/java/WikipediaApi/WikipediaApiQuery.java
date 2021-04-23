package WikipediaApi;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class WikipediaApiQuery {
    private Map<String, String> propMap;


    public WikipediaApiQuery(){
        this.propMap = new TreeMap<>();
    }

    private void addValue2Key(String key, String value, String sep){
        String prevValue = this.propMap.containsKey(key) ? this.propMap.get(key)+sep : "" ;
        this.propMap.put(key, prevValue + value);
    }

    public WikipediaApiQuery withAbstract(){
        addValue2Key("prop", "extracts", "|");
        //String prevValue = this.propMap.containsKey("prop") ? this.propMap.get("prop|") : "";
        //this.propMap.put("prop", prevValue+"extracts");
        return this;
    }

    public WikipediaApiQuery withUrl(){
        this.addValue2Key("inprop","url","|");
        this.addValue2Key("prop","info","|");
        return this;
    }

    public WikipediaApiQuery searchByTitle(String title){
        this.addValue2Key("titles", title.replace(' ','+'),"|");
        return this;
    }

    public String get(){
        Set<String> keys = this.propMap.keySet();
        String result = "";
        for (String key : keys ) {
            result += key+"="+this.propMap.get(key)+"&";
        }
        return result;
    }
}
