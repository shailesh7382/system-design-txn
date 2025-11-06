package delegate;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import service.ExcelValidationService;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class ParseValidateDelegate implements JavaDelegate {

  private final service.ExcelValidationService service;

  public ParseValidateDelegate(ExcelValidationService service) {
    this.service = service;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String filePath = (String) execution.getVariable("filePath");
    if (filePath == null || !Files.exists(Path.of(filePath))) {
      execution.setVariable("hasErrors", true);
      execution.setVariable("issues", List.of("Uploaded file not found"));
      return;
    }

    List<ExcelValidationService.Issue> issues;
    try (FileInputStream fis = new FileInputStream(filePath)) {
      issues = service.validate(fis);
    }

    boolean hasErrors = !issues.isEmpty();
    execution.setVariable("hasErrors", hasErrors);
    execution.setVariable("issues", issues.stream()
        .map(i -> String.format("Row %d [%s]: %s", i.row, i.column, i.message))
        .toList());
  }
}
