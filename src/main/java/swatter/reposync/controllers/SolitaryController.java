package swatter.reposync.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swatter.reposync.exceptions.ExceptionWithStatusCode;
import swatter.reposync.services.GitService;

import java.io.IOException;

@RestController
@RequestMapping("/solitary")
public class SolitaryController {
    private final GitService gitService;

    @Autowired
    public SolitaryController(GitService gitService) {
        this.gitService = gitService;
    }

    @PostMapping("/source")
    public void postRepoFromSource(@RequestBody String repoName, HttpServletResponse response) throws IOException {
        try {
            gitService.syncToLocal(repoName);
        } catch (ExceptionWithStatusCode e) {
            response.getWriter().println(e.getMessage());
            response.setStatus(e.getStatusCode());
            return;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @PostMapping("/target")
    public void postRepoToTarget(@RequestBody String repoName, HttpServletResponse response) throws IOException {
        try {
            gitService.syncToTarget(repoName);
        } catch (ExceptionWithStatusCode e) {
            response.getWriter().println(e.getMessage());
            response.setStatus(e.getStatusCode());
            return;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
