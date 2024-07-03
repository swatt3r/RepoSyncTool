package swatter.reposync.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swatter.reposync.entities.Repository;
import swatter.reposync.exceptions.ExceptionWithStatusCode;
import swatter.reposync.services.GitHubService;
import swatter.reposync.services.GitService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/all")
public class AllController {
    private final GitHubService gitHubService;
    private final GitService gitService;

    @Autowired
    public AllController(GitHubService gitHubService, GitService gitService) {
        this.gitHubService = gitHubService;
        this.gitService = gitService;
    }

    @GetMapping("/repos")
    public List<Repository> getAllRepositories(HttpServletResponse response) throws IOException {
        List<Repository> repositoryList = null;
        try {
            repositoryList = gitHubService.getRepositories();
        } catch (RuntimeException e) {
            response.getWriter().println();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        return repositoryList;
    }

    @PostMapping("/source")
    public void postAllRepoFromSource(HttpServletResponse response) throws IOException {
        List<Repository> repositoryList = gitHubService.getRepositories();

        for (Repository repository : repositoryList) {
            try {
                gitService.syncToLocal(repository.getName());
            } catch (ExceptionWithStatusCode e) {
                response.getWriter().println("Exception on repository \"" + repository.getName() + "\": " + e.getMessage());
                response.setStatus(e.getStatusCode());
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @PostMapping("/target")
    public void postAllRepoToTarget(HttpServletResponse response) throws IOException {
        List<String> repositoryList = gitService.getAllLocalRepos();

        for (String repo : repositoryList) {
            try {
                gitService.syncToTarget(repo);
            } catch (ExceptionWithStatusCode e) {
                response.getWriter().println("Exception on repository \"" + repo + "\": " + e.getMessage());
                response.setStatus(e.getStatusCode());
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
