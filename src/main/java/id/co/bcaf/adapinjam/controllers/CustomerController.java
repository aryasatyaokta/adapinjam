package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.dtos.CustomerRequest;
import id.co.bcaf.adapinjam.models.UserCustomer;
import id.co.bcaf.adapinjam.repositories.CustomerRepository;
import id.co.bcaf.adapinjam.services.CloudinaryService;
import id.co.bcaf.adapinjam.services.CustomerService;
import id.co.bcaf.adapinjam.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CustomerRepository userCustomerRepository;

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_ALL_CUSTOMER')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'CHECK_PROFILE_CUSTOMER')")
    @GetMapping("/check-profile")
    public ResponseEntity<?> checkProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Authorization");
        }
        String token = authHeader.substring(7).trim();
        String email = jwtUtil.extractEmail(token);

        boolean isCheck = customerService.isProfileComplete(email);
        if (!isCheck){
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body("Please complete profile before applying");
        }
        return ResponseEntity.ok("Profile complete. You can proceed with applications");
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'ADD_DETAILS_CUSTOMER')")
    @PostMapping("/add-customer-details")
    public ResponseEntity<?> addCustomerDetails(@RequestHeader("Authorization") String authHeader, @RequestBody CustomerRequest customerRequest){
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Authorization");
        }
        String token = authHeader.substring(7).trim();
        String email = jwtUtil.extractEmail(token);

        UserCustomer customer = customerService.addCustomerDetails(email, customerRequest);
        return ResponseEntity.ok(customer);
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'EDIT_DETAILS_CUSTOMER')")
    @PutMapping("/edit-customer-details")
    public ResponseEntity<?> editCustomerDetails(@RequestHeader("Authorization") String authHeader, @RequestBody CustomerRequest customerRequest) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Authorization");
        }
        String token = authHeader.substring(7).trim();
        String email = jwtUtil.extractEmail(token);

        UserCustomer updatedCustomer = customerService.editCustomerDetails(email, customerRequest);
        return ResponseEntity.ok(updatedCustomer);
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_IDCUSTOMER')")
    @GetMapping("/get-id-customer")
    public ResponseEntity<?> getCustomerId(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7).trim();
        String email = jwtUtil.extractEmail(token);

        UUID customerId = customerService.getCustomerIdByEmail(email);
        return ResponseEntity.ok(customerId);
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_PROFILE_CUSTOMER')")
    @GetMapping("/get-customer")
    public ResponseEntity<?> getCustomerProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7).trim();
        String email = jwtUtil.extractEmail(token);

        UserCustomer customer = customerService.getCustomerByToken(email);
        return ResponseEntity.ok(customer);
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'DETAIL_CUSTOMER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable UUID id) {
        try {
            UserCustomer customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/upload-foto")
    public ResponseEntity<String> uploadFoto(
            @PathVariable UUID id,
            @RequestParam(value = "fotoKtp", required = false) MultipartFile fotoKtp,
            @RequestParam(value = "fotoSelfie", required = false) MultipartFile fotoSelfie) {

        Optional<UserCustomer> userOpt = userCustomerRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User customer tidak ditemukan");
        }

        UserCustomer customer = userOpt.get();

        if (fotoKtp != null && !fotoKtp.isEmpty()) {
            String fotoktp = cloudinaryService.uploadImage(fotoKtp);
            customer.setFotoKtp(fotoktp);
        }

        if (fotoSelfie != null && !fotoSelfie.isEmpty()) {
            String fotoSelfieUrl = cloudinaryService.uploadImage(fotoSelfie);
            customer.setFotoSelfie(fotoSelfieUrl);
        }

        userCustomerRepository.save(customer);
        return ResponseEntity.ok("Foto berhasil diupload");
    }

    @PostMapping("/{id}/upload-profil")
    public ResponseEntity<String> uploadProfil(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryService.uploadImage(file);
        Optional<UserCustomer> userOpt = userCustomerRepository.findById(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User customer tidak ditemukan");
        }

        UserCustomer customer = userOpt.get();
        customer.setFotoProfil(imageUrl);
        userCustomerRepository.save(customer);

        return ResponseEntity.ok("Foto berhasil diupload. URL: " + imageUrl);
    }

}
