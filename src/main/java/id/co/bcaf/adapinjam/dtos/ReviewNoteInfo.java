package id.co.bcaf.adapinjam.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewNoteInfo {
    private String role;           // Nama peran: "Marketing", "Branch Manager", "Back Office"
    private String reviewerName;   // Nama pegawai yang memberikan review
    private String catatan;        // Isi catatan review

    public ReviewNoteInfo() {}

    public ReviewNoteInfo(String role, String reviewerName, String catatan) {
        this.role = role;
        this.reviewerName = reviewerName;
        this.catatan = catatan;
    }
}

