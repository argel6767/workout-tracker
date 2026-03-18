package com.pxbzi.workout_tracker.data_transfers;

import com.pxbzi.workout_tracker.data_transfers.models.DataExportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/data-transfers")
@RequiredArgsConstructor
public class DataTransferController {

    private final DataTransferService dataTransferService;

    @GetMapping
    public DataExportDto exportData() {
        return dataTransferService.exportData();
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> exportDataAsFile() throws IOException {
        byte[] fileBytes = dataTransferService.exportDataAsFile();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("workout-data.json")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
    }

    @PostMapping
    public void importData(@RequestBody DataExportDto data) {
        dataTransferService.importData(data);
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void importDataFromFile(@RequestParam("file") MultipartFile file) throws IOException {
        dataTransferService.importDataFromFile(file);
    }
}
