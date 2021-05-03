import WikipediaApi.WikipediaApiQuery;
import WikipediaApi.*;
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
}
