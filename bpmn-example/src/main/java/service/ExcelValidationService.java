package service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelValidationService {

  public static class Issue {
    public final int row;
    public final String column;
    public final String message;
    public Issue(int row, String column, String message) {
      this.row = row;
      this.column = column;
      this.message = message;
    }
  }

  /**
   * Basic demo rules:
   * - Expect a sheet named "Resources"
   * - Expect headers: ProjectCode, ResourceName, RateCurrency, Rate
   * - Rate must be > 0
   */
  public List<Issue> validate(InputStream in) throws Exception {
    List<Issue> issues = new ArrayList<>();
    try (Workbook wb = new XSSFWorkbook(in)) {
      Sheet sheet = wb.getSheet("Resources");
      if (sheet == null) {
        issues.add(new Issue(0, "-", "Sheet 'Resources' not found"));
        return issues;
      }

      Row header = sheet.getRow(0);
      if (header == null) {
        issues.add(new Issue(0, "-", "Header row missing"));
        return issues;
      }

      int colProject = findCol(header, "ProjectCode");
      int colName    = findCol(header, "ResourceName");
      int colCcy     = findCol(header, "RateCurrency");
      int colRate    = findCol(header, "Rate");
      if (colProject < 0) issues.add(new Issue(0, "ProjectCode", "Missing header"));
      if (colName < 0)    issues.add(new Issue(0, "ResourceName", "Missing header"));
      if (colCcy < 0)     issues.add(new Issue(0, "RateCurrency", "Missing header"));
      if (colRate < 0)    issues.add(new Issue(0, "Rate", "Missing header"));

      if (!issues.isEmpty()) return issues;

      for (int r = 1; r <= sheet.getLastRowNum(); r++) {
        Row row = sheet.getRow(r);
        if (row == null) continue;

        String project = getString(row.getCell(colProject));
        String name    = getString(row.getCell(colName));
        String ccy     = getString(row.getCell(colCcy));
        Double rate    = getNumeric(row.getCell(colRate));

        if (project == null || project.isBlank())
          issues.add(new Issue(r, "ProjectCode", "ProjectCode required"));
        if (name == null || name.isBlank())
          issues.add(new Issue(r, "ResourceName", "ResourceName required"));
        if (ccy == null || ccy.isBlank())
          issues.add(new Issue(r, "RateCurrency", "RateCurrency required"));
        if (rate == null || rate <= 0)
          issues.add(new Issue(r, "Rate", "Rate must be > 0"));
      }
    }
    return issues;
  }

  private int findCol(Row header, String name) {
    for (int i = 0; i < header.getLastCellNum(); i++) {
      Cell c = header.getCell(i);
      if (c != null && name.equalsIgnoreCase(c.getStringCellValue())) return i;
    }
    return -1;
  }

  private String getString(Cell c) {
    if (c == null) return null;
    if (c.getCellType() == CellType.STRING) return c.getStringCellValue();
    if (c.getCellType() == CellType.NUMERIC) return String.valueOf(c.getNumericCellValue());
    if (c.getCellType() == CellType.BOOLEAN) return String.valueOf(c.getBooleanCellValue());
    return null;
  }

  private Double getNumeric(Cell c) {
    if (c == null) return null;
    if (c.getCellType() == CellType.NUMERIC) return c.getNumericCellValue();
    if (c.getCellType() == CellType.STRING) {
      try { return Double.parseDouble(c.getStringCellValue()); } catch (Exception ignored) {}
    }
    return null;
  }
}
