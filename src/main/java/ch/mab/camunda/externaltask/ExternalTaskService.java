package ch.mab.camunda.externaltask;

import camundajar.impl.com.google.gson.Gson;
import camundajar.impl.com.google.gson.reflect.TypeToken;
import org.camunda.bpm.engine.rest.dto.externaltask.FetchExternalTasksDto;
import org.camunda.bpm.engine.rest.dto.externaltask.LockedExternalTaskDto;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// https://docs.camunda.org/manual/7.13/reference/rest/external-task/fetch/

@Service
public class ExternalTaskService {


    private final Gson gson = new Gson();

    public ExternalTaskService() {}

    public String post(String jsonPayload) throws IOException {
        // https://www.baeldung.com/httpurlconnection-post
        URL url = new URL("http://localhost:8080/external-task/fetchAndLock");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        return readResponse(connection);
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            return response.toString();
        }
    }

    public List<LockedExternalTaskDto> fetchAndLock(String topicName, String workerId, int maxTasksToFetch) {
        FetchExternalTasksDto fetchExternalTasksDto = new FetchExternalTasksDto();
        fetchExternalTasksDto.setWorkerId(workerId);
        fetchExternalTasksDto.setMaxTasks(maxTasksToFetch);

        FetchExternalTasksDto.FetchExternalTaskTopicDto fetchExternalTaskTopicDto = new FetchExternalTasksDto.FetchExternalTaskTopicDto();
        fetchExternalTaskTopicDto.setTopicName(topicName);
        fetchExternalTasksDto.setTopics(Collections.singletonList(fetchExternalTaskTopicDto));

        String jsonPayload = gson.toJson(fetchExternalTasksDto);

        try {
            String response = post(jsonPayload);
            Type listType = new TypeToken<ArrayList<LockedExternalTaskDto>>() {}.getType();
            return new Gson().fromJson(response, listType);
        } catch (IOException e) {
            // handle exception
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
