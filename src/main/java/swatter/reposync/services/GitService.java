package swatter.reposync.services;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swatter.reposync.exceptions.ExceptionWithStatusCode;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GitService {
    @Value("${localRepoDir}")
    private String localRepoDir;

    @Value("${githubUsername}")
    private String githubUsername;

    @Value("${gitlabUsername}")
    private String gitlabUsername;

    private final CredentialsProvider gitlabCredentials;

    private final CredentialsProvider githubCredentials;

    private final GitLabService gitLabService;

    @Autowired
    public GitService(CredentialsProvider gitlabCredentials, CredentialsProvider githubCredentials, GitLabService gitLabService) {
        this.gitlabCredentials = gitlabCredentials;
        this.githubCredentials = githubCredentials;
        this.gitLabService = gitLabService;
    }

    public void syncToLocal(String repoName) throws GitAPIException, ExceptionWithStatusCode {
        List<Ref> refs;

        String remoteURL = "https://github.com/" + githubUsername + "/" + repoName + ".git";

        File localRepo = new File(localRepoDir + repoName);
        if (!localRepo.exists()) {
            cloneToLocal(remoteURL, localRepo);
            return;
        }

        try (Git git = Git.open(localRepo)) {
            refs = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();

            for (Ref ref : refs) {
                String branch = ref.getName().substring(ref.getName().lastIndexOf("/") + 1);

                try {
                    git.checkout()
                            .setName(branch)
                            .call();
                } catch (GitAPIException e) {
                    git.branchCreate().setName(branch).call();
                    git.checkout()
                            .setName(branch)
                            .call();
                }


                git.pull()
                        .setRemote("origin")
                        .setRemoteBranchName(branch)
                        .setCredentialsProvider(githubCredentials)
                        .call();
            }
        } catch (IOException e) {
            throw new ExceptionWithStatusCode("Local repository \"" + repoName + "\" cannot be opened", 400);
        }
    }

    private void cloneToLocal(String remoteURL, File localRepo) throws ExceptionWithStatusCode {
        try (Git git = Git.cloneRepository()
                .setURI(remoteURL)
                .setDirectory(localRepo)
                .setCredentialsProvider(githubCredentials)
                .call()) {
            List<Ref> refs = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();

            for (Ref ref : refs) {
                String branch = ref.getName().substring(ref.getName().lastIndexOf("/") + 1);

                try {
                    git.branchCreate().setName(branch).call();
                } catch (GitAPIException ignored) {
                }
            }
        } catch (GitAPIException e) {
            try {
                FileUtils.cleanDirectory(localRepo);
                FileUtils.deleteDirectory(localRepo);
                throw new ExceptionWithStatusCode("Local repository \"" + localRepo.getName() + "\" cannot be created", 400);
            } catch (IOException ignored) {
            }
        }
    }

    public void syncToTarget(String repoName) throws GitAPIException, ExceptionWithStatusCode {
        File localRepo = new File(localRepoDir + repoName);
        if (!localRepo.exists()) {
            throw new ExceptionWithStatusCode("There is no local repository", 400);
        }

        try (Git git = Git.open(localRepo)) {
            String remoteURL = "https://gitlab.com/" + gitlabUsername + "/" + repoName + ".git";

            if (gitLabService.createRepo(repoName)) {
                RemoteAddCommand remoteAddCommand = git.remoteAdd();
                remoteAddCommand.setName("newOrigin");
                remoteAddCommand.setUri(new URIish(remoteURL));
                remoteAddCommand.call();
            }

            List<Ref> refs = git.branchList().call();
            PushCommand pushCommand = git.push();

            for (Ref ref : refs) {
                pushCommand.add(ref);
            }

            pushCommand.setRemote("newOrigin")
                    .setCredentialsProvider(gitlabCredentials)
                    .call();

        } catch (IOException e) {
            throw new ExceptionWithStatusCode("Local repository \"" + repoName + "\" cannot be opened", 400);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllLocalRepos() {
        List<String> reposNames = new ArrayList<>();
        File localRepo = new File(localRepoDir);

        if (localRepo.isDirectory()) {
            for (File repo : Objects.requireNonNull(localRepo.listFiles())) {
                reposNames.add(repo.getName());
            }
        }
        return reposNames;
    }
}
