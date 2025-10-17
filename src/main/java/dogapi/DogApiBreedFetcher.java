package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final String STATUS = "status";
    private final String SUCCESS_CODE = "success";
    private final String MESSAGE = "message";
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        final Request request = new Request.Builder()
                .url("https://dog.ceo/api/breed/" + breed + "/list")
                .build();

        try {
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getString(STATUS).equals(SUCCESS_CODE)) {
                final JSONArray breeds = responseBody.getJSONArray(MESSAGE);
                final List<String> subBreedsList = new ArrayList<>();
                for (int i = 0; i < breeds.length(); i++) {
                    subBreedsList.add(breeds.getString(i));
                }
                return subBreedsList;
            }
            else {
                throw new BreedNotFoundException(responseBody.getString(MESSAGE));
            }
        }
        catch (IOException e) {
            throw new BreedNotFoundException(breed);
        }
    }
}