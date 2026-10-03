package org.example.messmate.mapper;

import org.example.messmate.dto.studentdto.StudentResponseDto;
import org.example.messmate.entity.Student;

public class StudentMapper {

    private StudentMapper() {
        /* This utility class should not be instantiated */
    }

    public static StudentResponseDto toDto(Student student) {
        StudentResponseDto studentResponseDto = new StudentResponseDto();

        studentResponseDto.setEmail(
                student.getUser()
                        .getUsername()
        );

        studentResponseDto.setName(student.getName());

        studentResponseDto.setRollNumber(student.getRollNumber());

        studentResponseDto.setHostelId(
                student.getHostel()
                        .getId()
        );

        studentResponseDto.setHostelName(
                student.getHostel().getName()
        );

        return studentResponseDto;
    }
}
