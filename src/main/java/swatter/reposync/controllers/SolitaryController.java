package swatter.reposync.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swatter.reposync.exceptions.ExceptionWithStatusCode;
import swatter.reposync.services.GitService;

@RestController
@RequestMapping("/solitary")
public class SolitaryController {
    private final GitService gitService;

    @Autowired
    public SolitaryController(GitService gitService) {
        this.gitService = gitService;
    }

    @PostMapping("/source")
    public ResponseEntity<?> postRepoFromSource(@RequestBody String repoName) {
        try {
            gitService.syncToLocal(repoName);
        } catch (ExceptionWithStatusCode e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok("Success");
    }

    @PostMapping("/target")
    public ResponseEntity<?> postRepoToTarget(@RequestBody String repoName) {
        try {
            gitService.syncToTarget(repoName);
        } catch (ExceptionWithStatusCode e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok("Success");
    }
}
