package com.unicaes.contabilidad.controller;

import com.unicaes.contabilidad.model.AsientoContable;
import com.unicaes.contabilidad.model.DetalleAsiento;
import com.unicaes.contabilidad.model.Cuenta;
import com.unicaes.contabilidad.service.AsientoContableService;
import com.unicaes.contabilidad.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/asientos")
public class AsientoContableViewController {

    @Autowired
    private AsientoContableService asientoService;
    
    @Autowired
    private CuentaService cuentaService;

    @GetMapping
    public String listar(Model model) {
        List<AsientoContable> asientos = asientoService.obtenerTodos();
        model.addAttribute("asientos", asientos);
        return "asientos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        AsientoContable asiento = new AsientoContable();
        asiento.setFecha(LocalDateTime.now());
        asiento.setDetalles(new ArrayList<>());
        
        // Agregar 4 líneas de detalle vacías por defecto
        for (int i = 0; i < 4; i++) {
            DetalleAsiento detalle = new DetalleAsiento();
            detalle.setDebe(BigDecimal.ZERO);
            detalle.setHaber(BigDecimal.ZERO);
            asiento.getDetalles().add(detalle);
        }
        
        List<Cuenta> cuentas = cuentaService.obtenerCuentasActivas();
        
        model.addAttribute("asiento", asiento);
        model.addAttribute("cuentas", cuentas);
        model.addAttribute("accion", "Nuevo");
        return "asientos/form";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return asientoService.obtenerPorId(id)
                .map(asiento -> {
                    List<Cuenta> cuentas = cuentaService.obtenerCuentasActivas();
                    model.addAttribute("asiento", asiento);
                    model.addAttribute("cuentas", cuentas);
                    model.addAttribute("accion", "Editar");
                    return "asientos/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Asiento no encontrado");
                    return "redirect:/asientos";
                });
    }

    @PostMapping("/nuevo")
    public String crear(@ModelAttribute AsientoContable asiento,
                       @RequestParam(value = "cuentaId", required = false) List<Long> cuentaIds,
                       @RequestParam(value = "debe", required = false) List<String> debes,
                       @RequestParam(value = "haber", required = false) List<String> haberes,
                       @RequestParam(value = "referencia", required = false) List<String> referencias,
                       Authentication authentication,
                       RedirectAttributes redirectAttributes) {
        try {
            // Limpiar detalles existentes
            asiento.getDetalles().clear();
            
            // Procesar detalles del formulario
            if (cuentaIds != null && !cuentaIds.isEmpty()) {
                for (int i = 0; i < cuentaIds.size(); i++) {
                    if (cuentaIds.get(i) != null) {
                        BigDecimal debe = parseBigDecimal(debes.get(i));
                        BigDecimal haber = parseBigDecimal(haberes.get(i));
                        
                        // Solo agregar si hay movimiento (debe o haber mayor a 0)
                        if (debe.compareTo(BigDecimal.ZERO) > 0 || haber.compareTo(BigDecimal.ZERO) > 0) {
                            DetalleAsiento detalle = new DetalleAsiento();
                            detalle.setCuenta(cuentaService.obtenerPorId(cuentaIds.get(i)).orElse(null));
                            detalle.setDebe(debe);
                            detalle.setHaber(haber);
                            detalle.setReferencia(referencias != null && i < referencias.size() ? referencias.get(i) : "");
                            detalle.setAsientoContable(asiento);
                            asiento.getDetalles().add(detalle);
                        }
                    }
                }
            }
            
            // Establecer usuario creador
            if (authentication != null) {
                asiento.setCreadoPor(authentication.getName());
            }
            
            asientoService.crearAsiento(asiento);
            redirectAttributes.addFlashAttribute("success", "Asiento contable creado exitosamente");
            return "redirect:/asientos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear asiento: " + e.getMessage());
            return "redirect:/asientos/nuevo";
        }
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id,
                           @ModelAttribute AsientoContable asiento,
                           @RequestParam(value = "cuentaId", required = false) List<Long> cuentaIds,
                           @RequestParam(value = "debe", required = false) List<String> debes,
                           @RequestParam(value = "haber", required = false) List<String> haberes,
                           @RequestParam(value = "referencia", required = false) List<String> referencias,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            asiento.setId(id);
            
            // Limpiar detalles existentes
            asiento.getDetalles().clear();
            
            // Procesar detalles del formulario
            if (cuentaIds != null && !cuentaIds.isEmpty()) {
                for (int i = 0; i < cuentaIds.size(); i++) {
                    if (cuentaIds.get(i) != null) {
                        BigDecimal debe = parseBigDecimal(debes.get(i));
                        BigDecimal haber = parseBigDecimal(haberes.get(i));
                        
                        if (debe.compareTo(BigDecimal.ZERO) > 0 || haber.compareTo(BigDecimal.ZERO) > 0) {
                            DetalleAsiento detalle = new DetalleAsiento();
                            detalle.setCuenta(cuentaService.obtenerPorId(cuentaIds.get(i)).orElse(null));
                            detalle.setDebe(debe);
                            detalle.setHaber(haber);
                            detalle.setReferencia(referencias != null && i < referencias.size() ? referencias.get(i) : "");
                            detalle.setAsientoContable(asiento);
                            asiento.getDetalles().add(detalle);
                        }
                    }
                }
            }
            
            // Establecer usuario modificador
            if (authentication != null) {
                asiento.setModificadoPor(authentication.getName());
            }
            
            asientoService.guardar(asiento);
            redirectAttributes.addFlashAttribute("success", "Asiento contable actualizado exitosamente");
            return "redirect:/asientos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar asiento: " + e.getMessage());
            return "redirect:/asientos/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            asientoService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Asiento eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar asiento: " + e.getMessage());
        }
        return "redirect:/asientos";
    }

    @PostMapping("/{id}/registrar")
    public String registrar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            asientoService.registrarAsiento(id);
            redirectAttributes.addFlashAttribute("success", "Asiento registrado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar asiento: " + e.getMessage());
        }
        return "redirect:/asientos";
    }

    @PostMapping("/{id}/anular")
    public String anular(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            asientoService.anularAsiento(id);
            redirectAttributes.addFlashAttribute("success", "Asiento anulado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al anular asiento: " + e.getMessage());
        }
        return "redirect:/asientos";
    }
    
    private BigDecimal parseBigDecimal(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(valor.replace(",", ""));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
