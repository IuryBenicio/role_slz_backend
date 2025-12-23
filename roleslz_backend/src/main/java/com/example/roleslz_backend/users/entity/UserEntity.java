package com.example.roleslz_backend.users.entity;

import com.example.roleslz_backend.Utills.LocationData;
import com.example.roleslz_backend.events.entity.EventoEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@ToString
public class UserEntity implements UserDetails {

    @Id
    @Column(name =  "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    @NotBlank(message = "name é obrigatório")
    private String nome;

    @Column(name = "sobrenome")
    @NotBlank(message = "sobrenome é obrigatório")
    private String sobrenome;

    @Column(name = "sexo")
    @NotBlank(message = "idade é obrigatória")
    private Sexo sexo;

    @Column(name = "email", unique = true)
    @Email
    @NotBlank(message = "email é obrigatório")
    private String email;

    @Column(name = "email_verify")
    private boolean emailVerify = false;

    @Column(name = "password")
    @NotBlank(message = "senha é obrigatória")
    private String password;

    @Column(name = "role")
    @NotBlank(message = "role obrigatória")
    private Roles role;

    @Column(name = "idade")
    @NotBlank(message = "idade é obrigatória")
    private String idade;

    @Column(name = "coordenadas")
    private LocationData coordenadas;

    @Column(name = "CreatedAt")
    private CreatedDate createdAt;

    @OneToMany(mappedBy = "organizador", orphanRemoval = true)
    private List<EventoEntity> eventos;

    //Spring Security
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            if(this.role == Roles.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
            else return List.of(new SimpleGrantedAuthority("ROLE_USER"));    }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
        return true;
    }

}
