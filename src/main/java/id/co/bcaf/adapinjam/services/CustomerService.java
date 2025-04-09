package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.dtos.CustomerRequest;
import id.co.bcaf.adapinjam.models.Plafon;
import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.models.UserCustomer;
import id.co.bcaf.adapinjam.repositories.CustomerRepository;
import id.co.bcaf.adapinjam.repositories.PlafonRepository;
import id.co.bcaf.adapinjam.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlafonRepository plafonRepository;

    public boolean isProfileComplete(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return customerRepository.existsByUser(user);
    }

    public UserCustomer addCustomerDetails(String email, CustomerRequest customerRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (customerRepository.existsByUser(user)) {
            throw new RuntimeException("Customer details already exist");
        }

        int defaultPlafon = 1;
        Plafon plafon = plafonRepository.findById(defaultPlafon)
                .orElseThrow(() -> new RuntimeException("Plafon not found"));

        UserCustomer customer = new UserCustomer();
        customer.setUser(user);
        customer.setPlafon(plafon);
        customer.setNik(customerRequest.getNik());
        customer.setTempatTglLahir(customerRequest.getTempatTglLahir());
        customer.setNoTelp(customerRequest.getNoTelp());
        customer.setAlamat(customerRequest.getAlamat());
        customer.setNamaIbuKandung(customerRequest.getNamaIbuKandung());
        customer.setPekerjaan(customerRequest.getPekerjaan());
        customer.setGaji(customerRequest.getGaji());
        customer.setNoRek(customerRequest.getNoRek());
        customer.setStatusRumah(customerRequest.getStatusRumah());
//        customer.setSisaPlafon(customerRequest.getSisaPlafon());
        customer.setSisaPlafon(plafon.getJumlahPlafon());

        return customerRepository.save(customer);
    }

    public UserCustomer editCustomerDetails(String email, CustomerRequest customerRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserCustomer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setNik(customerRequest.getNik());
        customer.setTempatTglLahir(customerRequest.getTempatTglLahir());
        customer.setNoTelp(customerRequest.getNoTelp());
        customer.setAlamat(customerRequest.getAlamat());
        customer.setNamaIbuKandung(customerRequest.getNamaIbuKandung());
        customer.setPekerjaan(customerRequest.getPekerjaan());
        customer.setGaji(customerRequest.getGaji());
        customer.setNoRek(customerRequest.getNoRek());
        customer.setStatusRumah(customerRequest.getStatusRumah());
        customer.setSisaPlafon(customerRequest.getSisaPlafon());

        return customerRepository.save(customer);
    }

    public UUID getCustomerIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return customerRepository.findByUser(user)
                .map(UserCustomer::getId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
