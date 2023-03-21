package shop.mtcoding.project.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.project.config.annotation.LoginComp;
import shop.mtcoding.project.config.annotation.LoginUser;
import shop.mtcoding.project.config.exception.CustomException;
import shop.mtcoding.project.dto.common.ResponseDto;
import shop.mtcoding.project.dto.comp.CompResp.CompWriteJobsRespDto;
import shop.mtcoding.project.dto.jobs.JobsReq.JobsCheckBoxReqDto;
import shop.mtcoding.project.dto.jobs.JobsReq.JobsSearchReqDto;
import shop.mtcoding.project.dto.jobs.JobsReq.JobsUpdateReqDto;
import shop.mtcoding.project.dto.jobs.JobsReq.JobsWriteReqDto;
import shop.mtcoding.project.dto.jobs.JobsResp.JobsCheckOutDto;
import shop.mtcoding.project.dto.jobs.JobsResp.JobsDetailOutDto;
import shop.mtcoding.project.dto.jobs.JobsResp.JobsMainOutDto;
import shop.mtcoding.project.dto.jobs.JobsResp.JobsMatchRespDto;
import shop.mtcoding.project.dto.jobs.JobsResp.JobsSearchOutDto;
import shop.mtcoding.project.dto.jobs.JobsResp.JobsSearchkeyOutDto;
import shop.mtcoding.project.dto.jobs.JobsResp.JobsSuggestRespDto;
import shop.mtcoding.project.dto.resume.ResumeResp.ResumeIdRespDto;
import shop.mtcoding.project.dto.skill.RequiredSkillReq.RequiredSkillWriteReqDto;
import shop.mtcoding.project.dto.skill.ResumeSkillResp.ResumeSkillByUserRespDto;
import shop.mtcoding.project.dto.skill.ResumeSkillResp.ResumeSkillRespDto;
import shop.mtcoding.project.model.comp.Comp;
import shop.mtcoding.project.model.comp.CompRepository;
import shop.mtcoding.project.model.jobs.JobsRepository;
import shop.mtcoding.project.model.resume.ResumeRepository;
import shop.mtcoding.project.model.skill.SkillRepository;
import shop.mtcoding.project.model.user.User;
import shop.mtcoding.project.service.JobsService;
import shop.mtcoding.project.util.CheckValid;
import shop.mtcoding.project.util.DateUtil;

@Controller
@RequiredArgsConstructor
public class JobsController {
    private final JobsRepository jobsRepository;
    private final CompRepository compRepository;
    private final SkillRepository skillRepository;    
    private final ResumeRepository resumeRepository;
    private final JobsService jobsService;
    private final HttpSession session;

    @GetMapping("/comp/request/jobs")
    @ResponseBody
    public ResponseEntity<?> requestJobs() {
        Comp compSession = (Comp) session.getAttribute("compSession");
        List<JobsSuggestRespDto> jDtos = jobsRepository.findAllToSuggestReq(compSession.getCompId());
        for (JobsSuggestRespDto jDto : jDtos) {
            jDto.setLeftTime(DateUtil.dDay(jDto.getEndDate()));
        }
        return new ResponseEntity<>(new ResponseDto<>(1, "공고 불러오기 완료", jDtos), HttpStatus.OK);
    }

    @GetMapping("/jobs/search")
    @ResponseBody
    public ResponseEntity<?> searchJobs(@LoginUser User user, String keyword){
        if(ObjectUtils.isEmpty(keyword)){
            keyword = "검색어를 입력해 주세요 !!!";
            throw new CustomException("검색어가 없습니다.");
        }
        Integer num = null;
        if( user != null ) num = user.getUserId();

        List<JobsSearchOutDto> jDtos = jobsRepository.findBySearch(keyword, num);
        for (JobsSearchOutDto jDto : jDtos) {
            jDto.setLeftTime(DateUtil.dDay(jDto.getEndDate()));
        }        
        JobsSearchkeyOutDto jDto = JobsSearchkeyOutDto.builder()
                                            .keyword(keyword)
                                            .jobsSearchOutDto(jDtos)
                                            .build();
        return new ResponseEntity<>(new ResponseDto<>(1, "검색 완료", jDto), HttpStatus.OK);
    }

    @GetMapping("/jobs/info/search")
    public ResponseEntity<?> searchCheckbox(@LoginUser User user, JobsCheckBoxReqDto jobsDto) {
        if (ObjectUtils.isEmpty(jobsDto.getCareer())) jobsDto.setCareer("");
        Integer num = null;
        if( user != null ) num = user.getUserId();

        List<JobsCheckOutDto> jDtos = jobsRepository.findByCheckBox(jobsDto, num);
        for (JobsCheckOutDto jDto : jDtos) {
            jDto.setLeftTime(DateUtil.dDay(jDto.getEndDate()));
        }
        return new ResponseEntity<>(new ResponseDto<>(1, "검색 성공", jDtos), HttpStatus.OK);
    }

