package swatter.reposync.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import swatter.reposync.entities.Repository;
import swatter.reposync.exceptions.ExceptionWithStatusCode;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubService {
    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    @Value("${githubPAT}")
    private String githubPAT;

    @Autowired
    public GitHubService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public List<Repository> getRepositories() {
        List<Repository> repositoryList = new ArrayList<>();

        int page = 1;
        while (true) {
            String response = webClient
                    .get()
                    .uri("https://api.github.com/user/repos?page=" + page)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(githubPAT))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            error -> Mono.error(new ExceptionWithStatusCode("Authentication to the remote source cannot be performed", 401)))
                    .onStatus(HttpStatusCode::is5xxServerError,
                            error -> Mono.error(new ExceptionWithStatusCode("Remote source server is not responding", 500)))
                    .bodyToMono(String.class)
                    .block();

            if ("[]" .equals(response)) {
                break;
            }

            try {
                repositoryList.addAll(objectMapper.readValue(response, new TypeReference<>() {
                }));
            } catch (JsonProcessingException ignored) {
            }

            page++;
        }

        return repositoryList;
    }
}
