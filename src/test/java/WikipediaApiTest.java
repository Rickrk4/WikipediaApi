import WikipediaApi.WikipediaApiQuery;
import WikipediaApi.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class WikipediaApiTest {
    @Mock
    WikipediaApiQuery query;
    @Before
    public void setMock(){
        when(query.get()).thenReturn("inprop=url&prop=extracts%7Cinfo&redirects=1&titles=Nelson+Mandela&");
    }
    @Test
    public void queryTest() throws IOException, URISyntaxException, InterruptedException {
        WikipediaApi api = new WikipediaApi();
        //when(query.get()).thenReturn("inprop=url&prop=extracts%7Cinfo&redirects=1&titles=Nelson+Mandela&");
        WikipediaApi.WikipediaApiResponse response = api.makeQuery(query);
        assertNotNull(response);
        assertNotNull(response.getPage());
    }


    @Test
    public void asyncQueryTest() throws URISyntaxException, ExecutionException, InterruptedException {
        WikipediaApi api = new WikipediaApi();
        CompletableFuture<WikipediaApi.WikipediaApiResponse> future = api.makeQueryAsync(query);
        assertNotNull(future);
        WikipediaApi.WikipediaApiResponse response = future.get();
        assertNotNull(response);
        assertNotNull(response.getPage());
    }

    @Test
    public void queryTest2() throws IOException, URISyntaxException, InterruptedException {
        String search = "specimen with known storage state";
        WikipediaApi api = new WikipediaApi();
        WikipediaApiQuery query1 = new WikipediaApiQuery();
        WikipediaApi.WikipediaApiResponse response = api.makeQuery(query1.withUrl().withAbstract().allowRedirect().queryByTitle(search));
        JSONObject jsonObject = response.getPage();
        System.out.println(response.getPage() == null);
    }

    @Test
    public void illegalCharacter() throws IOException, InterruptedException {
        String str = "specimen+was+processed+in+accordance+with+the+FFPE+SOP+including+<1+hour+delay+to+fixation+and+23+hour+time+in+fixative";
        WikipediaApi api = new WikipediaApi();
        WikipediaApiQuery query1 = new WikipediaApiQuery();
        try {
            WikipediaApi.WikipediaApiResponse response = api.makeQuery(query1.withUrl().withAbstract().allowRedirect().queryByTitle(str.replace('<','+')));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void findByIdTest(){
        String id = "21492751";
        WikipediaApi api = new WikipediaApi();
        WikipediaApiQuery query1 = new WikipediaApiQuery();
        try {
            WikipediaApi.WikipediaApiResponse response = api.makeQuery(query1.withUrl().withAbstract().allowRedirect().queryByPageId(id));
            assertNotNull( response.getPage());
        } catch (IOException | URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findTest() throws IOException, URISyntaxException, InterruptedException {
        WikipediaApi api = new WikipediaApi();
        WikipediaApiQuery query = new WikipediaApiQuery();
        WikipediaApi.WikipediaApiResponse response = api.makeQuery(query.withUrl().withAbstract().queryByTitle("Nelson Mandela"));
        assertNotNull(response.getPage());
    }

    @Test
    public void searchMultiTitleTest(){
        String id = "Nelson";
        WikipediaApi api = new WikipediaApi();
        WikipediaApiQuery query1 = new WikipediaApiQuery();
        try {
            WikipediaApi.WikipediaApiResponse response = api.makeQuery(query1.searchByTitle(id));
            JSONArray jsonArray = response.getPages();
            assertNotNull(jsonArray);
            assertFalse(jsonArray.isEmpty());
        } catch (IOException | URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void urlWithSearchTest(){
        String id = "Nelsonno";
        WikipediaApi api = new WikipediaApi();
        WikipediaApiQuery query1 = new WikipediaApiQuery();
        try {
            WikipediaApi.WikipediaApiResponse response = api.makeQuery(query1.withAbstract().withUrl().searchByTitle(id));
            JSONObject jsonObject = response.getPage();

        } catch (IOException | URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
