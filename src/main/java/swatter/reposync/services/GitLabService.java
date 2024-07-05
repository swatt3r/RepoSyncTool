package swatter.reposync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import swatter.reposync.exceptions.ExceptionWithStatusCode;

import java.util.HashMap;
import java.util.Map;

@Service
public class GitLabService {

    @Value("${gitlabPAT}")
    private String gitlabPAT;
    private final WebClient webClient;

    @Autowired
    public GitLabService(WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean createRepo(String repoName) {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("name", repoName);

        try {
            webClient
                    .post()
                    .uri("https://gitlab.com/api/v4/projects")
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(gitlabPAT))
                    .body(BodyInserters.fromValue(bodyMap))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            error -> Mono.error(new ExceptionWithStatusCode("Authentication to the remote source cannot be performed", 401)))
                    .onStatus(HttpStatusCode::is5xxServerError,
                            error -> Mono.error(new ExceptionWithStatusCode("Remote source server is not responding", 500)))
                    .toBodilessEntity()
                    .block();
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }
}
