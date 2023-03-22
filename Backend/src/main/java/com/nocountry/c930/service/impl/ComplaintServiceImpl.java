package com.nocountry.c930.service.impl;

import com.nocountry.c930.dto.ComplaintDto;
import com.nocountry.c930.service.IComplaintService;
import org.springframework.stereotype.Service;

@Service
public class ComplaintServiceImpl implements IComplaintService {

    @Override
    public ComplaintDto createComplaint(ComplaintDto dto) {

        return null;
    }

    @Override
    public ComplaintDto getComplaint(Long id) {
        

        return null;
    }

    @Override
    public ComplaintDto updateComplaint(Long id, ComplaintDto dto) {

        return dto;
    }

    @Override
    public void deleteComplaint(Long id) {
        
    }
}
