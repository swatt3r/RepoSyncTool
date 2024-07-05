package swatter.reposync.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository implements Serializable {
    String name;

    public String getName() {
        return name;
    }

}
