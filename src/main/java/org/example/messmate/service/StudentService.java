package org.example.messmate.service;

import org.example.messmate.dto.otpDto.OtpSendRequestDto;
import org.example.messmate.dto.otpDto.OtpSendResponseDto;
import org.example.messmate.dto.studentdto.RemoveStudentVerifyRequestDto;
import org.example.messmate.dto.studentdto.StudentResponseDto;
import org.example.messmate.entity.Hostel;
import org.example.messmate.enums.OtpPurpose;
import org.example.messmate.exception.ResourceNotFoundException;
import org.example.messmate.exception.UserNotFoundException;
import org.example.messmate.entity.Student;
import org.example.messmate.mapper.StudentMapper;
import org.example.messmate.repository.HostelRepository;
import org.example.messmate.repository.StudentRepository;
import org.example.messmate.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final HostelRepository hostelRepository;
    private final OtpService otpService;
    private final UserRepository userRepository;

    public StudentService(
            StudentRepository studentRepository,
            HostelRepository hostelRepository,
            OtpService otpService, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.hostelRepository = hostelRepository;
        this.otpService = otpService;
        this.userRepository = userRepository;
    }

    public void changeHostelById(Long studentId, Long hostelId) {
        Student student = studentRepository.findById(studentId).orElse(null);

        if(student == null) throw new UserNotFoundException("Student not found");

        Hostel hostel = hostelRepository.findById(hostelId).orElse(null);

        if(hostel == null) throw new ResourceNotFoundException("Hostel not found");

        student.setHostel(hostel);
    }

    public List<StudentResponseDto> getAllStudentsByHostelId(
            Long hostelId
    ) {
        List<Student> students =
                studentRepository.findAllByHostelId(hostelId);

        List<StudentResponseDto> studentsDto = new ArrayList<>();
        for (Student student : students) {
            studentsDto.add(
                    StudentMapper.toDto(student)
            );
        }
        return studentsDto;
    }

    public OtpSendResponseDto sendStudentRemoveOtp(
            OtpSendRequestDto otpSendRequestDto
    ) {
        String identifier =
                otpSendRequestDto
                        .getIdentifier()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        otpService.requestOtp(
                identifier,
                OtpPurpose.REMOVE_STUDENT,
                otpSendRequestDto.getChannel()
        );

        OtpSendResponseDto responseDto =
                new OtpSendResponseDto();

        responseDto.setIdentifier(identifier);
        return responseDto;
    }

    @Transactional
    public void removeStudentsByHostelId(
            Long id,
            RemoveStudentVerifyRequestDto removeStudentVerifyRequestDto
    ) {
        String identifier =
                removeStudentVerifyRequestDto
                        .getIdentifier()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        otpService.verifyOtp(
                identifier,
                OtpPurpose.REMOVE_STUDENT,
                removeStudentVerifyRequestDto.getOtp()
        );

        List<String> studentIdentifiers =
                removeStudentVerifyRequestDto
                        .getStudentIdentifiers();
        

        userRepository.deleteAllByUsernameIn(studentIdentifiers);

    }
}
