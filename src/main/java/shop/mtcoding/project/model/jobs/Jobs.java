package shop.mtcoding.project.model.jobs;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Jobs {
    private int jobsId;
    private String title;
    private String content;
    private String position;
    private String career;
    private String education;
    private String address;
    private Integer scrapCount;
    private Timestamp endDate;
    private Integer compId;
    private String homepage;
    private Timestamp createdAt;
}
