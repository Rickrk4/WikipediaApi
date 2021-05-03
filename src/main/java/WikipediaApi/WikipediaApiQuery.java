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
    public WikipediaApiQuery allowRedirect(){
        this.addValue2Key("redirects","1","|");
        return this;
    }

    /**
     *
     * @param url
     * @return encoded url
     *
     * The api use the "|" char that cause a bad formed uri.
     * Other encoder like URLEncoder.encode replace another character that should remain in uri, which
     * cause another bad formed uri.
     */
    public static String encoder(String url){
        return url.replace("|", "%7C").replace("<","%3C").replace(">","%3E");
    }

    public String get(){
        Set<String> keys = this.propMap.keySet();
        String result = "";
        for (String key : keys ) {
            result += key+"="+this.propMap.get(key)+"&";
        }
        return encoder(result);
    }
}
