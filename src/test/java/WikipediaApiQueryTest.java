import WikipediaApi.WikipediaApiQuery;
import org.junit.Test;
import static org.junit.Assert.*;

public class WikipediaApiQueryTest {
    @Test
    public void correctUriTest(){
        WikipediaApiQuery api = new WikipediaApiQuery();
        String result = WikipediaApiQuery.CreateQuery().allowRedirect().withAbstract().withUrl().queryByTitle("Nelson Mandela").get();
        assertFalse(result.contains("|"));
    }

    @Test
    public void makeUriTest(){
        WikipediaApiQuery apiQuery = new WikipediaApiQuery();
        String result = WikipediaApiQuery.CreateQuery().allowRedirect().withAbstract().withUrl().queryByTitle("Nelson Mandela").get();
        String expected = "explaintext&inprop=url&prop=extracts%7Cinfo&redirects=1&titles=Nelson+Mandela&";
        assertEquals(expected,result);
    }

}
