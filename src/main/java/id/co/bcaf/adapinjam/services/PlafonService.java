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
        return plafonRepository.findByDeletedFalse();
    }

    public Optional<Plafon> getPlafonByUserId(String userId) {
        return plafonRepository.findByUserId(userId);  // Pastikan repository mendukung pencarian berdasarkan userId
    }

    public Optional<Plafon> getPlafonById(Integer id) {
        return plafonRepository.findById(id)
                .filter(plafon -> !plafon.isDeleted());
    }

    public Plafon updatePlafon(Integer id, Plafon updatedPlafon) {
        Plafon existingPlafon = plafonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plafon dengan ID " + id + " tidak ditemukan"));

        existingPlafon.setJenisPlafon(updatedPlafon.getJenisPlafon());
        existingPlafon.setJumlahPlafon(updatedPlafon.getJumlahPlafon());
        existingPlafon.setBunga(updatedPlafon.getBunga());

        return plafonRepository.save(existingPlafon);
    }

    public void softDeletePlafon(Integer id) {
        Plafon plafon = plafonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plafon dengan ID " + id + " tidak ditemukan"));

        plafon.setDeleted(true);
        plafonRepository.save(plafon);
    }


}
