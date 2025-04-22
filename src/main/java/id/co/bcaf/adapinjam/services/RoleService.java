package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.models.Role;
import id.co.bcaf.adapinjam.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Integer id) {
        return roleRepository.findById(id);
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Optional<Role> updateRole(Integer id, Role roleDetails) {
        return roleRepository.findById(id).map(role -> {
            role.setNameRole(roleDetails.getNameRole());
            return roleRepository.save(role);
        });
    }

    public boolean deleteRole(Integer id) {
        return roleRepository.findById(id).map(role -> {
            roleRepository.delete(role);
            return true;
        }).orElse(false);
    }
}
