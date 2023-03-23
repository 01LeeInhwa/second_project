package shop.mtcoding.project.controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.project.config.annotation.LoginComp;
import shop.mtcoding.project.config.exception.CustomApiException;
import shop.mtcoding.project.dto.common.ResponseDto;
import shop.mtcoding.project.dto.comp.CompReq.CompJoinReqDto;
import shop.mtcoding.project.dto.comp.CompReq.CompLoginReqDto;
import shop.mtcoding.project.dto.comp.CompReq.CompPasswordReqDto;
import shop.mtcoding.project.dto.comp.CompReq.CompUpdateReqDto;
import shop.mtcoding.project.dto.comp.CompResp.CompApplyOutDto;
import shop.mtcoding.project.dto.comp.CompResp.CompHomeOutDto;
import shop.mtcoding.project.dto.comp.CompResp.CompHomeOutDto.JobsManageJobsRespDto;
import shop.mtcoding.project.dto.comp.CompResp.CompLoginRespDto;
import shop.mtcoding.project.dto.comp.CompResp.CompProfileOutDto;
import shop.mtcoding.project.dto.comp.CompResp.CompUpdatePhotoOutDto;
import shop.mtcoding.project.dto.comp.CompResp.CompUpdateRespDto;

import shop.mtcoding.project.dto.resume.ResumeResp.ResumeMatchPageOutDto;
import shop.mtcoding.project.dto.resume.ResumeResp.ResumePublicOutDto;
import shop.mtcoding.project.dto.scrap.CompScrapResp.CompScrapPageOutDto;

import shop.mtcoding.project.model.apply.ApplyRepository;
import shop.mtcoding.project.model.comp.Comp;
import shop.mtcoding.project.model.comp.CompRepository;
import shop.mtcoding.project.model.jobs.JobsRepository;
import shop.mtcoding.project.model.resume.ResumeRepository;
import shop.mtcoding.project.model.scrap.ScrapRepository;
import shop.mtcoding.project.model.skill.SkillRepository;
import shop.mtcoding.project.model.suggest.SuggestRepository;
import shop.mtcoding.project.service.CompService;
import shop.mtcoding.project.util.CheckValid;
import shop.mtcoding.project.util.DateUtil;
import shop.mtcoding.project.util.Sha256;

@Controller
@RequiredArgsConstructor
public class CompController {

    private final SkillRepository skillRepository;
    private final HttpSession session;
    private final ResumeRepository resumeRepository;
    private final JobsRepository jobsRepository;
    private final ApplyRepository applyRepository;
    private final ScrapRepository scrapRepository;
    private final SuggestRepository suggestRepository;
    private final CompService compService;
    private final CompRepository compRepository;

