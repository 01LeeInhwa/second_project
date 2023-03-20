package shop.mtcoding.project.model.resume;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import shop.mtcoding.project.dto.resume.ResumeReq.ResumeCheckboxReqDto;
import shop.mtcoding.project.dto.resume.ResumeReq.ResumeUpdateReqDto;
import shop.mtcoding.project.dto.resume.ResumeReq.ResumeWriteReqDto;
import shop.mtcoding.project.dto.resume.ResumeResp.ResumeDetailRespDto;
import shop.mtcoding.project.dto.resume.ResumeResp.ResumeIdRespDto;
import shop.mtcoding.project.dto.resume.ResumeResp.ResumeManageRespDto;
import shop.mtcoding.project.dto.resume.ResumeResp.ResumeMatchRespDto;
import shop.mtcoding.project.dto.resume.ResumeResp.ResumeReadRespDto;
import shop.mtcoding.project.dto.resume.ResumeResp.ResumeSaveRespDto;
import shop.mtcoding.project.dto.resume.ResumeResp.ResumeSearchRespDto;
import shop.mtcoding.project.dto.resume.ResumeResp.ResumeUpdateRespDto;

@Mapper
public interface ResumeRepository {
        public void findAll();

        public ResumeSaveRespDto findById(Integer resumeId);

        public List<ResumeIdRespDto> findResumeIdByUserId(Integer userId);
        
        public List<ResumeMatchRespDto> findMatchResumeByCompId(Integer compId);

        public ResumeUpdateRespDto findUpdateById(Integer resumeId);

        public Resume findByResumeId(Integer resumeId);

        public List<ResumeManageRespDto> findAllByUserId(Integer userId);

        public List<ResumeReadRespDto> findAllResumebyState();

        public ResumeDetailRespDto findDetailPublicResumebyById(
                @Param("resumeId") Integer resumeId,
                @Param("compId") Integer compId
        );
        public List<ResumeSearchRespDto> findResumeByCheckBox(
                @Param("resumeDto") ResumeCheckboxReqDto resumeDto
        );

        public int insert(
                @Param("rDto") ResumeWriteReqDto rDto);

        public int updateById(
                @Param("rDto") ResumeUpdateReqDto rDto);

        public int deleteById(Integer resumeId);
}
