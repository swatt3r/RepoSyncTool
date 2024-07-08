package swatter.reposync.entities;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import swatter.reposync.services.GitLabService;

@Component
public class Target {
    @Value("${gitlabPAT}")
    private String gitlabPAT;

    @Value("${gitlabUsername}")
    private String gitlabUsername;

    private final GitLabService gitLabService;

    private final CredentialsProvider credentialsProvider;

    @Autowired
    public Target(GitLabService gitLabService) {
        this.credentialsProvider = new UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", gitlabPAT);
        this.gitLabService = gitLabService;
    }

    public String getUsername() {
        return this.gitlabUsername;
    }

    public GitLabService getService() {
        return this.gitLabService;
    }

    public CredentialsProvider getCredentials() {
        return this.credentialsProvider;
    }


    public String getURL() {
        return "https://gitlab.com/";
    }
}
