package com.unicaes.contabilidad.service;

import com.unicaes.contabilidad.model.Cuenta;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelGeneratorService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generateBalanceGeneral(List<Cuenta> activos, List<Cuenta> pasivos,
                                          List<Cuenta> patrimonio, BigDecimal totalActivos,
                                          BigDecimal totalPasivos, BigDecimal totalPatrimonio,
                                          LocalDate fechaCorte) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Balance General");

        // Estilos
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle moneyStyle = createMoneyStyle(workbook);
        CellStyle totalStyle = createTotalStyle(workbook);

        int rowNum = 0;

        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BALANCE GENERAL - UNICAES");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        Row subtitleRow = sheet.createRow(rowNum++);
        Cell subtitleCell = subtitleRow.createCell(0);
        subtitleCell.setCellValue("Al " + fechaCorte.format(DATE_FORMATTER));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
        rowNum++;

        // ACTIVOS
        rowNum = addSection(sheet, rowNum, "ACTIVOS", activos, totalActivos, headerStyle, dataStyle, moneyStyle, totalStyle);
        rowNum++;

        // PASIVOS
        rowNum = addSection(sheet, rowNum, "PASIVOS", pasivos, totalPasivos, headerStyle, dataStyle, moneyStyle, totalStyle);
        rowNum++;

        // PATRIMONIO
        rowNum = addSection(sheet, rowNum, "PATRIMONIO", patrimonio, totalPatrimonio, headerStyle, dataStyle, moneyStyle, totalStyle);

        // Ajustar columnas
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 4000);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    public byte[] generateEstadoResultados(List<Cuenta> ingresos, List<Cuenta> gastos,
                                            BigDecimal totalIngresos, BigDecimal totalGastos,
                                            BigDecimal utilidadNeta, LocalDate fechaInicio,
                                            LocalDate fechaFin) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Estado de Resultados");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle moneyStyle = createMoneyStyle(workbook);
        CellStyle totalStyle = createTotalStyle(workbook);

        int rowNum = 0;

        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("ESTADO DE RESULTADOS - UNICAES");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        Row subtitleRow = sheet.createRow(rowNum++);
        Cell subtitleCell = subtitleRow.createCell(0);
        subtitleCell.setCellValue("Del " + fechaInicio.format(DATE_FORMATTER) + " al " + fechaFin.format(DATE_FORMATTER));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
        rowNum++;

        // INGRESOS
        rowNum = addSection(sheet, rowNum, "INGRESOS", ingresos, totalIngresos, headerStyle, dataStyle, moneyStyle, totalStyle);
        rowNum++;

        // GASTOS
        rowNum = addSection(sheet, rowNum, "GASTOS", gastos, totalGastos, headerStyle, dataStyle, moneyStyle, totalStyle);
        rowNum++;

        // UTILIDAD NETA
        Row utilidadRow = sheet.createRow(rowNum);
        Cell utilidadLabelCell = utilidadRow.createCell(1);
        utilidadLabelCell.setCellValue("UTILIDAD NETA:");
        utilidadLabelCell.setCellStyle(totalStyle);

        Cell utilidadValueCell = utilidadRow.createCell(2);
        utilidadValueCell.setCellValue(utilidadNeta.doubleValue());
        utilidadValueCell.setCellStyle(totalStyle);

        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 4000);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    public byte[] generateBalanceComprobacion(List<Cuenta> cuentas, BigDecimal totalDebitos,
                                               BigDecimal totalCreditos, LocalDate fechaCorte) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Balance de Comprobación");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle moneyStyle = createMoneyStyle(workbook);
        CellStyle totalStyle = createTotalStyle(workbook);

        int rowNum = 0;

        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BALANCE DE COMPROBACIÓN - UNICAES");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        Row subtitleRow = sheet.createRow(rowNum++);
        Cell subtitleCell = subtitleRow.createCell(0);
        subtitleCell.setCellValue("Al " + fechaCorte.format(DATE_FORMATTER));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
        rowNum++;

        // Encabezados
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Código", "Cuenta", "Debe", "Haber"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        for (Cuenta cuenta : cuentas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(cuenta.getCodigo());
            row.getCell(0).setCellStyle(dataStyle);
            
            row.createCell(1).setCellValue(cuenta.getNombre());
            row.getCell(1).setCellStyle(dataStyle);

            if (cuenta.getNaturaleza().toString().equals("DEUDORA") && cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                Cell debeCell = row.createCell(2);
                debeCell.setCellValue(cuenta.getSaldo().doubleValue());
                debeCell.setCellStyle(moneyStyle);
                
                row.createCell(3).setCellValue("-");
                row.getCell(3).setCellStyle(dataStyle);
            } else if (cuenta.getNaturaleza().toString().equals("ACREEDORA") && cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                row.createCell(2).setCellValue("-");
                row.getCell(2).setCellStyle(dataStyle);
                
                Cell haberCell = row.createCell(3);
                haberCell.setCellValue(cuenta.getSaldo().doubleValue());
                haberCell.setCellStyle(moneyStyle);
            } else {
                row.createCell(2).setCellValue("-");
                row.getCell(2).setCellStyle(dataStyle);
                row.createCell(3).setCellValue("-");
                row.getCell(3).setCellStyle(dataStyle);
            }
        }

        // Totales
        Row totalRow = sheet.createRow(rowNum);
        Cell totalLabelCell = totalRow.createCell(1);
        totalLabelCell.setCellValue("TOTALES:");
        totalLabelCell.setCellStyle(totalStyle);

        Cell totalDebitoCell = totalRow.createCell(2);
        totalDebitoCell.setCellValue(totalDebitos.doubleValue());
        totalDebitoCell.setCellStyle(totalStyle);

        Cell totalCreditoCell = totalRow.createCell(3);
        totalCreditoCell.setCellValue(totalCreditos.doubleValue());
        totalCreditoCell.setCellStyle(totalStyle);

        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 4000);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    private int addSection(Sheet sheet, int startRow, String sectionName, List<Cuenta> cuentas,
                           BigDecimal total, CellStyle headerStyle, CellStyle dataStyle,
                           CellStyle moneyStyle, CellStyle totalStyle) {
        int rowNum = startRow;

        // Encabezado de sección
        Row sectionRow = sheet.createRow(rowNum++);
        Cell sectionCell = sectionRow.createCell(0);
        sectionCell.setCellValue(sectionName);
        sectionCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

        // Encabezados de columnas
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Código", "Cuenta", "Saldo"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        for (Cuenta cuenta : cuentas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(cuenta.getCodigo());
            row.getCell(0).setCellStyle(dataStyle);
            
            row.createCell(1).setCellValue(cuenta.getNombre());
            row.getCell(1).setCellStyle(dataStyle);
            
            Cell moneyCell = row.createCell(2);
            moneyCell.setCellValue(cuenta.getSaldo().doubleValue());
            moneyCell.setCellStyle(moneyStyle);
        }

        // Total
        Row totalRow = sheet.createRow(rowNum++);
        Cell totalLabelCell = totalRow.createCell(1);
        totalLabelCell.setCellValue("TOTAL:");
        totalLabelCell.setCellStyle(totalStyle);

        Cell totalValueCell = totalRow.createCell(2);
        totalValueCell.setCellValue(total.doubleValue());
        totalValueCell.setCellStyle(totalStyle);

        return rowNum;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createMoneyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