    @GetMapping("/jobs/info")
    public ResponseEntity<?> info(@LoginUser User user) throws Exception {
        Integer num = null;
        if( user != null ) num = user.getUserId();

        List<JobsMainOutDto> jDtos = jobsRepository.findAlltoMain(num);
        for (JobsMainOutDto jDto1 : jDtos) {
            jDto1.setLeftTime(DateUtil.dDay(jDto1.getEndDate()));
        }
        return new ResponseEntity<>(new ResponseDto<>(1, "조회 성공", jDtos), HttpStatus.OK);
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<?> viewJobs(@LoginUser User user, @PathVariable Integer id) {
        CheckValid.inNull(jobsRepository.findById(id), "조회한 공고가 존재하지 않습니다.");
        Integer num = null;
        if( user != null ) num = user.getUserId();
        JobsDetailOutDto jDto = jobsRepository.findByJobsDetail(id, num);
        jDto.setLeftTime(DateUtil.dDay(jDto.getEndDate()));
        jDto.setFormatEndDate(DateUtil.format(jDto.getEndDate()));
        return new ResponseEntity<>(new ResponseDto<>(1, "상세 공고조회 성공", jDto), HttpStatus.OK);
    }

    @GetMapping("/comp/jobs/write")
    public ResponseEntity<?> writeJobs(@LoginComp Comp comp) {
        CompWriteJobsRespDto cDto = compRepository.findByIdToJobs(comp.getCompId());
        CheckValid.inNullApi(cDto, "회사정보가 없습니다.");
        return new ResponseEntity<>(new ResponseDto<>(1, "공고 작성 양식 불러오기 성공", cDto), HttpStatus.OK);
    }

    @GetMapping("/jobs/{id}/update")
    public ResponseEntity<?> updateJobs(@PathVariable Integer id) {
        CheckValid.inNull(jobsRepository.findById(id), "조회한 공고가 존재하지 않습니다.");
        JobsDetailOutDto jDto = jobsRepository.findByJobsDetail(id, null);
        jDto.setLeftTime(DateUtil.dDay(jDto.getEndDate()));
        jDto.setFormatEndDate(DateUtil.format(jDto.getEndDate()));
        return new ResponseEntity<>(new ResponseDto<>(1, "공고 업데이트 양식 불러오기 성공", jDto), HttpStatus.OK);
    }

    @PostMapping("/jobs/info/list")
    public ResponseEntity<?> searchJobsSize(@LoginUser User user, @RequestBody JobsCheckBoxReqDto jDto) {
        if (ObjectUtils.isEmpty(jDto.getCareer())) jDto.setCareer("");
        Integer num = null;
        if( user != null ) num = user.getUserId();

        List<JobsCheckOutDto> jDtos = jobsRepository.findByCheckBox(jDto, num);
        for (JobsCheckOutDto jDto1 : jDtos) {
            jDto1.setLeftTime(DateUtil.dDay(jDto1.getEndDate()));
        }
        return new ResponseEntity<>(new ResponseDto<>(1, "검색 성공", jDtos.size()), HttpStatus.OK);
    }

    @GetMapping("/user/jobs/interest")
    public ResponseEntity<?> interest(@LoginUser User user) {
        List<JobsMatchRespDto> jDtos = jobsService.공고매칭서비스(user.getUserId());
        return new ResponseEntity<>(new ResponseDto<>(1, "매칭 공고 조회 성공", jDtos), HttpStatus.OK);
    }
    

    @PostMapping("/comp/jobs/write")
    public ResponseEntity<?> writeJobs(@Valid @RequestBody JobsWriteReqDto jDto, @LoginComp Comp comp) {
        Integer jobsId = jobsService.공고작성(jDto, comp.getCompId());
        return new ResponseEntity<>(new ResponseDto<>(1, "저장 완료", jobsId), HttpStatus.CREATED);
    }

    @PutMapping("/comp/jobs/update")
    public ResponseEntity<?> updateJobs(@Valid @RequestBody JobsUpdateReqDto jDto, @LoginComp Comp comp) {
        Integer jobdId = jobsService.공고수정(jDto, comp.getCompId());
        return new ResponseEntity<>(new ResponseDto<>(1, "저장 완료", jobdId), HttpStatus.CREATED);
    }

    @DeleteMapping("/jobs/{id}/delete")
    public ResponseEntity<?> deleteJobs(@PathVariable Integer id, @LoginComp Comp comp){
        if( ObjectUtils.isEmpty(jobsRepository.findById(id))){
            throw new CustomException("조회한 공고가 존재하지 않습니다.");
        }
        jobsService.공고삭제(id, comp.getCompId());
        return new ResponseEntity<>(new ResponseDto<>(1, "공고 삭제 성공", null), HttpStatus.OK);
    }
}
// ⬜ 채용정보 "/jobs/info"
// ⬜ 공고 "/jobs/1"

// 🟦 공고등록 "/comp/jobs/write
// 🟦 공고수정 "/jobs/공고번호/update"