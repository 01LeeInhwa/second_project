package shop.mtcoding.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.mtcoding.project.config.exception.CustomApiException;
import shop.mtcoding.project.dto.apply.ApplyReq.ApplyReqDto;
import shop.mtcoding.project.dto.apply.ApplyReq.ApplyUpdateReqDto;
import shop.mtcoding.project.model.apply.Apply;
import shop.mtcoding.project.model.apply.ApplyRepository;
import shop.mtcoding.project.model.jobs.Jobs;
import shop.mtcoding.project.model.jobs.JobsRepository;
import shop.mtcoding.project.model.notiify.NotifyRepository;
import shop.mtcoding.project.model.resume.Resume;
import shop.mtcoding.project.model.resume.ResumeRepository;

@Transactional(readOnly = true)
@Service
public class ApplyService {
    
    @Autowired
    private ApplyRepository applyRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private JobsRepository jobsRepository;

    @Autowired
    private NotifyRepository notifyRepository;

    @Transactional
    public void 지원하기(ApplyReqDto aDto, Integer userId){
        if ( userId != aDto.getUserId()){
            throw new CustomApiException("정상적인 접근이 아닙니다." , HttpStatus.FORBIDDEN);
        }
        Resume resumePS = resumeRepository.findByResumeId(aDto.getResumeId());
        if ( resumePS == null){
            throw new CustomApiException("존재하지 않는 이력서 입니다.");
        }
        Jobs jobsPS = jobsRepository.findById(aDto.getJobsId());
        if ( jobsPS == null ){
            throw new CustomApiException("존재하지 않는 공고 입니다.");
        }
        Integer result = 0;
        try {
            applyRepository.insert(aDto);
            result = aDto.getApplyId();
        } catch (Exception e) {
            throw new CustomApiException("서버에 일시적인 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            notifyRepository.insert(result, null);
        } catch (Exception e) {
            throw new CustomApiException("서버에 일시적인 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Integer 합격(ApplyUpdateReqDto aDto, Integer compId) {
        if (compId != aDto.getCompId()){
            throw new CustomApiException("수정 권한이 없습니다." , HttpStatus.FORBIDDEN);
        }
        Apply applyPS = applyRepository.findByApplyId(aDto.getApplyId());
        if ( applyPS == null){
            throw new CustomApiException("존재하지 않는 지원자입니다.");
        }
        try {
            applyRepository.updateByApplyId(aDto);
        } catch (Exception e) {
            throw new CustomApiException("서버에 일시적인 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return 1;
    }

    @Transactional
    public Integer 불합격(ApplyUpdateReqDto aDto, Integer compId) {
        if (compId != aDto.getCompId()){
            throw new CustomApiException("수정 권한이 없습니다." , HttpStatus.FORBIDDEN);
        }
        Apply applyPS = applyRepository.findByApplyId(aDto.getApplyId());
        if ( applyPS == null){
            throw new CustomApiException("존재하지 않는 지원자입니다.");
        }
        try {
            applyRepository.updateByApplyId(aDto);
        } catch (Exception e) {
            throw new CustomApiException("서버에 일시적인 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return 1;
    }
}

