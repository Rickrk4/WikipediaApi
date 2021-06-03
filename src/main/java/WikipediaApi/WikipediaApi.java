package WikipediaApi;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WikipediaApi {

    @Deprecated
    public static String address = "https://en.wikipedia.org/w/api.php?format=json&action=query&explaintext&redirects=1&prop=info|extracts&inprop=url&";
    public static String actionQuery = "action=query&format=json&";
    public static final String api = "https://en.wikipedia.org/w/api.php";

    public static class WikipediaApiResponse{

        private HttpResponse<Supplier<JSONObject>> response;
        private JSONObject responseBody;
        private JSONObject pages;
        private JSONArray allPages;
        public WikipediaApiResponse(HttpResponse<Supplier<JSONObject>> response) {
            this.response = response;
            this.pages = null;
        }

        private void handleResponse(){
            this.responseBody = this.response.body().get().getJSONObject("query");
            if (responseBody.has("searchinfo")){
                allPages = responseBody.getJSONArray("search");
                if (responseBody.getJSONObject("searchinfo").getInt("totalhits") > 0){
                    pages = allPages.getJSONObject(0);
                }
            } else {
                this.pages = responseBody.getJSONObject("pages");
                String firstPage = pages.keys().next();
                if (firstPage.equals("-1")){
                    pages = null;
                }
            }
        }

        /**
         *
         * This method return the first page retrieved from wikipedia or null if no pages are found.
         * @return JSONObject with the fields requested in the query.
         */
        public JSONObject getPage(){

            if (this.responseBody == null){
                handleResponse();
                return this.pages;
            }


            if (this.pages == null) {
                try {
                    this.pages = this.response.body().get().getJSONObject("query").getJSONObject("pages");
                    String firstPage = pages.keys().next();
                    if (firstPage.equals("-1")){
                        return null;
                    }
                    return pages.getJSONObject(firstPage);
                } catch (org.json.JSONException e){
                    return null;
                }
            }
            return null;
        }

        public JSONArray getPages(){
            if (responseBody == null){
                handleResponse();
            }
            return allPages;
        }

        public HttpResponse<Supplier<JSONObject>> getResponse(){
            return this.response;
        }

        public static class WikipediaApiResponseSupplier implements Supplier<WikipediaApiResponse> {
            private HttpClient client;
            private HttpRequest request;

            public WikipediaApiResponseSupplier(HttpClient client, HttpRequest request) {
                this.client = client;
                this.request = request;
            }


            @Override
            public WikipediaApiResponse get() {
                try {
                    HttpResponse<Supplier<JSONObject>> response = client.send(request, new JsonBodyHandler<JSONObject>(JSONObject.class));
                    return new WikipediaApiResponse(response);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }

    @Deprecated
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


    /**
     * Extract uri from WikipediaApiQuery and return the result of the makeQuery method with uri.
     *
     * @param query as WikipediaApiQuery object
     * @return WikipediaApiResponse
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public WikipediaApiResponse makeQuery(WikipediaApiQuery query) throws IOException, URISyntaxException, InterruptedException {
        return makeQuery(makeUri(query.get()));
    }


    @Deprecated
    public JSONObject makeQuery(String query) throws IOException {
        URL url = new URL(api + "?" + actionQuery + '&' + query);
        return this.makeQuery(url);
    }

    private URI makeUri(String args) throws URISyntaxException {
        return new URI(api + "?" + actionQuery + '&' + args);
    }

    /**
     *
     * @param uri the uri of the http request
     * @return WikipediaApiResponse
     * @throws IOException
     * @throws InterruptedException
     */
    public WikipediaApiResponse makeQuery(URI uri) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(uri).header("Accept","application/json").build();
        var response = client.send(request, new JsonBodyHandler<JSONObject>(JSONObject.class));
        WikipediaApiResponse wikipediaApiResponse = new WikipediaApiResponse(response);
        return  wikipediaApiResponse;
    }

    @Deprecated
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

    /**
     *
     * @param uri
     * @return CompletableFuture that retrive a WikipediaApiResponse
     */
    public CompletableFuture makeQueryAsync(URI uri){
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(uri).header("Accept","application/json").build();
        return CompletableFuture.supplyAsync(new WikipediaApiResponse.WikipediaApiResponseSupplier(client, request));
    }

    public CompletableFuture makeQueryAsync(WikipediaApiQuery query) throws URISyntaxException {
        return makeQueryAsync(makeUri(query.get()));
    }

    @Deprecated
    public JSONObject searchByTitle(String title){
        String query = "titles=" + title.replace(' ','+');
        try {
            return makeQuery(query);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    public CompletableFuture searchByTitleAsync(String title) throws URISyntaxException {
        String query = "titles=" + title.replace(' ','+');
        return makeQueryAsync(makeUri(query));
    }
}
