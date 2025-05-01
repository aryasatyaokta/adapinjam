package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.Plafon;
import id.co.bcaf.adapinjam.services.PlafonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/plafon")
public class PlafonController {
    private final PlafonService plafonService;

    public PlafonController(PlafonService plafonService) {
        this.plafonService = plafonService;
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'CREATE_PLAFON')")
    @PostMapping
    public ResponseEntity<Plafon> createPlafon(@RequestBody Plafon plafon) {
        Plafon savedPlafon = plafonService.createPlafon(plafon);
        return ResponseEntity.ok(savedPlafon);
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_ALL_PLAFON')")
    @GetMapping
    public ResponseEntity<List<Plafon>> getAllPlafons(){
        List<Plafon> plafons = plafonService.getAllPlafons();
        return ResponseEntity.ok(plafons);
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_PLAFON_BY_ID')")
    @GetMapping("/{id}")
    public ResponseEntity<Plafon> getPlafonById(@PathVariable Integer id) {
        return plafonService.getPlafonById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'UPDATE_PLAFON')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Plafon> updatePlafon(@PathVariable Integer id, @RequestBody Plafon plafon) {
        Plafon updated = plafonService.updatePlafon(id, plafon);
        return ResponseEntity.ok(updated);
    }

}
