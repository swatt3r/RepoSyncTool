package swatter.reposync.entities;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import swatter.reposync.services.GitHubService;

@Component
public class Source {
    private final String username;

    private final GitHubService gitHubService;

    private final CredentialsProvider credentialsProvider;

    public Source(@Autowired GitHubService gitHubService, Environment env) {
        this.credentialsProvider = new UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", env.getProperty("githubPAT", ""));
        this.gitHubService = gitHubService;
        this.username = env.getProperty("githubUsername", "");
    }

    public String getUsername() {
        return this.username;
    }

    public GitHubService getService() {
        return this.gitHubService;
    }

    public CredentialsProvider getCredentials() {
        return credentialsProvider;
    }

    public String getURL() {
        return "https://github.com/";
    }
}
