package id.co.bcaf.adapinjam.securitys;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dnq7cspw2",
                "api_key", "218466915326667",
                "api_secret", "hc7ACl4v1975-30yLzOHTTUO9Vw",
                "secure", true
        ));
    }
}

