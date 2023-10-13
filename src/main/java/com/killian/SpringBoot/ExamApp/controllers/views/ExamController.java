package com.killian.SpringBoot.ExamApp.controllers.views;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.killian.SpringBoot.ExamApp.models.Exam;
import com.killian.SpringBoot.ExamApp.repositories.ExamRepository;
import com.killian.SpringBoot.ExamApp.repositories.QuestionRepository;

@Controller
@RequestMapping(path = "/user/exam")
public class ExamController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/create-exam-page")
    public String createExamPage(Model model) {
        return "create-exam";
    }

    @GetMapping("/select-subject-and-grade")
    public String createExamFromBankPage1(Model model) {

        List<String> subjects = questionRepository.findDistinctSubjects();
        List<Integer> grades = questionRepository.findDistinctGrades();
        model.addAttribute("subjects", subjects);
        model.addAttribute("grades", grades);
        return "select-subject-and-grade";
    }

    @GetMapping("/create-exam-from-bank-page")
    public String createExamFromBankPage(
            @RequestParam("selectedSubject") String selectedSubject,
            @RequestParam("selectedGrade") int selectedGrade,
            Model model) {

        List<String> chapters = questionRepository.findDistinctChaptersBySubjectAndGrade(selectedSubject,
                selectedGrade);

        model.addAttribute("selectedGrade", selectedGrade);
        model.addAttribute("selectedSubject", selectedSubject);
        model.addAttribute("chapters", chapters);

        return "create-exam-from-bank";
    }

    @PostMapping("/create-exam-from-bank")
    public String createQuestion(
            @RequestParam("subject") String subject,
            @RequestParam("grade") int grade,
            @RequestParam("name") String name,
            @RequestParam("amount") int amount,
            @RequestParam("questionCountForEachChapter") List<Integer> questionCountForEachChapter,
            Model model) {

        Exam newExam = new Exam();
        newExam.setName(name);
        newExam.setSubject(subject);

        // List<Question> easyQuestions =
        // questionRepository.findRandomEasyQuestions(easyQuestionCount);
        // List<Question> mediumQuestions =
        // questionRepository.findRandomMediumQuestions(mediumQuestionCount);
        // List<Question> hardQuestions =
        // questionRepository.findRandomHardQuestions(hardQuestionCount);

        // List<Question> questions = Stream.of(easyQuestions, mediumQuestions,
        // hardQuestions)
        // .flatMap(Collection::stream)
        // .collect(Collectors.toList());
        // newExam.setQuestions(questions);

        String message = null;
        try {
            examRepository.save(newExam);
            message = "Successful! Exam added to database.";
        } catch (Exception e) {
            message = "Failed! Exam name is taken.";
        }
        model.addAttribute("message", message);
        List<String> subjects = questionRepository.findDistinctSubjects();
        model.addAttribute("subjects", subjects);

        return "create-exam";
    }

    @GetMapping("/view-exams-by-filter-page")
    public String getQuestionsByFilterPage(Model model) {

        List<String> subjects = examRepository.findDistinctSubjects();
        model.addAttribute("subjects", subjects);

        // Initially, display questions from the first subject
        if (!subjects.isEmpty())
            model.addAttribute("selectedSubject", subjects.get(0));

        return "exams-by-filter";
    }

    @GetMapping("/get-exams-by-subject")
    public String getExamsBySelectedSubject(
            @RequestParam("selectedSubject") String selectedSubject,
            Model model) {

        List<String> subjects = examRepository.findDistinctSubjects();

        List<Exam> exams = examRepository.findBySubject(selectedSubject);
        List<String> examNames = exams.stream()
                .map(Exam::getName)
                .collect(Collectors.toList());

        model.addAttribute("subjects", subjects);
        model.addAttribute("selectedSubject", selectedSubject);
        model.addAttribute("examNames", examNames);

        return "exams-by-filter";
    }

    @GetMapping("/get-exam-by-name/{name}")
    public String viewExam(
            @PathVariable String name,
            Model model) {

        Exam exam = examRepository.findByName(name);
        model.addAttribute("exam", exam);

        return "exam-by-name";
    }
}