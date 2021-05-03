import WikipediaApi.WikipediaApiQuery;
import org.junit.Test;
import static org.junit.Assert.*;

public class WikipediaApiQueryTest {
    @Test
    public void correctUriTest(){
        WikipediaApiQuery api = new WikipediaApiQuery();
        String result = api.allowRedirect().withAbstract().withUrl().searchByTitle("Nelson Mandela").get();
        assertFalse(result.contains("|"));
    }

    @Test
    public void makeUriTest(){
        WikipediaApiQuery apiQuery = new WikipediaApiQuery();
        String result = apiQuery.allowRedirect().withAbstract().withUrl().searchByTitle("Nelson Mandela").get();
        String expected = "inprop=url&prop=extracts%7Cinfo&redirects=1&titles=Nelson+Mandela&";
        assertEquals(result,expected);
    }
}
