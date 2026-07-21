package com.weg.quicktransfer.service;

import org.springframework.stereotype.Service;

import com.weg.quicktransfer.dto.interview.InterviewRequestDTO;
import com.weg.quicktransfer.dto.interview.InterviewResponseDTO;
import com.weg.quicktransfer.mapper.InterviewMapper;
import com.weg.quicktransfer.repo.InterviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final InterviewMapper interviewMapper;

    public InterviewResponseDTO create(InterviewRequestDTO interviewRequestDTO) {
        
    }
}
