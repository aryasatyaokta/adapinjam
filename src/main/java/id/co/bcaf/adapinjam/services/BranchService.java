package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.models.Branch;
import id.co.bcaf.adapinjam.repositories.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Optional<Branch> getBranchById(UUID id) {
        return branchRepository.findById(id);
    }

    public Branch createBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    public Branch updateBranch(UUID id, Branch updatedBranch) {
        return branchRepository.findById(id).map(branch -> {
            branch.setBranch(updatedBranch.getBranch());
            branch.setBranch(updatedBranch.getAddress());
            branch.setLatitude(updatedBranch.getLatitude());
            branch.setLongitude(updatedBranch.getLongitude());
            return branchRepository.save(branch);
        }).orElseThrow(() -> new RuntimeException("Branch not found"));
    }

    public void deleteBranch(UUID id) {
        branchRepository.deleteById(id);
    }
}
