package org.example.messmate.mapper;

import org.example.messmate.dto.hostelDto.HostelResponseDto;
import org.example.messmate.entity.Hostel;

public class HostelMapper {
    private HostelMapper() {
        /* This utility class should not be instantiated */
    }

    public static HostelResponseDto toDto(Hostel hostel) {
        HostelResponseDto hostelResponseDto = new HostelResponseDto();
        hostelResponseDto.setId(hostel.getId());
        hostelResponseDto.setName(hostel.getName());
        hostelResponseDto.setResidents(hostel.getResidents());
        hostelResponseDto.setHostelEmail(hostel.getEmail());
        hostelResponseDto.setHostelPhone(hostel.getPhone());

        if(hostel.getAccountant() != null){
            hostelResponseDto.setAccountantName(
                    hostel
                            .getAccountant()
                            .getName()
            );

            hostelResponseDto.setAccountantPhone(
                    hostel
                            .getAccountant()
                            .getPhone()
            );

            hostelResponseDto.setLoginId(
                    hostel.getAccountant()
                            .getUser()
                            .getUsername()
            );
        }
        return hostelResponseDto;
    }
}
