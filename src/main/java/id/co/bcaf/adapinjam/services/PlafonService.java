package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.models.Plafon;
import id.co.bcaf.adapinjam.repositories.PlafonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlafonService {

    private final PlafonRepository plafonRepository;

    public PlafonService(PlafonRepository plafonRepository) {
        this.plafonRepository = plafonRepository;
    }

    public Plafon createPlafon(Plafon plafon) {
        return plafonRepository.save(plafon);
    }

    public List<Plafon> getAllPlafons() {
        return plafonRepository.findAll();
    }

    public Optional<Plafon> getPlafonById(Integer id) {
        return plafonRepository.findById(id);
    }

    public Plafon updatePlafon(Integer id, Plafon updatedPlafon) {
        Plafon existingPlafon = plafonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plafon dengan ID " + id + " tidak ditemukan"));

        existingPlafon.setJenisPlafon(updatedPlafon.getJenisPlafon());
        existingPlafon.setJumlahPlafon(updatedPlafon.getJumlahPlafon());
        existingPlafon.setBunga(updatedPlafon.getBunga());

        return plafonRepository.save(existingPlafon);
    }

}
