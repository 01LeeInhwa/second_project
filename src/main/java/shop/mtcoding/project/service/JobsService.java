package shop.mtcoding.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import shop.mtcoding.project.config.exception.CustomApiException;
import shop.mtcoding.project.dto.jobs.JobsReq.JobsSearchReqDto;
import shop.mtcoding.project.dto.jobs.JobsReq.JobsUpdateReqDto;
import shop.mtcoding.project.dto.jobs.JobsReq.JobsWriteReqDto;
import shop.mtcoding.project.model.comp.CompRepository;
import shop.mtcoding.project.model.jobs.Jobs;
import shop.mtcoding.project.model.jobs.JobsRepository;
import shop.mtcoding.project.model.skill.SkillRepository;

@Transactional(readOnly = true)
@Service
public class JobsService {
    
    @Autowired
    private JobsRepository jobsRepository;

    @Autowired
    private CompRepository compRepository;

    @Autowired
    private SkillRepository skillRepository;


    public void 공고검색(JobsSearchReqDto jDto) {
    }

    @Transactional
    public Integer 공고작성(JobsWriteReqDto jDto, Integer compId) {
        Integer jobsId = 0;
        if ( compId != jDto.getCompId()){
            throw new CustomApiException("정상적인 접근이 아닙니다.", HttpStatus.FORBIDDEN);
        }

        JobsUpdateReqDto jUDto = new JobsUpdateReqDto();
        jUDto.setCompName(jDto.getCompName());
        jUDto.setPhoto(jDto.getPhoto());
        jUDto.setRepresentativeName(jDto.getRepresentativeName());
        jUDto.setHomepage(jDto.getHomepage());

        try {
            compRepository.updateById(jUDto);
        } catch (Exception e) {
            throw new CustomApiException("서버에 일시적인 오류가 발생했습니다11.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            jobsRepository.insert(jDto);
            jobsId = jDto.getJobsId();
        } catch (Exception e) {
            throw new CustomApiException("서버에 일시적인 오류가 발생했습니다22.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if ( !ObjectUtils.isEmpty(jDto.getSkillList()) ){

            try {
                skillRepository.insertRequiredSkill(jDto.getSkillList(),jDto.getJobsId());
            } catch (Exception e) {
                throw new CustomApiException("서버에 일시적인 오류가 발생했습니다33.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return jobsId;
    }

    @Transactional
    public Integer 공고수정(JobsUpdateReqDto jDto, Integer compId) {
        Integer jobsId = 0;
        if ( compId != jDto.getCompId()){
            throw new CustomApiException("정상적인 접근이 아닙니다.", HttpStatus.FORBIDDEN);
        }
        try {
            compRepository.updateById(jDto);
        } catch (Exception e) {
            throw new CustomApiException("서버에 일시적인 오류가 발생했습니다11.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            jobsRepository.updateById(jDto);
            jobsId = jDto.getJobsId();
        } catch (Exception e) {
            throw new CustomApiException("서버에 일시적인 오류가 발생했습니다22.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            skillRepository.deleteByJobsId(jDto.getJobsId());
        } catch (Exception e) {
            throw new CustomApiException("서버에 일시적인 오류가 발생했습니다22.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if ( !ObjectUtils.isEmpty(jDto.getSkillList()) ){
            try {
                skillRepository.insertRequiredSkill(jDto.getSkillList(),jDto.getJobsId());
                // skillRepository.updateRequiredSkillById(jDto.getSkillList());
            } catch (Exception e) {
                throw new CustomApiException("서버에 일시적인 오류가 발생했습니다33.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return jobsId;
    }

    @Transactional
    public void 공고삭제(Integer id, Integer compId) {
        Jobs jobsPS = jobsRepository.findById(id);
        if ( jobsPS == null ){
            throw new CustomApiException("해당 공고가 존재 하지 않습니다.");
        }
        if ( jobsPS.getCompId() != compId){
            throw new CustomApiException("공고 삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        try {
            jobsRepository.deleteById(id);
        } catch (Exception e) {
            throw new CustomApiException("서버에 일시적인 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
