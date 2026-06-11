package org.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KycDto {
    private Long id;
    private Long userId;
    private String documentUrl;
    private String status;
}