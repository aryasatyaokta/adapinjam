package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.Plafon;
import id.co.bcaf.adapinjam.services.PlafonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/plafon")
public class PlafonController {
    private final PlafonService plafonService;

    public PlafonController(PlafonService plafonService) {
        this.plafonService = plafonService;
    }

    @PostMapping
    public ResponseEntity<Plafon> createPlafon(@RequestBody Plafon plafon) {
        Plafon savedPlafon = plafonService.createPlafon(plafon);
        return ResponseEntity.ok(savedPlafon);
    }

    @GetMapping
    public ResponseEntity<List<Plafon>> getAllPlafons(){
        List<Plafon> plafons = plafonService.getAllPlafons();
        return ResponseEntity.ok(plafons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plafon> getPlafonById(@PathVariable Integer id) {
        return plafonService.getPlafonById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
