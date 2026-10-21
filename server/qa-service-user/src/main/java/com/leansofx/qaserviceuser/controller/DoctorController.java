package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.DoctorResponse;
import com.leansofx.qaserviceuser.model.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public List<DoctorResponse> listDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private DoctorResponse toResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.id(),
                doctor.username(),
                doctor.name(),
                doctor.title(),
                doctor.department(),
                doctor.avatar(),
                doctor.experience(),
                doctor.specialties(),
                doctor.isActive());
    }
}
