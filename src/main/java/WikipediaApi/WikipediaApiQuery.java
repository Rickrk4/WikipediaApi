package WikipediaApi;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class WikipediaApiQuery {
    /**
     * Here we store the key to add at the query and their values.
     */
    private Map<String, String> propMap;


    public WikipediaApiQuery(){
        this.propMap = new TreeMap<>();
    }

    /**
     * it's the inner method that store the necessary key to obtain the requested data by wikipedia.
     * @param key the name of the key to assert
     * @param value the value of the key
     * @param sep the separator between different values of the same key, because to obtain some specific data, like the
     *            abstract more than one value must be asserted, and some of them is shared with other data.
     */
    private void addValue2Key(String key, String value, String sep){
        String prevValue = this.propMap.containsKey(key) ? this.propMap.get(key)+sep : "" ;
        this.propMap.put(key, prevValue + value);
    }

    /**
     * Request url of the page. Note that this work only with the queries methods.
     * @return
     */
    public WikipediaApiQuery withAbstract(){
        addValue2Key("prop", "extracts", "|");
        addValue2Key("explaintext","","|");
        //String prevValue = this.propMap.containsKey("prop") ? this.propMap.get("prop|") : "";
        //this.propMap.put("prop", prevValue+"extracts");
        return this;
    }

    /**
     * Request the url of the page. Note that this work only with the queries methods.
     * @return
     */
    public WikipediaApiQuery withUrl(){
        this.addValue2Key("inprop","url","|");
        this.addValue2Key("prop","info","|");
        return this;
    }

    /**
     * This mathod make use of the API:QUERY, it request only one page that match exactly with the give title.
     * @param title
     * @return
     */
    public WikipediaApiQuery queryByTitle(String title){
        this.addValue2Key("titles", title.replace(' ','+'),"|");
        return this;
    }

    /**
     * This method make use of the API:SEARCH, it request multiple pages that have the given title as a factor.
     * Note that the methods that use API:SEARCH can't provide much information, request specific information to
     * this methods will not have any effect.
     * @param title
     * @return
     */
    public WikipediaApiQuery searchByTitle(String title){
        title = title.replace(' ','+');
        this.addValue2Key("list", "search","|");
        this.addValue2Key("srsearch", title,"|");
        return this;
    }

    /**
     * It return the page of the given id.
     * @param id
     * @return
     */
    public WikipediaApiQuery queryByPageId(String id){
        this.addValue2Key("pageids", id.replace(' ','+'),"|");
        return this;
    }

    /**
     * If the page matching the title is a redirect page will be returned the page pointed by the redirect page.
     *
     * @return
     */
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

    /**
     * This method make the query unpacking the given parameters.
     * @return
     */
    public String get(){
        Set<String> keys = this.propMap.keySet();
        String result = "";
        for (String key : keys ) {
            String value = this.propMap.get(key);

            result += key + ( !value.isBlank() ? "=" + value : "") + "&";
        }
        return encoder(result);
    }

    public static WikipediaApiQuery CreateQuery(){
        return new WikipediaApiQuery();
    }

}
