package com.unicaes.contabilidad.controller;

import com.unicaes.contabilidad.model.AsientoContable;
import com.unicaes.contabilidad.model.Cuenta;
import com.unicaes.contabilidad.repository.AsientoContableRepository;
import com.unicaes.contabilidad.repository.CuentaRepository;
import com.unicaes.contabilidad.service.ExcelGeneratorService;
import com.unicaes.contabilidad.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private AsientoContableRepository asientoContableRepository;

    @Autowired
    private ExcelGeneratorService excelGeneratorService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping
    public String index() {
        return "reportes/index";
    }

    @GetMapping("/balance-general")
    public String balanceGeneral(Model model,
                                  @RequestParam(required = false) String fecha) {
        LocalDate fechaCorte = fecha != null ? LocalDate.parse(fecha) : LocalDate.now();
        List<Cuenta> todasCuentas = cuentaRepository.findAll();

        // Separar cuentas por tipo
        Map<String, List<Cuenta>> cuentasPorTipo = todasCuentas.stream()
                .collect(Collectors.groupingBy(c -> c.getTipo().toString()));

        // Calcular totales
        BigDecimal totalActivos = calcularTotalPorTipo(todasCuentas, "ACTIVO");
        BigDecimal totalPasivos = calcularTotalPorTipo(todasCuentas, "PASIVO");
        BigDecimal totalPatrimonio = calcularTotalPorTipo(todasCuentas, "PATRIMONIO");

        model.addAttribute("fechaCorte", fechaCorte);
        model.addAttribute("activos", cuentasPorTipo.getOrDefault("ACTIVO", new ArrayList<>()));
        model.addAttribute("pasivos", cuentasPorTipo.getOrDefault("PASIVO", new ArrayList<>()));
        model.addAttribute("patrimonio", cuentasPorTipo.getOrDefault("PATRIMONIO", new ArrayList<>()));
        model.addAttribute("totalActivos", totalActivos);
        model.addAttribute("totalPasivos", totalPasivos);
        model.addAttribute("totalPatrimonio", totalPatrimonio);
        model.addAttribute("totalPasivosPatrimonio", totalPasivos.add(totalPatrimonio));

        return "reportes/balance-general";
    }

    @GetMapping("/estado-resultados")
    public String estadoResultados(Model model,
                                    @RequestParam(required = false) String fechaInicio,
                                    @RequestParam(required = false) String fechaFin) {
        LocalDate inicio = fechaInicio != null ? LocalDate.parse(fechaInicio) : LocalDate.now().withDayOfMonth(1);
        LocalDate fin = fechaFin != null ? LocalDate.parse(fechaFin) : LocalDate.now();

        List<Cuenta> todasCuentas = cuentaRepository.findAll();

        List<Cuenta> ingresos = todasCuentas.stream()
                .filter(c -> c.getTipo().toString().equals("INGRESO"))
                .collect(Collectors.toList());

        List<Cuenta> gastos = todasCuentas.stream()
                .filter(c -> c.getTipo().toString().equals("GASTO"))
                .collect(Collectors.toList());

        BigDecimal totalIngresos = calcularTotalPorTipo(todasCuentas, "INGRESO");
        BigDecimal totalGastos = calcularTotalPorTipo(todasCuentas, "GASTO");
        BigDecimal utilidadNeta = totalIngresos.subtract(totalGastos);

        model.addAttribute("fechaInicio", inicio);
        model.addAttribute("fechaFin", fin);
        model.addAttribute("ingresos", ingresos);
        model.addAttribute("gastos", gastos);
        model.addAttribute("totalIngresos", totalIngresos);
        model.addAttribute("totalGastos", totalGastos);
        model.addAttribute("utilidadNeta", utilidadNeta);

        return "reportes/estado-resultados";
    }

    @GetMapping("/libro-mayor")
    public String libroMayor(Model model,
                             @RequestParam(required = false) Long cuentaId) {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        model.addAttribute("cuentas", cuentas);

        if (cuentaId != null) {
            Cuenta cuenta = cuentaRepository.findById(cuentaId).orElse(null);
            if (cuenta != null) {
                // Aquí deberías obtener los movimientos de la cuenta
                // Por ahora solo mostramos la información básica
                model.addAttribute("cuentaSeleccionada", cuenta);
            }
        }

        return "reportes/libro-mayor";
    }

    @GetMapping("/libro-diario")
    public String libroDiario(Model model,
                              @RequestParam(required = false) String fechaInicio,
                              @RequestParam(required = false) String fechaFin) {
        LocalDate inicio = fechaInicio != null ? LocalDate.parse(fechaInicio) : LocalDate.now().withDayOfMonth(1);
        LocalDate fin = fechaFin != null ? LocalDate.parse(fechaFin) : LocalDate.now();

        List<AsientoContable> asientos = asientoContableRepository.findAll().stream()
                .filter(a -> {
                    LocalDate fechaAsiento = a.getFecha().toLocalDate();
                    return !fechaAsiento.isBefore(inicio) && !fechaAsiento.isAfter(fin);
                })
                .sorted(Comparator.comparing(AsientoContable::getFecha))
                .collect(Collectors.toList());

        model.addAttribute("fechaInicio", inicio);
        model.addAttribute("fechaFin", fin);
        model.addAttribute("asientos", asientos);

        return "reportes/libro-diario";
    }

    @GetMapping("/flujo-efectivo")
    public String flujoEfectivo(Model model,
                                @RequestParam(required = false) String fechaInicio,
                                @RequestParam(required = false) String fechaFin) {
        LocalDate inicio = fechaInicio != null ? LocalDate.parse(fechaInicio) : LocalDate.now().withDayOfMonth(1);
        LocalDate fin = fechaFin != null ? LocalDate.parse(fechaFin) : LocalDate.now();

        // Obtener cuentas de efectivo (caja, bancos)
        List<Cuenta> cuentasEfectivo = cuentaRepository.findAll().stream()
                .filter(c -> c.getCodigo().startsWith("1.1") || c.getNombre().toLowerCase().contains("caja") 
                        || c.getNombre().toLowerCase().contains("banco"))
                .collect(Collectors.toList());

        BigDecimal saldoInicial = cuentasEfectivo.stream()
                .map(Cuenta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("fechaInicio", inicio);
        model.addAttribute("fechaFin", fin);
        model.addAttribute("cuentasEfectivo", cuentasEfectivo);
        model.addAttribute("saldoInicial", saldoInicial);
        model.addAttribute("saldoFinal", saldoInicial);

        return "reportes/flujo-efectivo";
    }

    @GetMapping("/balance-comprobacion")
    public String balanceComprobacion(Model model,
                                       @RequestParam(required = false) String fecha) {
        LocalDate fechaCorte = fecha != null ? LocalDate.parse(fecha) : LocalDate.now();
        List<Cuenta> cuentas = cuentaRepository.findAll();

        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;

        for (Cuenta cuenta : cuentas) {
            if (cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                if (cuenta.getNaturaleza().toString().equals("DEUDORA")) {
                    totalDebitos = totalDebitos.add(cuenta.getSaldo());
                } else {
                    totalCreditos = totalCreditos.add(cuenta.getSaldo());
                }
            }
        }

        model.addAttribute("fechaCorte", fechaCorte);
        model.addAttribute("cuentas", cuentas);
        model.addAttribute("totalDebitos", totalDebitos);
        model.addAttribute("totalCreditos", totalCreditos);
        model.addAttribute("diferencia", totalDebitos.subtract(totalCreditos));

        return "reportes/balance-comprobacion";
    }

    private BigDecimal calcularTotalPorTipo(List<Cuenta> cuentas, String tipo) {
        return cuentas.stream()
                .filter(c -> c.getTipo().toString().equals(tipo))
                .map(Cuenta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ============= ENDPOINTS DE EXPORTACIÓN A EXCEL =============

    @GetMapping("/balance-general/excel")
    public ResponseEntity<byte[]> exportarBalanceGeneralExcel(@RequestParam(required = false) String fecha) {
        try {
            LocalDate fechaCorte = fecha != null ? LocalDate.parse(fecha) : LocalDate.now();
            List<Cuenta> todasCuentas = cuentaRepository.findAll();

            Map<String, List<Cuenta>> cuentasPorTipo = todasCuentas.stream()
                    .collect(Collectors.groupingBy(c -> c.getTipo().toString()));

            List<Cuenta> activos = cuentasPorTipo.getOrDefault("ACTIVO", new ArrayList<>());
            List<Cuenta> pasivos = cuentasPorTipo.getOrDefault("PASIVO", new ArrayList<>());
            List<Cuenta> patrimonio = cuentasPorTipo.getOrDefault("PATRIMONIO", new ArrayList<>());

            BigDecimal totalActivos = calcularTotalPorTipo(todasCuentas, "ACTIVO");
            BigDecimal totalPasivos = calcularTotalPorTipo(todasCuentas, "PASIVO");
            BigDecimal totalPatrimonio = calcularTotalPorTipo(todasCuentas, "PATRIMONIO");

            byte[] excelBytes = excelGeneratorService.generateBalanceGeneral(
                    activos, pasivos, patrimonio, totalActivos, totalPasivos, totalPatrimonio, fechaCorte);

            String filename = "Balance_General_" + fechaCorte.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/estado-resultados/excel")
    public ResponseEntity<byte[]> exportarEstadoResultadosExcel(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        try {
            LocalDate inicio = fechaInicio != null ? LocalDate.parse(fechaInicio) : LocalDate.now().withDayOfMonth(1);
            LocalDate fin = fechaFin != null ? LocalDate.parse(fechaFin) : LocalDate.now();

            List<Cuenta> todasCuentas = cuentaRepository.findAll();

            List<Cuenta> ingresos = todasCuentas.stream()
                    .filter(c -> c.getTipo().toString().equals("INGRESO"))
                    .collect(Collectors.toList());

            List<Cuenta> gastos = todasCuentas.stream()
                    .filter(c -> c.getTipo().toString().equals("GASTO"))
                    .collect(Collectors.toList());

            BigDecimal totalIngresos = calcularTotalPorTipo(todasCuentas, "INGRESO");
            BigDecimal totalGastos = calcularTotalPorTipo(todasCuentas, "GASTO");
            BigDecimal utilidadNeta = totalIngresos.subtract(totalGastos);

            byte[] excelBytes = excelGeneratorService.generateEstadoResultados(
                    ingresos, gastos, totalIngresos, totalGastos, utilidadNeta, inicio, fin);

            String filename = "Estado_Resultados_" + inicio.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + 
                            "_" + fin.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/balance-comprobacion/excel")
    public ResponseEntity<byte[]> exportarBalanceComprobacionExcel(@RequestParam(required = false) String fecha) {
        try {
            LocalDate fechaCorte = fecha != null ? LocalDate.parse(fecha) : LocalDate.now();
            List<Cuenta> cuentas = cuentaRepository.findAll();

            BigDecimal totalDebitos = BigDecimal.ZERO;
            BigDecimal totalCreditos = BigDecimal.ZERO;

            for (Cuenta cuenta : cuentas) {
                if (cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                    if (cuenta.getNaturaleza().toString().equals("DEUDORA")) {
                        totalDebitos = totalDebitos.add(cuenta.getSaldo());
                    } else {
                        totalCreditos = totalCreditos.add(cuenta.getSaldo());
                    }
                }
            }

            byte[] excelBytes = excelGeneratorService.generateBalanceComprobacion(
                    cuentas, totalDebitos, totalCreditos, fechaCorte);

            String filename = "Balance_Comprobacion_" + fechaCorte.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============= ENDPOINTS DE EXPORTACIÓN A PDF =============

    @GetMapping("/balance-general/pdf")
    public ResponseEntity<byte[]> exportarBalanceGeneralPdf(@RequestParam(required = false) String fecha) {
        try {
            LocalDate fechaCorte = fecha != null ? LocalDate.parse(fecha) : LocalDate.now();
            List<Cuenta> todasCuentas = cuentaRepository.findAll();

            Map<String, List<Cuenta>> cuentasPorTipo = todasCuentas.stream()
                    .collect(Collectors.groupingBy(c -> c.getTipo().toString()));

            List<Cuenta> activos = cuentasPorTipo.getOrDefault("ACTIVO", new ArrayList<>());
            List<Cuenta> pasivos = cuentasPorTipo.getOrDefault("PASIVO", new ArrayList<>());
            List<Cuenta> patrimonio = cuentasPorTipo.getOrDefault("PATRIMONIO", new ArrayList<>());

            BigDecimal totalActivos = calcularTotalPorTipo(todasCuentas, "ACTIVO");
            BigDecimal totalPasivos = calcularTotalPorTipo(todasCuentas, "PASIVO");
            BigDecimal totalPatrimonio = calcularTotalPorTipo(todasCuentas, "PATRIMONIO");

            byte[] pdfBytes = pdfGeneratorService.generateBalanceGeneral(
                    activos, pasivos, patrimonio, totalActivos, totalPasivos, totalPatrimonio, fechaCorte);

            String filename = "Balance_General_" + fechaCorte.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/estado-resultados/pdf")
    public ResponseEntity<byte[]> exportarEstadoResultadosPdf(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        try {
            LocalDate inicio = fechaInicio != null ? LocalDate.parse(fechaInicio) : LocalDate.now().withDayOfMonth(1);
            LocalDate fin = fechaFin != null ? LocalDate.parse(fechaFin) : LocalDate.now();

            List<Cuenta> todasCuentas = cuentaRepository.findAll();

            List<Cuenta> ingresos = todasCuentas.stream()
                    .filter(c -> c.getTipo().toString().equals("INGRESO"))
                    .collect(Collectors.toList());

            List<Cuenta> gastos = todasCuentas.stream()
                    .filter(c -> c.getTipo().toString().equals("GASTO"))
                    .collect(Collectors.toList());

            BigDecimal totalIngresos = calcularTotalPorTipo(todasCuentas, "INGRESO");
            BigDecimal totalGastos = calcularTotalPorTipo(todasCuentas, "GASTO");
            BigDecimal utilidadNeta = totalIngresos.subtract(totalGastos);

            byte[] pdfBytes = pdfGeneratorService.generateEstadoResultados(
                    ingresos, gastos, totalIngresos, totalGastos, utilidadNeta, inicio, fin);

            String filename = "Estado_Resultados_" + inicio.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + 
                            "_" + fin.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/balance-comprobacion/pdf")
    public ResponseEntity<byte[]> exportarBalanceComprobacionPdf(@RequestParam(required = false) String fecha) {
        try {
            LocalDate fechaCorte = fecha != null ? LocalDate.parse(fecha) : LocalDate.now();
            List<Cuenta> cuentas = cuentaRepository.findAll();

            BigDecimal totalDebitos = BigDecimal.ZERO;
            BigDecimal totalCreditos = BigDecimal.ZERO;

            for (Cuenta cuenta : cuentas) {
                if (cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                    if (cuenta.getNaturaleza().toString().equals("DEUDORA")) {
                        totalDebitos = totalDebitos.add(cuenta.getSaldo());
                    } else {
                        totalCreditos = totalCreditos.add(cuenta.getSaldo());
                    }
                }
            }

            byte[] pdfBytes = pdfGeneratorService.generateBalanceComprobacion(
                    cuentas, totalDebitos, totalCreditos, fechaCorte);

            String filename = "Balance_Comprobacion_" + fechaCorte.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
