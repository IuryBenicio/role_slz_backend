package com.example.roleslz_backend.Tables.users.entity;

import com.example.roleslz_backend.Tables.business.entity.BusinessEntity;
import com.example.roleslz_backend.Utills.BaseEntity.BaseEntity;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserEntity extends BaseEntity implements UserDetails {

    @Id
    @Column(name =  "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nome")
    @NotBlank(message = "nome é obrigatório")
    private String nome;

    @Column(name = "sobrenome")
    @NotBlank(message = "sobrenome é obrigatório")
    private String sobrenome;

    @Column(name = "sexo")
    @NotBlank(message = "sexo é obrigatória")
    private Sexo sexo;

    @Column(name = "email", unique = true)
    @Email
    @NotBlank(message = "email é obrigatório")
    private String email;

    @Column(name = "is_enable")
    private boolean enable = false;

    @Column(name = "password")
    @NotBlank(message = "senha é obrigatória")
    private String password;

    @Column(name = "role")
    @NotBlank(message = "role obrigatória")
    private Roles role = Roles.REGULAR;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "idade")
    @NotBlank(message = "idade é obrigatória")
    private Integer idade;

    @OneToMany(mappedBy = "organizador", orphanRemoval = true)
    private List<EventoEntity> eventos;

    @OneToMany
    @JoinColumn(name = "historico_eventos")
    private List<EventoEntity> historicoEventos;

    @OneToOne
    @JoinColumn(name = "business_id")
    private BusinessEntity business;


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
        return enable;
    }

}
