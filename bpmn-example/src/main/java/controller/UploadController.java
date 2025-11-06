package controller;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadController {

  private final RuntimeService runtimeService;

  public UploadController(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  @PostMapping(value = "/start", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> start(@RequestPart("file") MultipartFile file) throws Exception {
    String filename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "input.xlsx" : file.getOriginalFilename());
    Path dest = Path.of(System.getProperty("java.io.tmpdir"), System.currentTimeMillis() + "-" + filename);
    Files.copy(file.getInputStream(), dest);

    var vars = Map.<String, Object>of(
        "filePath", dest.toString()
    );

    var pi = runtimeService.startProcessInstanceByKey("excelFlow", vars);
    return ResponseEntity.ok(Map.of("processInstanceId", pi.getProcessInstanceId(),
                                    "tempFile", dest.toString()));
  }
}
