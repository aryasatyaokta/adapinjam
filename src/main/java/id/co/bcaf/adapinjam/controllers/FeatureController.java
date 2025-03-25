package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.Feature;
import id.co.bcaf.adapinjam.repositories.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/features")
public class FeatureController {

    @Autowired
    private FeatureRepository featureRepository;

    @GetMapping
    public List<Feature> getAllFeatures(){
        return featureRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feature> getFeatureById(@PathVariable int id) {
        Optional<Feature> feature = featureRepository.findById(id);
        return feature.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Feature> createFeature(@RequestBody Feature feature){
        Feature savedFeature = featureRepository.save(feature);
        return ResponseEntity.ok(savedFeature);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feature> updateFeature(@PathVariable int id, @RequestBody Feature updatedFeature) {
        return featureRepository.findById(id).map(feature -> {
            feature.setName_feature(updatedFeature.getName_feature());
            featureRepository.save(feature);
            return ResponseEntity.ok(feature);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeature(@PathVariable int id) {
        if (!featureRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        featureRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
