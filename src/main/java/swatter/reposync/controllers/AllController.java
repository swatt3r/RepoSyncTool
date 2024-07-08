package swatter.reposync.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swatter.reposync.entities.Repository;
import swatter.reposync.exceptions.ExceptionWithStatusCode;
import swatter.reposync.services.GitHubService;
import swatter.reposync.services.GitService;

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
    public ResponseEntity<?> getAllRepositories() {
        List<Repository> repositoryList;
        try {
            repositoryList = gitHubService.getRepositories();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(repositoryList);
    }

    @PostMapping("/source")
    public ResponseEntity<?> postAllRepoFromSource() {
        List<Repository> repositoryList = gitHubService.getRepositories();

        for (Repository repository : repositoryList) {
            try {
                gitService.syncToLocal(repository.getName());
            } catch (ExceptionWithStatusCode e) {
                return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(e.getMessage());
            }
        }

        return ResponseEntity.ok("Success");
    }

    @PostMapping("/target")
    public ResponseEntity<?> postAllRepoToTarget() {
        List<String> repositoryList = gitService.getAllLocalRepos();

        for (String repo : repositoryList) {
            try {
                gitService.syncToTarget(repo);
            } catch (ExceptionWithStatusCode e) {
                return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(e.getMessage());
            }
        }

        return ResponseEntity.ok("Success");
    }
}
