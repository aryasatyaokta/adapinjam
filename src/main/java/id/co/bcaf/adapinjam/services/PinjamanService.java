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
    public void bayarPinjaman(UUID pinjamanId, int jumlahTenor, double jumlahBayar) {
        Pinjaman pinjaman = pinjamanRepository.findById(pinjamanId)
                .orElseThrow(() -> new RuntimeException("Pinjaman tidak ditemukan."));

        if (pinjaman.getLunas()) {
            throw new RuntimeException("Pinjaman ini sudah lunas.");
        }

        // Validasi tenor & jumlah bayar
        if (jumlahTenor <= 0 || jumlahTenor > pinjaman.getSisaTenor()) {
            throw new RuntimeException("Jumlah tenor tidak valid.");
        }

        double totalBayarDiharapkan = pinjaman.getAngsuran() * jumlahTenor;
        if (jumlahBayar < totalBayarDiharapkan) {
            throw new RuntimeException("Jumlah bayar kurang dari angsuran seharusnya: " + totalBayarDiharapkan);
        }

        // Kurangi sisa tenor & sisa pokok hutang
        pinjaman.setSisaTenor(pinjaman.getSisaTenor() - jumlahTenor);
        pinjaman.setSisaPokokHutang(pinjaman.getSisaPokokHutang() - totalBayarDiharapkan);

        // Jika sisa tenor = 0 dan sisa pokok hutang <= 0 maka tandai lunas
        if (pinjaman.getSisaTenor() == 0 || pinjaman.getSisaPokokHutang() <= 0) {
            pinjaman.setLunas(true);
            pinjaman.setSisaPokokHutang(0.0);
            pinjaman.setAngsuran(0.0);
            pinjaman.getCustomer().setSisaPlafon(pinjaman.getCustomer().getSisaPlafon() + pinjaman.getAmount());

            // Simpan perubahan customer dan cek upgrade plafon
            customerRepository.save(pinjaman.getCustomer());

            UUID customerId = pinjaman.getCustomer().getId();
            List<Pinjaman> lunasList = pinjamanRepository.findByCustomer_IdAndLunasTrue(customerId);
            double totalLunas = lunasList.stream().mapToDouble(Pinjaman::getAmount).sum();

            upgradePlafonIfEligible(pinjaman.getCustomer(), totalLunas);
        }
        pinjamanRepository.save(pinjaman);
    }

}

