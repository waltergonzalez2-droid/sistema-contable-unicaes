package com.unicaes.contabilidad.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.unicaes.contabilidad.service.CuentaService;
import com.unicaes.contabilidad.model.Cuenta;

@Controller
public class WebController {
    
    @Autowired
    private CuentaService cuentaService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Cargar estadísticas reales de la base de datos
        long totalCuentas = cuentaService.obtenerTodasLasCuentas().size();
        
        model.addAttribute("totalCuentas", totalCuentas);
        model.addAttribute("totalAsientos", 0L); // Por ahora 0
        model.addAttribute("totalPeriodos", 0L); // Por ahora 0
        
        return "dashboard";
    }

    @GetMapping("/cuentas")
    public String cuentas(Model model) {
        model.addAttribute("cuentas", cuentaService.obtenerTodasLasCuentas());
        return "cuentas/lista";
    }

    @GetMapping("/cuentas/nueva")
    public String nuevaCuenta(Model model) {
        model.addAttribute("cuenta", new Cuenta());
        return "cuentas/form";
    }

    @PostMapping("/cuentas/nueva")
    public String guardarCuenta(@ModelAttribute Cuenta cuenta, RedirectAttributes redirectAttributes) {
        try {
            cuentaService.crearCuenta(cuenta);
            redirectAttributes.addFlashAttribute("mensaje", "Cuenta creada exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            return "redirect:/cuentas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al crear la cuenta: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/cuentas/nueva";
        }
    }

    @GetMapping("/cuentas/{id}/editar")
    public String editarCuenta(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return cuentaService.obtenerPorId(id)
            .map(cuenta -> {
                model.addAttribute("cuenta", cuenta);
                model.addAttribute("cuentas", cuentaService.obtenerTodasLasCuentas());
                return "cuentas/form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("mensaje", "Cuenta no encontrada");
                redirectAttributes.addFlashAttribute("tipo", "error");
                return "redirect:/cuentas";
            });
    }

    @PostMapping("/cuentas/{id}/editar")
    public String actualizarCuenta(@PathVariable Long id, @ModelAttribute Cuenta cuenta, RedirectAttributes redirectAttributes) {
        try {
            cuenta.setId(id);
            cuentaService.crearCuenta(cuenta); // El mismo método sirve para actualizar
            redirectAttributes.addFlashAttribute("mensaje", "Cuenta actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            return "redirect:/cuentas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar la cuenta: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/cuentas/" + id + "/editar";
        }
    }

    @PostMapping("/cuentas/{id}/eliminar")
    public String eliminarCuenta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cuentaService.eliminarCuenta(id);
            redirectAttributes.addFlashAttribute("mensaje", "Cuenta eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("mensaje", "No se puede eliminar la cuenta porque tiene subcuentas o movimientos asociados. Puedes desactivarla en su lugar.");
            redirectAttributes.addFlashAttribute("tipo", "error");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar la cuenta: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/cuentas";
    }
}


