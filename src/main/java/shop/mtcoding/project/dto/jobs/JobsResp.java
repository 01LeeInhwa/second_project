package shop.mtcoding.project.dto.jobs;

import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class JobsResp {

    @Getter
    @Setter
    public static class JobsIdRespDto{
        private Integer jobsId;
    }
    @Getter
    @Setter
    @ToString
    public static class JobsMainRespDto {
        private Integer jobsId;
        private String compName;
        private String photo;
        private String title;
        private String career;
        private String education;
        private String position;
        private List<String> skillList;
        private String address;
        private Integer userScrapId;
        private Long leftTime;
        private Timestamp endDate;
    }

    @Getter
    @Setter
    @ToString
    public static class JobsMainRecommendRespDto {
        private Integer jobsId;
        private String compName;
        private String photo;
        private String title;
        private String career;
        private String education;
        private String position;
        private List<String> skillList;
        private String address;
        private Integer userScrapId;
        private Long leftTime;
        private Timestamp endDate;
    }

    @Getter
    @Setter
    @ToString
    public static class JobsMatchRespDto {
        private Integer jobsId;
        private String compName;
        private String photo;
        private String title;
        private String career;
        private String education;
        private String position;
        private List<String> skillList;
        private String address;
        private Integer userScrapId;
        private Long leftTime;
        private Timestamp endDate;
    }

    @Getter
    @Setter
    @ToString
    public static class JobsSearchRespDto {
        private Integer jobsId;
        private String compName;
        private String photo;
        private String title;
        private String career;
        private String education;
        private String position;
        private List<String> skillList;
        private String address;
        private Integer userScrapId;
        private Long leftTime;
        private Timestamp endDate;
    }

    @Getter
    @Setter
    @ToString
    public static class JobsDetailRespDto {
        private Integer jobsId;
        private Integer compId;
        private String photo;
        private String compName;
        private String title;
        private String content;
        private String career;
        private String education;
        private String receipt;
        private List<String> skillList;
        private String position;
        private String address;
        private String homepage;
        private Timestamp endDate;
        private String formatEntDate;
        private String formatEndDate;
        private Integer state;
        private Long leftTime;
        private String representativeName;
        private Integer userScrapId;
    }

    @Getter
    @Setter
    public static class JobsRequiredSkill {
        private String skill;
    }

    @Getter
    @Setter
    public static class JobsUpdateRespDto {
        private Integer jobsId;
        private String photo;
        private String compName;
        private String representativeName;
        private String establishmentDate;
        private Integer employees;
        private String homepage;
        private String title;
        private String content;
        private String education;
        private String career;
        private String position;
        private List<String> skillList;
        private String address;
        private Timestamp endDate;
        private String receipt;
        private Integer userScrapId;
    }

    @Getter
    @Setter
    public static class JobsManageJobsRespDto {
        private Integer num;
        private Integer jobsId;
        private String title;
        private String position;
        private String career;
        private Timestamp endDate;
        private List<String> skillList;
        private Long leftTime;
    }

    @Getter
    @Setter
    @ToString
    public static class JobsSuggestRespDto {
        private Integer jobsId;
        private String title;
        private String position;
        // private List<String> skillList;
        private Timestamp endDate;
    }

}
