package com.example.roleslz_backend.Tables.users.DTOS.Req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTOProfileReq {
    long id;
    String nome;
    String email;
}
