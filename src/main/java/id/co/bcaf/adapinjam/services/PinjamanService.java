package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.models.Pinjaman;
import id.co.bcaf.adapinjam.models.Plafon;
import id.co.bcaf.adapinjam.models.UserCustomer;
import id.co.bcaf.adapinjam.repositories.CustomerRepository;
import id.co.bcaf.adapinjam.repositories.PinjamanRepository;
import id.co.bcaf.adapinjam.repositories.PlafonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class PinjamanService {

    @Autowired
    private PinjamanRepository pinjamanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PlafonRepository plafonRepository;

    public void createPinjamanFromPengajuan(UserCustomer customer, Double amount, Integer tenor, Double bunga, Double angsuran) {
        Pinjaman pinjaman = new Pinjaman();
        pinjaman.setCustomer(customer);
        pinjaman.setAmount(amount);
        pinjaman.setTenor(tenor);
        pinjaman.setBunga(bunga);
        pinjaman.setAngsuran(angsuran);
        pinjaman.setSisaTenor(tenor);
        pinjaman.setSisaPokokHutang((angsuran * tenor));
        pinjaman.setLunas(false);

        pinjamanRepository.save(pinjaman);
    }

    public void markAsLunas(UUID pinjamanId) {
        Pinjaman pinjaman = pinjamanRepository.findById(pinjamanId)
                .orElseThrow(() -> new RuntimeException("Pinjaman not found"));

        pinjaman.setLunas(true);
        pinjaman.setSisaTenor(0);
        pinjaman.setSisaPokokHutang(0.0);
        pinjaman.setAngsuran(0.0);
        UserCustomer customer = pinjaman.getCustomer();
        customer.setSisaPlafon(customer.getSisaPlafon() + pinjaman.getAmount());

        pinjamanRepository.save(pinjaman);
        customerRepository.save(customer);

        UUID customerId = pinjaman.getCustomer().getId();
        List<Pinjaman> lunasList = pinjamanRepository.findByCustomer_IdAndLunasTrue(customerId);
        double totalLunas = lunasList.stream().mapToDouble(Pinjaman::getAmount).sum();

        upgradePlafonIfEligible(pinjaman.getCustomer(), totalLunas);
    }

    private void upgradePlafonIfEligible(UserCustomer customer, double totalLunas) {
        Plafon current = customer.getPlafon();
        Double currentPlafon = current.getJumlahPlafon();

        List<Plafon> all = plafonRepository.findAll();
        all.sort(Comparator.comparing(Plafon::getJumlahPlafon));

        for (Plafon next : all) {
            if (next.getJumlahPlafon() > currentPlafon && totalLunas >= next.getJumlahPlafon()) {
                customer.setPlafon(next);
                customer.setSisaPlafon(next.getJumlahPlafon());
                customerRepository.save(customer);
                break;
            }
        }
    }

    public List<Pinjaman> getByCustomerId(UUID customerId) {
        return pinjamanRepository.findByCustomer_Id(customerId);
    }
}

