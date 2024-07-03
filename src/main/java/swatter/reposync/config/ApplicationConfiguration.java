package swatter.reposync.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Value("${githubPAT}")
    private String githubPAT;

    @Value("${gitlabPAT}")
    private String gitlabPAT;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    CredentialsProvider githubCredentials() {
        return new UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", githubPAT);
    }

    @Bean
    CredentialsProvider gitlabCredentials() {
        return new UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", gitlabPAT);
    }
}
