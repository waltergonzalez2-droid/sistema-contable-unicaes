package com.unicaes.contabilidad.controller;

import com.unicaes.contabilidad.model.PeriodoContable;
import com.unicaes.contabilidad.service.PeriodoContableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/periodos")
public class PeriodoContableController {

    @Autowired
    private PeriodoContableService periodoService;

    @GetMapping
    public String listar(Model model) {
        List<PeriodoContable> periodos = periodoService.obtenerTodos();
        model.addAttribute("periodos", periodos);
        return "periodos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("periodo", new PeriodoContable());
        model.addAttribute("accion", "Nuevo");
        return "periodos/form";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return periodoService.obtenerPorId(id)
                .map(periodo -> {
                    model.addAttribute("periodo", periodo);
                    model.addAttribute("accion", "Editar");
                    return "periodos/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Período no encontrado");
                    return "redirect:/periodos";
                });
    }

    @PostMapping("/nuevo")
    public String crear(@ModelAttribute PeriodoContable periodo, RedirectAttributes redirectAttributes) {
        try {
            periodoService.guardar(periodo);
            redirectAttributes.addFlashAttribute("success", "Período contable creado exitosamente");
            return "redirect:/periodos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear período: " + e.getMessage());
            return "redirect:/periodos/nuevo";
        }
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id, @ModelAttribute PeriodoContable periodo, 
                           RedirectAttributes redirectAttributes) {
        try {
            periodo.setId(id);
            periodoService.guardar(periodo);
            redirectAttributes.addFlashAttribute("success", "Período contable actualizado exitosamente");
            return "redirect:/periodos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar período: " + e.getMessage());
            return "redirect:/periodos/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            periodoService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Período eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar período: " + e.getMessage());
        }
        return "redirect:/periodos";
    }

    @PostMapping("/{id}/cerrar")
    public String cerrarPeriodo(@PathVariable Long id, Authentication authentication, 
                               RedirectAttributes redirectAttributes) {
        try {
            String usuario = authentication != null ? authentication.getName() : "Sistema";
            periodoService.cerrarPeriodo(id, usuario);
            redirectAttributes.addFlashAttribute("success", "Período cerrado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cerrar período: " + e.getMessage());
        }
        return "redirect:/periodos";
    }

    @PostMapping("/{id}/reabrir")
    public String reabrirPeriodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            periodoService.reabrirPeriodo(id);
            redirectAttributes.addFlashAttribute("success", "Período reabierto exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al reabrir período: " + e.getMessage());
        }
        return "redirect:/periodos";
    }
}
