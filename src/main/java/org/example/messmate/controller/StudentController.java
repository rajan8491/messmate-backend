package org.example.messmate.controller;

import org.example.messmate.entity.Student;
import org.example.messmate.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PutMapping("/change-hostel")
    public ResponseEntity<String> changeHostel(@RequestBody Long hostelId) {
        Student student = new Student();
        studentService.changeHostelById(student.getId(), hostelId);

        return ResponseEntity.ok().body("Hostel has been changed successfully");
    }



}