    // 완료
    @PostMapping("/comp/join")
    public ResponseEntity<?> join(@Valid CompJoinReqDto compJoinReqDto, BindingResult bindingResult) {
        CompJoinReqDto compJoinOutDto = compService.회원가입(compJoinReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "회원가입완료", compJoinOutDto), HttpStatus.OK);
    }

    // 완료
    @GetMapping("/comp/profileUpdateForm")
    public ResponseEntity<?> profileUpdateForm(@LoginComp Comp comp) {
        CompProfileOutDto compPS = compRepository.findCompPhoto(comp.getCompId());
        return new ResponseEntity<>(new ResponseDto<>(1, "사진 수정페이지 조회 성공", compPS), HttpStatus.OK);
    }

    // 완료
    @GetMapping("/comp/emailCheck")
    public ResponseEntity<?> sameEmailCheck(String email) {
        Comp compPS = compRepository.findByCompEmail(email);
        if (compPS != null) {
            throw new CustomApiException("동일한 email이 존재합니다.");
        }
        return new ResponseEntity<>(new ResponseDto<>(1, "해당 email은 사용 가능합니다.", null), HttpStatus.OK);
    }

    // 완료
    @GetMapping("/comp/join")
    public String joinComp() {
        return "comp/joinForm";
    }

    // 완료
    @PostMapping("/comp/login")
    public ResponseEntity<?> login(@Valid CompLoginReqDto compLoginReqDto, BindingResult bindingResult,
            HttpServletResponse httpServletResponse) {
        CompLoginRespDto principal = compService.로그인(compLoginReqDto);
        if (principal == null) {
            throw new CustomApiException("존재하지 않는 회원입니다.");
        } else {
            if (compLoginReqDto.getRememberEmail() == null) {
                compLoginReqDto.setRememberEmail("");
            }
            if (compLoginReqDto.getRememberEmail().equals("on")) {
                Cookie cookie = new Cookie("rememberEmail", compLoginReqDto.getEmail());
                httpServletResponse.addCookie(cookie);
            } else {
                Cookie cookie = new Cookie("remember", "");
                cookie.setMaxAge(0);
                httpServletResponse.addCookie(cookie);
            }
            session.invalidate();
            Comp comp = compRepository.findByEmailAndPassword(compLoginReqDto.getEmail(),
                    compLoginReqDto.getPassword());
            session.setAttribute("compSession", comp);
            return new ResponseEntity<>(new ResponseDto<>(1, "로그인 성공", principal), HttpStatus.OK);
        }
    }

    // 완료
    @GetMapping("/comp/login")
    public String loginComp() {
        return "comp/loginForm";
    }

    // 완료
    @GetMapping("/comp/comphome")
    public ResponseEntity<?> compMyhome(@LoginComp Comp comp) {
        // 이미지는 세션에서 추출하면됨, 세션에 들어있음 사진 수정후 기업홈을 리로드하는데 이때 세션을 업데이트해야함
        CompHomeOutDto compResult = compService.기업홈정보와매칭이력서(comp);
        return new ResponseEntity<>(new ResponseDto<>(1, "기업 홈 조회 성공", compResult), HttpStatus.OK);
    }

   
    // 완료
    @PostMapping("/comp/passwordCheck")
    public @ResponseBody ResponseEntity<?> samePasswordCheck(@RequestBody CompPasswordReqDto compPasswordReqDto) {
        compPasswordReqDto.setPassword(Sha256.encode(compPasswordReqDto.getPassword()));
        Comp compPS = compRepository.findByCompidAndPassword(compPasswordReqDto.getCompId(),
                compPasswordReqDto.getPassword());
        if (compPS == null) {
            throw new CustomApiException("비밀번호가 틀렸습니다.");
        }
        return new ResponseEntity<>(new ResponseDto<>(1, "인증에 성공하였습니다.", null), HttpStatus.OK);
    }

    // 완료
    @PutMapping("/comp/update")
    public @ResponseBody ResponseEntity<?> updateComp(@LoginComp Comp comp,
            @Valid @RequestBody CompUpdateReqDto compUpdateReqDto, BindingResult bindingResult) {
        compUpdateReqDto.setPassword(Sha256.encode(compUpdateReqDto.getPassword()));

        CompUpdateReqDto compPS = compService.회사정보수정(compUpdateReqDto, comp.getCompId());
        Comp compSession = compRepository.findByCompId(compPS.getCompId());
        session.setAttribute("compSession", compSession);
        return new ResponseEntity<>(new ResponseDto<>(1, "수정완료", compPS), HttpStatus.OK);
    }

    // 완료
    @GetMapping("/comp/update")
    public @ResponseBody ResponseEntity<?> updateForm(@LoginComp Comp comp, CompUpdateRespDto compUpdateRespDto) {
        CompUpdateRespDto compPS = compRepository.findByCompId1(comp.getCompId());
        return new ResponseEntity<>(new ResponseDto<>(1, "회원 수정 완료", compPS), HttpStatus.OK);
    }

    // 완료
    @PutMapping("/comp/profileUpdate")
    public @ResponseBody ResponseEntity<?> profileUpdate(@LoginComp Comp comp, MultipartFile photo) throws Exception {
        CheckValid.inNullApi(photo, "사진이 전송 되지 않았습니다.");
        String result = compService.프로필사진수정(photo, comp.getCompId());
        comp.setPhoto(result);
        CompUpdatePhotoOutDto update = CompUpdatePhotoOutDto.builder()
                .compId(comp.getCompId())
                .photo(result)
                .build();
        session.setAttribute("compSession", comp);
        return new ResponseEntity<>(new ResponseDto<>(1, "프로필 수정 성공", update), HttpStatus.OK);
    }

    // 완료
    @GetMapping("/comp/apply")
    public ResponseEntity<?> apply(@LoginComp Comp comp) {
        // 여기도 마찬가지로 사진은 세션에서 가져가면 됩니다. 사진 업데이트하고 세션 업데이트 + 페이지 리로드 필요
        CompApplyOutDto result = compRepository.findApplyAndSuggestByCompId(comp.getCompId());
        return new ResponseEntity<>(new ResponseDto<>(1, "기업의 지원 및 제안 페이지 데이터 조회 완료", result), HttpStatus.OK);
    }

    // 완료
    @GetMapping("/comp/jobs")
    public ResponseEntity<?> manageJobs(@LoginComp Comp comp) {
        // 사진이 이상하면 세션을 업데이트 해야함
        List<JobsManageJobsRespDto> jDtos = jobsRepository.findByIdtoManageJobs(comp.getCompId());
        for (JobsManageJobsRespDto jDto : jDtos) {
            jDto.setLeftTime(DateUtil.dDay(jDto.getEndDate()));
        }
        return new ResponseEntity<>(new ResponseDto<>(1, "공고 관리 페이지 조회 성공", jDtos), HttpStatus.OK);
    }

    // 완료
    @GetMapping("/comp/resume/read")
    public ResponseEntity<?> readResume(@LoginComp Comp comp) {
        List<ResumePublicOutDto> rLists = resumeRepository.findAllResumebyState(comp.getCompId());
        return new ResponseEntity<>(new ResponseDto<>(1, "공개 이력서 조회 성공", rLists), HttpStatus.OK);
    }

    // 완료
    @GetMapping("/comp/resume/scrap")
    public ResponseEntity<?> scrapResume(@LoginComp Comp comp) {
        // 마찬가지로 사진은 세션 업데이트해서 가져가면 됩니다.
        List<CompScrapPageOutDto> sList = scrapRepository.findScrapOutByCompId(comp.getCompId());
        return new ResponseEntity<>(new ResponseDto<>(1, "기업의 이력서 스크랩페이지 조회 성공", sList), HttpStatus.OK);
    }

    // 완료
    @GetMapping("/comp/talent")
    public ResponseEntity<?> talent(@LoginComp Comp comp) {
        ResumeMatchPageOutDto result = compService.추천인재(comp);
        return new ResponseEntity<>(new ResponseDto<>(1, "추천 인재 조회 성공", result), HttpStatus.OK);
    }

}

// ⬜ 기업회원가입 "/comp/join"
// ⬜ 기업로그인 "/comp/login

// 🟦 🔐 기업권한필요 🔐 🟦
// 🟦 기업홈 "/comp/myhome"
// 🟦 기업수정 "/comp/update"
// 🟦 지원자현황 "/comp/apply"
// 🟦 이력서 열람 "/comp/resume/read"
// 🟦 이력서 스크랩 "/comp/resume/scrap"
// 🟦 인재추천 "/comp/talent"