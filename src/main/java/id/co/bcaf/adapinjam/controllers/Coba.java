package id.co.bcaf.adapinjam.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coba")
public class Coba {
    @GetMapping("test")
    public ResponseEntity<String> testToken() {
        return ResponseEntity.status(HttpStatus.OK).body("Missing or invalid Authorization header");
    }
}
