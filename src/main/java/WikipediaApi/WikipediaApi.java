package WikipediaApi;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class WikipediaApi {
    public static String address = "https://en.wikipedia.org/w/api.php?format=json&action=query&explaintext&redirects=1&prop=info|extracts&inprop=url&";
    public static String actionQuery = "action=query&explaintext&format=json&";
    public static final String api = "https://en.wikipedia.org/w/api.php";

    private static String makeHttpGetRequest(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Now it's "open", we can set the request method, headers etc.
        connection.setRequestProperty("accept", "application/json");

        // This line makes the request
        InputStream responseStream = connection.getInputStream();


        // Finally we have the response
        String string = IOUtils.toString(responseStream, StandardCharsets.UTF_8);

        return string;
    }


    public JSONObject makeQuery(WikipediaApiQuery query) throws IOException {
        return this.makeQuery(query.get());
    }



    public JSONObject makeQuery(String query) throws IOException {
        URL url = new URL(api + "?" + actionQuery + '&' + query);
        return this.makeQuery(url);
    }

    public JSONObject makeQuery(URL url) throws IOException {
        // Open a connection(?) on the URL(??) and cast the response(???)
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Now it's "open", we can set the request method, headers etc.
        connection.setRequestProperty("accept", "application/json");

        // This line makes the request
        InputStream responseStream = connection.getInputStream();


        // Finally we have the response
        String string = IOUtils.toString(responseStream, StandardCharsets.UTF_8);

        try{
            JSONObject jsonObject = new JSONObject(string);
            JSONObject response = jsonObject.getJSONObject("query").getJSONObject("pages");
            String firstKey = response.keys().next();
            if (!firstKey.equals("-1"))
                return response.getJSONObject(firstKey);

        } catch (JSONException err) {
            System.out.println("Exception : "+err.toString());
        }
        return null;
    }

    public JSONObject searchByTitle(String title){
        String query = "titles=" + title.replace(' ','+');
        try {
            return makeQuery(query);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
