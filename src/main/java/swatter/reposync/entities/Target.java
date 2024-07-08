package swatter.reposync.entities;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import swatter.reposync.services.GitLabService;

@Component
public class Target {
    private final String username;

    private final GitLabService gitLabService;

    private final CredentialsProvider credentialsProvider;

    @Autowired
    public Target(GitLabService gitLabService, Environment env) {
        this.credentialsProvider =
                new UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", env.getProperty("gitlabPAT", ""));
        this.gitLabService = gitLabService;
        this.username = env.getProperty("gitlabUsername", "");
    }

    public String getUsername() {
        return this.username;
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
