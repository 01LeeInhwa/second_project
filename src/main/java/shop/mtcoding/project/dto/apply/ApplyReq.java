package shop.mtcoding.project.dto.apply;

import lombok.Getter;
import lombok.Setter;

public class ApplyReq {
    
    @Getter
    @Setter
    public static class ApplyReqDto{
        private Integer resumeId;
        private Integer jobsId;
        private Integer userId;
        private Integer applyId;
    }

    @Getter
    @Setter
    public static class ApplyUpdateReqDto{
        private Integer applyId;
        private Integer compId;
        private Integer state;
    }
    
}
