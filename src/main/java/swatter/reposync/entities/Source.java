package swatter.reposync.entities;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import swatter.reposync.services.GitHubService;

@Component
public class Source {
    @Value("${githubPAT}")
    private String githubPAT;

    @Value("${githubUsername}")
    private String githubUsername;

    private final GitHubService gitHubService;

    private final CredentialsProvider credentialsProvider;

    @Autowired
    public Source(GitHubService gitHubService) {
        this.credentialsProvider = new UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", githubPAT);
        this.gitHubService = gitHubService;
    }

    public String getUsername() {
        return this.githubUsername;
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
