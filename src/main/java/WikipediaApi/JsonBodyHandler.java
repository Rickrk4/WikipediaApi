package WikipediaApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<Supplier<JSONObject>> {

    private static final ObjectMapper om = new ObjectMapper();
    private final Class<T> targetClass;


    public JsonBodyHandler(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public HttpResponse.BodySubscriber<Supplier<JSONObject>> apply(HttpResponse.ResponseInfo responseInfo) {
        return asJSON(this.targetClass);
    }


    public static <W> HttpResponse.BodySubscriber<Supplier<JSONObject>> asJSON(Class<W> targetType) {
        HttpResponse.BodySubscriber<String> upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);

        return HttpResponse.BodySubscribers.mapping(
                upstream,
                inputStream -> {
                    return new Supplier<JSONObject>() {
                        @Override
                        public JSONObject get() {
                            return new JSONObject(inputStream);
                        }
                    };
                });
    }
}
