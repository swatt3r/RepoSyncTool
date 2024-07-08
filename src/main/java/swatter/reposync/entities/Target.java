package swatter.reposync.entities;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import swatter.reposync.services.GitLabService;

@Component
public class Target {
    private final Environment env;

    private final GitLabService gitLabService;

    private final CredentialsProvider credentialsProvider;

    @Autowired
    public Target(GitLabService gitLabService, Environment env) {
        this.credentialsProvider =
                new UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", env.getProperty("gitlabPAT", ""));
        this.gitLabService = gitLabService;
        this.env = env;
    }

    public String getUsername() {
        return this.env.getProperty("gitlabUsername", "");
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
