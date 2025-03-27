package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.dtos.CustomerRequest;
import id.co.bcaf.adapinjam.models.UserCustomer;
import id.co.bcaf.adapinjam.services.CustomerService;
import id.co.bcaf.adapinjam.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private JwtUtil jwtUtil;

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

}
